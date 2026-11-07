# 笔记 · 通配符与 PECS

> 对应黑马 **192 泛型的通配符和综合练习**。
> 三问骨架。通配符是泛型的另一半，面试高频追问点。

## 一、它是什么

> 一句话说清两个通配符。

- `? extends T`（上界）= **T 及 T 的所有子类** ，类型范围被限定在「T 以下」，可以安全地当成 T 来读，但不能写。
- `? super T`（下界）= **T 及 T 的所有父类** ，类型范围被限定在「T 以上」，可以安全地写入 T，但读出来只能当 `Object`。

**对比表（核心）：**

|          | `? extends T`   | `? super T`     |
| -------- | --------------- | --------------- |
| 能接收的类型   | T 及 T 的子类       | T 及 T 的父类       |
| 能读（get）吗 | 能，读出来是 T        | 能，读出来是 Object   |
| 能写（add）吗 | **不能**（不知道具体子类） | **能**（写 T 一定安全） |
| 比喻       | 生产者（往外拿）        | 消费者（往里放）        |

---

## 二、它为什么存在（面试分界线）

### 1. 为什么 `List<? extends Number>` 不能 add？

> **答** ：`List<? extends Number>` 在运行期可能是 `List<Integer>`，也可能是 `List<Double>`，甚至 `List<Long>`。编译器只知道「元素的静态类型是 Number 的某个子类」，但 **不知道具体是哪一个** 。假如允许 `list.add(1)`，而实际容器是 `List<Double>`，就会把一个 Integer 塞进 Double 列表，运行时炸掉。所以编译器在 **编译期直接禁止写** （除 `null` 外）。

### 2. 为什么 `List<? super Integer>` 能 add Integer，但 get 出来是 Object？

> **答** ：下界是 Integer，实际容器可能是 `List<Integer>`、`List<Number>` 甚至 `List<Object>`。无论哪一种， **Integer 都是它们的子类，放进去一定安全** ，所以 `add` 允许。
>
> 但 `get` 时，编译器只知道「元素类型是 Integer 的某个父类」，具体是 `Number` 还是 `Object` 不确定。为了不破坏类型安全，只能取 **最保守的公共上界 `Object`** ，所以 `list.get(0)` 的静态类型是 `Object`，得强转才能当 Integer 用。

### 3. PECS 原则（用自己的话说）

> Producer Extends, Consumer Super ——如果一个方法只是从集合里往外取数据（生产），就用 `? extends T`；如果只是往集合里放数据（消费），就用 `? super T`。既读又写，就别用通配符，直接用具体泛型。

---

## 三、什么场景 / 有什么坑

### 我今天踩的坑（写真实过程）

- **坑 1** ：`addIntegers(List<? super Integer>)` 一开始写成返回 `void`，但自查里要用 `after - before` 校验新增个数，改成返回 `int` 又忘了把方法注释同步改掉，导致注释和签名不一致。

  **教训** ：签名的返回值类型、注释里的说明、自查里的 `check`，三处必须一起改。
- **坑 2** ：写 `sum(List<? extends Number>)` 时想在循环里 `list.add(...)` 做累加，IDE 直接报红——`? extends` 不允许写。后来改成局部变量 `double sum = 0; for (Number n : list) sum += n.doubleValue();` 才对。

  **教训** ：`? extends` 只读，累加用的是外部变量，不是往集合里写。
- **坑 3** ：`pecsRule()` 一开始只写了中文「生产者用 extends，消费者用 super」，check 找的是英文 `contains("Producer")`，直接 FAIL。

  **教训** ：自查关键词是英文，字符串里必须带上 `Producer` / `Consumer` / `extends` 这几个原词。

### 必答追问

- `List<?>`（无界通配符）和 `List<Object>` 有什么区别？
     List<?> 表示「元素类型是某个未知类型」的列表，只能读成 Object、不能写（除 null），它可以接收 List<Integer>、List<String> 等任意 List，是 只读的通用容器参数 。List<Object> 是一个 具体的 、元素类型为 Object 的列表，可以 add 任何对象、也可以读成 Object，但它 不能 接收 List<String>（泛型不协变）。一句话：List<?> 是「什么都可能是」，List<Object> 是「确定就是 Object」。
- 为什么 `List<String>` 不能赋给 `List<Object>`？（不变性：Java 泛型没有协变）
    Java 泛型是 不变（invariant） 的：List<String> 和 List<Object> 之间没有继承关系，尽管 String 是 Object 的子类。原因是 类型安全 ：如果允许这个赋值，就能往 List<String> 里塞 Object（比如 Integer），从 List<String> 里取出的东西却不是 String——会破坏类型系统。数组（String[] 是 Object[] 的子类）是协变的，所以数组存在类似的运行时错误风险（ArrayStoreException），这也是泛型设计时特意避开数组协变的原因。
- `Collections.copy(List<? super T> dest, List<? extends T> src)` 为什么这样设计？—— 这是 PECS 的标准答案，背下来。
    src 是 数据源 ，方法只从里面读元素（生产），所以用 ? extends T，可以接收 T 及其子类的列表；
    dest 是 目标 ，方法只往里面写元素（消费），所以用 ? super T，可以接收 T 及其父类的列表。

---

## 自测（合上笔记，能说出来再打勾）

- [ ] `? extends` / `? super` 对比表能默写
- [ ] 能说清「extends 不能 add、super 不能 get」的原因
- [ ] PECS 原则能背，并能用 Collections.copy 举例
- [ ] 能说出 Java 泛型「不变性」
- [ ] 能说清 `List<?>` 和 `List<Object>` 的区别
