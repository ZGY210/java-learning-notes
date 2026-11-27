# 18 · 可变参数与 Collections 工具类

> 对应黑马 **217（可变参数 12:22）、218（Collections 05:49）**，外加 **219–223 综合练习**里出现的工具方法。
> 这一天内容不多，但坑特别多 —— 面试官爱问「Arrays.asList 返回的 list 能不能 add」，答错的人一大片。
> 用法：先扫一遍问题，看视频，关掉视频凭记忆答。

---

## 一、可变参数（217）

1. `void f(int... nums)` 在字节码层面，`nums` 的真实类型是什么？

    就是 int[]（int 数组）。字节码层面方法签名就是 f([I)V。

3. 下面三段调用的等价关系：

    f() → 等价于 f(new int[]{})
    f(1, 2) → 等价于 f(new int[]{1, 2})
    f(new int[]{1,2}) → 就是直接传数组， 不需要再打包 ，直接调用即可（这正是它俩同签名能互通的原因）

5. 为什么**不能**同时定义 `f(int... nums)` 和 `f(int[] nums)`？

    因为编译后就是 同一个方法签名 ，属于重复定义，报 already defined。

7. 为什么可变参数必须在形参列表的**最后一位**？举一个会被编译器拒绝的写法。

    编译器要从某个实参开始"打包成数组"，必须知道打包的起点，所以它只能固定在末尾。
    反例：void f(int... nums, int last) → 编译报错。

9. 一个方法能有几个可变参数？为什么？

    最多一个。多个的话编译器无法划分每个可变参数各自从哪开始打包，与"必须在最后一位"直接冲突。

11. 传 `null` 进去会怎样？

    ```java
    sum(null);              // 编译报错：null 有歧义，编译器不知道该按 int... 还是别的方式匹配（Actually在只有一个方法的场景下，会发出警告/可能编译过，但实际会 NPE)
    sum((int[]) null);      // 能编译，运行时进入方法，nums 是 null → 遍历时 NullPointerException
    sum();                  // 0，等价于 sum(new int[]{})，空数
    ```

    三行的结果分别是什么，为什么不是一样的结果？

    结论：sum() 是空数组不是 null，所以返回 0；显式传 null 数组会在 for-each 时 NPE。 （这是三行结果不一样的根本原因）

---

## 二、Collections 工具类（218）

7. 写出下面每个方法的作用，并说清「有没有副作用（会不会改原集合）」：

   | 方法                       | 作用        | 改原集合吗            |
   | ------------------------ | --------- | ---------------- |
   | `sort(list)`             | 升序排序      | **改**            |
   | `reverse(list)`          | 原地反转      | **改**            |
   | `shuffle(list)`          | 随机打乱      | **改**            |
   | `max(list)`              | 返回最大元素    | 不改（只查）           |
   | `frequency(list, x)`     | 统计 x 出现次数 | 不改               |
   | `binarySearch(list, x)`  | 二分查找下标    | 不改（但 **要求已排序** ） |
   | `unmodifiableList(list)` | 返回只读视图    | 不改原集合本身          |
   | `synchronizedList(list)` | 返回线程安全包装  | 不改（返回包装器）        |
   | `emptyList()`            | 返回空列表常量   | 不改（无原集合）         |

8. `Collections.sort` 底层用的是什么排序算法？为什么用它（提示：和「数据是否近乎有序」有关）？

    Java 8+ 用的是  TimSort （归并排序的优化变体）。选它的原因是： 对"几乎已有序"的数据接近 O(n)，最坏 O(n log n)，且稳定排序 。相比快排，TimSort 能利用已有的有序片段（run），真实业务里数据往往是半有序的。

10. `Collections.binarySearch` 之前必须做什么？没做会怎样（返回什么）？

    必须先对列表排序（升序） ，且排序规则要和查找时的比较规则一致。没排序就查，结果 不确定 ——可能返回错误下标，也可能是负数（负数表示"该插入的位置 × -1 - 1"，但这个负数此时也没意义）。 它不报错，是静默返回错误结果，这才是它最坑的地方。

12. `Collections.synchronizedList` 返回的 list 真的线程安全了吗？遍历的时候呢？

    单个方法调用 （如一次 add、一次 get）是安全的，内部加了锁。
    但遍历（for-each / iterator）不安全 ，因为迭代过程不是原子的，中途别人 add 会抛 ConcurrentModificationException。遍历时必须手动 synchronized (list) { ... } 包起来。
    另外，复合操作（如"先 contains 再 add"）也不是原子的，即便单方法安全，整体逻辑仍有竞态。
    结论：只是把每个方法套了 synchronized，不解决迭代和复合操作问题。

---

## 三、四个「不可变 / 固定长度」的坑（本节最重要）

11. 填表：

    | 写法                                    | 能 add/remove | 能 set | 改了影响原数组/原集合                    |
    | ------------------------------------- | ------------ | ----- | ------------------------------ |
    | `Arrays.asList(1,2,3)`                | 不能           | **能** | **会** （它是原数组视图）                |
    | `List.of(1,2,3)`                      | 不能           | 不能    | 无原集合                           |
    | `Collections.unmodifiableList(list)`  | 不能           | 不能    | **会** （原 list 改了它跟着变，但你不能通过它改） |
    | `new ArrayList<>(Arrays.asList(...))` | **能**        | 能     | 不会（独立拷贝）                       |

12. `Arrays.asList(new int[]{1,2,3})` 返回的 list 里有几个元素？为什么不是 3 个？
    （提示：`asList(T... a)` 的 T 是什么）

    1 个 。因为 asList(T... a) 的 T 推断成了 int[]，整个数组被当成 一个对象 塞进 list，得到的是 List<int[]> 而不是 List<Integer>。要用 Integer[] 才会展开成 3 个。

14. 在 `List.of("c","a","b")` 上直接 `Collections.sort(...)` 会发生什么？为什么？

    抛 UnsupportedOperationException，因为 sort 内部要 set 写回，而 List.of 不可变。必须先拷贝。

16. 为什么 `ArrayList` 允许 `add(null)`，而 `List.of` 不允许？

    ArrayList 是普通可变容器，null 只是"一个没指向对象的引用"，可以存。
    List.of 是 Java 9 引入的不可变集合，设计上 明确禁止 null ，避免空指针语义混乱，也方便 JDK 内部做只读优化（例如某些实现共用同一个空列表实例）。传 null 直接抛 NPE。

18. 真实项目里，一个方法收到 `List<T>` 参数，如果要修改变量（比如排序），你该怎么做才安全？

    先防御性拷贝再改 
    理由：入参可能是 List.of、Arrays.asList、unmodifiableList 任何一种不可变/视图实现，直接改要么抛异常，要么污染调用方的数据。拷贝是唯一通用安全的做法。

---

## 四、对上你自己写的代码

16. `VarargsCollectionsDemo.sortCopy` 为什么要先 `new ArrayList<>(src)`，不复制会怎样？

    不复制就 Collections.sort(src)：如果 src 是 List.of 直接抛异常；如果 src 是可变的， 会把调用方传进来的集合改掉 （副作用），违反"返回新 list、不改 src"的约定。

18. `maxFrequency` 里你用了 `Collections.frequency`，它的时间复杂度是多少？如果数据是 100 万条，你会怎么改？

    复杂度  O(n²) ：外层遍历 n 次，每次 frequency 内部再扫 n 次。
    100 万条时要改成  HashMap 计数一遍 O(n) :
    Map<String,Integer> count = new HashMap<>();
    for (String w : words) count.merge(w, 1, Integer::sum)// 然后遍历 count 取最大值;：

20. `maxFrequency` 如果有两个元素出现次数并列最多，你返回的是哪一个？这是「约定」还是「错误」？

    用你这版 Math.max 的写法， 返回的是"最大次数"这个数字，不返回元素，所以并列问题不存在 。
    如果要返回元素，用 c > max 会返回 先遇到的 那个，这是 约定 不是错误——但必须和调用方说清楚，否则调用方假定"唯一"就会出 bug。

22. `listOfIsImmutable` 里你捕获的是什么异常？如果别人写的代码用 `catch (Exception e)` 兜住它，会有什么坏处？

    捕获的是 UnsupportedOperationException（属于 RuntimeException）。
    别人用 catch (Exception e) 兜住它的坏处：
    吞掉了本该暴露的问题 ，比如误以为集合可变就返回了错误结论；
    catch (Exception e) 会连带兜住 NPE、ClassCastException、IndexOutOfBounds 等， 掩盖真实 bug ，调试时根本定位不到；
    可读性上，看不出代码"预期"会抛什么，维护者容易误解。
    结论：捕获要精确到具体异常类型，宽泛 catch 是坏味道。

---

## 五、综合练习（219–223）挑三道

20. 从 219–223 里挑 **一道你觉得最难的**，写出它的思路（不用写完整代码，写清「用什么集合、为什么」。

    用 HashMap<String,Integer> 做计数，因为需要按元素分组聚合且要 O(1) 查找；输出若要求按次数排序，则把 entrySet 转成 list 用 Collections.sort 自定义比较器。不用 TreeMap，因为排序依据是 value 不是 key。

22. 这五集里有没有出现「本该用 A 集合却用了 B」的例子？说出来。

    典型： 统计词频本该用 HashMap，却用 List + 双重循环 / Collections.frequency ，这就是 O(n²) 的典型误用；或者 需要保证 key 有序本该 TreeMap，却用 HashMap 再手动排序 。

24. 如果让你自己出一道「综合练习」，考「HashMap 统计 + 排序输出」，你会怎么出？

    split + toLowerCase 清洗、map.merge 计数、entrySet 转 List、Comparator 先按 value 降序再按 key 升序。

---

## 六、自测打分

答完 22 题数一下：

- **≥ 17 题**：可变参数和工具类过关，可以进 11/27 的收口周
- **12–16 题**：第三节（四个坑）重读一遍，明天补答
- **≤ 11 题**：把第三节那张表默写三遍 —— 这四条是面试和真实代码的高频雷区

---

## 我今天答不出的题（自己记）

> 
