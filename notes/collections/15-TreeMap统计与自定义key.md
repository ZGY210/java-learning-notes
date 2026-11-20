# 15 · TreeMap 统计与自定义 key

> 归属 `notes/collections/` ｜ 写于 2026-11-19 ｜ 对应黑马 209、210
> **这是问答卷，不是提纲。** 下面每一行都是要你自己写答案的问题。
> 写不出答案的地方，留 `?` 并写一句「我卡在哪」，不要跳过。

---

## 一、它是什么

1. 底层数据结构
   红黑树（Red-Black Tree），一种自平衡二叉查找树。

2. "序"由谁决定
   由 key 的 比较规则 决定：key 实现了 Comparable 就按它的 compareTo；构造时传了 Comparator 就按它。两者都没有就抛异常。默认是 key 的自然顺序。

3. put / get / remove 复杂度
   都是  O(log n) 。每次操作从根往下走一条树高为 log n 的路径，红黑树树高被限制在 2log(n+1) 内，所以是 O(log n)，而不是 HashMap 的 O(1)。

4. null key 允许吗
   TreeMap  不允许 null key （空树第一次 put null 直接 NullPointerException，因为要调 compare 找位置）。对照：

   1.HashMap 允许 1 个 null key（放哈希桶 0 号位）
   2.TreeSet 不允许 null（底层是 TreeMap）
   3.HashSet 允许 1 个 null（底层是 HashMap）

---

## 二、自定义 key 为什么必须写 Comparable 或 Comparator

5. 抛了什么异常
   java.lang.ClassCastException，消息形如 class Student cannot be cast to class java.lang.Comparable。根因是红黑树插入时要比较大小，key 没实现 Comparable 也没传 Comparator。

6. 为何 HashMap 不要求，TreeMap 要求
   HashMap 靠 hashCode() 定位桶、equals() 判相等，不需要"大小"概念。
   TreeMap 靠 比较大小 来排列和定位节点，"相等"也由 compare == 0 定义。
   判"相等"的机制差在：HashMap 用 hashCode + equals；TreeMap 用 compareTo/comparator 是否为 0

7. Comparable 与 Comparator 的场景
   Comparable：类 自带 默认排序，implements Comparable<T></T>，重写 compareTo，一种排序规则。
   Comparator： 外部 排序策略，可写多个、可临时指定，不侵入类本身。

---

## 三、最大的那个坑：TreeMap 判「相等」看的是什么

8. 我用一个只比年龄的 comparator 放进去张三(20) 和 王五(20)，结果 map 的 size 是多少？为什么？

     size = 1 。两人 compare 返回 0，TreeMap 就认定是同一个 key，后 put 的覆盖前面的 value（key 对象保留先插入的那个）。

10. 这两个人的 equals 和 hashCode 在这件事里起任何作用了吗？

    完全没起作用 。TreeMap 判重只看 compareTo/comparator 的返回值，equals/hashCode 一点不参与。这跟 HashMap 恰好相反。

12. 所以如果 comparator 写得不对（比如所有年龄都相同），会发生什么灾难？

    如果比较器恒返回 0（比如只按年龄且所有人同龄）， 所有 key 都被当成同一个 ，map 永远只有 1 个元素，数据全丢。这是最隐蔽的"数据静默丢失"bug。

---

## 四、用 TreeMap 做统计

11. TreeMap 统计词频的好处

    统计完 天然按 key 有序 ，直接遍历就是排好序的结果；HashMap 统计完还得额外 sort 一步

13. 统计类通用三步（模板）

        建容器：Map<K></K> map = new TreeMap<>();
        遍历计数：map.merge(k, 1, Integer::sum);
        输出：map.entrySet().stream().map(e -> e.getKey()+"="+e.getValue())...

14. 既要按 key 又要按 value（Top K）能直接搞定吗

    不能。TreeMap 只能按 key 有序。要按 value 取 Top 3，常见做法：
    用 PriorityQueue（小根堆）维护 Top K，O(n log k)；
    或把 entry 放进 List 后 sort(Comparator.comparing(Map.Entry::getValue).reversed())，再取前 3。

---

## 五、范围查询（面试爱问）

14. `subMap` 有哪两种用法？左闭右开怎么表达？

    两参：subMap(fromKey, toKey)，等价于左闭右开 [fromKey, toKey)。
    四参：subMap(fromKey, fromInclusive, toKey, toInclusive)，可自定义开闭。
    左闭右开写法：subMap(from, true, to, false)。

16. 用 TreeMap 做「找出年龄在 [20, 30) 的人」，时间复杂度是多少？如果换成 List 遍历呢？

    TreeMap：定位起点 O(log n)，之后顺序遍历区间内 m 个元素，整体  O(log n + m) 。
    List 遍历： O(n) ，且不支持有序区间剪枝，必须全扫。

18. TreeMap 还有哪些导航方法？（写出至少 4 个：firstKey / lastKey / floorKey / ceilingKey / higherKey / lowerKey，并各写一句它们干什么）

    firstKey()：最小 key
    lastKey()：最大 key
    floorKey(k)：≤ k 的最大 key（含等于）
    ceilingKey(k)：≥ k 的最小 key（含等于）
    higherKey(k)：> k 的最小 key（不含等于）
    lowerKey(k)：< k 的最大 key（不含等于）

---

## 六、和 Java 后端的连接点（面试加分）

17. 项目里哪儿见到 TreeMap

    排行榜/按分数、时间排序的榜单；
    按时间区间查日志、账单、订单（subMap 范围查）；
    定时任务按执行时间排序；
    需要有序输出 JSON、报表配置等。

19. Redis zset 为什么用跳表不用红黑树

    跳表 ：实现简单、支持平均 O(log n) 的范围查询与排名；插入删除只改局部指针， 并发下更容易加锁 ；范围遍历沿底层链表顺序走，缓存友好。
    红黑树 ：最坏 O(log n) 更稳，但范围查询需要中序回溯，实现与并发调整复杂。
    取舍：Redis 单线程，追求实现简单、范围操作高效，所以选跳表。

---

## 七、自测（对自己诚实，别看书）

| #   | 问题                        | 能答  |
| --- | ------------------------- | --- |
| 1   | TreeMap 判相等看什么？           | ☐   |
| 2   | 自定义 key 为什么要 Comparable？  | ☐   |
| 3   | subMap 左闭右开怎么写？           | ☐   |
| 4   | TreeMap 统计 vs HashMap 统计  | ☐   |
| 5   | floorKey 和 ceilingKey 的区别 | ☐   |

答对 ≥4 才能睡。
