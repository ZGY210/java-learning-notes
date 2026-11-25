# 17 · TreeMap 源码与红黑树

> 对应黑马 **215、216 集**（TreeMap 源码超详细解析 上/下）。
> 用法：**先通读一遍问题，再去看视频，看完关掉视频凭记忆答**。答不出的地方回去重看那一小段，别整集重播。
> 这一篇的目标不是「知道红黑树是什么」，是**能在白板上用 60 秒讲清 TreeMap 为什么快、为什么不用 equals**。

---

## 一、Entry 结构（最简单的一节，但要背）

1. TreeMap 的每个节点（`Entry`）有哪几个字段？分别指向什么？

    Entry 有 5 个字段：key、value、left、right、parent。另外还有 color（boolean，记录红/黑）。left/right 指向左右孩子，parent 指向父节点（HashMap 的 Node 没有 parent，TreeMap 有，因为旋转要向上回溯）。

3. `Entry` 有没有 `hash` 字段？为什么不需要？

    没有 hash 字段。因为 TreeMap 不靠哈希定位，靠 compareTo 沿树往下比较，哈希值没用。

5. `Entry` 有没有 `next` 字段？和 HashMap 的 `Node` 比，少了什么、多了什么？

    没有 next。HashMap 的 Node 有 next（拉链法解决哈希冲突），TreeMap 是树结构，用 left/right/parent 组织，不需要 next。但多了 left、right、parent、color。

---

## 二、查找为什么是 O(log n)

4. TreeMap 的 `get(key)` 大致分几步？从根节点开始怎么走？

    从根节点开始：用 key 和当前节点 compareTo 比较，小于走左，大于走右，等于就找到；每走一步树高减半层级（剪掉一半），所以是 O(log n)。

6. 每次比较用的是什么方法？（提示：不是 `equals`）

    用 compareTo（或 Comparator.compare），不是 equals。

8. 如果树退化成一条链（比如按 1,2,3,4,5 的顺序插入），查找会变成多少复杂度？红黑树靠什么防止这件事？

    按 1,2,3,4,5 顺序插入，树退化成一条链（全是右孩子），查找变成  O(n) 。红黑树靠 旋转 + 变色 维持平衡防止退化。

10. 二叉搜索树的平均复杂度是 O(log n)，最坏是 O(n)。红黑树的最坏是多少？为什么？

    红黑树最坏也是  O(log n) 。因为五条性质保证了树高 ≤ 2log(n+1)，树高被限制在对数级，所以最坏仍是对数。

---

## 三、红黑树的五条性质

8. 写出红黑树的五条性质（一条都不能少）：

    每个节点非红即黑；
    根节点是黑色；
    每个叶子节点（NIL）是黑色；
    红节点的两个子节点都是黑色（不能有连续两个红）；
    从任一节点到其所有后代叶子节点的路径上，黑节点数量相同（黑高相同）。

10. 这五条性质里，哪一条是「不允许连续两个红」？它保证了什么？

    第 4 条「不允许连续两个红」。它保证了： 从根到叶的最长路径不会超过最短路径的两倍 ——因为最短路径全黑，最长路径红黑交替，红节点数不会超过黑节点数，从而最长 ≤ 2 × 最短。

12. 为什么「最长路径不超过最短路径的两倍」能推出「树高 ≤ 2log(n+1)」？

    设黑高为 bh，则最短路径长度 ≥ bh，最长 ≤ 2bh。而含 n 个节点的树高度至少对应节点数 ≥ 2^bh − 1，即 bh ≤ log(n+1)。所以最长路径（树高）≤ 2bh ≤  2log(n+1) 。

---

## 四、左旋与右旋

11. 左旋的三行指针是什么？（你自己写一遍）

    TreeNode newRoot = root.right;
    root.right = newRoot.left;
    newRoot.left = root;
    return newRoot;

13. 旋转会不会改变中序遍历的顺序？为什么这一点对红黑树很关键？

    不改变。旋转只改父子指针的"形状"，但 始终满足"左 < 根 < 右" ，所以中序序列不变。这对红黑树很关键——因为红黑树本质是二叉搜索树，旋转用于修平衡，但绝不能破坏 BST 的有序性，否则查找就错了。

15. 插入一个新节点后，红黑树为了恢复性质会做哪两件事？（提示：一件事改颜色，一件事改结构）

    两件事： 变色 （改红黑）和 旋转 （改结构）。

17. 什么情况下只需要变色、不需要旋转？什么情况必须旋转？

    父节点是黑色时，只需变色（或直接插入完成）；当出现连续红节点（叔叔为红 → 变色；叔叔为黑或 null → 旋转 + 变色）必须旋转。

