package practice.mycollection;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 手写版 ArrayList —— 对照 JDK 源码练习，含 modCount 的 fail-fast 机制。
 *
 * <p>已实现（D1）：懒初始化、1.5 倍扩容、get / set / add / remove / size / isEmpty。</p>
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
     * 真正存数据的数组。
     * 声明成 Object[] 而不是 E[]，因为 Java 的泛型数组不能直接 new（E[] 是编译错误）。
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
     * 无参构造：懒初始化，elementData 先保持 null，第一次 add 时才申请数组。
     */
    public MyArrayList() {
    }

    /**
     * 指定初始容量。
     *
     * @param initialCapacity 期望初始容量，不能为负
     */
    public MyArrayList(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
        }
        this.elementData = new Object[initialCapacity];
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
     */
    public void trimToSize() {
        if (size < elementData.length) {
            elementData = Arrays.copyOf(elementData, size);
            // 缩容只是换了个数组，size 没变、元素顺序没变，但 JDK 并不在这里加 modCount，
            // 因为迭代器持有的引用会随之失效——这是 JDK 的一个历史遗留细节，知道即可。
        }
    }

    /** @return 仅供测试观察用：当前底层数组长度 */
    int capacity() {
        return elementData == null ? 0 : elementData.length;
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
     * 保证数组至少能装下 minCapacity 个元素。
     * <p>扩容策略：JDK 原版写法 oldCapacity + (oldCapacity >> 1)，即约 1.5 倍。
     * 之所以不是 2 倍（HashMap 才是 2 倍），是为了在「空间浪费」与「扩容次数」之间取平衡。</p>
     */
    private void ensureCapacity(int minCapacity) {
        if (elementData == null) {
            elementData = new Object[Math.max(DEFAULT_CAPACITY, minCapacity)];
            return;
        }
        if (minCapacity > elementData.length) {
            int newCapacity = elementData.length + (elementData.length >> 1);
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            elementData = Arrays.copyOf(elementData, newCapacity);
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
