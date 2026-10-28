# 笔记 · List 五种遍历与迭代器 remove

> 对应黑马 **186 增强 for + lambda**、**187 List 五种遍历**。
> 三问骨架，答不出来就回去看那一段。

## 一、它是什么

### List 的五种遍历，各自一行说清

| # | 写法 | 适用场景 |
|---|---|---|
| 1 | 普通 for（按下标） | |
| 2 | 增强 for（for-each） | |
| 3 | `Iterator` | |
| 4 | `ListIterator` | |
| 5 | `forEach + lambda` | |

### 增强 for 的本质

`for (String s : list)` 编译后等价于：

```java
// 写在下面
```

---

## 二、它为什么存在（面试分界线）

### 1. 为什么「边遍历边删除」不能用普通 for / 增强 for？

> 提示：普通 for 删了元素后，下标会怎样？增强 for 删了元素会抛什么异常？

### 2. 为什么迭代器自己的 `remove()` 不抛 ConcurrentModificationException，而集合的 `remove()` 会抛？

> 提示：你昨天读的 `modCount` / `expectedModCount`。今天写完迭代器 `remove()` 后，把这个问题的答案**用一行代码**表达出来（就是那一行同步语句）。

---

## 三、什么场景 / 有什么坑

### 我今天踩的坑（写真实过程）

- 坑 1：

### 三个必答追问

- 连续调两次 `it.remove()` 会发生什么？为什么？（提示：`lastRet` 的值）
- `next()` 之前调 `it.remove()` 呢？
- `ListIterator` 比 `Iterator` 多出什么能力？什么场景用得上？

---

## 自测（合上笔记，能说出来再打勾）

- [ ] 五种遍历能背出名字 + 一句话场景
- [ ] 能默写 `for-each` 编译后的等价代码
- [ ] 能说清「迭代器 remove 为什么不抛 CME」的那行关键代码
- [ ] 能答出连续两次 remove / next 前 remove 的后果
