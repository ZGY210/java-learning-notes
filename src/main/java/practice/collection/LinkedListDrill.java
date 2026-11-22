package practice.collection;

import java.util.ArrayList;
import java.util.List;

/**
 * 双向链表指针靶场（2026-11-21）
 *
 * ============================ 为什么要做这个 ============================
 * 你 11/20 说：LRU 那题「只写得出 class Node，addFirst 和 remove 写不出来」。
 *
 * 这不是 LRU 没学会，这是**双向链表的指针操作**这一层动作没形成肌肉记忆。
 * 就像你不会因为「不懂算法」而写不出快排，你是「swap 写不熟」。
 *
 * 所以今天不碰 LRU 的思路，只练 4 个动作 + 1 个组合动作。
 * 把这 5 个方法写熟，LRU 的第 2 步和第 4 步就变成了「调这几个方法」。
 *
 * ============================ 结构约定 ============================
 *   head(哑) <-> n1 <-> n2 <-> n3 <-> tail(哑)
 *
 * —— 永远有两个哑节点，永远不判 null。
 * 这是面试写链表题最省事的一种写法：所有插入都在两个节点「之间」做。
 * （另一种写法是不用哑节点，每次判 head==null 和 node.prev==null。
 *   你现在只需要掌握哑节点这一种，另一种等 11/22 再试。）
 */
public class LinkedListDrill {

    static class Node {
        int val;
        Node prev;
        Node next;

        Node(int val) {
            this.val = val;
        }

        @Override
        public String toString() {
            return String.valueOf(val);
        }
    }

    // 哑头、哑尾。注意：它们不存业务数据，val 是哨兵值。
    private final Node head = new Node(-1);
    private final Node tail = new Node(-1);
    private int size = 0;

    /** 构造时就把它俩接上，这一步我给你写好了 —— 这就是「空表」的样子。 */
    public LinkedListDrill() {
        head.next = tail;
        tail.prev = head;
    }

    // ==================================================================
    // TODO 1（最基础，必须闭眼写对）
    // 在头部插入一个新节点。也就是插在 head 和「原来的第一个节点」之间。
    //
    // 一共 4 行指针赋值，缺一行就断链。想清楚顺序：
    //   先让新节点记住自己的前后，再让两边的邻居认识它。
    //   顺序反了会怎样？（这题答不出来说明还没真懂 —— 写进 notes/16 的问题里）
    // ==================================================================
    void addFirst(int v) {
        Node newNode = new Node(v);
        Node cur = head.next;
        newNode.next = cur;
        newNode.prev = head;
        cur.prev = newNode;
        head.next=newNode;
        size++;
    }

    // ==================================================================
    // TODO 2（今天最重要的一题）
    // 把一个已经在链表里的节点 n 摘掉。
    //
    // 关键点：**不需要判 n 是不是头或尾**，因为哑节点存在，
    // 所以 n.prev 和 n.next 一定不为 null。这就是哑节点的价值。
    // 另外：摘掉之后 size 要处理。
    // ==================================================================
    void remove(Node n) {
        Node prev = n.prev;
        Node next = n.next;
        n.prev = null;
        n.next = null;
        prev.next = next;
        next.prev = prev;
        size--;
    }

    // ==================================================================
    // TODO 3
    // 正序输出所有真实节点的值（从 head.next 走到 tail 停）。
    // 注意：循环条件是「节点 != tail」，不是「节点 != null」。
    // ==================================================================
    List<Integer> toListForward() {
       Node cur = head.next;
       List<Integer> list = new ArrayList<>();
       while (cur != tail) {
           list.add(cur.val);
           cur = cur.next;
       }
       return list;
    }

    // ==================================================================
    // TODO 4
    // 逆序输出（从 tail.prev 用 prev 往回走，走到 head 停）。
    // 这题是检验你的 prev 指针有没有接对 —— forward 对但 backward 错，
    // 说明你某个 4 行赋值里漏了 prev。
    // ==================================================================
    List<Integer> toListBackward() {
        Node cur = tail.prev;
        List<Integer> list = new ArrayList<>();
        while (cur != head) {
            list.add(cur.val);
            cur = cur.prev;
        }
        return list;
    }

    // ==================================================================
    // TODO 5（组合动作 —— 这就是 LRU 淘汰逻辑的雏形）
    // 从头部往后找第一个 val == v 的节点，调 remove 删掉，返回 true；
    // 找不到返回 false。
    //
    // 想一想：LRU 淘汰尾部节点时，只需要 tail.prev 和 remove(tail.prev)，
    // 那么为什么 LRU 还要额外存一个 int key 字段？（昨天那道题的第一问）
    // ==================================================================
    boolean removeValue(int v) {
       Node cur = head.next;
       while (cur != tail) {
           if (cur.val==v) {
               remove(cur);
               return true;
           }
           cur = cur.next;
       }
       return false;
    }

    /** size 由 add/remove 维护，你只要保证 addFirst 和 remove 都改对了它。 */
    int size() {
        return size;
    }

