# 2026-10-13 · 手写 MyArrayList（第一版 / 精简版）

## 这是什么

这是 **2026-10-13** 写的一版 `MyArrayList`，比仓库正稿更早、更小。保留它作为**可对比的起点证据**。

## 实测对比（不是估计，是逐文件 diff + 方法表核对出来的）

| | 这一版（10-13） | 仓库正稿（`src/main/java/practice/mycollection/`） |
|---|---|---|
| `MyArrayList.java` 体积 | 6.1 KB | 8.8 KB |
| 方法数 | 12 | 13（多一个 `trimToSize()`） |
| 自测断言 | 19 处 `check(...)` | 33 处 `check(...)` |
| 1.5 倍扩容 `length + (length >> 1)` | ✅ 有 | ✅ 有 |
| `modCount` / fail-fast | ✅ 有 | ✅ 有 |
| 内部类 `Itr implements Iterator<E>` | ✅ 有 | ✅ 有 |
| `main()` 演示 | ✅ 有 | ✅ 有 |

**共同的方法**：`MyArrayList()` · `MyArrayList(int)` · `add` · `get` · `set` · `remove(int)` · `size` · `isEmpty` · `checkIndex` · `ensureCapacity` · `iterator` · `main`。

> 结论：两版**核心机制完全一致**，正稿多的只是 `trimToSize()` 和更细的自测。这不是「从零到有」的对比，而是「写对了，然后补完整」的对比 —— 这本身也是有价值的记录。

## ⚠️ 一份仍欠的账：懒初始化

**两版都没有** JDK `ArrayList` 的懒初始化（`DEFAULTCAPACITY_EMPTY_ELEMENTDATA`，第一次 `add` 才把容量给到 10）—— 这里用的是构造时直接开数组。

**10/26 集合模块开学后要补上**，并回答：JDK 为什么非要延迟到第一次 `add` 才分配？省的是什么？

## 为什么不把 `out/` 里的 4 个 `.class` 一起归档

编译产物不进版本库：

- `.class` 是**可再生成的**，跟着源码走只会让仓库变脏
- 二进制无法 diff，点开是乱码
- 仓库 `.gitignore` 已经挡住 `out/`、`target/`、`*.class`

**判据**：别人 clone 你的仓库，跑一条命令就能得到和你完全一样的结果 —— 那才算干净。

> 本目录不在 `src/main/java/` 下，**不参与编译**，只是为了留档。
