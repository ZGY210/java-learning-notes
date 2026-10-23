package practice.algo;

/**
 * QuickSort —— 快速排序（学员自写版）
 *
 * 对应黑马 180 快速排序（26:01）。
 * 快速排序是分治 + 递归的经典，面试必考手写，必须能默写 partition。
 *
 * 关键理解：partition 把数组按 pivot 分成「左边都小、右边都大」两段，
 * 然后对左右两边再递归。
 *
 * ⚠️ 本文件是教练生成的骨架（2026-10-22）。2 个方法体由你实现。
 */
public class QuickSort {

    private QuickSort() {
        // 工具类不实例化
    }

    /**
     * 快速排序（原地，升序）。
     * 接口：对 a 的 [low, high] 区间进行排序（左右都闭）。
     */
    public static void quickSort(int[] a, int low, int high) {
        if (low >= high) return;

        // TODO 1：调用 partition 得到划分点位置
        // int p = partition(a, low, high);

        // TODO 2：递归左右两段
        // quickSort(a, low, p - 1);
        // quickSort(a, p + 1, high);

        throw new UnsupportedOperationException("TODO 1 和 2");
    }

    /**
     * partition（核心中的核心）：选 pivot（最后一个元素），
     * 把比它小的放到左边，比它大的放右边，返回 pivot 的最终下标。
     *
     * <pre>
     * int pivot = a[high];
     * int i = low - 1;              // i 记录「已知比 pivot 小的最右位置」
     * for (int j = low; j < high; j++) {
     *     if (a[j] < pivot) {
     *         i++;
     *         swap(a, i, j);
     *     }
     * }
     * swap(a, i + 1, high);
     * return i + 1;
     * </pre>
     */
    public static int partition(int[] a, int low, int high) {
        throw new UnsupportedOperationException("TODO partition");
    }

    private static void swap(int[] a, int i, int j) {
        int t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

    // ==================================================================
    // 下面是教练写好的 4 条自查（不动）。判据是：排序正确 + 能默写 partition。
    // ==================================================================

    private static int failed = 0;

    private static void check(String name, boolean ok) {
        if (!ok) failed++;
        System.out.println((ok ? "[PASS] " : "[FAIL] ") + name);
    }

    private static boolean isSorted(int[] a) {
        for (int i = 1; i < a.length; i++) if (a[i-1] > a[i]) return false;
        return true;
    }

    public static void main(String[] args) {
        System.out.println("===== QuickSort 自查 =====\n");

        int[] a1 = {5, 3, 8, 1, 9, 2, 7};
        quickSort(a1, 0, a1.length - 1);
        check("随机数组升序", isSorted(a1));
        System.out.print("  结果: ");
        for (int x : a1) System.out.print(x + " ");
        System.out.println();

        int[] a2 = {1, 2, 3, 4, 5};
        quickSort(a2, 0, a2.length - 1);
        check("已排序数组", isSorted(a2));

        int[] a3 = {9, 7, 5, 3, 1};
        quickSort(a3, 0, a3.length - 1);
        check("逆序数组升序", isSorted(a3));

        int[] e = {42};
        quickSort(e, 0, 0);
        check("单元素不崩", isSorted(e));

        System.out.println();
        System.out.println(failed == 0
                ? "===== ALL GREEN —— 可以提交 ====="
                : "===== 还有 " + failed + " 条没过 =====");
    }
}