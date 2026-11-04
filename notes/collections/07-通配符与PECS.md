# 笔记 · 通配符与 PECS

> 对应黑马 **192 泛型的通配符和综合练习**。
> 三问骨架。通配符是泛型的另一半，面试高频追问点。

## 一、它是什么

> 一句话说清两个通配符。

- `? extends T`（上界）= ______
- `? super T`（下界）= ______

**对比表（核心）：**

| | `? extends T` | `? super T` |
|---|---|---|
| 能接收的类型 | T 及 T 的子类 | T 及 T 的父类 |
| 能读（get）吗 | 能，读出来是 T | 能，读出来是 Object |
| 能写（add）吗 | **不能**（不知道具体子类） | **能**（写 T 一定安全） |
| 比喻 | 生产者（往外拿） | 消费者（往里放） |

---

## 二、它为什么存在（面试分界线）

### 1. 为什么 `List<? extends Number>` 不能 add？

> 提示：运行期它可能是 `List<Integer>`，也可能是 `List<Double>`。add 一个 Number 进去不保证类型安全，编译期就禁止。

### 2. 为什么 `List<? super Integer>` 能 add Integer，但 get 出来是 Object？

> 提示：下限是 Integer，实际容器可能是 `List<Number>` 甚至 `List<Object>`，add Integer 一定安全；但 get 出来不能确定具体类型，只能当 Object。

### 3. PECS 原则（用自己的话说）

> Producer Extends, Consumer Super —— ______

---

## 三、什么场景 / 有什么坑

### 我今天踩的坑（写真实过程）

- 坑 1：

### 必答追问

- `List<?>`（无界通配符）和 `List<Object>` 有什么区别？
- 为什么 `List<String>` 不能赋给 `List<Object>`？（不变性：Java 泛型没有协变）
- `Collections.copy(List<? super T> dest, List<? extends T> src)` 为什么这样设计？—— 这是 PECS 的标准答案，背下来。

---

## 自测（合上笔记，能说出来再打勾）

- [ ] `? extends` / `? super` 对比表能默写
- [ ] 能说清「extends 不能 add、super 不能 get」的原因
- [ ] PECS 原则能背，并能用 Collections.copy 举例
- [ ] 能说出 Java 泛型「不变性」
- [ ] 能说清 `List<?>` 和 `List<Object>` 的区别
