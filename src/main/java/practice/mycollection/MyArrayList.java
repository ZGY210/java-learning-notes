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
 * 构造器只赋引用，第一次 add 进 grow 前按引用比较决定开 10 还是按 minCapacity 开。</p>
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
 *   <li>JDK 的迭代器有 remove() / forEachRemaining()，本类只做 hasNext/next。</li>
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
     *
     * @return 按 Collection 接口约定，永远返回 true
     */
    public boolean add(E e) {
        ensureCapacity(size + 1);
        elementData[size++] = e;
        modCount++;   // 结构变了
        return true;
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
     * <p>关键只有一句：把后面的元素整体往前挪一格，然后把最后一格置 null 帮助 GC。</p>
     */
    @SuppressWarnings("unchecked")
    public E remove(int index) {
        checkIndex(index);
        E old = (E) elementData[index];
        int moved = size - index - 1;
        if (moved > 0) {
            // 参数含义：从 index+1 开始搬，搬到 index 处，一共搬 moved 个
            System.arraycopy(elementData, index + 1, elementData, index, moved);
        }
        elementData[--size] = null;
        modCount++;   // 结构变了
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
     * 保证数组至少能装下 minCapacity 个元素（add 的扩容入口）。
     * <p>JDK 的结构是两层：ensureCapacity/ensureCapacityInternal 判断「要不要扩」，
     * grow 负责「扩多大」。这里合并成一个方法，但保留了 JDK 最关键的判断顺序——
     * <b>先看是不是 DEFAULTCAPACITY_EMPTY_ELEMENTDATA，再看容量够不够</b>。</p>
     */
    private void ensureCapacity(int minCapacity) {
        if (elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
            // 无参构造后第一次 add：把 minCapacity 抬到至少 DEFAULT_CAPACITY，
            // 直接开 10 格，而不是开 1 格下次又扩 —— 这就是「首次扩容按默认容量」。
            minCapacity = Math.max(DEFAULT_CAPACITY, minCapacity);
        }
        if (minCapacity > elementData.length) {
            grow(minCapacity);
        }
    }

    /**
     * 真正的扩容动作：按 minCapacity 开新数组，把旧元素搬过去。
     * <p>扩容策略：JDK 原版写法 oldCapacity + (oldCapacity >> 1)，即约 1.5 倍。
     * 之所以不是 2 倍（HashMap 才是 2 倍），是为了在「空间浪费」与「扩容次数」之间取平衡。</p>
     */
    private void grow(int minCapacity) {
        int oldCapacity = elementData.length;
        int newCapacity = oldCapacity + (oldCapacity >> 1);   // 1.5 倍
        if (newCapacity < minCapacity) {
            newCapacity = minCapacity;   // 一次加很多（如 addAll）时，1.5 倍可能还不够，按需补齐
        }
        elementData = Arrays.copyOf(elementData, newCapacity);
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
     * 迭代器实现，带 fail-fast。
     * <p>三个要点：cursor 指向「下一个要返回的元素」；expectedModCount 在创建时定格；
     * 每次 next() 先对比 modCount 与 expectedModCount。</p>
     */
    private class Itr implements Iterator<E> {

        /** 下一个要返回的元素下标，从 0 开始。 */
        private int cursor;

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
            return (E) elementData[cursor++];
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
    }
}
