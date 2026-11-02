# 笔记 · LinkedList 源码精读

> 对应黑马 **190 LinkedList 和迭代器的源码分析**。
> 三问骨架。今天的核心是「把 ArrayList 和 LinkedList 的选择题彻底答死」。

## 一、它是什么

> 一句话说清 LinkedList 的底层结构。

`LinkedList` = 双向链表：每个节点有 `prev` + `next` 两个指针，链表类还持有 `first` 和 `last` 两个引用。

**画一个包含 3 个节点的双向链表**（标出 first / last / prev / next）：

```
（画在下面）
```

---

## 二、它为什么存在（面试分界线）

### 1. 为什么 LinkedList 尾插是 O(1)？

> 提示：单链表尾插要「从头走到尾」，O(n)。LinkedList 凭什么不用走？

### 2. 为什么 LinkedList 的 get(i) 要做「从近的一头找」的优化？

> 提示：`index < size/2` 从头，否则从尾。这个优化的前提是什么？（因为有 prev 指针，能倒着走）

### 3. ArrayList vs LinkedList 完整选择表（把 10/28 的 5 行补全到 7 行）

| 操作 | ArrayList | LinkedList | 为什么 |
|---|---|---|---|
| 按下标随机访问 get(i) | O(1) | O(n) | 数组直接算地址；链表要顺着走 |
| 尾部插入 add(e) | 均摊 O(1) | O(1) | 数组偶尔扩容；链表有 last |
| 头部插入 addFirst | O(n) | O(1) | 数组要整体后移；链表改 first |
| 中间插入 add(i,e) | O(n) | O(n) | **都要先找到位置**（链表的 O(1) 插入是「找到之后」才成立） |
| 中间删除 remove(i) | O(n) | O(n) | 同上 |
| 遍历 | O(n)，连续内存快 | O(n)，内存不连续慢 | 缓存命中率差异 |
| 内存占用 | 连续，紧凑 | 每个节点多 2 个指针 | 链表有指针开销 |

---

## 三、什么场景 / 有什么坑

### 我今天踩的坑（写真实过程）

- 坑 1：

### 必答追问

- 「LinkedList 增删比 ArrayList 快」这句话错在哪？（要能一句话说死它）
- 什么时候 LinkedList 真的比 ArrayList 强？（频繁**头插头删**、当队列用——但 JDK 更推荐用 `ArrayDeque`）
- `ArrayDeque` 为什么比 LinkedList 更适合当栈和队列？（数组 + 循环下标，无指针开销，缓存友好）

---

## 自测（合上笔记，能说出来再打勾）

- [ ] 双向链表结构能默画（first/last/prev/next）
- [ ] 能说清尾插 O(1) 的原因
- [ ] 能说清 get 从近一头找的优化
- [ ] 选择表 7 行能默写
- [ ] 能一句话说死「LinkedList 增删快」的误区
