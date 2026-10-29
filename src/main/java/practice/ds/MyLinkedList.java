package practice.ds;

/**
 * MyLinkedList —— 手写单链表（学员自写版）
 *
 * 对应黑马 188 数据结构（栈、队列、数组、链表）。
 * 为什么要手写链表：LinkedList 的源码就是基于它；面试常让手写「反转链表」，
 * 不写一遍单链表，反转时指针绕来绕去必错。
 *
 * 注意：这是单链表，每个节点只有 val 和 next，没有 prev。
 *
 * ⚠️ 本文件是教练生成的骨架（2026-10-28）。8 个方法体全部由你实现。
 */
public class MyLinkedList<E> {

    /** 链表节点：值 + 指向下一个节点的引用。 */
    private static class Node<E> {
        E val;
        Node<E> next;

        Node(E val) {
            this.val = val;
        }
    }

    /** 头节点（不是虚拟头，就是真正的第一个节点）；空链表时为 null。 */
    private Node<E> head;

    /** 元素个数。 */
    private int size;

    public MyLinkedList() {
        // 初始：head = null, size = 0
    }

    /**
     * 尾插：把元素追加到链表末尾。
     * <pre>
     * list.add("a"); list.add("b"); list.add("c") → [a, b, c]
     * </pre>
     * 提示：如果 head 是 null 直接挂上；否则从头走到最后一个节点，把 next 接上。
     */
    public void add(E e) {
        throw new UnsupportedOperationException("TODO 1");
    }

    /**
     * 按下标插入：在 index 位置插入元素，原 index 及之后的元素整体后移。
     * <pre>
     * [a, b, c].add(1, "x") → [a, x, b, c]
     * </pre>
     * 提示：index 必须 0 <= index <= size；index==0 是「插到头部」的特殊情况（head 要改）；
     * 否则先走到 index-1 的节点，把新节点接在它和它原来的 next 之间。
     */
    public void add(int index, E e) {
        throw new UnsupportedOperationException("TODO 2");
    }

    /**
     * 按下标读取。
     * <pre>
     * [a, b, c].get(1) == "b"
     * </pre>
     * 提示：链表没有下标，只能从头走 index 步；越界抛 IndexOutOfBoundsException。
     */
    public E get(int index) {
        throw new UnsupportedOperationException("TODO 3");
    }

    /**
     * 按下标删除，返回被删元素。
     * <pre>
     * [a, b, c].remove(1) == "b" → [a, c]
     * </pre>
     * 提示：index==0 时 head = head.next；否则走到 index-1 的节点，让它跳过 index 那个节点。
     * 越界抛 IndexOutOfBoundsException。
     */
    public E remove(int index) {
        throw new UnsupportedOperationException("TODO 4");
    }

    /** @return 元素个数 */
    public int size() {
        throw new UnsupportedOperationException("TODO 5");
    }

    /** @return 是否为空 */
    public boolean isEmpty() {
        throw new UnsupportedOperationException("TODO 6");
    }

    /**
     * 反转链表（迭代版，面试最高频手写题）。
     * <pre>
     * [a, b, c].reverse() → [c, b, a]
     * </pre>
     * 提示（三指针套路）：
     *   prev = null
     *   cur = head
     *   while (cur != null) {
     *       next = cur.next;   // 先记住下一个，别弄丢
     *       cur.next = prev;   // 掉头
     *       prev = cur;
     *       cur = next;
     *   }
     *   head = prev;
     */
    public void reverse() {
        throw new UnsupportedOperationException("TODO 7");
    }

    /** 便于肉眼验证：把链表打印成 [a, b, c] 的形式。 */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Node<E> cur = head;
        while (cur != null) {
            sb.append(cur.val);
            if (cur.next != null) {
                sb.append(", ");
            }
            cur = cur.next;
        }
        return sb.append("]").toString();
    }

    // ==================================================================
    // 下面是教练写好的 10 条自查，**不要改**。全部 PASS 才算今天过关。
    // ==================================================================

    private static int failed = 0;

    private static void check(String name, Object actual, Object expect) {
        boolean ok = java.util.Objects.equals(actual, expect);
        if (!ok) {
            failed++;
        }
        System.out.println((ok ? "[PASS] " : "[FAIL] ") + name + " = " + actual
                + (ok ? "" : "   期望: " + expect));
    }

    public static void main(String[] args) {
        System.out.println("===== MyLinkedList 10 条自查 =====\n");

        MyLinkedList<String> list = new MyLinkedList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        check("1. 尾插后 size==3", list.size(), 3);
        check("2. toString", list.toString(), "[a, b, c]");
        check("3. get(0)", list.get(0), "a");
        check("4. get(2)", list.get(2), "c");

        list.add(1, "x");                          // [a, x, b, c]
        check("5. 中间插入", list.toString(), "[a, x, b, c]");

        check("6. remove(1) 返回被删元素", list.remove(1), "x");   // [a, b, c]
        check("7. remove(0) 删头", list.remove(0), "a");           // [b, c]
        check("8. remove(size-1) 删尾", list.remove(list.size() - 1), "c");  // [b]

        list.add("d");
        list.add("e");                             // [b, d, e]
        list.reverse();                            // [e, d, b]
        check("9. 反转后", list.toString(), "[e, d, b]");

        try {
            list.get(99);
            check("10. 越界抛异常", false, true);
        } catch (IndexOutOfBoundsException e) {
            check("10. 越界抛异常", true, true);
        }

        System.out.println();
        System.out.println(failed == 0
                ? "===== ALL GREEN（10/10）—— 可以提交 ====="
                : "===== 还有 " + failed + " 条没过 =====");
    }
}
