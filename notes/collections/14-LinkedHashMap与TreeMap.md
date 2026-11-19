# 14 · LinkedHashMap 与 TreeMap

> 归属 `notes/collections/` ｜ 写于 2026-11-18 ｜ 对应黑马 207–208（LinkedHashMap / TreeMap 基本使用和练习1）

---

## 一、它是什么

> 一句话：同样是 Map，三种实现解决三种问题 —— 要快用 HashMap、要保序用 LinkedHashMap、要排序和范围查询用 TreeMap。

填（用你自己的话，一行说清一个）：

- HashMap ：底层是数组加链表或红黑树的哈希表；查找、插入平均 O(1)，但遍历顺序不可预测。
- LinkedHashMap ：在 HashMap 的每个元素上增加了 before/after 指针，串成双向链表，因此能记录插入顺序或访问顺序。
- TreeMap ：底层红黑树；key 有序，操作 O(log n)，支持排序遍历、范围查询和邻近查找。

**必须回答：LinkedHashMap 为什么能保序？**（提示：它在 HashMap 的 Node 基础上多加了 before/after 两个指针，串成一条双向链表）

```
HashMap ：底层是数组加链表或红黑树的哈希表；查找、插入平均 O(1)，但遍历顺序不可预测。
LinkedHashMap ：在 HashMap 的每个元素上增加了 before/after 指针，串成双向链表，因此能记录插入顺序或访问顺序。
TreeMap ：底层红黑树；key 有序，操作 O(log n)，支持排序遍历、范围查询和邻近查找。
```

---

## 二、为什么需要它

> 一句话：HashMap 的遍历顺序不可靠，业务里「按插入顺序展示」「按 key 排序」「查一个区间」都很常见。

填：

```
HashMap 只保证“键值对的正确存取”，不保证遍历顺序。它甚至无法保证同一个 key 明天还在原来的位置，扩容后会重新散列。业务中“按用户下单顺序展示订单”“按价格排序展示商品”“查 10:00-12:00 的日志”都很常见，纯 HashMap 做不到，所以需要 LinkedHashMap（保序）和 TreeMap（排序与范围查询）。
```

**必须回答：什么场景你会选 TreeMap 而不用 HashMap？**（至少两个）

```
需要按 key 有序遍历 ：如排行榜、按时间戳输出日志、字典序展示分类。
需要范围查询 ：如查价格 100-500 的商品、查某时间段的订单（subMap/headMap/tailMap）。
需要找最近的键 ：如缓存里找“小于等于某个值”的最大 key（floorKey/ceilingKey）。
需要稳定可复现的遍历顺序 ：如生成固定顺序的 JSON、做 diff 对比。
```

---

## 三、今天的主线：LinkedHashMap 的两种模式

| 模式   | 构造参数                      | 迭代顺序      | 典型用途       |
| ---- | ------------------------- | --------- | ---------- |
| 插入顺序 | `accessOrder = false`（默认） | 按 put 的先后 | 有序配置、结果集保序 |
| 访问顺序 | `accessOrder = true`      | 按最近一次访问先后 | LRU 缓存     |

**必须回答：`accessOrder = true` 时，`get()` 做了什么？为什么它能变成 LRU？**

填：

```
get() 命中节点后，会调用 afterNodeAccess(e)：把该节点从双向链表当前位置摘下来，挂到链表尾部，同时把尾指针指向它。于是链表头是 最久没被访问的 ，尾是 最近刚访问的 。“访问即搬家到尾部”正好就是把最近用过的放到最后，因此淘汰链表头就是淘汰最久未用的，天然构成 LRU。
注意：只有命中时才搬家；get 未命中不搬。put 新节点会追加到尾部；put 覆盖已有 key 时，会更新 value 并把节点移到尾部。
```

**必须回答：`removeEldestEntry` 什么时候被调用？返回 true 会怎样？**

填：

```
它在每次 put（以及 putAll）插入新条目后，由 afterNodeInsertion 内部调用。返回 true 表示“应该删掉最老的条目”，于是 LinkedHashMap 会把双向链表头部节点（最久未访问或最早插入）从 map 中移除，并返回给调用方（返回值仅表示移除了，无数据返回）。
注意：只有命中时才搬家；get 未命中不搬。put 新节点会追加到尾部；put 覆盖已有 key 时，会更新 value 并把节点移到尾部。
```

**必须回答：为什么说「LinkedHashMap 就是现成的 LRU」？那你自己手写 LRU 还有什么意义？**

（提示：面试常让你手写 LRU，考的是 HashMap + 双向链表的组合能力，不是让你调 API）

填：

```
因为 LinkedHashMap(16, 0.75f, true) 加上重写 removeEldestEntry 返回 size() > capacity，三行代码就是一个完整 LRU：访问顺序已经自动维护，淘汰策略也内置。自己手写完全没有必要，生产上直接用它。
手写的意义在于 面试考点 ：手写 LRU 考的是能否把  HashMap（O(1) 定位）+ 双向链表（O(1) 移动/删除，头尾哨兵）  组合起来，涉及节点摘除时 prev.next = next 与 next.prev = prev 的指针维护、put 命中时移到头部、容量超限时删尾等细节。这是对数据结构的理解考察，不是“你会不会调 API”。（你 11/15 那套题里 LRU 就是高频题。）
```

---

## 四、TreeMap 的排序与范围查询

**必须回答：TreeMap 底层是什么数据结构？put/get 的时间复杂度？**

填：

```
底层是一棵 红黑树 ，即近似平衡的二叉搜索树。put/get/containsKey/remove 都是 O(log n)。红黑树通过 5 条性质保证树高不超过 2log(n+1)，避免退化成链表。
```

