# 笔记 · ArrayList 源码精读

> 对应黑马 **189 ArrayList 源码分析**。
> 三问骨架。今天最重要的产出是「三张流程图」，画出来才算真懂。

## 一、它是什么

> ArrayList` = 一个 `Object[]` 数组 + 一个 `size` 变量 + 一个 `modCount计数器
>
> - `elementData`：真正存元素的数组，`transient` 修饰（不参与默认序列化）；
> - `size`：当前 **实际元素个数** ，不是数组容量，容量 = `elementData.length`；
> - `modCount`：结构修改次数，继承自 `AbstractList`，专供 fail-fast 迭代器校验用。

---

## 二、它为什么存在（面试分界线）

### 三个核心方法，各画一张流程图（画在下面，拍照或文字描述）

#### 1. `add(E e)` 做了什么（从进方法到 return true）

```
add(E e)
  └→ ensureCapacityInternal(size + 1)      // 先保证装得下
        └→ 判断 elementData 是否还是空数组
              ├─ 是 → minCapacity = max(DEFAULT_CAPACITY=10, size+1)
              └─ 否 → minCapacity 保持 size+1
        └→ ensureExplicitCapacity(minCapacity)
              ├─ if (minCapacity - elementData.length > 0)  → 装不下
              │      └→ grow(minCapacity)              // 扩容
              └─ 否则跳过（热路径，只做一次比较）
  └→ elementData[size++] = e               // 赋值并后移 size（两步合写）
  └→ modCount++                            // 结构变了，记账
  └→ return true                           // 旧版 JDK 返回 void，1.5 后为了兼容 Collection 接口返回 true
```

#### 2. `grow(int minCapacity)` 怎么扩容

```
grow(minCapacity)
  └→ oldCapacity = elementData.length
  └→ newCapacity = oldCapacity + (oldCapacity >> 1)   // 1.5 倍
  └→ if (newCapacity - minCapacity < 0)                // 1.5 倍还不够
  │      newCapacity = minCapacity                     // 直接开这么大（最小开 10 也走这条）
  └→ if (newCapacity - MAX_ARRAY_SIZE > 0)             // 超过软上限
  │      newCapacity = hugeCapacity(minCapacity)       // 过溢出检查，最多给 MAX_VALUE
  └→ elementData = Arrays.copyOf(elementData, newCapacity)  // 底层 System.arraycopy 搬数据

```

#### 3. `remove(int index)` 怎么删

```
remove(int index)
  └→ rangeCheck(index)                    // 越界就抛 IndexOutOfBoundsException
  └→ oldValue = elementData(index)        // 先把值存下来
  └→ int numMoved = size - index - 1      // 后面要整体前移的元素个数
  └→ if (numMoved > 0)
  │      System.arraycopy(elementData, index+1,
  │                       elementData, index, numMoved)  // 后面前移，覆盖被删的
  └→ elementData[--size] = null           // 尾位清空，帮 GC
  └→ modCount++                           // 结构变了，记账
  └→ return oldValue                      // 返回被删的旧值

```

### 三个必答追问

1. 为什么扩容是 **1.5 倍**（`oldCapacity + oldCapacity >> 1`），不是 2 倍？
     2 倍太浪费空间、1.1 倍又要频繁拷贝，1.5 倍是在两者之间取的常用折中值 。
3. `MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8` 这个 -8 是干什么的？
    -8 是给“数组对象头”预留的空间。数组对象本身在内存里除了元素数据，还要存对象头（mark word + 类型指针等），在部分 JVM 实现下这部分约占几个字节到十几个字节。如果真给到 Integer.MAX_VALUE，数组对象的总内存大小加上对象头可能超过某些 JVM 能表示的最大对象大小（2^31 - 1 字节量级），导致分配时出现 OutOfMemoryError: Requested array size exceeds VM limit。减 8 是一个留有余量的常见做法。另外 hugeCapacity 里还留了 Integer.MAX_VALUE 这一级兜底：真正需要到那个量级时，允许到 Integer.MAX_VALUE，再超就直接 OutOfMemoryError。
5. 为什么 `remove` 要把 `elementData[--size]` 置成 `null`？
    防止 内存泄漏 。数组里那个位置虽然逻辑上已经删掉了（size 减了），但数组本身还持有对象的强引用；只要 ArrayList 对象还活着，数组就不会被回收，被删掉的元素就无法被 GC 判定为垃圾，形成“逻辑上删了、内存里没释放”的情况。把它置 null 后，该引用的可达性断掉，对象才能被垃圾回收。这就是所谓“ 没用的引用主动置 null ”（Stack 的 pop 也做同样的清理）。

---

## 三、什么场景 / 有什么坑

### 我今天对照 JDK 找出的 3 条差异（写进这里，这是我的账）

