package practice.mycollection;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Arrays;

/**
 * 手写版 ArrayList —— 只实现 D1 要求的 7 个方法。
 * <p>
 * 与 JDK 的真实 ArrayList 相比，这里故意少了一样东西（留给 D2）：
 * modCount / expectedModCount：遍历过程中被修改结构就 fail-fast 抛 ConcurrentModificationException。
 * 目前的实测战绩是 17 项 PASS / 1 项 FAIL，失败的唯一一项就是它。
 */
public class MyArrayList<E> implements Iterable<E> {

    /** 默认初始容量，和 JDK 保持一致。private 表示只有本类能用；static 表示属于类而不是属于某个对象。 */
    private static final int DEFAULT_CAPACITY = 10;

    /** 真正存数据的数组。声明成 Object[] 而不是 E[]，是因为 Java 的泛型数组不能直接 new（E[] 不可以）。 */
    private Object[] elementData;

    /** 当前元素个数。它和 elementData.length 不是一回事：数组通常会留有空位。 */
    private int size;

    /**
     * 无参构造：什么都不做，数组先保持 null（懒初始化）。
     * 好处：new MyArrayList<>() 不占内存，第一次 add 时才真正申请数组。
     */
    public MyArrayList() {
        // 故意留空：elementData 此时是 null
    }

    /**
     * 指定初始容量。
     *
     * @param initialCapacity 期望的初始容量
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
     * @return 按 Collection 接口约定，add 永远返回 true
     */
    public boolean add(E e) {
        ensureCapacity(size + 1);
        elementData[size] = e;
        size++;
        return true;
    }

    /**
     * 按下标读取。
     *
     * @param index 下标，从 0 开始
     * @return 该位置的元素
     */
    @SuppressWarnings("unchecked")
    public E get(int index) {
        checkIndex(index);
        return (E) elementData[index];
    }

    /**
     * 按下标覆盖，并返回被覆盖的旧值。
     *
     * @return 被覆盖的旧元素
     */
    @SuppressWarnings("unchecked")
    public E set(int index, E element) {
        checkIndex(index);
        E old = (E) elementData[index];
        elementData[index] = element;
        return old;
    }

    /**
     * 删除下标 index 处的元素，并返回它。
     * <p>
     * 关键只有一句：把后面的元素整体往前挪一格。
     * D1 原本把这里留白，但验收脚本 MyArrayListTest 里调用了 remove，
     * 不写它就编不过，所以这里先补上"能跑通"的版本；
     * D2 再回头对照 JDK 的 System.arraycopy 写法重写一遍。
     */
    @SuppressWarnings("unchecked")
    public E remove(int index) {
        checkIndex(index);
        E old = (E) elementData[index];
        // 把 index+1 之后的元素整体左移一位，覆盖掉要删的那个
        System.arraycopy(elementData, index + 1, elementData, index, size - index - 1);
        elementData[--size] = null;   // 最后一格置 null，帮垃圾回收
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
     * 下标合法性检查。
     * 注意用的是 size 而不是 elementData.length：后者可能更大，
     * 用长度判断会放过"数组有空位但元素不存在"的下标。
     */
    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }

    /**
     * 保证数组至少能装下 minCapacity 个元素。
     * 两种情况：数组还没建（null）、数组容量不够。
     */
    private void ensureCapacity(int minCapacity) {
        if (elementData == null) {
            // 懒初始化：至少给 DEFAULT_CAPACITY，但如果一上来就要更多，就按更大的来
            elementData = new Object[Math.max(DEFAULT_CAPACITY, minCapacity)];
            return;
        }
        if (minCapacity > elementData.length) {
            // 近似 1.5 倍扩容：JDK 原版写法是 oldCapacity + (oldCapacity >> 1)
            int newCapacity = elementData.length + (elementData.length >> 1);
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            elementData = Arrays.copyOf(elementData, newCapacity);
        }
    }

    /**
     * 返回一个迭代器。真正遍历的逻辑放在内部类 Itr 里，
     * 这就是"迭代器模式"：调用方只管 hasNext/next，不需要知道底层是数组还是链表。
     */
    @Override
    public Iterator<E> iterator() {
        return new Itr();
    }

    /** 迭代器实现。内部类可以直接访问外部类的 elementData / size。 */
    private class Itr implements Iterator<E> {

        /** cursor 是"下一个要返回的元素"的下标，从 0 开始。 */
        private int cursor;

        @Override
        public boolean hasNext() {
            return cursor < size;
        }

        @Override
        @SuppressWarnings("unchecked")
        public E next() {
            if (cursor >= size) {
                // 迭代器已经走到头了，再 next 就是调用方的错误
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
        System.out.println("size=" + list.size());
        System.out.println("get(0)=" + list.get(0));
        System.out.println("set(1, \"c\") 返回的旧值=" + list.set(1, "c"));
        for (String s : list) {
            System.out.println("遍历到：" + s);
        }
    }
}
