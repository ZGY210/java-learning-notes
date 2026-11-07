package practice.ds;

/**
 * MyBinarySearchTree —— 手写二叉搜索树 BST（学员自写版）
 *
 * 对应黑马 193 数据结构（二叉树、二叉查找树、平衡二叉树）。
 * BST 是后面 TreeMap、红黑树的地基：左小右大，中序遍历天然升序。
 *
 * ⚠️ 本文件是教练生成的骨架（2026-11-02）。3 个 TODO 由你实现。
 * 为简单起见只支持 int，不搞泛型——先把树的指针结构搞懂。
 */
public class MyBinarySearchTree {

    /** 树节点：值 + 左右子节点。 */
    private static class Node {
        int val;
        Node left;
        Node right;

        Node(int val) {
            this.val = val;
        }
    }

    /** 根节点；空树为 null。 */
    private Node root;

    /** 节点个数。 */
    private int size;

    public MyBinarySearchTree() {
        // 初始：root = null, size = 0
    }

    /**
     * TODO 1 · 插入：小的放左、大的放右。
     * 提示：递归辅助方法 insert(Node node, int val)；
     *       node 为 null 时新建节点；val < node.val 往左；val > node.val 往右；等于则忽略（去重）。
     */
    public void insert(int val) {
        root = insert(root, val);   // 根节点可能变（树为空时）
    }

    // 递归辅助方法
    private Node insert(Node node, int val) {
        if (node == null) {
            size++;
            return new Node(val);   // 走到空位，建新节点
        }
        if (val < node.val) {
            node.left = insert(node.left, val);     // 往左
        } else if (val > node.val) {
            node.right = insert(node.right, val);   // 往右
        }
        // val == node.val：什么都不做，直接返回原节点 → 去重
        return node;
    }

    /**
     * TODO 2 · 查找：是否存在 val。
     * 提示：递归 contains(Node node, int val)；node 为 null 返回 false；
     *       val == node.val 返回 true；否则按大小去左/右子树找。
     */
    public boolean contains(int val) {
        return contains(root, val);   // 从根开始查
    }

    // 递归辅助方法
    private boolean contains(Node node, int val) {
        if (node == null) {
            return false;             // 走到空位，说明没找到
        }
        if (val == node.val) {
            return true;              // 找到了
        }
        return val < node.val
                ? contains(node.left, val)    // 小 → 左边找
                : contains(node.right, val);  // 大 → 右边找
    }


    /**
     * TODO 3 · 中序遍历：左 → 根 → 右，返回升序字符串（逗号分隔）。
     * 提示：递归 inOrder(Node node, StringBuilder sb)；先左、再当前、再右。
     */
    public String inOrder() {
        StringBuilder sb = new StringBuilder();
        inOrder(root, sb);
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    private void inOrder(Node node, StringBuilder sb) {
        if (node == null) {
            return;                    // 空节点直接返回，不返回 ""
        }
        inOrder(node.left, sb);        // ① 左
        sb.append(node.val).append(",");  // ② 根
        inOrder(node.right, sb);
    }
    /** @return 节点个数 */
    public int size() {
        return size;
    }

    /** @return 是否为空 */
    public boolean isEmpty() {
        return size == 0;
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
        System.out.println("===== MyBinarySearchTree 8 条自查 =====\n");

        MyBinarySearchTree bst = new MyBinarySearchTree();
        bst.insert(5);
        bst.insert(3);
        bst.insert(8);
        bst.insert(1);
        bst.insert(4);
        bst.insert(7);
        bst.insert(9);

        check("1. 插入 7 个后 size==7", bst.size(), 7);
        check("2. 中序遍历天然升序", bst.inOrder(), "1,3,4,5,7,8,9");
        check("3. contains(4)=true", bst.contains(4), true);
        check("4. contains(9)=true", bst.contains(9), true);
        check("5. contains(6)=false", bst.contains(6), false);
        check("6. contains(0)=false", bst.contains(0), false);
        check("7. 空树 isEmpty", new MyBinarySearchTree().isEmpty(), true);
        check("8. 空树 contains 不崩", new MyBinarySearchTree().contains(1), false);

        System.out.println();
        System.out.println(failed == 0
                ? "===== ALL GREEN（8/8）—— 可以提交 ====="
                : "===== 还有 " + failed + " 条没过 =====");
    }
}
