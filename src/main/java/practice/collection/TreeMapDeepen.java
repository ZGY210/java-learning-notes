package practice.collection;

import java.util.Comparator;
import java.util.TreeMap;

/**
 * TreeMap 源码靶场（2026-11-23）
 *
 * ============================ 一点背景 ============================
 * HashMap 你从「会写」练到了「会讲」（HashMapDeepen 8/8）。
 * TreeMap 的源码比 HashMap 短，但它底下的东西更硬：**红黑树**。
 *
 * 黑马 215、216 讲的就是 TreeMap 内部 Entry 的结构 + 红黑树的五条性质 + 左旋右旋。
 * 这个靶场把那几个抽象的东西变成 4 个能跑的方法：
 *   1. 为什么 TreeMap 查找是 O(log n)      → 用二分查找步数证明
 *   2. 为什么 TreeMap 完全不用 equals      → 造一个 equals 永远为 true 的 key
 *   3. 为什么 TreeMap 不能放 null key      → 直接跑一遍看抛什么异常
 *   4. 红黑树靠什么维持平衡                 → 手写一次左旋，验证中序遍历不变
 *
 * 现在：PASS 0 / FAIL 8
 * 目标：PASS 8 / FAIL 0
 */
public class TreeMapDeepen {

    // ==================================================================
    // TODO 1
    // 在有序数组 sorted 里二分查找 target，返回「比较了几次」。
    // 找不到返回 -1。
    //
    // 为什么练这个：TreeMap 的查找根本不是「树」，本质就是二分 ——
    //   红黑树保证树高 O(log n)，等价于「每次砍一半」。
    //   你在 16 个元素的数组里查，最多比 5 次；1024 个元素最多 11 次。
    //   这就是 TreeMap.get() 是 O(log n) 的全部原因。
    //
    // 提示：lo=0, hi=len-1，while(lo<=hi)，每次 mid=(lo+hi)>>>1，count++。
    // ==================================================================
    static int binarySearchSteps(int[] sorted, int target) {
        throw new UnsupportedOperationException("TODO 1");
    }

    // ==================================================================
    // TODO 2（今天最反直觉的一题）
    // 下面我给了你一个类 EvilKey：
    //   - equals 永远返回 true（所有 EvilKey 互相「相等」）
    //   - hashCode 恒为 1（所有 EvilKey 哈希相同）
    //   - 但 compareTo 是按 id 比的，id 不同就是不相等
    //
    // 你的任务：往一个 TreeMap<EvilKey,String> 里放 3 个 id 不同的 EvilKey，
    //           返回这个 map 的 size()。
    //
    // 如果你返回 3 —— 说明 TreeMap 完全不看 equals 和 hashCode，只看 compareTo。
    // 如果你返回 1 —— 你把 HashMap 的行为错套过来了。
    //
    // 这题答对，你就永远不会在面试里说错「TreeMap 靠什么去重」。
    // ==================================================================
    static int treeMapUsesCompareTo() {
        throw new UnsupportedOperationException("TODO 2");
    }

    // ==================================================================
    // TODO 3
    // 返回一个 boolean[2]：
    //   [0] = 往「自然排序」的 TreeMap 里 put(null, "x") 会不会抛 NPE（抛了返回 true）
    //   [1] = 往「带 Comparator.nullsFirst 的 TreeMap」里 put(null, "x") 会不会成功（成功返回 true）
    //
    // 提示：用 try/catch 把异常吃掉，别让测试崩。
    //   自然排序时 TreeMap 要调 key.compareTo(...) 来决定往左走还是往右走 ——
    //   null 调不了任何方法，所以 NPE。这是 TreeMap 和 HashMap 最大的行为差异之一。
    // ==================================================================
    static boolean[] treeMapNullKey() {
        throw new UnsupportedOperationException("TODO 3");
    }

    // ==================================================================
    // TODO 4（红黑树的基础动作，手写一次就懂）
    // 实现左旋：以 root 为支点，把 root 的右孩子提上来当新的根。
    //
    //   旋转前：        旋转后：
    //      root            y
    //      /  \           / \
    //     a    y      root   c
    //         / \      /  \
    //        b   c    a    b
    //
    // 三行指针：
    //   root.right = y.left;
    //   y.left = root;
    //   return y;              ← 新根是 y
    //
    // 你要写的就这三行（判空也顺手做一下）。
    // 为什么红黑树需要旋转？因为插入/删除会破坏「红黑」的颜色规则，
    // 旋转是「不改中序遍历顺序、只改树形」的唯一手段 —— 这就是下面第 7、8 条断言要验的事。
    // ==================================================================
    static TreeNode rotateLeft(TreeNode root) {
        throw new UnsupportedOperationException("TODO 4");
    }

