package practice.ds;

/**
 * MyDoublyLinkedList —— 手写双向链表（学员自写版）
 *
 * 对应黑马 190 LinkedList 源码分析。JDK LinkedList 底层就是双向链表，
 * 每个节点有 prev/next 两个指针，链表类还持有 first/last 两个引用。
 * 对比你昨天手写的单链表：多存 prev 和 last，换来「尾插 O(1)」「倒着找」。
 *
 * ⚠️ 本文件是教练生成的骨架（2026-10-30）。6 个方法体全部由你实现。
 */
public class MyDoublyLinkedList<E> {

    /** 双向节点：值 + 前驱 + 后继。 */
    private static class Node<E> {
        E val;
        Node<E> prev;
        Node<E> next;

        Node(E val) {
            this.val = val;
        }
    }

    /** 头节点引用；空链表时为 null。 */
    private Node<E> first;

    /** 尾节点引用；空链表时为 null。 */
    private Node<E> last;

    /** 元素个数。 */
    private int size;

    public MyDoublyLinkedList() {
        // 初始：first = null, last = null, size = 0
    }

    /**
     * 头插：插到链表最前面。
     * <pre>
     * addFirst("a"); addFirst("b") → [b, a]
     * </pre>
     * 提示：新节点的 next 指向旧 first；旧 first 的 prev 指向新节点；first 更新为新节点。
     * 如果链表本来为空，last 也要指向新节点。
     */
    public void addFirst(E e) {
        throw new UnsupportedOperationException("TODO 1");
    }

    /**
     * 尾插：追加到链表末尾。有了 last 引用，这一步是 O(1)，不用从头走。
     * <pre>
     * addLast("a"); addLast("b") → [a, b]
     * </pre>
     */
    public void addLast(E e) {
        throw new UnsupportedOperationException("TODO 2");
    }

    /**
     * 删头，返回被删元素；空链表抛 NoSuchElementException。
     * <pre>
     * [a, b].removeFirst() == "a" → [b]
     * </pre>
     * 提示：删到只剩一个时，last 也要置 null。
     */
    public E removeFirst() {
        throw new UnsupportedOperationException("TODO 3");
    }

    /**
     * 删尾，返回被删元素；空链表抛 NoSuchElementException。
     * <pre>
     * [a, b].removeLast() == "b" → [a]
     * </pre>
     */
    public E removeLast() {
        throw new UnsupportedOperationException("TODO 4");
    }

    /**
     * 按下标读取。JDK 优化：从离 index 近的那头开始走。
     * <pre>
     * [a, b, c].get(1) == "b"
     * </pre>
     * 提示：index < size/2 时从 first 走 index 步；否则从 last 倒着走 size-1-index 步。
     * 越界抛 IndexOutOfBoundsException。
     */
    public E get(int index) {
        throw new UnsupportedOperationException("TODO 5");
    }

    /** @return 元素个数 */
    public int size() {
        throw new UnsupportedOperationException("TODO 6");
    }

    /** 便于肉眼验证：把链表打印成 [a, b, c] 的形式。 */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Node<E> cur = first;
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
    // 下面是教练写好的 8 条自查，**不要改**。全部 PASS 才算今天过关。
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
        System.out.println("===== MyDoublyLinkedList 8 条自查 =====\n");

        MyDoublyLinkedList<String> list = new MyDoublyLinkedList<>();
        list.addLast("a");
        list.addLast("b");
        list.addLast("c");
        check("1. 尾插后 size==3", list.size(), 3);
        check("2. toString", list.toString(), "[a, b, c]");

        list.addFirst("x");                         // [x, a, b, c]
        check("3. 头插", list.toString(), "[x, a, b, c]");

        check("4. get(2) 从头走", list.get(2), "b");
        check("5. get(3) 从尾走", list.get(3), "c");

        check("6. removeFirst 返回 x", list.removeFirst(), "x");   // [a, b, c]
        check("7. removeLast 返回 c", list.removeLast(), "c");     // [a, b]
        check("8. 剩 [a, b]", list.toString(), "[a, b]");

        System.out.println();
        System.out.println(failed == 0
                ? "===== ALL GREEN（8/8）—— 可以提交 ====="
                : "===== 还有 " + failed + " 条没过 =====");
    }
}
