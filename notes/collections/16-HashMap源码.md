# 16 · HashMap 源码（黑马 211–212）

> 归属 `notes/collections/` ｜ 写于 2026-11-20 ｜ 对应黑马 211（21:11）、212（21:19）
>
> **这是问答卷：只有问题，没有答案，没有提示性表格。**
> 规则：先把视频看一遍（1.5–2 倍速），然后**关掉视频**，凭记忆写。
> 哪个答不上来，就是明天回去把这 2 分钟重看一遍的地方。

---

## 一、结构（答完这节你才算知道 HashMap 长什么样）

### 1.1

JDK 1.8 的 HashMap 底层是哪三种结构组合起来的？分别在什么条件下切换？

    默认：数组 + 链表
    链表长度 ≥ 8 且数组容量 ≥ 64 → 链表转红黑树
    红黑树节点数降到 6 → 退化回链表

### 1.2

`table`（也就是 `Node<K,V>[] table`）什么时候才真正被创建？是 `new HashMap()` 的时候吗？

    不是 new 的时候。new HashMap() 只是设了参数（loadFactor），table 是 null。 第一次 put  时由 resize() 才真正分配数组。

### 1.3

一个 `Node` 里存了几个字段？分别是什么？

    4 个：int hash、K key、V value、Node<K,V> next。

### 1.4

源码里 `DEFAULT_INITIAL_CAPACITY`、`DEFAULT_LOAD_FACTOR`、`TREEIFY_THRESHOLD`、`UNTREEIFY_THRESHOLD`、`MIN_TREEIFY_CAPACITY` 这五个常量分别是多少？

    DEFAULT_INITIAL_CAPACITY =  16
    DEFAULT_LOAD_FACTOR =  0.75f
    TREEIFY_THRESHOLD =  8
    UNTREEIFY_THRESHOLD =  6
    MIN_TREEIFY_CAPACITY =  64

---

## 二、put 的完整流程（**这一节必须能背下来，面试第一问**）

### 2.1

`put(K key, V value)` 内部只调了一个方法，是哪个？

    putVal(hash(key), key, value, false, true)。就这一个。

### 2.2

`putVal` 一进来第一步做了什么判断？为什么这一步很重要？

    第一步判断 if ((tab = table) == null || (t = tab.length) == 0)，为空就 resize()。
    重要性： 懒加载 ——new HashMap() 不建数组，省内存；第一次 put 才分配，这是 1.2 的答案，也是必须 put 时先判空的原因。

### 2.3

数组下标是怎么算出来的？源码里用的是取模 `%` 吗？如果不是，用的是什么？为什么可以用它代替取模？

    用 (n - 1) & hash，不是 %。等价是因为 n 是 2 的幂，n-1 低位全 1，& 恰好保留低位，等于取模，但位运算快得多（前面 TODO 2 已经讨论过）。

### 2.4

算出下标后，如果那个位置是 null，直接做什么？

    该位置为 null，说明没冲突，直接 tab[i] = newNode(hash, key, value, null)，然后 ++modCount; if (++size > threshold) resize();，返回。

### 2.5

如果那个位置**不是** null，源码依次判断了哪三种情况？（提示：从「是不是同一个 key」的角度想，有三种可能）

    头节点 hash 相等且 (k = p.key) == key || (key != null && key.equals(k)) → 就是它，覆盖 value
    头节点是树节点（p instanceof TreeNode）→ 走 putTreeVal 在红黑树里找/插
    都不是 → 遍历链表，找到相同的 key 就覆盖；走到链尾还没有，就尾插新节点（链表长度达阈值则 treeifyBin）

### 2.6

链表遍历过程中，如果找到相同的 key 了，怎么判断「相同」的？（调了哪两个方法？顺序是什么？）

    判断「相同」先比 hash，hash 相同再走 == 或 equals：
    p.hash == hash && ((k = p.key) == key || (key != null && key.equals(k)))
    顺序： hash 相等 → 引用 == 相等 → equals 。先用 hash 快速排除，== 是短路优化（同一个对象直接判定，不用调 equals

