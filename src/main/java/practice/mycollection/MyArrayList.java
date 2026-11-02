package practice.mycollection;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 手写版 ArrayList —— 对照 JDK 源码练习，含 modCount 的 fail-fast 机制。
 *
 * <p>已实现（D1）：1.5 倍扩容、get / set / add / remove / size / isEmpty。</p>
 *
 * <p>D3 改造（2026-10-29）：懒初始化对齐 JDK —— 不再用 null，也不用「构造即开数组」，
 * 而是两个共享空数组常量 {@code EMPTY_ELEMENTDATA} / {@code DEFAULTCAPACITY_EMPTY_ELEMENTDATA}，
 * 构造器只赋引用，第一次 add 进 grow 前按引用比较决定开 10 还是按 minCapacity 开。
 * 迭代器补上 remove()（lastRet 三步善后）。</p>
 *
 * <p>D4 源码精读（2026-11-02）三个方向：
 * ① modCount++ 移到公开方法开头（add/remove 先记账再干活，grow 内部不动 modCount）；
 * ② 补 MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8 软上限 + hugeCapacity 兜底 + 溢出安全的
 *   newCapacity（旧写法在 oldCapacity 超 14.3 亿时 1.5 倍会溢出成负数，静默退化为每次 +1 格）；
 * ③ remove 的 elementData[--size] = null 注释补全「游离对象 / loitering object」的 GC 原理。</p>
 *
 * <p>本次补上（D2 核心）：<b>modCount 与 expectedModCount</b>。
 * 这是 JDK 集合最容易被面试问到、也最容易写漏的一处设计：
 * 迭代器创建时记住当时的 modCount，每次 next() 前对比一次，
 * 一旦发现结构被外部改动，立刻抛 {@link ConcurrentModificationException}，
 * 从而把「遍历到一半数据错乱」这种极难排查的 bug 变成一眼可见的异常。</p>
 *
 * <p>与 JDK 真实实现的差异（知道就好，不必照抄）：
 * <ul>
 *   <li>JDK 用 {@code System.arraycopy} 之外的 add(index,e) 元素右移逻辑，本类只实现尾部 add；</li>
 *   <li>JDK 的 remove 还会把空间缩容（trimToSize 才做），本类不缩；</li>
 *   <li>JDK 的迭代器还有 forEachRemaining()，本类做到 hasNext/next/remove。</li>
 * </ul>
 *
 * @param <E> 元素类型
 * @author 学员自写（2026-10-14）
 */
public class MyArrayList<E> implements Iterable<E> {

    /** 默认初始容量，与 JDK 保持一致。 */
    private static final int DEFAULT_CAPACITY = 10;

    /**
     * 共享的空数组，给 new MyArrayList<>(0) 这种「明确说了不要容量」的实例用。
     * <p>所有空实例共用这一个数组对象，不为每个实例单独 new —— 省内存，
     * 而且用 == 引用比较就能判断「是不是那个空数组」，比 null 检查便宜。</p>
     */
    private static final Object[] EMPTY_ELEMENTDATA = {};

