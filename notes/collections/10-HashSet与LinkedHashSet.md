# 10 · HashSet 与 LinkedHashSet

> 归属 `notes/collections/` ｜ 写于 2026-11-04 ｜ 对应黑马 196
> 三问法：**它是什么 → 为什么需要它 → 什么时候会坑死你**。三问都答不出来，等于没学。

---

## 一、它是什么（一句话定义 + 底层是什么）

> 提示：不要只写「HashSet 是一个无序不重复的集合」。面试官要听的是 **它底层靠什么数据结构做到不重复**。

我要自己写出来的：HashSet 底层是 ……（一个什么结构），它的 value 存的是什么，为什么说它「其实是个只有 key 的 Map」。

补充要回答的三个点：

1. 为什么 HashSet 的 `add` 平均是 O(1)？它走了哪几步？
2. HashSet 允不允许 `null`？能放几个？为什么？
3. HashSet 的遍历顺序由什么决定？

---

## 二、为什么需要它（没有它会怎样）

> 提示：讲清楚「用 List 做去重」有多难受，才能说明 HashSet 存在的价值。

我要自己写出来的：

- 用 `List.contains()` 去重是 O(?) 的，数据量到 10 万会怎样？
- HashSet 的去重为什么能做到平均 O(1)？
- 什么时候该用 `LinkedHashSet` 而不是 `HashSet`？（一句话讲清业务场景）

---

## 三、坑（这一节最重要，写不出坑说明还没真懂）

按下面 5 个坑逐条填，每条都要写「现象 → 原因 → 怎么避免」：

| # | 坑 | 现象 | 原因 | 怎么避免 |
|---|---|---|---|---|
| 1 | 只重写 `equals` 不重写 `hashCode` | | | |
| 2 | 以为 HashSet 有序（1,2,3 加进去出来也是 1,2,3） | | | |
| 3 | 往 Set 放可变对象，放进去又改字段 | | | |
| 4 | 遍历 Set 的同时 `remove` | | | |
| 5 | 用 `HashSet` 存需要保序的数据（如最近浏览记录） | | | |

> 第 1 条是面试原题：「两个对象 equals 相等，hashCode 一定相等吗？反过来呢？」
> 第 3 条值得动手验证：`Student s = new Student("张三"); set.add(s); s.name = "李四"; set.contains(s)` 返回什么？

---

## 四、HashSet / LinkedHashSet / TreeSet 三者对比（自己画表）

| | HashSet | LinkedHashSet | TreeSet |
|---|---|---|---|
| 底层结构 | | | |
| 是否有序 | | | |
| 顺序由什么决定 | | | |
| 允许 null 吗 | | | |
| add / contains 复杂度 | | | |
| 元素必须实现什么 | | | |
| 典型场景 | | | |

---

## 五、自测（闭卷，不许看笔记）

- [ ] 不看笔记说出 `add` 的完整 5 步：hashCode → 扰动 → 定位桶 → equals 比较 → 放入/放弃
- [ ] 不看笔记说出「为什么两个 equals 相等的对象 hashCode 必须相等」
- [ ] 不看笔记说出 HashSet 和 LinkedHashSet 底层分别是什么，多出来的链表维护了什么
- [ ] 能举一个「必须用 LinkedHashSet」的业务例子，并说明用 HashSet 会出什么 bug

---

## 六、今天的代码证据

- `practice/collection/HashSetDemo.java` —— 10 项自查 ALL GREEN
- `leetcode/0217-存在重复元素.md` —— 两版题解（排序法 / HashSet 法）

> 一句话总结（写完这句才算收工）：**HashSet 去重不是靠「比较所有元素」，而是靠「先算位置，再比一次」。**
