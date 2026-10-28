# 笔记 · Collection 与 Iterator

> 对应黑马 **184 Collection**（28:30）+ **185 迭代器**（31:07）。
> 每篇笔记只回答三个问题，答不出来就回去看那一段。

## 一、它是什么

> Collection 是：单列集合的 **顶层接口** ，规定了"一组元素"的通用操作（add、remove、size、iterator），是 List/Set/Queue 的共同父接口。
>
> Iterator  是：集合的 **迭代器** ，提供 `hasNext() / next() / remove()` 一套统一方式，逐个访问集合里的元素，而不暴露集合内部结构。

两者的关系（谁实现谁、谁生产谁）：

- `Collection` **继承** `Iterable`（所以能被 foreach）；
- `Collection` 的实现类 **负责生产** `Iterator`（`iterator()` 返回一个具体迭代器）；
- 迭代器反过来 **依赖并遍历** 集合，两者是"集合生产迭代器、迭代器遍历集合"的关系。

```
Iterable
└── Collection
    ├── List
    ├── Set
    └── Queue
```

**必须能答**：为什么 `Map` 不继承 `Collection`？

> 因为语义根本不同：`Collection` 是" **一组元素** "，API 是 `add / iterator`；`Map` 是" **键值映射** "，API 是 `put / get / keySet`。硬塞进去的话，`add` 这个方法放一个元素进 Map 到底算 key 还是 value？没法定义。所以 Map 另起一门，但可以 **通过 `entrySet()` / `keySet()` / `values()` 转成 Collection** 来遍历。

---

## 二、它为什么存在（解决了什么问题）

> **这一层是面试的分界线。**

### 1. 为什么要有 `Collection` 这个接口，而不是每个类各写各的？

**统一契约 + 面向接口编程** 。上层代码（比如 `Collections.sort`、自定义工具方法）只依赖 Collection 接口，就能操作所有实现类。参数写成 `Collection` 就能接收 ArrayList、HashSet、LinkedList， **一处编写到处复用** ；否则每个类一套 API，调用方要写无数重载。这是 **多态** 最典型的落地。

### 2. 为什么要有 `Iterator`？直接用 `for (int i = 0; i < size; i++)` 不行吗？

> 不行，因为 **下标访问把客户端和底层实现耦合了** ：
>
> - 数组、ArrayList 能按下标访问，但 **LinkedList 不能** （没有 `get(i)` 的 O(1) 概念，硬写会 O(n²)）；
> - 如果客户端自己按下标遍历，就 **必须知道底层是数组还是链表** ——这就是耦合。
> - 更关键的是 **并发安全** ：统一走 Iterator，才能用 `modCount / expectedModCount` 做 **fail-fast** ，边遍历边改立刻抛 `ConcurrentModificationException`。

### 3. `for (X x : list)` 到底编译成了什么？把等价的代码写出来

```java
// for (X x : list) { ... } 编译后等价于：
for (Iterator<X> it = list.iterator(); it.hasNext(); ) {
    X x = it.next();
    // ... 循环体
}

```

---

## 三、什么场景用它、有什么坑

### 我今天对照 JDK 源码发现的 3 条差异

> 这是今天的第二个代码任务（见 `journal/2026-10-26.md`）。写「我原来错在哪」，不要抄 JDK。
> 对照对象：JDK 21 `java.util.ArrayList`（IDEA Ctrl+N 打开；命令行验证在 `%TEMP%` 下从 `src.zip` 提取）。

1. **懒初始化的载体：我用 `null`，JDK 用两个共享空数组常量。**
   JDK 里有 `EMPTY_ELEMENTDATA = {}` 和 `DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {}` 两个长度 0 的数组，无参构造只赋引用不开堆；两个常量内容一样但必须是两个对象——第一次 add 时靠 `elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA` 这个**引用比较**区分「开 10 格」还是「按 minCapacity 最小开」。我原来用 `null` 表示未初始化，代价是 `ensureCapacity`、`trimToSize`、`capacity()` 处处要判 null；`new MyArrayList<>(0)` 我原来还会真的 `new Object[0]` 浪费一个对象。