    /** 给你用的右旋，写完左旋自己对照一下就知道两边是对称的。 */
    static TreeNode rotateRight(TreeNode root) {
        TreeNode x = root.left;
        root.left = x.right;
        x.right = root;
        return x;
    }

    // ==================================================================
    // ============================ 基础设施 ============================
    // 以下代码给你用，不用改。
    // ==================================================================

    static class EvilKey implements Comparable<EvilKey> {
        final int id;

        EvilKey(int id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            return true;   // 所有 EvilKey 互相相等
        }

        @Override
        public int hashCode() {
            return 1;      // 所有 EvilKey 哈希相同
        }

        @Override
        public int compareTo(EvilKey o) {
            return Integer.compare(this.id, o.id);   // 只有这里能区分
        }
    }

    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int val) {
            this.val = val;
        }
    }

    /** 中序遍历，拼成 "1,2,3" 这种字符串。 */
    static String inorder(TreeNode root) {
        StringBuilder sb = new StringBuilder();
        inorderHelper(root, sb);
        return sb.length() > 0 ? sb.substring(0, sb.length() - 1) : "";
    }

    private static void inorderHelper(TreeNode node, StringBuilder sb) {
        if (node == null) {
            return;
        }
        inorderHelper(node.left, sb);
        sb.append(node.val).append(',');
        inorderHelper(node.right, sb);
    }

    /** 造一棵 2 为根、1 和 3 为左右孩子的小树。 */
    static TreeNode sampleTree() {
        TreeNode root = new TreeNode(2);
        root.left = new TreeNode(1);
        root.right = new TreeNode(3);
        return root;
    }

    // ==================================================================
    // ============================ 自测区 ============================
    // 不要修改。
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
        System.out.println("=== TreeMapDeepen 自测 ===");

        int[] a16 = new int[16];
        for (int i = 0; i < 16; i++) {
            a16[i] = i;
        }
        int[] a1024 = new int[1024];
        for (int i = 0; i < 1024; i++) {
            a1024[i] = i;
        }

        check("1. 16 个元素查 0：比较次数在 1~5 之间（2^5=32）", () -> {
            int s = binarySearchSteps(a16, 0);
            return s >= 1 && s <= 5;
        });

        check("2. 16 个元素查 99（不存在）：返回 -1", () -> binarySearchSteps(a16, 99) == -1);

        check("3. 1024 个元素查 777：比较次数 ≤ 11（2^11=2048）", () -> {
            int s = binarySearchSteps(a1024, 777);
            return s >= 1 && s <= 11;
        });

        check("4. TreeMap 放 3 个 equals 全等但 compareTo 不同的 key → size==3", () ->
                treeMapUsesCompareTo() == 3);

        check("5. 自然排序 TreeMap put(null) → 抛 NPE", () -> {
            boolean[] r = treeMapNullKey();
            return r != null && r.length == 2 && r[0];
        });

        check("6. 带 Comparator.nullsFirst 的 TreeMap put(null) → 成功", () -> {
            boolean[] r = treeMapNullKey();
            return r != null && r.length == 2 && r[1];
        });

        check("7. rotateLeft(2) 后根变成 3，中序仍是 1,2,3", () -> {
            TreeNode newRoot = rotateLeft(sampleTree());
            return newRoot != null && newRoot.val == 3 && "1,2,3".equals(inorder(newRoot));
        });

        check("8. 左旋再右旋能还原：根回到 2，中序仍是 1,2,3", () -> {
            TreeNode back = rotateRight(rotateLeft(sampleTree()));
            return back != null && back.val == 2 && "1,2,3".equals(inorder(back));
        });

        System.out.println();
        System.out.println("结果：PASS " + pass + " / FAIL " + fail);

        if (fail == 0) {
            System.out.println();
            System.out.println("全绿。你现在能讲清这四件事了：");
            System.out.println("  1. TreeMap.get() 为什么是 O(log n)？");
            System.out.println("  2. TreeMap 靠 compareTo 去重，不是 equals/hashCode —— 为什么？");
            System.out.println("  3. 为什么 TreeMap 自然排序下不能放 null key，加 Comparator 就行？");
            System.out.println("  4. 旋转为什么不改变中序遍历？红黑树靠它维持平衡？");
        }
    }
}
