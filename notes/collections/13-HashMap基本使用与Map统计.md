# 13 · HashMap 基本使用与 Map 统计

> 归属 `notes/collections/` ｜ 写于 2026-11-17 ｜ 对应黑马 204–206（HashMap 基本使用 / 练习一自定义对象 / 练习二统计）

---

## 一、它是什么

> 一句话：HashMap 是「键 → 值」的映射表，键唯一，**用空间换时间**，平均 O(1) 查到一个值。

填：

```
HashMap 是基于哈希表实现的「键 → 值」映射。key 唯一，靠 hashCode() 找出位置、equals() 确认，平均 O(1) 完成查询，是典型的空间换时间。
```

**必须回答：HashMap 和 TreeMap / LinkedHashMap 差在哪？**（一句话一个）

|               | 顺序                         | 底层               | 查找       |
| ------------- | -------------------------- | ---------------- | -------- |
| HashMap       | 无序                         | 数组 + 链表/红黑树（哈希表） | O(1)     |
| LinkedHashMap | 插入顺序（或访问顺序）                | 哈希表 + 双向链表       | O(1)     |
| TreeMap       | key 排序（自然序/自定义 Comparator） | 红黑树              | O(log n) |

---

## 二、为什么需要它

> 一句话：用「名字」查「信息」，如果用 List 只能 for 循环，100 万条就是 100 万次比较。

填：

```
用「名字」直接查「信息」是 O(1)，而 List 查一个元素必须从头 for 循环，最坏 O(n)。数据量大时差距是线性的。
```

**必须回答：什么时候**不该**用 HashMap？**（提示：要排序 / 要范围查询 / key 很少）

填：

```
要排序 （要按 key 或 value 有序）→ 用 TreeMap；
要做范围查询 （比如找 10~20 之间的 key）→ 用 TreeMap 的 subMap；
key 很少且固定  → 用数组，比哈希表更省更好；
对内存敏感或需要确定性迭代顺序  → 也慎用 HashMap。
```

---

## 三、今天的主线：三种统计写法（同一件事，三种写法）

统计 `["a","b","a","c","a"]`：

**写法一（最啰嗦，但面试常让你手写）**

```
Integer count = map.get(word);
if (count == null) { map.put(word, 1); } else { map.put(word, count + 1); }
```

**写法二（getOrDefault）** —— 自己写出来：

```
for (String w : list) {
    map.put(w, map.getOrDefault(w, 0) + 1);
}
```

**写法三（merge）** —— 自己写出来，并解释 `Integer::sum` 是什么：

```
for (String w : list) {
    map.merge(w, 1, Integer::sum);
}
```

**必须回答：三种写法里，`get` 返回 null 的时候分别是怎么处理的？**（这是 206 集的坑）

填：

```
写法一 （原始 get）：返回 null 直接参与运算会 NPE，所以要手动 if (count == null) 判断，null 时放 1。
写法二 （getOrDefault）：传了兜底值 0，返回 null 时自动变成 0，不用判断。
写法三 （merge）：第三个参数只在 key 已存在时执行，不存在时直接用第二个参数值放进去，内部已处理 null，不会 NPE、不会拆箱失败。
```

---

## 四、自定义对象当 key（205 集的核心）

**必须回答：不重写 `equals` / `hashCode` 会发生什么？为什么？**

（提示：HashMap 的两步定位 —— 先 hashCode 找桶，再 equals 比内容）

填：

```
会发生「明明内容相同却查不到 / 去重失败」 。因为默认实现里 hashCode() 按内存地址算，equals() 比是不是同一个引用。HashMap 定位是 两步 ：先用 hashCode 找桶，再用 equals 比内容。两个内容相同的对象地址不同 → 哈希不同 → 进了不同桶 → 连 equals 的机会都没有，自然查不到。
```

**必须回答：只重写 `equals` 不重写 `hashCode` 行不行？**

填：

```
不行 。equals 相等必须保证 hashCode 也相等。只写 equals 的话哈希值还是按内存地址算，内容相同的对象哈希不同，会进入不同桶，根本碰不到一起，equals 形同虚设，HashSet 里会出现重复。
```

---

## 五、坑（至少写 5 个）

1. put 已存在的 key → 返回（填： 被覆盖掉的旧 value ），不是 null。
2. key 是自定义对象但没重写 equals/hashCode → 填： 内容相同的 key 会被当成两个不同的 key，get 不到、去重失效 。
3. 遍历时用 map.remove(k) → 填： ConcurrentModificationException  异常，正确做法是 用 Iterator 的 iterator.remove() ，或先收集待删 key 再统一删。
4. get 查不到返回（填： null ）；如果 value 真的存了 null，就分不清「没这个 key」和「值是 null」→ 业务里（填： value 不允许存 null，或者用 containsKey 额外判断 / 用 getOrDefault 给默认值 ）。
5. HashMap 初始容量 16、负载因子 0.75 → 填： 元素个数超过 16 × 0.75 = 12 时扩容，容量扩到原来的 2 倍（32），并重新分配所有元素到新桶 。
6. computeIfAbsent 做分组为什么比「先 get 判 null 再 put」好？
   填： 一行搞定，语义清晰；只在 key 不存在时才执行 new ArrayList<>()，不会白建对象；而且它返回的一定是 map 上那个 List，可以链式 .add()。手写 get 判断需要 3~5 行，还容易漏 put。

---

## 六、自测（盖住上面，自己答）

1. HashMap 平均 O(1)，最坏情况是多少？什么情况下会退化？
2. 为什么 key 建议用不可变对象（String / Integer）？
3. `map.put(k,v)` 和 `map.putIfAbsent(k,v)` 的区别？
4. `entrySet` 遍历比 `keySet + get` 快在哪？
5. 用 Map 做词频统计，写出 `merge` 那一行。
6. Top-K 问题：Map 统计完，下一步用什么结构取前 K？

---

## 七、一句话总结（写完收工）

> 如果面试官问「Map 你除了 put/get 还会什么」，我用一句话回答：

```
会根据场景选： 计数用 merge / getOrDefault，分组用 computeIfAbsent，遍历用 entrySet 比 keySet+get 快一倍，自定义对象当 key 必须重写 equals 和 hashCode，需要有序或范围查询要换 TreeMap。
```
