# 笔记 · LinkedList 源码精读

> 对应黑马 **190 LinkedList 和迭代器的源码分析**。
> 三问骨架。今天的核心是「把 ArrayList 和 LinkedList 的选择题彻底答死」。

## 一、它是什么

> 一句话说清 LinkedList 的底层结构。

`LinkedList` = 双向链表：每个节点有 `prev` + `next` 两个指针，链表类还持有 `first` 和 `last` 两个引用。

**画一个包含 3 个节点的双向链表**（标出 first / last / prev / next）：

```
        first                                              last
          ↓                                                  ↓
    ┌───────────┐      ┌───────────┐      ┌───────────┐
null←─│ prev     │←─────│ prev     │←─────│ prev     │
    │ val = A   │      │ val = B   │      │ val = C   │
    │     next ─┼─────→│     next ─┼─────→│     next ─┼──→ null
    └───────────┘      └───────────┘      └───────────┘

```

---

## 二、它为什么存在（面试分界线）

### 1. 为什么 LinkedList 尾插是 O(1)？

> 因为链表类 **额外保存了 `last` 引用** ，尾插时只要 `last.next = newNode; newNode.prev = last; last = newNode;`，三步搞定，用不着从 `first` 顺着 `next` 走到尾。
>
> 对比单链表：只有 `head`，尾插必须"从头走到尾"找到最后一个节点，那是 O(n)。 **关键区别就在这个 `last` 字段** ——这也是 JDK `LinkedList` 内部真实实现（`linkLast` 方法）。

### 2. 为什么 LinkedList 的 get(i) 要做「从近的一头找」的优化？

> ```java
> Node<E> node(int index) {
>     if (index < (size >> 1)) {      // 前半段，从 first 往后走
>         // 从 first 走 index 步
>     } else {                         // 后半段，从 last 往前走
>         // 从 last 走 size-1-index 步
>     }
> }
> ```
>
> **前提就是每个节点都有 `prev` 指针，能倒着走。** 单链表没有 `prev`，只能从头走，最坏 O(n)；双向链表取两端较近的一侧，最坏步数从 n 降到 n/2。
>
> 注意：这只是常数优化， **复杂度仍是 O(n)** ，不要误答成"get 变成 O(1)"。

| 操作             | ArrayList  | LinkedList  | 为什么                                |
| -------------- | ---------- | ----------- | ---------------------------------- |
| 按下标随机访问 get(i) | O(1)       | O(n)        | 数组直接算地址；链表要顺着走                     |
| 尾部插入 add(e)    | 均摊 O(1)    | O(1)        | 数组偶尔扩容；链表有 last                    |
| 头部插入 addFirst  | O(n)       | O(1)        | 数组要整体后移；链表改 first                  |
| 中间插入 add(i,e)  | O(n)       | O(n)        | **都要先找到位置**（链表的 O(1) 插入是「找到之后」才成立） |
| 中间删除 remove(i) | O(n)       | O(n)        | 同上                                 |
| 遍历             | O(n)，连续内存快 | O(n)，内存不连续慢 | 缓存命中率差异                            |
| 内存占用           | 连续，紧凑      | 每个节点多 2 个指针 | 链表有指针开销                            |

---

## 三、什么场景 / 有什么坑

### 我今天踩的坑（写真实过程）

- 坑 1：`removeFirst` 删到只剩一个节点时，`last` 没置 null
    错因：删掉最后一个元素后 first == null，但 last 仍指着那个已删除的节点，成了悬空引用；下次 addLast 会去改一个"幽灵节点"的 next。而且 last == null 这个判断从一开始就不可能成立，等于整段是死代码。
    
    修正：判断条件改成 if (first == null) { last = null; }，或直接写成 else 分支。
    
    教训 ：双链表的 单节点状态 是边界特例——first == last 时，删或加都必须同时动两个引用。这和之前写 MyLinkedList 时遇到的是同一类问题。

### 必答追问

- 「LinkedList 增删比 ArrayList 快」这句话错在哪？（要能一句话说死它）
    一句话打死： 只有"已经站在那个节点上"的增删才是 O(1)，而 LinkedList 找到那个节点本身就要 O(n)，总账和 ArrayList 持平、甚至更慢（还要多花一次遍历）。
- 什么时候 LinkedList 真的比 ArrayList 强？（频繁**头插头删**、当队列用——但 JDK 更推荐用 `ArrayDeque`）
    频繁头插头删、当作队列用（addFirst/removeFirst 都 O(1)）。但 JDK 更推荐 ArrayDeque 。
- `ArrayDeque` 为什么比 LinkedList 更适合当栈和队列？（数组 + 循环下标，无指针开销，缓存友好）
    数组 + 循环下标（头尾指针在数组里绕圈），无指针开销、内存连续、缓存命中率高；LinkedList 每个元素都要额外两个指针，且节点分散在堆上，遍历时缓存不友好。结论： 栈和队列用 ArrayDeque，别用 LinkedList 。

---

## 自测（合上笔记，能说出来再打勾）

- [ ] 双向链表结构能默画（first/last/prev/next）
- [ ] 能说清尾插 O(1) 的原因
- [ ] 能说清 get 从近一头找的优化
- [ ] 选择表 7 行能默写
- [ ] 能一句话说死「LinkedList 增删快」的误区