    // ==================================================================
    // ============================ 自测区 ============================
    // 下面是 10 条断言。**不要修改**，写完 TODO 直接跑。
    // 当前预期：PASS 0 / FAIL 10（每个 TODO 都会抛 UnsupportedOperationException）
    // 目标：PASS 10 / FAIL 0
    // ==================================================================

    static int pass = 0;
    static int fail = 0;

    static void check(String name, java.util.function.BooleanSupplier body) {
        boolean ok;
        try {
            ok = body.getAsBoolean();
        } catch (Throwable t) {
            fail++;
            System.out.println("  FAIL  " + name + "   → " + t.getClass().getSimpleName() + ": " + t.getMessage());
            return;
        }
        if (ok) {
            pass++;
            System.out.println("  PASS  " + name);
        } else {
            fail++;
            System.out.println("  FAIL  " + name);
        }
    }

    public static void main(String[] args) {
        System.out.println("=== LinkedListDrill 自测 ===");

        check("1. 空表：size==0 且正序为空", () -> {
            LinkedListDrill d = new LinkedListDrill();
            return d.size() == 0 && d.toListForward().isEmpty();
        });

        check("2. addFirst(1,2,3) 后正序 == [3, 2, 1]", () -> {
            LinkedListDrill d = new LinkedListDrill();
            d.addFirst(1);
            d.addFirst(2);
            d.addFirst(3);
            return d.toListForward().equals(List.of(3, 2, 1));
        });

        check("3. 同上，逆序 == [1, 2, 3]（检验 prev 有没有接对）", () -> {
            LinkedListDrill d = new LinkedListDrill();
            d.addFirst(1);
            d.addFirst(2);
            d.addFirst(3);
            return d.toListBackward().equals(List.of(1, 2, 3));
        });

        check("4. removeValue(2) 删中间 == true 且剩 [3, 1]", () -> {
            LinkedListDrill d = new LinkedListDrill();
            d.addFirst(1);
            d.addFirst(2);
            d.addFirst(3);
            boolean removed = d.removeValue(2);
            return removed && d.toListForward().equals(List.of(3, 1));
        });

        check("5. 再删一次 2 == false（不存在）", () -> {
            LinkedListDrill d = new LinkedListDrill();
            d.addFirst(1);
            d.addFirst(2);
            d.addFirst(3);
            d.removeValue(2);
            return !d.removeValue(2);
        });

        check("6. 删头节点 3 == true 且剩 [1]", () -> {
            LinkedListDrill d = new LinkedListDrill();
            d.addFirst(1);
            d.addFirst(2);
            d.addFirst(3);
            d.removeValue(2);
            boolean removed = d.removeValue(3);
            return removed && d.toListForward().equals(List.of(1));
        });

        check("7. 删尾节点 1 == true 且表空", () -> {
            LinkedListDrill d = new LinkedListDrill();
            d.addFirst(1);
            d.addFirst(2);
            d.addFirst(3);
            d.removeValue(2);
            d.removeValue(3);
            boolean removed = d.removeValue(1);
            return removed && d.size() == 0 && d.toListForward().isEmpty();
        });

        check("8. 删空后再 addFirst(9)：正序逆序都 == [9]", () -> {
            LinkedListDrill d = new LinkedListDrill();
            d.addFirst(1);
            d.removeValue(1);
            d.addFirst(9);
            return d.toListForward().equals(List.of(9)) && d.toListBackward().equals(List.of(9));
        });

        check("9. addFirst 100 次后 size==100 且头元素是 100", () -> {
            LinkedListDrill d = new LinkedListDrill();
            for (int i = 1; i <= 100; i++) {
                d.addFirst(i);
            }
            return d.size() == 100 && d.toListForward().get(0) == 100;
        });

        check("10. 交替增删 100 轮后 size 与内容都正确", () -> {
            LinkedListDrill d = new LinkedListDrill();
            for (int i = 0; i < 100; i++) {
                d.addFirst(i);
                if (i % 2 == 0) {
                    d.removeValue(i);
                }
            }
            // 剩下的应该是所有奇数 i（升序，因为每次 addFirst 到最前）
            List<Integer> expected = new ArrayList<>();
            for (int i = 99; i >= 1; i -= 2) {
                expected.add(i);
            }
            return d.size() == expected.size() && d.toListForward().equals(expected);
        });

        System.out.println();
        System.out.println("结果：PASS " + pass + " / FAIL " + fail);

        if (fail == 0) {
            System.out.println();
            System.out.println("全绿。现在回到 leetcode/0146-LRU缓存-第二遍闭卷.md 的第 2 步：");
            System.out.println("  addFirst(node) 就是你 TODO 1 的泛化版（参数从 int 变成 Node）");
            System.out.println("  remove(node)   就是你 TODO 2，一个字都不用改");
            System.out.println("你会发现 LRU 的链表部分，今天已经写完了。");
        }
    }
}
