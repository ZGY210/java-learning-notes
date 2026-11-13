# 11 · TreeSet 两种排序（自然排序 vs 比较器排序）

> 归属 `notes/collections/` ｜ 写于 2026-11-11 ｜ 对应黑马 **197（TreeSet 第一种排序方式，40:47）**、**198（TreeSet 第二种排序方式和综合练习，30:35）**

---

## 一、它是什么

**要求：用你自己的话写，不许抄视频原句。写不出来说明还没懂。**

TreeSet 是 Set 的一个实现，底层是  红黑树（TreeMap） （因为它要把元素按大小放进树里左右分支，靠树的结构维持有序）。

它的两个特点：

1. 元素不重复（和 HashSet 共有的）
2. 元素自动有序（只有它有的）

它能排序，靠的是**两种方式**，二选一：

| 方式    | 怎么给规则              | 什么时候用                           | 代码长什么样               |
| ----- | ------------------ | ------------------------------- | -------------------- |
| 自然排序  | 元素自己实现Comparable接口 | 元素只有一种「天然顺序」，且你有权改这个类           | `new TreeSet<>()`    |
| 比较器排序 | 外部传一个 Comparator   | 元素有多种排序需求 / 元素类改不了（比如 JDK 自带的类） | `new TreeSet<>(cmp)` |

> 一句话总结两者关系：____________________

---

## 二、为什么需要它

**要求：回答「用 HashSet + Collections.sort() 也能得到有序结果，为什么还要 TreeSet？」**

至少写两条理由：

1. ________

   持续有序 ：HashSet + Collections.sort() 是"排一次序，之后你再 add 新元素就乱序了"；TreeSet 是"每次插入自动放到正确位置，任何时刻取出来都是有序的"，省去反复重排。
2. ________

   去重和排序一步完成 ：HashSet 只去重不排序，还得再 Collections.sort() 一遍；TreeSet 插入时去重、定位一次搞定，少一遍遍历。
3. 支持范围查询 ：floor / ceiling / headSet / subSet 这些"找最接近的、取某个区间"的操作，HashSet 完全给不了（HashSet 只能精确查"有没有"），这是 TreeSet 独有的能力。

> 提示方向：一次性排序 vs 持续有序；每次插入的代价；去重和排序能不能一步做完；范围查询（`floor` / `ceiling` / `headSet` / `subSet`）HashSet 能不能给你。

---

## 三、场景与坑

这里是今天最值钱的部分。**每一条都要配一句你自己的解释，不要只抄结论。**

### 坑 1：TreeSet 判重靠的是 `compareTo == 0`，不是 `equals`

- 我在 `TreeSetDemo` 里做了什么实验：造两个"按 id 相同但其它字段不同"的对象加进 TreeSet，同时加了 HashSet 做对照 
- 结果：TreeSet 只留下一个，HashSet 也可能留一个（看 equals/hashCode 是否重写）
- 为什么：TreeSet 插入时只调用 compareTo，返回 0 就当成"同一个元素"拒绝加入， 根本不看 equals
- 对照：HashSet 判重靠的是  hashCode()  和  equals()  两个方法

> 追问（面试常问）：如果 `compareTo` 返回 0 但 `equals` 返回 false，
> TreeSet 认为这两个对象是「同一个」，HashSet 认为「不是同一个」——
> 所以在两个集合之间倒数据会**丢元素**。你能举个例子吗？
>
> 核心：两个集合的"判重标准"不一样，跨集合复制时会按新集合的标准再筛一遍，标准更严的那个方向就会丢数据。

### 坑 2：`compareTo` 忘了写「次级比较」，会静默丢数据

- 现象：年龄相同的两个不同的人，加进 TreeSet 后只剩一个
- 原因： compareTo 只比了年龄，年龄相同时返回 0，TreeSet 认为两个是同一个，第二个被拒绝
- 修法： 在 compareTo 里补上"年龄相同时再比姓名/学号"之类的第二级、第三级比较 ，保证"只要不是真正同一个对象，最终一定返回非 0"（如 Integer.compare(age1, age2) != 0 ? ... : name1.compareTo(name2)）。你 TODO 1 里就是这么补的"次级比较"。

### 坑 3：TreeSet 不允许 null

- new TreeSet<Integer></Integer>().add(null) 会抛  NullPointerException
- 原因： 插入时要调用 compareTo 排序，null 身上没有可调用的 compareTo ，一比较就 NPE
- 对照：HashSet 可以存  1  个 null（只算 hashCode，null 的哈希约定为 0）

### 坑 4：`first()` / `last()` 在空集合上不是返回 null

- 空 TreeSet 调 first() 抛  NoSuchElementException
- 所以取之前要先  isEmpty() 判断 （或用 pollFirst() / pollLast()，空时返回 null）

### 坑 5：TreeSet 不是线程安全的

- 多线程同时写会： 数据错乱 / 元素丢失 / 抛出并发修改异常（ConcurrentModificationException）
- 需要并发有序集合时用什么： ConcurrentSkipListSet （提示：它内部用跳表，天生支持并发；不能给 TreeSet 加锁硬扛，因为一锁就把并发性能锁没了，而且迭代器会 fail-fast）

### 坑 6（性能）：TreeSet 插入比 HashSet 慢

- 时间复杂度：TreeSet 插入  O(log n) ，HashSet 插入平均  O(1)
- 原因： TreeSet 插入要走树的一层层比较，路径长度约 log₂n；HashSet 直接按哈希值定位桶，平均一下就找到
- 所以：只需要去重不需要排序时，用  HashSet （或 LinkedHashSet，如果还要保插入顺序）

---

## 四、一张图说清「什么时候选哪个」

| 需求                    | 选谁                   | 理由                                         |
| --------------------- | -------------------- | ------------------------------------------ |
| 只要去重，不要顺序             | HashSet              | 插入 O(1)，最快                                 |
| 去重 + 保持插入顺序           | LinkedHashSet        | 底层哈希表 + 双向链表，记住顺序                          |
| 去重 + 按大小排序            | TreeSet              | 红黑树自动排序，插入 O(log n)                        |
| 去重 + 自定义顺序            | TreeSet + Comparator | new TreeSet<>(cmp) 外部传规则                   |
| 需要「找比 x 小的最大元素」这类范围查询 | TreeSet              | 独有 floor/ceiling/headSet/subSet，HashSet 没有 |

---

## 自测（合上笔记，口述 60 秒，说得出来才算过）

- [ ] 我能说出自然排序和比较器排序的区别，以及各自什么时候用
- [ ] 我能解释「TreeSet 判重靠 compareTo 返回 0」，并说出它和 HashSet 判重的差别
- [ ] 我能说出 TreeSet 不允许 null 的原因
- [ ] 我能说出 TreeSet 和 HashSet 的插入时间复杂度，以及为什么
- [ ] 我能举一个「TreeSet 和 HashSet 互转会丢元素」的例子

---

## 我还想问的

- ________