2. **扩容入口的默认容量判断位置：我写在「数组为 null 的分支」里，JDK 写在进 grow 之前抬 minCapacity。**
   JDK 21 的链路是 `add(E) → add(e, elementData, size) → grow(size+1)`，`grow` 里 `oldCapacity > 0 || elementData != DEFAULTCAPACITY_EMPTY_ELEMENTDATA` 不成立时（= 无参构造后第一次 add）走 `new Object[Math.max(DEFAULT_CAPACITY, minCapacity)]`；其余情况 `ArraysSupport.newLength(oldCapacity, minCapacity - oldCapacity, oldCapacity >> 1)`——第三参 `oldCapacity >> 1` 就是 1.5 倍扩容，且用移位不用乘除。我的 1.5 倍公式 `oldCapacity + (oldCapacity >> 1)` 数值上等价，这点没错；错在把「第一次开 10」和「后续扩容」混在 null 分支里，导致 `new MyArrayList<>(0)` 后第一次 add 也会开 10，而 JDK 只开 1。

3. **modCount +1 的覆盖面：我只在 add/remove 里加，JDK 在 trimToSize、ensureCapacity、clear、addAll、removeAll 里全都加。**
   JDK 的原则是「凡是可能让在途迭代器失效的操作都算结构修改」——`trimToSize()` 换了数组对象（size 没变也 +1）、公开的 `ensureCapacity(int)` 预扩容也 +1。我原来 trimToSize 不加 modCount，还在注释里写「JDK 并不在这里加」——对照源码后发现这条注释是**错的**，已改正。另外 JDK 的 `remove(int)` 内部走 `fastRemove`（跳过边界检查、不返回值的私有方法），modCount 在那里 +1，公开的 `remove(Object)` / `remove(int)` / 迭代器的 `remove()` 全部收敛到这一个点。

**关掉源码，一句话说出我原来错在哪：**

> **原来错在** ：用 `null` 表示未初始化，代价是 `ensureCapacity / trimToSize / capacity()` 处处要判 null；`new MyArrayList<>(0)` 我还真的 `new Object[0]`，浪费一个对象。
>
> **原来错在** ：trimToSize 不加 modCount，还在注释里写"JDK 并不在这里加"——对照源码后发现这条注释是错的，已改正。另外 JDK 的 `remove(int)` 内部走 `fastRemove`（跳过边界检查、不返回值的私有方法），modCount 在那里 +1，公开的 `remove(Object)` / `remove(int)` / 迭代器的 `remove()` 全部收敛到这一个点。

### 坑（写「我踩过 / 我差点踩」）

- **我踩过** ：曾经以为 `EMPTY_ELEMENTDATA` 和 `DEFAULTCAPACITY_EMPTY_ELEMENTDATA` 可以合并成一个，省事；结果 `new ArrayList<>(0)` 和 `new ArrayList<>()` 的首次扩容行为就区分不开了——前者该开 1、后者该开 10。
- **差点踩** ：`trimToSize` 时只复制数组没动 modCount，误以为"元素没增删就不算结构修改"；实际上 **数组对象换了** ，正遍历的迭代器还拿着旧数组，必须 +1 触发 fail-fast。
- **差点踩** ：把增强 for 当成下标循环理解，在 `for (X x : list)` 里直接 `list.remove(x)`，触发 `ConcurrentModificationException`——后来才明白它是迭代器语法糖，删除要用 `iterator.remove()`。

---

## 自测（合上笔记，能说出来再打勾）

- [ ] 能用一句话说清 `Collection` 是什么
- [ ] 能说清 `Iterator` 为什么存在（而不是「因为要遍历」这种废话）
- [ ] 能默写 `for-each` 编译后的等价代码
- [ ] 能说出 `Map` 不继承 `Collection` 的理由
- [ ] 能说出至少一个坑