**必须回答：TreeMap 判断「两个 key 相等」用的是什么？和 HashMap 有什么本质不同？**

（提示：compareTo/compare 返回 0，**不是** equals）

填：

```
用 compareTo（实现 Comparable）或 compare（传入 Comparator） 返回 0  来判定相等，不用 equals。HashMap 则用 hashCode 定位桶、equals 比较。本质差异：TreeMap 完全忽略 hashCode 和 equals，只要比较器认为相等就是同一个 key。
后果：两个对象 equals 为 false，但比较器返回 0 时，TreeMap 会把它们当成同一个 key，后来的 put 覆盖前面的 value，size 不增加。反过来，equals 为 true 但比较器不为 0 时，会被当成两个不同 key 同时存在。所以自定义 key 时，比较逻辑必须与 equals 语义保持一致。
```

**必须回答：`floorKey` / `ceilingKey` / `firstKey` / `lastKey` / `subMap` 各返回什么？找不到时怎么办？**

填：

```
floorKey(k)：≤ k 的最大 key；没有则返回 null
ceilingKey(k)：≥ k 的最小 key；没有则返回 null
firstKey()：最小 key；空 map 抛 NoSuchElementException
lastKey()：最大 key；空 map 抛 NoSuchElementException
subMap(from, fromInc, to, toInc)：返回 [from, to) 区间的 视图 （NavigableMap），不是拷贝；from > to 时抛 IllegalArgumentException
```

---

## 五、坑（至少写 5 个）

1. TreeMap 的 key 没实现 Comparable、也没传 Comparator →put 第一个元素时抛 ClassCastException，报错大意：“class X cannot be cast to class java.lang.Comparable”。
2. Comparator 判两个 key「相等」→ compare 返回 0 时，TreeMap 认作同一 key，后 put 的 value  覆盖 前一个，size 不增加。典型表现：按长度排序时，"ab" 和 "cd" 冲突，只剩一个 key。
3. LinkedHashMap 的 `accessOrder = true` 下，遍历时 `get` 会改顺序 → 遍历中调用 get 会把节点挪到尾部，若在 for-each 中操作，可能抛 ConcurrentModificationException；即使不抛，遍历顺序也被打乱，导致漏遍历或重复遍历。
4. `subMap` 返回的是**视图**不是拷贝 → 改视图（put/remove）会 直接影响原 map ，反之亦然。且向视图 put 超出范围的值抛 IllegalArgumentException。
5. HashMap/LinkedHashMap 允许 null key，TreeMap 允许吗？为什么？→ TreeMap 不允许 ，因为 null 无法参与比较，put(null, v) 抛 NullPointerException。value 可以 null。
6. LinkedHashMap 内存比 HashMap 多多少？（提示：每个 Entry 多两个引用）→ 每个 Entry 多两个引用（before、after），64 位 JVM 下开启指针压缩时每个 Entry 多 8 字节，外加一个 Entry 对象头，大约多 30%~40% 内存。数据量大时是明显开销，换取的是保序能力。

---

## 六、自测（盖住上面自己答）

1. 三种 Map 的底层分别是什么？复杂度分别多少？
    HashMap：数组 + 链表/红黑树，平均 O(1)，最坏 O(n)（全冲突）或 O(log n)（树化后）
    LinkedHashMap：HashMap + 双向链表，O(1)，但多一份链表维护成本
    TreeMap：红黑树，O(log n)
3. LinkedHashMap 怎么做到保序的？
    Entry 多 before/after 指针，全部串成双向链表，哈希桶负责查找、链表负责顺序。
5. 用 LinkedHashMap 实现 LRU，两个关键设置是什么？
    ① 构造器传 accessOrder = true；
    ② 重写 removeEldestEntry 返回 size() > capacity。
7. TreeMap 为什么能做范围查询，HashMap 为什么不能？
    TreeMap 红黑树是有序结构，中序遍历单调有序，按排序关系二分定位区间边界 O(log n)；HashMap 桶按哈希值散列，key 之间没有任何顺序，只能全表扫描 O(n)。
9. `floorKey(25)` 和 `ceilingKey(25)` 在 key = 10,20,30 时分别返回什么？
    floorKey(25) → 20，ceilingKey(25) → 30。

---

## 七、必答：为什么 hashCode 里要用 31

> 昨天你用 IDEA 生成了 equals/hashCode，今天你自己手写了一遍。那 **31** 到底是什么？

写出至少 3 条理由：

1. floorKey(25) → 20，ceilingKey(25) → 30。31 是奇素数 ：乘奇素数能降低乘积碰撞概率。用偶数在模运算下容易信息丢失（如乘 2 相当于左移，低位全 0），质数分布更均匀。
2. 可被 JVM 优化成移位减法 ：31 * i == (i << 5) - i，现代 JVM 会自动做这个优化，比乘法快，这是 31 被选中的性能原因。为什么不是 37、41？它们也是素数，但没有这个位运算等价形式。
3. 31 不大不小 ：太小（如 3、7）容易碰撞，太大（如 997）容易溢出且无必要。31 让结果落在合理的 int 范围内，溢出率与碰撞率平衡得比较好，是实践中的经验最优值（JDK 源码如 String.hashCode() 也用 31）。

---

> 如果面试官问「Map 有几种实现，你怎么选」，我用一句话回答：

```
Map 三兄弟：HashMap 要快（O(1) 无序）、LinkedHashMap 要序（O(1) 保插入/访问顺序）、TreeMap 要排序和范围（O(log n) 有序）；看需求选，别一律用 HashMap。
```
