# 笔记 · List 五种遍历与迭代器 remove

> 对应黑马 **186 增强 for + lambda**、**187 List 五种遍历**。
> 三问骨架，答不出来就回去看那一段。

## 一、它是什么

### List 的五种遍历，各自一行说清

| #   | 写法                 | 适用场景                                        |
| --- | ------------------ | ------------------------------------------- |
| 1   | 普通 for（按下标）        | 需要 **下标 i** ，或要 **边遍历边按索引删除** （倒序删）         |
| 2   | 增强 for（for-each）   | 只读遍历，代码最简洁、日常最常用                            |
| 3   | `Iterator`         | 需要在遍历中 **安全删除元素** （`it.remove()`）           |
| 4   | `ListIterator`     | 需要 **双向遍历** （`hasPrevious`）、 **边遍历边改/加** 元素 |
| 5   | `forEach + lambda` | 只读遍历，函数式写法，一行搞定                             |

### 增强 for 的本质

`for (String s : list)` 编译后等价于：

```java
// 写在下面
for (Iterator<String> it = list.iterator(); it.hasNext(); ) {
    String s = it.next();
    // 循环体
}

```

---

## 二、它为什么存在（面试分界线）

### 1. 为什么「边遍历边删除」不能用普通 for / 增强 for？

> - **普通 for（正序按 i++）** ：删掉元素后，后面的元素 **整体前移一格** ，而 `i` 还在自增，会 **跳过紧跟其后的那个元素** ，漏删。要么 **倒序遍历** （`for (int i = size-1; i >= 0; i--)`），要么每次删完 `i--` 补回来。
> - **增强 for** ：底层是 `Iterator`，但你拿不到 `it` 去调用 `it.remove()`。只要在循环里调了 `list.remove()`，`modCount` 变了而 `expectedModCount` 没变，下一次 `next()` 立刻抛 **`ConcurrentModificationException`** 。

### 2. 为什么迭代器自己的 `remove()` 不抛 ConcurrentModificationException，而集合的 `remove()` 会抛？

> - **集合的 `remove()`** ：只让 `modCount++`，`expectedModCount` 留在旧值 → 下次 `next()` 检查 `modCount != expectedModCount` → 抛 CME。
> - **迭代器的 `remove()`** ：删完立刻执行 `expectedModCount = modCount` 追平 → 下次检查相等 → 正常继续。

---

## 三、什么场景 / 有什么坑

### 我今天踩的坑（写真实过程）

- **坑 1** ：写 `removeByIterator` 时忘了在方法开头 `Iterator<String> it = list.iterator();`，直接用 `it.hasNext()`，编译器报 `cannot find symbol - variable it`。 **根因** ：迭代器是"要自己 new/拿 的对象"，不是 list 自带的变量，不声明就不存在。
- **坑 2** ：一开始在循环里用 `list.remove(...)` 而不是 `it.remove()`，测试时直接抛 `ConcurrentModificationException`。 **根因** ：没理解"集合删只动 modCount、迭代器删会同步 expectedModCount"这条区别。
- **坑 3** ：`target.equals(it.next())` 一开始写成 `it.next().equals(target)`，若元素为 null 会空指针。 **改成常量在前** 就安全了。

### 三个必答追问

- 连续调两次 `it.remove()` 会发生什么？为什么？（提示：`lastRet` 的值）
     第二次会抛 IllegalStateException。因为第一次 remove() 末尾把 lastRet = -1，第二次进来 if (lastRet < 0) 成立直接抛异常。remove() 删的必须是"上次 next() 返回的那个元素"，不允许凭空删或连删。
- `next()` 之前调 `it.remove()` 呢？
    也抛 IllegalStateException 。此时还没调用过 next()，lastRet 初值就是 -1，同样卡在 if (lastRet < 0) 上。
- `ListIterator` 比 `Iterator` 多出什么能力？什么场景用得上？
    多出：① 双向遍历 （hasPrevious() / previous()）；② 修改 已有元素（set()）；③ 插入 元素（add()）；④ 能拿到 下标（nextIndex() / previousIndex()）。

**场景** ：需要在遍历中 **替换元素、插入元素** ，或者要 **从后往前遍历** 时用它。

---

## 自测（合上笔记，能说出来再打勾）

- [ ] 五种遍历能背出名字 + 一句话场景
- [ ] 能默写 `for-each` 编译后的等价代码
- [ ] 能说清「迭代器 remove 为什么不抛 CME」的那行关键代码
- [ ] 能答出连续两次 remove / next 前 remove 的后果