| #   | JDK 有而我没有的                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             | 我补上了吗                                            | 为什么 JDK 要这么做                                                                                                                                                                                                                                                                  |
| --- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | ------------------------------------------------ | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1   | 懒初始化的载体：JDK 用两个共享空数组常量（`EMPTY_ELEMENTDATA` / `DEFAULTCAPACITY_EMPTY_ELEMENTDATA`）表示“还没开数组”，构造器只赋引用，靠 `==` 引用比较区分“首次 add 开 10”还是“按 minCapacity 最小开”；我原来用 `null`，到处要判空                                                                                                                                                                                                                                                                                                                                                                                   | 已补，改为两个空数组常量                                     | 用 `null` 做哨兵要处处判空，容易 NPE；用空数组常量既零长度、又能用 `==` 快速区分“未初始化”的两种状态，PATH 更清晰。而且共享同一个空数组实例，不额外占内存溢出安全：`oldCapacity + (oldCapacity>>1)` 在 `oldCapacity` 接近 `Integer.MAX_VALUE` 时会 int 溢出为负，导致 `newCapacity` 比 `minCapacity` 还小，扩容失效、容量一步一格地爬，性能雪崩；JDK 用不易溢出的计算方式加软上限，保证扩容一定成功或明确抛出 OOM |
| 2   | 扩容的计算与入口：JDK 在 `grow` 里用 `ArraysSupport.newLength(oldCapacity, minGrowth, oldCapacity >> 1)` 做溢出安全的 1.5 倍扩容，带 `MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8` 软上限和 `hugeCapacity` 兜底；我原来直接 `oldCapacity + (oldCapacity >> 1)`，容量超 ~14.3 亿时会溢出成负数、静默退化成每次 +1 格<br/>已                                                                                                                                                                                                                                                                                           | 已补，接入 newLength 与软上限检查                           | 溢出安全：`oldCapacity + (oldCapacity>>1)` 在 `oldCapacity` 接近 `Integer.MAX_VALUE` 时会 int 溢出为负，导致 `newCapacity` 比 `minCapacity` 还小，扩容失效、容量一步一格地爬，性能雪崩；JDK 用不易溢出的计算方式加软上限，保证扩容一定成功或明确抛出 OOM                                                                                          |
| 3   | | `modCount +1` 的覆盖面和位置：JDK 在所有可能让迭代器失效的操作里都 +1（`trimToSize`、公开的 `ensureCapacity`、`clear`、`addAll`，`remove` 系列收敛到 `fastRemove` 一个点），且放在操作 **开头** “先记账再干活”；我原来只在 add/remove 里加、还放在操作末尾，`trimToSize` 干脆没加（注释里甚至写反了“JDK 不加”） \| 已补，统一放在方法开头，补齐 `trimToSize`/`clear`/`addAll` 等入口 \| 核心目的是 **fail-fast** ：迭代器在 `next()`/`remove()` 里先比对 `modCount` 是否等于 `expectedModCount`，不等立刻抛 `ConcurrentModificationException`。只要操作改变了结构就必须记账，登记点必须覆盖全面、且在真正改动前登记，否则并发修改可能被漏检；放在末尾的话，操作中途抛异常就不会记账，迭代器层面就检测不到 \|<br/>\| --- \| --- \| --- \|<br/><br/>**坑（我踩过 / 我差点踩）** | 已补，统一放在方法开头，补齐 `trimToSize`/`clear`/`addAll` 等入口 | 核心目的是 **fail-fast** ：迭代器在 `next()`/`remove()` 里先比对 `modCount` 是否等于 `expectedModCount`，不等立刻抛 `ConcurrentModificationException`。只要操作改变了结构就必须记账，登记点必须覆盖全面、且在真正改动前登记，否则并发修改可能被漏检；放在末尾的话，操作中途抛异常就不会记账，迭代器层面就检测不到                                                                   |

### 坑（写「我踩过 / 我差点踩」）

- **坑 1：`remove` 删元素后没把尾部位置置 null。** 拿着 `size--` 就算完事了，对象被删了但数组还强引用着它，只要 list 活着就回收不掉。这是真的内存泄漏，不是理论问题：批量增删时如果忘记清理，老对象会一直堆积，甚至带着它引用的整棵对象树都无法回收。置 `null` 这一行是必须的。
- **坑 2：直接写 `oldCapacity + (oldCapacity >> 1)` 不查溢出。** 数据量小永远看不出问题，等你真把容量搞到接近 2^31 时，`newCapacity` 会变成负数，`Arrays.copyOf` 直接抛 `NegativeArraySizeException`，而且这个 bug 在测试环境几乎复现不出来。
- **坑 3：`modCount` 只在 add/remove 里加。** 结果是 `clear()`、`addAll()`、`trimToSize()` 之后再拿迭代器遍历，该抛异常的时候不抛，迭代器的 fail-fast 形同虚设。面试里问“为什么 for-each 里 remove 会抛 ConcurrentModificationException”，答不上记账点是关键。

## 自测（合上笔记，能说出来再打勾）

- [ ] 三张流程图能默画
- [ ] 能说清 1.5 倍扩容的理由
- [ ] 能说清 `MAX_ARRAY_SIZE` 的 -8
- [ ] 能说清 remove 置 null 的原因（帮助 GC）
- [ ] 3 条差异和 JDK 的理由都写全了
