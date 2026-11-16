# 12 · Map 接口与三种遍历

> 归属 `notes/collections/` ｜ 写于 2026-11-16 ｜ 对应黑马 199–203（双列集合特点 / Map 常用 API / 三种遍历）

---

## 一、它是什么

> 一句话：Map 是**双列集合**，一次存两个值 —— 键（key）和值（value），键不能重复。

填：

```

```

**必须自己画一次这张对照表**（两边没有继承关系，是两套独立体系）：

|            | Collection（单列）                | Map（双列）                       |
| ---------- | ----------------------------- | ----------------------------- |
| 存什么        | 单个元素                          | 键值对 Entry<K,V>                |
| 谁存元素       | 直接存                           | 键和值成对存，键唯一                    |
| 常用实现       | List / Set（ArrayList、HashSet） | HashMap、LinkedHashMap、TreeMap |
| 能不能 Key 重复 | 元素可重复（List），Set 不可            | Key 不能重复，重复就覆盖                |

---

## 二、为什么需要它

> 一句话：List 要想「按名字找人」只能从头遍历（O(n)），Map 能一步定位（平均 O(1)）。

填：

```

```

用一句话说明：**如果你要「用 A 查 B」，就该用 Map；如果只是「按顺序存一堆」，用 List。**

---

## 三、三种遍历（今天的主菜）

|     | 写法     | 关键 API                               | 什么时候用        |
| --- | ------ | ------------------------------------ | ------------ |
| 遍历一 | 键找值    | `keySet()` + `get(k)`                | 只需要 key      |
| 遍历二 | 键值对    | `entrySet()` + `getKey()/getValue()` | **推荐，性能最好**  |
| 遍历三 | lambda | `forEach((k,v) -> ...)`              | 代码最短，不能改局部变量 |

**必须回答：为什么「遍历一」比「遍历二」慢？**
（提示：`get(k)` 内部又做了什么？）

填：

```
get(k) 内部要重新计算 hash、定位桶、再比较 key，相当于每个元素查了两遍表（一遍 keySet 取 key，一遍 get 定位 value）；而 entrySet 只遍历一次，Entry 里 key/value 都已备好。
```

**必须回答：`forEach` 里的 lambda 能不能 `count++` 累加外面方法的局部变量？为什么？**

填：

```
不能。lambda 捕获的外部局部变量必须 effectively final（不能被重新赋值）。count++ 等于修改重新赋值，编译直接报错。变通办法：用 int[] count = {0} 改数组元素（改内容不改引用），或用 AtomicInteger。
```

---

## 四、坑（至少写 4 个）

1. put 返回值：key 不存在返回  null ，key 已存在返回  旧值
2. HashMap 遍历顺序  不是  插入顺序，要保序用  LinkedHashMap
3. get(k) 查不到返回  null ，不是抛异常；value 存 null 后 get 和「不存在」就分不清了 → 用 containsKey 判断存在性
4. 自定义对象当 key 必须重写  hashCode  和  equals ，否则用 new 出来的等价对象查不到（原因：默认用对象地址算 hash、比引用，两个内容相同的对象地址不同，落到不同桶）
5. 遍历中删除元素 →  ConcurrentModificationException ，正确做法是用  Iterator 的 remove() ，或 collect 后再删，或 removeIf

---

## 五、自测（盖住上面，自己答）

1. Map 和 Collection 有继承关系吗？
    没有继承关系 。Map 不在 Collection 体系下，是两套独立接口（Collection 存单列，Map 存键值对）。  
3. `HashMap` 的 key 能不能是 null？`Hashtable` 呢？
    HashMap 的 key  可以 为 null（且只能一个 null key）。Hashtable 的 key  不能 为 null，会抛 NullPointerException。
5. 三种遍历里，哪一种「同时需要 key 和 value」时最该用？为什么？
    遍历二 entrySet  最该用。一次遍历同时拿到 key 和 value，只查一次表，性能最好。
7. `map.put("a", 1)` 和 `map.put("a", 2)` 第二次返回什么？
    map.put("a", 1) 返回  null ；map.put("a", 2) 返回  1 （旧值）。
9. `entrySet` 拿到的每个元素是什么类型？它的 `getKey()` 返回什么？
    entrySet 拿到的每个元素是  Map.Entry<K, V> ，getKey() 返回  该 Entry 的键（K 类型） 。

---

## 六、一句话总结（写完今天就收工）

> 如果面试官问「Map 你会用吗」，我用一句话回答：

```
「Map 我熟，双列键值对、键唯一；日常首选 HashMap，要保序用 LinkedHashMap、要排序用 TreeMap；遍历优先 entrySet，因为它一次遍历同时拿到 key 和 value，比 keySet+get 少查一次表；自定义 key 记得重写 hashCode 和 equals。」
```