### 2.7

新节点是插在链表**头部**还是**尾部**？JDK 1.7 呢？为什么 1.8 要改？

    1.8  尾插 ，1.7 是 头插 。
    1.8 改成尾插是因为头插在扩容时会把链表顺序倒置，多线程下容易形成环，造成死循环（JDK 7 的经典 bug）。

### 2.8

`put` 的最后，有一步 `++modCount` 和一个 `if` 判断，这个 `if` 判断的是什么？满足之后调用了哪个方法？

    ++modCount（记录结构修改次数，用于 fail-fast）；if (++size > threshold) 判断 大小是否超过阈值 ，满足则调 resize()。

---

## 三、hash 扰动（面试第二问）

### 3.1

`hash(Object key)` 方法里做了什么事？写出来（就一行）。

    return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);

### 3.2

为什么要 `h >>> 16`？直接把 `hashCode()` 拿去用不行吗？

    因为算下标只用到 hash 的 低位 。如果直接用 hashCode，高位信息全被丢掉，只要低位相同就会冲突。右移 16 位异或，把高 16 位掺进低 16 位，让高位也参与定位，减少冲突。（跟你 TODO 1 讨论的一样。

### 3.3

如果 key 是 null，hash 值是多少？所以 null 能作为 key 吗？放在数组的哪个位置？

    key 为 null 时 hash =  0 ，所以  null 可以做 key 。下标 0 → 放在 table[0]。整个 HashMap 只允许  1 个  null key（因为 key 唯一）；null value 可以有很多个。

---

## 四、扩容 resize（面试第三问）

### 4.1

扩容的触发条件是什么？`threshold` 是怎么算出来的？

    触发条件两处：
    ① size 超过 threshold（++size > threshold）；
    ② 初始化时 table 为空也调它。
    threshold = capacity × loadFactor，比如 16 × 0.75 = 12。

### 4.2

容量为什么必须是 **2 的幂**？如果初始化时你传了 `new HashMap<>(10)`，实际容量是多少？

    容量必须是 2 的幂，是为了 (n-1) & hash 能正确代替取模、且低位全 1 让所有桶都能用到。
    new HashMap<>(10) → 实际容量  16 （tableSizeFor 会向上取最近的 2 的幂）。

### 4.3

扩容后，原来链表上的节点怎么分到新数组？JDK 1.8 用了什么巧妙的判断？（提示：`(e.hash & oldCap) == 0`）

    JDK 1.8 用 (e.hash & oldCap) == 0 判断：为 true 放低位链（原下标位置），为 false 放高位链（原下标 + oldCap）。

### 4.4

**关键问题**：`(e.hash & oldCap) == 0` 为 true 的节点留在原位置，为 false 的挪到「原下标 + 老容量」。为什么这个判断能成立？（这题答不上来说明 4.2 你没真懂）

    为什么成立：扩容后下标 = hash & (newCap-1)，newCap = oldCap × 2，只多出一个高位。这个高位正好对应 hash & oldCap。这一位是 0 → 新下标 = 老下标；是 1 → 新下标 = 老下标 + oldCap。所以不用重新算 hash，只看这一位就能决定去向。这依赖 4.2「容量是 2 的幂」，所以 4.2 不懂这题必错。

---

## 五、树化（面试第四问）

### 5.1

链表长度到多少开始考虑转红黑树？

    链表长度到  8  开始考虑转红黑树。

### 5.2

**但是**，只有链表长度 ≥ 8 就一定会转吗？还有什么前置条件？`MIN_TREEIFY_CAPACITY` 是多少？

    不一定。前提是 数组容量 ≥ 64 （MIN_TREEIFY_CAPACITY =  64 ）。容量不够时不树化，而是先 resize() 扩容。

### 5.3

