# 18 · 可变参数与 Collections 工具类

> 对应黑马 **217（可变参数 12:22）、218（Collections 05:49）**，外加 **219–223 综合练习**里出现的工具方法。
> 这一天内容不多，但坑特别多 —— 面试官爱问「Arrays.asList 返回的 list 能不能 add」，答错的人一大片。
> 用法：先扫一遍问题，看视频，关掉视频凭记忆答。

---

## 一、可变参数（217）

1. `void f(int... nums)` 在字节码层面，`nums` 的真实类型是什么？

2. 下面三段调用的等价关系：
   - `f()` → 等价于 `f(` 　　　 `)`
   - `f(1, 2)` → 等价于 `f(` 　　　 `)`
   - `f(new int[]{1,2})` → ？

3. 为什么**不能**同时定义 `f(int... nums)` 和 `f(int[] nums)`？

4. 为什么可变参数必须在形参列表的**最后一位**？举一个会被编译器拒绝的写法。

5. 一个方法能有几个可变参数？为什么？

6. 传 `null` 进去会怎样？
   ```java
   sum(null);              // ？
   sum((int[]) null);      // ？
   sum();                  // ？
   ```
   三行的结果分别是什么，为什么不是一样的结果？

---

## 二、Collections 工具类（218）

7. 写出下面每个方法的作用，并说清「有没有副作用（会不会改原集合）」：

| 方法 | 作用 | 改原集合吗 |
|---|---|---|
| `Collections.sort(list)` | | |
| `Collections.reverse(list)` | | |
| `Collections.shuffle(list)` | | |
| `Collections.max(list)` | | |
| `Collections.frequency(list, x)` | | |
| `Collections.binarySearch(list, x)` | | |
| `Collections.unmodifiableList(list)` | | |
| `Collections.synchronizedList(list)` | | |
| `Collections.emptyList()` | | |

8. `Collections.sort` 底层用的是什么排序算法？为什么用它（提示：和「数据是否近乎有序」有关）？

9. `Collections.binarySearch` 之前必须做什么？没做会怎样（返回什么）？

10. `Collections.synchronizedList` 返回的 list 真的线程安全了吗？遍历的时候呢？

---

## 三、四个「不可变 / 固定长度」的坑（本节最重要）

11. 填表：

| 写法 | 能不能 add/remove | 能不能 set（改元素） | 改了会不会影响原数组/原集合 |
|---|---|---|---|
| `Arrays.asList(1,2,3)` | | | |
| `List.of(1,2,3)` | | | |
| `Collections.unmodifiableList(list)` | | | |
| `new ArrayList<>(Arrays.asList(...))` | | | |

12. `Arrays.asList(new int[]{1,2,3})` 返回的 list 里有几个元素？为什么不是 3 个？
    （提示：`asList(T... a)` 的 T 是什么）

13. 在 `List.of("c","a","b")` 上直接 `Collections.sort(...)` 会发生什么？为什么？

14. 为什么 `ArrayList` 允许 `add(null)`，而 `List.of` 不允许？

15. 真实项目里，一个方法收到 `List<T>` 参数，如果要修改变量（比如排序），你该怎么做才安全？

---

## 四、对上你自己写的代码

16. `VarargsCollectionsDemo.sortCopy` 为什么要先 `new ArrayList<>(src)`，不复制会怎样？

17. `maxFrequency` 里你用了 `Collections.frequency`，它的时间复杂度是多少？如果数据是 100 万条，你会怎么改？

18. `maxFrequency` 如果有两个元素出现次数并列最多，你返回的是哪一个？这是「约定」还是「错误」？

19. `listOfIsImmutable` 里你捕获的是什么异常？如果别人写的代码用 `catch (Exception e)` 兜住它，会有什么坏处？

---

## 五、综合练习（219–223）挑三道

20. 从 219–223 里挑 **一道你觉得最难的**，写出它的思路（不用写完整代码，写清「用什么集合、为什么」。

   
21. 这五集里有没有出现「本该用 A 集合却用了 B」的例子？说出来。

22. 如果让你自己出一道「综合练习」，考「HashMap 统计 + 排序输出」，你会怎么出？

---

## 六、自测打分

答完 22 题数一下：

- **≥ 17 题**：可变参数和工具类过关，可以进 11/27 的收口周
- **12–16 题**：第三节（四个坑）重读一遍，明天补答
- **≤ 11 题**：把第三节那张表默写三遍 —— 这四条是面试和真实代码的高频雷区

---

## 我今天答不出的题（自己记）

>
