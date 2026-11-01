# 笔记 · ArrayList 源码精读

> 对应黑马 **189 ArrayList 源码分析**。
> 三问骨架。今天最重要的产出是「三张流程图」，画出来才算真懂。

## 一、它是什么

> 一句话说清 ArrayList 的底层结构。

`ArrayList` = ______（一个 `Object[]` 数组 + 一个 `size` 变量 + 一个 `modCount` 计数器）

---

## 二、它为什么存在（面试分界线）

### 三个核心方法，各画一张流程图（画在下面，拍照或文字描述）

#### 1. `add(E e)` 做了什么（从进方法到 return true）

```
（用箭头画出：ensureCapacity 判断 → 可能 grow → 赋值 elementData[size++] → modCount++ → return）
```

#### 2. `grow(int minCapacity)` 怎么扩容

```
（画出：oldCapacity → newCapacity = oldCapacity + oldCapacity >> 1 → 不够就取 minCapacity
 → 检查 MAX_ARRAY_SIZE → Arrays.copyOf）
```

#### 3. `remove(int index)` 怎么删

```
（画出：rangeCheck → 取旧值 → System.arraycopy 前移 → --size 置 null → modCount++ → return 旧值）
```

### 三个必答追问

1. 为什么扩容是 **1.5 倍**（`oldCapacity + oldCapacity >> 1`），不是 2 倍？
2. `MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8` 这个 -8 是干什么的？
3. 为什么 `remove` 要把 `elementData[--size]` 置成 `null`？

---

## 三、什么场景 / 有什么坑

### 我今天对照 JDK 找出的 3 条差异（写进这里，这是我的账）

| # | JDK 有而我没有的 | 我补上了吗 | 为什么 JDK 要这么做 |
|---|---|---|---|
| 1 | | | |
| 2 | | | |
| 3 | | | |

### 坑（写「我踩过 / 我差点踩」）

- 坑 1：

---

## 自测（合上笔记，能说出来再打勾）

- [ ] 三张流程图能默画
- [ ] 能说清 1.5 倍扩容的理由
- [ ] 能说清 `MAX_ARRAY_SIZE` 的 -8
- [ ] 能说清 remove 置 null 的原因（帮助 GC）
- [ ] 3 条差异和 JDK 的理由都写全了