    /**
     * 共享的空数组，给无参构造的实例用。
     * <p>它和 EMPTY_ELEMENTDATA 内容一样（都是长度 0），但必须是两个不同的对象：
     * 第一次 add 时靠引用比较区分——是它就开 DEFAULT_CAPACITY(10) 格，
     * 是 EMPTY_ELEMENTDATA 就按需最小扩。JDK 注释原话：
     * "distinguish this from EMPTY_ELEMENTDATA to know how much to inflate when first element is added"。</p>
     */
    private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};

    /**
     * 真正存数据的数组。
     * 声明成 Object[] 而不是 E[]，因为 Java 的泛型数组不能直接 new（E[] 是编译错误）。
     * <p>注意：改造之后它<b>永远不为 null</b>——构造器至少会赋一个共享空数组，
     * 所以所有用到它的方法都不需要再做 null 判断（这正是 JDK 不用 null 做懒初始化的原因）。</p>
     */
    private Object[] elementData;

    /** 当前元素个数。注意它和 elementData.length 不是一回事：数组通常留有空位。 */
    private int size;

    /**
     * 结构被修改的次数（structural modification）。
     * <p>定义：凡是改变了 size、或让元素位置发生移动的操作，都要 +1。
     * 只覆盖 set（值变但 size 不变）不算结构修改，所以 set 不动 modCount。</p>
     */
    private int modCount;

    /**
     * 无参构造：不分配数组，只把引用指向共享的 DEFAULTCAPACITY_EMPTY_ELEMENTDATA。
     * <p>对比之前两版的做法：第一版保持 null，第二版立刻 new Object[initialCapacity]。
     * JDK 的方式是「空数组常量 + 引用赋值」：new 一个空列表几乎零成本（不碰堆），
     * 又不用像 null 方案那样在 ensureCapacity/trimToSize/capacity() 里到处判 null。</p>
     */
    public MyArrayList() {
        this.elementData = DEFAULTCAPACITY_EMPTY_ELEMENTDATA;
    }

    /**
     * 指定初始容量。
     * <p>三种分支与 JDK 完全一致：正数才真的开数组；0 用共享空数组
     * （开 0 长度数组没有意义，还要多占一个对象头）；负数抛 IllegalArgumentException。</p>
     *
     * @param initialCapacity 期望初始容量，不能为负
     */
    public MyArrayList(int initialCapacity) {
        if (initialCapacity > 0) {
            this.elementData = new Object[initialCapacity];
        } else if (initialCapacity == 0) {
            this.elementData = EMPTY_ELEMENTDATA;
        } else {
            throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
        }
    }

    /**
     * 在末尾追加一个元素。
     * <p><b>对照 JDK 修正了 modCount++ 的位置（方向 1）</b>：
     * JDK 21 的 add 是 {@code modCount++; add(e, elementData, size);}——先 +1，
     * 再进「判断要不要 grow → elementData[s] = e → size = s + 1」的私有小方法；
     * 也就是说 modCount++ 放在<b>公开方法这一层、且在一切实际操作之前</b>，
     * 扩容（grow）内部反而不动 modCount。我之前放在赋值之后，语义上不算错
     * （没有代码会在两者之间观测 modCount），但顺序对齐 JDK 有个实际好处：
     * 万一 add 中途抛异常（比如 grow 抛 OutOfMemoryError），modCount 已经 +1，
     * 在途迭代器会 fail-fast，不会带着「结构可能已坏」的状态继续被信任。</p>
     *
     * @return 按 Collection 接口约定，永远返回 true
     */
    public boolean add(E e) {
        modCount++;   // 结构将变，JDK 的顺序：先记账再干活
        add(e, elementData, size);
        return true;
    }

    /**
     * add 的私有实现，与 JDK 同构。
     * <p>JDK 把它单独拆出来的原因写在源码注释里：让 add(E) 的字节码小于 35 字节
     * （-XX:MaxInlineSize 默认值），这样在 C1 编译的循环里 add(E) 能被内联。
     * 我暂时不做这层性能考量，但保留同样的结构——扩容判断只看
     * {@code s == elementData.length}（满了才 grow），比每次进 ensureCapacity
     * 比较 minCapacity 少一层调用。</p>
     */
    private void add(E e, Object[] elementData, int s) {
        if (s == elementData.length) {
            elementData = grow(s + 1);
        }
        elementData[s] = e;
        size = s + 1;
    }

    /**
     * 按下标读取。
     */
    @SuppressWarnings("unchecked")
    public E get(int index) {
        checkIndex(index);
        return (E) elementData[index];
    }

    /**
     * 按下标覆盖，并返回被覆盖的旧值。
     * <p>注意：set 只换值、不改变结构，所以<b>不</b>增加 modCount，
     * 因此在 for-each 里调用 set 不会触发 ConcurrentModificationException —— 这是符合 JDK 行为的。</p>
     */
    @SuppressWarnings("unchecked")
    public E set(int index, E element) {
        checkIndex(index);
        E old = (E) elementData[index];
        elementData[index] = element;
        return old;
    }

    /**
     * 删除下标 index 处的元素并返回它。
     * <p>关键只有一句：把后面的元素整体往前挪一格，然后把最后一格置 null。</p>
     * <p><b>为什么必须置空（方向 3）</b>：remove 之后 size 减 1，最后一格在「逻辑上」
     * 已经没有元素了，但数组槽位里还挂着被删对象的引用——这叫 <b>loitering object（游离对象）</b>。
     * 只要数组还引用着它，GC 就认为它可达、永远不回收；如果那是个 100MB 的大对象，
     * 删掉之后内存却纹丝不动，就是典型的内存泄漏。置 null 是把引用掐断，让 GC 能收走它。
     * 注意只置「逻辑边界外的格子」：下标 &lt; size 的位置马上会被覆盖或本来就有效，不用置。</p>
     * <p>modCount++ 的位置对齐 JDK：JDK 在 fastRemove 的<b>开头</b>就 +1，
     * 先记账再搬元素，理由同 add——中途出异常也不留下「结构可疑但计数没变」的状态。</p>
     */
    @SuppressWarnings("unchecked")
    public E remove(int index) {
        checkIndex(index);
        modCount++;   // 结构将变，先记账（JDK fastRemove 的顺序）
        E old = (E) elementData[index];
        int moved = size - index - 1;
        if (moved > 0) {
            // 参数含义：从 index+1 开始搬，搬到 index 处，一共搬 moved 个
            System.arraycopy(elementData, index + 1, elementData, index, moved);
        }
        elementData[--size] = null;   // 掐断游离对象的引用，帮助 GC
        return old;
    }

    /** @return 元素个数（不是数组长度） */
    public int size() {
        return size;
    }

    /** @return 是否为空 */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * 让底层数组缩到刚好装下当前元素。
     * 场景：一次性 add 了几万条又删到剩几条，此时数组还占着几万格内存。
     * <p>对照 JDK 修正了两处（我之前注释里写反了）：
     * JDK 的 trimToSize 会先 modCount++（换了数组对象，在途迭代器必须失效）；
     * 且 size==0 时直接复用共享的 EMPTY_ELEMENTDATA，不 new 新数组。</p>
     */
    public void trimToSize() {
        modCount++;   // JDK 在这里也 +1：数组引用被替换，旧迭代器持有的视图已不可信
        if (size < elementData.length) {
            elementData = (size == 0)
                    ? EMPTY_ELEMENTDATA
                    : Arrays.copyOf(elementData, size);
        }
    }

    /** @return 仅供测试观察用：当前底层数组长度 */
    int capacity() {
        return elementData.length;   // elementData 改造后永不为 null，不用再判了
    }

    /**
     * 下标合法性检查。
     * <p>用的是 size 而不是 elementData.length：后者可能更大，
     * 用长度判断会放过「数组有空位但元素不存在」的下标。</p>
     */
    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }

    /**
     * 数组最大软上限（方向 2）：Integer.MAX_VALUE - 8。
     * <p><b>为什么是 -8</b>：有些 JVM（如 HotSpot）对数组长度有自己的实现上限，
     * 申请接近 Integer.MAX_VALUE 的数组会抛
     * {@code OutOfMemoryError: Requested array size exceeds VM limit}——即使堆还够。
     * 实际限制取决于 JVM 实现（比如对象头大小，HotSpot 的对象头常见就是 8 字节量级），
     * 所以 JDK 保守地取 MAX_VALUE - 8，确保低于任何可能遇到的实现限制。
     * 这是「软」上限：真有需求超过它时还是允许超（见 hugeCapacity）。</p>
     */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    /**
     * 扩容入口：保证数组至少能装下 minCapacity 个元素。
     * <p>JDK 21 里「要不要扩」的判断在 add(e, elementData, s)（s == length 才 grow），
     * 「首次开 10」的判断在 grow 内部（看 elementData 是不是 DEFAULTCAPACITY_EMPTY_ELEMENTDATA）。
     * 我之前把首次判断放在单独的 ensureCapacity 里用 null 分支做，上一轮已改成空数组常量，
     * 这一轮把结构也对齐：ensureCapacity 不再需要，逻辑全部收进 grow。</p>
     */
    private Object[] grow(int minCapacity) {
        int oldCapacity = elementData.length;
        if (oldCapacity > 0 || elementData != DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
            // 正常扩容路径：按 1.5 倍（oldCapacity >> 1）优先，不够 minCapacity 就按需补齐
            int newCapacity = newCapacity(oldCapacity, minCapacity - oldCapacity, oldCapacity >> 1);
            return elementData = Arrays.copyOf(elementData, newCapacity);
        } else {
            // 无参构造后第一次 add（oldCapacity==0 且是 DEFAULTCAPACITY 空数组）：直接开 10
            return elementData = new Object[Math.max(DEFAULT_CAPACITY, minCapacity)];
        }
    }

    /**
     * 溢出安全的新容量计算（方向 2 补的核心）。
     * <p>我之前的写法 {@code oldCapacity + (oldCapacity >> 1)} 有个隐藏 bug：
     * 当 oldCapacity 超过约 14.3 亿时，1.5 倍的结果<b>溢出成负数</b>，
     * 负数又必然小于 minCapacity，于是「兜底取 minCapacity」把问题掩盖了——
     * 扩容退化成每次只加 1 格，性能悄悄变成 O(n²)，还不报错。
     * JDK 的做法：先算 prefLength（允许溢出），用 {@code 0 < prefLength} 把
     * 「溢出成负数」和「0」两种情况一起挡掉，超上限的走 hugeCapacity 冷路径。</p>
     *
     * @param minGrowth  最少要加多少格（必须为正）
     * @param prefGrowth 最好加多少格（1.5 倍策略里的 oldCapacity >> 1）
     */
    private static int newCapacity(int oldCapacity, int minGrowth, int prefGrowth) {
        int prefLength = oldCapacity + Math.max(minGrowth, prefGrowth); // 可能溢出
        if (0 < prefLength && prefLength <= MAX_ARRAY_SIZE) {
            return prefLength;
        } else {
            return hugeCapacity(oldCapacity, minGrowth);   // 冷路径单独放，方便 JIT 内联热路径
        }
    }

    /**
     * 逼近数组上限时的兜底（方向 2）。JDK 叫 hugeLength/hugeCapacity，逻辑一致：
     * <ul>
     *   <li>minLength 连 int 都溢出（&lt;0）→ 抛 OutOfMemoryError，这是真装不下了；</li>
     *   <li>minLength 在软上限内 → 返回 MAX_ARRAY_SIZE，一次给到位；</li>
     *   <li>minLength 超过软上限但在 int 范围内 → 返回 minLength，
     *       让分配去碰 JVM 的真实上限（软上限只是保守估计，不是硬规则）。</li>
     * </ul>
     */
    private static int hugeCapacity(int oldCapacity, int minGrowth) {
        int minLength = oldCapacity + minGrowth;
        if (minLength < 0) { // int 溢出，说明要求的长度超过 Integer.MAX_VALUE
            throw new OutOfMemoryError(
                    "Required array length " + oldCapacity + " + " + minGrowth + " is too large");
        } else if (minLength <= MAX_ARRAY_SIZE) {
            return MAX_ARRAY_SIZE;
        } else {
            return minLength;
        }
    }

    /**
     * 返回迭代器。真正遍历的逻辑在内部类 Itr 里 —— 这就是迭代器模式：
     * 调用方只管 hasNext/next，不需要知道底层是数组还是链表。
     */
    @Override
    public Iterator<E> iterator() {
        return new Itr();
    }

    /**
     * 迭代器实现，带 fail-fast 和 remove()。
     * <p>四个要点：cursor 指向「下一个要返回的元素」；lastRet 记住「上一次返回的下标」；
     * expectedModCount 在创建时定格；每次 next()/remove() 先对比 modCount 与 expectedModCount。</p>
     */
    private class Itr implements Iterator<E> {

        /** 下一个要返回的元素下标，从 0 开始。 */
        private int cursor;

        /**
         * 上一次 next() 返回的元素下标；-1 表示还没返回过任何元素。
         * <p>remove() 靠它知道「要删的是哪个」，也靠它拒绝「还没 next 就 remove」和
         * 「连续 remove 两次」这两种非法调用。JDK 用的也是这个名字、这个初值。</p>
         */
        private int lastRet = -1;

        /** 迭代器创建时的 modCount 快照，用来检测「遍历期间结构被改」。 */
        private int expectedModCount = modCount;

        /** fail-fast 检查：结构被外部改过就立刻炸，不要带着错乱数据继续跑。 */
        private void checkForComodification() {
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException(
                        "遍历过程中集合被修改：modCount=" + modCount + ", expected=" + expectedModCount);
            }
        }

        @Override
        public boolean hasNext() {
            // hasNext 不加检查，与 JDK 一致：只有真正取元素时才校验
            return cursor < size;
        }

        @Override
        @SuppressWarnings("unchecked")
        public E next() {
            checkForComodification();
            if (cursor >= size) {
                throw new NoSuchElementException();
            }
            E value = (E) elementData[cursor];
            lastRet = cursor;   // 记住这次返回的位置，供 remove() 使用
            cursor++;
            return value;
        }

        /**
         * 删除上一次 next() 返回的那个元素。三步（与 JDK Itr.remove 结构一致）：
         * <ol>
         *   <li>lastRet < 0 → IllegalStateException：还没 next 过、或已经 remove 过一次，
         *       此时「上一个返回的元素」不存在，删无可删；</li>
         *   <li>先 checkForComodification() 确认没有外部改动，再委托给外部类的
         *       remove(lastRet)——真正搬移元素、modCount++ 的都是它；</li>
         *   <li>善后三件事：cursor = lastRet（删除点后面的元素整体左移了一格，
         *       cursor 原本指向的位置现在被后一个元素占据，退回去一格才不会跳过元素）；
         *       lastRet = -1（防连续 remove 两次）；
         *       expectedModCount = modCount（<b>关键</b>：外部类的 remove 让 modCount +1 了，
         *       这次修改是迭代器自己发起的、不是并发修改，必须重新同步快照，
         *       否则下一次 next() 的 checkForComodification 会把自己误杀）。</li>
         * </ol>
         */
        @Override
        public void remove() {
            if (lastRet < 0) {
                throw new IllegalStateException();
            }
            checkForComodification();
            try {
                MyArrayList.this.remove(lastRet);
                cursor = lastRet;
                lastRet = -1;
                expectedModCount = modCount;   // 同步！否则下次 next() 抛 CME
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }
    }

    /** 便于肉眼验证的小入口。 */
    public static void main(String[] args) {
        // ----- 验证懒初始化：构造后不开数组，第一次 add 才开 10 格 -----
        MyArrayList<String> lazy = new MyArrayList<>();
        System.out.println("add 之前底层数组长度 = " + lazy.capacity());   // 应为 0
        lazy.add("a");
        System.out.println("add(\"a\") 之后底层数组长度 = " + lazy.capacity()); // 应为 10

        // ----- 验证 new MyArrayList<>(0)：用共享空数组，第一次 add 按 minCapacity 开 -----
        MyArrayList<String> zero = new MyArrayList<>(0);
        System.out.println("new MyArrayList<>(0) 底层数组长度 = " + zero.capacity()); // 应为 0
        zero.add("a");
        System.out.println("对容量 0 的实例 add 后长度 = " + zero.capacity()); // 应为 1（不是 10）

        MyArrayList<String> list = new MyArrayList<>();
        list.add("a");
        list.add("b");
        System.out.println("size=" + list.size() + ", 底层数组长度=" + list.capacity());
        System.out.println("get(0)=" + list.get(0));
        System.out.println("set(1, \"c\") 返回的旧值=" + list.set(1, "c"));
        for (String s : list) {
            System.out.println("遍历到：" + s);
        }

        System.out.println("\n--- 扩容轨迹（初始容量 10，加到第 11 个时开始扩）---");
        MyArrayList<Integer> grow = new MyArrayList<>();
        int last = 0;
        for (int i = 0; i < 40; i++) {
            grow.add(i);
            if (grow.capacity() != last) {
                last = grow.capacity();
                System.out.println("size=" + grow.size() + " 时扩容到 " + last);
            }
        }

        System.out.println("\n--- 验收：迭代器 remove() 删掉 b，不抛异常 ---");
        MyArrayList<String> itrList = new MyArrayList<>();
        itrList.add("a");
        itrList.add("b");
        itrList.add("c");
        Iterator<String> it = itrList.iterator();
        while (it.hasNext()) {
            String s = it.next();
            if ("b".equals(s)) {
                it.remove();   // 只有迭代器自己的 remove 才不会触发 fail-fast
            }
        }
        System.out.println("删完后 size = " + itrList.size());          // 应为 2
        System.out.println("剩下的元素 = " + itrList.get(0) + ", " + itrList.get(1)); // 应为 a, c
    }
}