---

## 五、TreeMap 的行为怪癖（面试爱问）

15. TreeMap 去重靠 `compareTo` 还是 `equals`？举一个「两者结论不一致」的例子，说明 TreeMap 会怎么处理。

    去重靠  compareTo ，不靠 equals。例子：就是你写的 EvilKey，equals 永远 true、hashCode 恒为 1 但 compareTo 按 id 比。3 个不同 id 放 TreeMap → 3 个元素（compareTo 认为不同）；放 HashMap → 1 个元素（equals 认为相同）。TreeMap 完全无视 equals。

17. 为什么自然排序的 TreeMap 不能放 `null` key？加了 `Comparator` 为什么就可以？

    自然排序时插入 null 要调 null.compareTo() → NPE，因为 null 没有任何方法可调。加了 Comparator（如 Comparator.nullsFirst(...)）后，null 交给 Comparator 处理，不碰 compareTo，所以能放。

19. `TreeMap` 和 `HashMap` 的 `containsKey` 分别依赖什么？

    TreeMap.containsKey 依赖  compareTo （沿树找，comp==0 即存在）；HashMap.containsKey 依赖  hashCode + equals 。

21. `LinkedHashMap` 和 `TreeMap` 都能保序，它们保的是一回事吗？

    不是一回事。LinkedHashMap 保的是 插入顺序/访问顺序 （双向链表串起来）；TreeMap 保的是 key 的大小排序 （红黑树中序有序）。一个按时间线，一个按大小。

---

## 六、复杂度与选型

19. 填表：

    | 操作           | HashMap | TreeMap  | LinkedHashMap |
    | ------------ | ------- | -------- | ------------- |
    | get          | O(1)    | O(log n) | O(1)          |
    | put          | O(1)    | O(log n) | O(1)          |
    | 是否有序         | 否       | key 有序   | 插入/访问有序       |
    | 能否放 null key | 能（1 个）  | 自然排序不能   | 能（1 个）        |
    | 是否线程安全       | 否       | 否        | 否             |

20. 什么业务场景你会选 TreeMap 而不是 HashMap？举两个真实例子（不是「需要排序」这种空话）。
    按分数区间/时间段查最近值 ：如学生成绩按分数建 TreeMap，用 floorKey(85) 快速找"不高于 85 的最高分"→ 等级划分。HashMap 做不到范围查找。
    IP 归属地 / 时间窗口限流 ：用 subMap 或 ceilingKey 按前缀或时间戳区间查询，TreeMap 的 NavigableMap 接口（floorKey、ceilingKey、subMap）天然支持，这是 HashMap 完全没有的能力。

---

## 七、对上你自己写的代码

21. 你在 `TreeMapDeepen` 里放的 3 个 `EvilKey`（`equals` 永远 true、`hashCode` 恒为 1、`compareTo` 按 id 比），TreeMap 里最终有几个元素？为什么？

    3 个元素。因为 TreeMap 用 compareTo 判等，3 个 id 不同 → compareTo 都返回非 0 → 3 个独立节点。

23. 同一批 `EvilKey` 如果放进 `HashMap`，会有几个元素？为什么两者结论相反？

    1 个元素。因为 HashMap 先用 hashCode 定位（全是 1，同一个桶），再用 equals 判等（永远 true）→ 认为是同一个 key，后面的覆盖前面的。结论相反的原因：TreeMap 只看 compareTo，HashMap 只看 hashCode+equals。

25. 你写的 `rotateLeft` 和 `rotateRight`，旋转前后的中序遍历为什么一样？用「所有比根小的都在左边」这句话解释。

    因为旋转不改 BST 的有序性。「所有比根小的都在左边，大的都在右边」这个不变式始终成立，中序遍历顺序是 左→根→右，无论形状怎么变，中序结果都是升序的同一序列。

---

## 八、自测打分

答完上面 23 题，数一下自己答对几题：

- **≥ 18 题**：TreeMap 这一章你过了，直接进 217（可变参数）
- **13–17 题**：把第三、四节（五条性质 + 旋转）重读一遍，明天补答
- **≤ 12 题**：红黑树这块先别硬啃，把「TreeMap 靠 compareTo、O(log n)、不能放 null」三条记住就够了，剩下的留到 12 月复盘

> 老实说：红黑树的完整插入修复（变色 + 旋转的六种情况）**面试几乎不会让你手推**。
> 面试官只问「五条性质是什么」「旋转为什么有用」「为什么 O(log n)」。
> 所以这一篇的重点是第三、四、五节，不是手推修复流程。

---

## 我今天答不出的题（自己记）

> 9与10
