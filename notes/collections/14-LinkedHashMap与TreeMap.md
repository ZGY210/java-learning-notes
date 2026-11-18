# 14 · LinkedHashMap 与 TreeMap

> 归属 `notes/collections/` ｜ 写于 2026-11-18 ｜ 对应黑马 207–208（LinkedHashMap / TreeMap 基本使用和练习1）

---

## 一、它是什么

> 一句话：同样是 Map，三种实现解决三种问题 —— 要快用 HashMap、要保序用 LinkedHashMap、要排序和范围查询用 TreeMap。

填（用你自己的话，一行说清一个）：

- HashMap：
- LinkedHashMap：
- TreeMap：

**必须回答：LinkedHashMap 为什么能保序？**（提示：它在 HashMap 的 Node 基础上多加了 before/after 两个指针，串成一条双向链表）

```

```

---

## 二、为什么需要它

> 一句话：HashMap 的遍历顺序不可靠，业务里「按插入顺序展示」「按 key 排序」「查一个区间」都很常见。

填：

```

```

**必须回答：什么场景你会选 TreeMap 而不用 HashMap？**（至少两个）

```

```

---

## 三、今天的主线：LinkedHashMap 的两种模式

| 模式 | 构造参数 | 迭代顺序 | 典型用途 |
|---|---|---|---|
| 插入顺序 | `accessOrder = false`（默认） | | |
| 访问顺序 | `accessOrder = true` | | **LRU 缓存** |

**必须回答：`accessOrder = true` 时，`get()` 做了什么？为什么它能变成 LRU？**

填：

```

```

**必须回答：`removeEldestEntry` 什么时候被调用？返回 true 会怎样？**

填：

```

```

**必须回答：为什么说「LinkedHashMap 就是现成的 LRU」？那你自己手写 LRU 还有什么意义？**

（提示：面试常让你手写 LRU，考的是 HashMap + 双向链表的组合能力，不是让你调 API）

填：

```

```

---

## 四、TreeMap 的排序与范围查询

**必须回答：TreeMap 底层是什么数据结构？put/get 的时间复杂度？**

填：

```

```

**必须回答：TreeMap 判断「两个 key 相等」用的是什么？和 HashMap 有什么本质不同？**

（提示：compareTo/compare 返回 0，**不是** equals）

填：

```

```

**必须回答：`floorKey` / `ceilingKey` / `firstKey` / `lastKey` / `subMap` 各返回什么？找不到时怎么办？**

填：

```

```

---

## 五、坑（至少写 5 个）

1. TreeMap 的 key 没实现 Comparable、也没传 Comparator → 填：抛什么异常
2. Comparator 判两个 key「相等」→ 填：会发生什么覆盖
3. LinkedHashMap 的 `accessOrder = true` 下，遍历时 `get` 会改顺序 → 填：
4. `subMap` 返回的是**视图**不是拷贝 → 填：改视图会影响原 map 吗
5. HashMap/LinkedHashMap 允许 null key，TreeMap 允许吗？为什么？
6. LinkedHashMap 内存比 HashMap 多多少？（提示：每个 Entry 多两个引用）

---

## 六、自测（盖住上面自己答）

1. 三种 Map 的底层分别是什么？复杂度分别多少？
2. LinkedHashMap 怎么做到保序的？
3. 用 LinkedHashMap 实现 LRU，两个关键设置是什么？
4. TreeMap 为什么能做范围查询，HashMap 为什么不能？
5. `floorKey(25)` 和 `ceilingKey(25)` 在 key = 10,20,30 时分别返回什么？

---

## 七、必答：为什么 hashCode 里要用 31

> 昨天你用 IDEA 生成了 equals/hashCode，今天你自己手写了一遍。那 **31** 到底是什么？

写出至少 3 条理由：

1.
2.
3.

---

## 八、一句话总结（写完收工）

> 如果面试官问「Map 有几种实现，你怎么选」，我用一句话回答：

```

```
