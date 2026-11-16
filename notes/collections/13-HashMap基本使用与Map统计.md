# 13 · HashMap 基本使用与 Map 统计

> 归属 `notes/collections/` ｜ 写于 2026-11-17 ｜ 对应黑马 204–206（HashMap 基本使用 / 练习一自定义对象 / 练习二统计）

---

## 一、它是什么

> 一句话：HashMap 是「键 → 值」的映射表，键唯一，**用空间换时间**，平均 O(1) 查到一个值。

填：

```

```

**必须回答：HashMap 和 TreeMap / LinkedHashMap 差在哪？**（一句话一个）

| | 顺序 | 底层 | 查找 |
|---|---|---|---|
| HashMap | | | O(1) |
| LinkedHashMap | | | O(1) |
| TreeMap | | | O(log n) |

---

## 二、为什么需要它

> 一句话：用「名字」查「信息」，如果用 List 只能 for 循环，100 万条就是 100 万次比较。

填：

```

```

**必须回答：什么时候**不该**用 HashMap？**（提示：要排序 / 要范围查询 / key 很少）

填：

```

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

```

**写法三（merge）** —— 自己写出来，并解释 `Integer::sum` 是什么：

```

```

**必须回答：三种写法里，`get` 返回 null 的时候分别是怎么处理的？**（这是 206 集的坑）

填：

```

```

---

## 四、自定义对象当 key（205 集的核心）

**必须回答：不重写 `equals` / `hashCode` 会发生什么？为什么？**

（提示：HashMap 的两步定位 —— 先 hashCode 找桶，再 equals 比内容）

填：

```

```

**必须回答：只重写 `equals` 不重写 `hashCode` 行不行？**

填：

```

```

---

## 五、坑（至少写 5 个）

1. `put` 已存在的 key → 返回（填：____），不是 null
2. key 是自定义对象但没重写 equals/hashCode → 填：
3. 遍历时用 `map.remove(k)` → 填：______ 异常，正确做法是 ______
4. `get` 查不到返回（填：____）；如果 value 真的存了 null，就分不清「没这个 key」和「值是 null」→ 所以业务里（填：______）
5. HashMap 的初始容量 16、负载因子 0.75 → 填：什么时候扩容、扩到多少
6. `computeIfAbsent` 做分组为什么比「先 get 判 null 再 put」好？

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

```
