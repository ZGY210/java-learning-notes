# 15 · TreeMap 统计与自定义 key

> 归属 `notes/collections/` ｜ 写于 2026-11-19 ｜ 对应黑马 209、210
> **这是问答卷，不是提纲。** 下面每一行都是要你自己写答案的问题。
> 写不出答案的地方，留 `?` 并写一句「我卡在哪」，不要跳过。

---

## 一、它是什么

1. TreeMap 的底层数据结构是什么？（写具体名字）

2. TreeMap 的 key 是有序的。这个「序」是谁决定的？

3. TreeMap 的 put / get / remove 时间复杂度是多少？为什么？

4. TreeMap 允许 null key 吗？为什么？（对照：HashMap、TreeSet、HashSet 各自允许吗？）

---

## 二、自定义 key 为什么必须写 Comparable 或 Comparator

5. 我今天写 `new TreeMap<>()` 放自定义对象，直接抛了什么异常？把异常全名写下来。

6. 为什么 HashMap 放自定义对象不要求 Comparable，TreeMap 就要求？两者判断「相等」的机制差在哪？

7. `Comparable` 和 `Comparator` 分别用在什么场景？TreeMap 两种排序怎么写？

---

## 三、最大的那个坑：TreeMap 判「相等」看的是什么

8. 我用一个只比年龄的 comparator 放进去张三(20) 和 王五(20)，结果 map 的 size 是多少？为什么？

9. 这两个人的 equals 和 hashCode 在这件事里起任何作用了吗？

10. 所以如果 comparator 写得不对（比如所有年龄都相同），会发生什么灾难？

---

## 四、用 TreeMap 做统计

11. 用 TreeMap 统计词频的好处是什么？和 HashMap 统计完再排序比，少做哪一步？

12. 统计类题目的通用三步是什么？（写出来，这是模板）

13. 如果我既想按 key 排序、又想按 value 排序（比如取 Top 3），TreeMap 还能直接搞定吗？那该怎么做？

---

## 五、范围查询（面试爱问）

14. `subMap` 有哪两种用法？左闭右开怎么表达？

15. 用 TreeMap 做「找出年龄在 [20, 30) 的人」，时间复杂度是多少？如果换成 List 遍历呢？

16. TreeMap 还有哪些导航方法？（写出至少 4 个：firstKey / lastKey / floorKey / ceilingKey / higherKey / lowerKey，并各写一句它们干什么）

---

## 六、和 Java 后端的连接点（面试加分）

17. 真实项目里你会在哪儿见到 TreeMap？（提示：不需要猜，想想「按分数排名」「按时间区间查」这类需求）

18. 为什么 Redis 的 zset 没用 tree，而用跳表？这和 TreeMap 的红黑树比，优缺点各是什么？

---

## 七、自测（对自己诚实，别看书）

| # | 问题 | 能答 |
|---|---|---|
| 1 | TreeMap 判相等看什么？ | ☐ |
| 2 | 自定义 key 为什么要 Comparable？ | ☐ |
| 3 | subMap 左闭右开怎么写？ | ☐ |
| 4 | TreeMap 统计 vs HashMap 统计 | ☐ |
| 5 | floorKey 和 ceilingKey 的区别 | ☐ |

答对 ≥4 才能睡。
