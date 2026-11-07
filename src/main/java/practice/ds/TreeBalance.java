package practice.ds;

/**
 * TreeBalance —— 平衡检查实验（学员自写版）
 *
 * 对应黑马 194 平衡二叉树旋转机制 + 195 红黑树。
 * 今天不写新树，而是用代码回答一个问题：BST 为什么会退化？「平衡」到底指什么？
 * 答案就是这两个方法：height（高度）和 isBalanced（是否平衡）。
 *
 * ⚠️ 本文件是教练生成的骨架（2026-11-03）。2 个 TODO 由你实现。
 */
public class TreeBalance {

    /** 树节点。 */
    private static class Node {
        int val;
        Node left;
        Node right;

        Node(int val) {
            this.val = val;
        }
    }

    /** 构建一棵平衡的 BST：按 5,3,8,1,4,7,9 插入。 */
    private static Node buildBalanced() {
        int[] vals = {5, 3, 8, 1, 4, 7, 9};
        Node root = null;
        for (int v : vals) {
            root = insert(root, v);
        }
        return root;
    }

    /** 构建一棵退化链：按 1,2,3,4,5 顺序插入。 */
    private static Node buildDegenerate() {
        int[] vals = {1, 2, 3, 4, 5};
        Node root = null;
        for (int v : vals) {
            root = insert(root, v);
        }
        return root;
    }

    private static Node insert(Node node, int val) {
        if (node == null) {
            return new Node(val);
        }
        if (val < node.val) {
            node.left = insert(node.left, val);
        } else if (val > node.val) {
            node.right = insert(node.right, val);
        }
        return node;
    }

    /**
     * TODO 1 · 树的高度：最长路径的边数。
     * 提示：空节点高度为 -1（AVL 的标准写法，这样单节点高度 = 0）；
     *       height(node) = max(height(left), height(right)) + 1。
     */
    public static int height(Node node) {
        throw new UnsupportedOperationException("TODO 1");
    }

    /**
     * TODO 2 · 是否平衡：左右子树高度差 ≤ 1，且左右子树各自也平衡。
     * 提示：空节点 → true；
     *       |height(left) - height(right)| > 1 → false；
     *       否则递归判断左右子树。
     */
    public static boolean isBalanced(Node node) {
        throw new UnsupportedOperationException("TODO 2");
    }

    // ==================================================================
    // 下面是教练写好的 6 条自查，**不要改**。全部 PASS 才算今天过关。
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
        System.out.println("===== TreeBalance 6 条自查 =====\n");

        Node balanced = buildBalanced();
        Node degenerate = buildDegenerate();

        check("1. 空树高度 -1", height(null), -1);
        check("2. 单节点高度 0", height(new Node(42)), 0);
        check("3. 平衡树高度 2", height(balanced), 2);          // 5,3,8,1,4,7,9 → 高 2
        check("4. 退化链高度 4", height(degenerate), 4);        // 1,2,3,4,5 → 高 4
        check("5. 平衡树 isBalanced=true", isBalanced(balanced), true);
        check("6. 退化链 isBalanced=false", isBalanced(degenerate), false);

        System.out.println();
        System.out.println(failed == 0
                ? "===== ALL GREEN（6/6）—— 可以提交 ====="
                : "===== 还有 " + failed + " 条没过 =====");
    }
}