红黑树节点数降到多少会退化回链表？

    红黑树节点降到  6  退化回链表（UNTREEIFY_THRESHOLD）

### 5.4

为什么阈值是 8 和 6，中间不留 7？（提示：和扩容时 7 个节点怎么分有关）

    中间留 7 是缓冲带，防止在 7 附近反复树化/退化来回抖动。而且扩容时 7 个节点可能被拆成两个桶，本来就不再满足树化条件，所以阈值错开更稳妥。

---

## 六、几个「为什么」

### 6.1

加载因子为什么是 0.75？换成 0.5 或 1.0 各会怎样？

    0.75 是空间和时间折中。负载因子越高，冲突越多、查询变慢；越低，数组用不满、稀疏浪费内存。0.75 配合 2 的幂，capacity × 0.75 恰好是整数（16→12），冲突概率和空间利用率平衡点。换 0.5 浪费空间、扩容频繁；换 1.0 冲突明显增多。

### 6.2

`table` 为什么被声明成 `transient`？

    table 声明 transient，因为不同 JVM 的 hash 计算结果可能不同（hashCode 依赖对象地址等），直接序列化数组下标会错乱，所以序列化时跳过，反序列化后重建。

### 6.3

HashMap 的容量为什么总是 2 的幂，而 `tableSizeFor` 方法是怎么把 10 变成 16 的？

    因为 2 的幂才能让 (n-1) 全 1、& 代替 % 成立，且桶均匀。
    tableSizeFor(10)：先把 10-1=9 逐位右移或（n |= n>>>1; n |= n>>>2; ...）把低位数全填 1 变成 0b1111...，最后 +1 得到最近的 2 的幂 →  16 。

### 6.4

HashMap 允许 null key 和 null value 吗？TreeMap 呢？ConcurrentHashMap 呢？（三个一起答，这是高频对比题）

    ConcurrentHashMap 不允许任何 null，因为并发下 null 会引起歧义，无法区分「不存在」和「值就是 null」。

|                   | null key                 | null value |
| ----------------- | ------------------------ | ---------- |
| HashMap           | ✅                        | ✅          |
| TreeMap           | ❌（nil key 无法比较，除非自定义比较器） | ✅          |
| ConcurrentHashMap | ❌                        | ❌          |

---

## 七、和「你写过的代码」对上号

### 7.1

你 11/20 写的 `MyHashMapDemo` 里，`hash()` 和 `indexFor()` 两个方法对应源码里的哪两段？

    hash() 对应源码的扰动函数 static final int hash(Object key)；indexFor() 对应 (n-1) & hash 那段下标计算（1.8 里这段是内联在 putVal 里的，没有单独方法，1.7 才有 indexFor）。

### 7.2

你的 `resize()` 里，节点重新分配用的是取模 `hash % newCap` 还是位运算？为什么你的实现可以取巧？

    如果你用位运算 (e.hash & oldCap) == 0 分高低位，那就是取巧；也可以用 hash % newCap。能取巧的前提是 容量是 2 的幂 ，这时 & 等于 %，所以能直接走位运算。（若你用了 % 也对，只是慢一点。）

### 7.3

你的 `MyHashMap` 有没有做树化？如果没有，说明它和 JDK 的真实差距在哪（一句话）。

    没做树化的话差距就在： 极端冲突时你的链表退化成 O(n)，JDK 是 O(log n) 。一句话：「JDK 有链表→红黑树的兜底，我的没有。」

---

## 八、自测打分

| 得分                                   | 判定                          |
| ------------------------------------ | --------------------------- |
| 本节 6 题以上答满且 2.2 / 2.5 / 4.4 / 5.2 都对 | 可以进 11/22 的「集合 20 问」        |
| 2.5 或 4.4 有一个含糊                      | 明天早上重看 211 的对应 3 分钟，再答一遍    |
| 超过 8 题空着                             | 视频没真正看懂，212 先别看，回到 211 重看一遍 |

**我的自评得分：__ / 8 节**
