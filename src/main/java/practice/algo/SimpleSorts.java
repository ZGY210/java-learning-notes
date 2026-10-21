package practice.algo;

/**
 * SimpleSorts —— 冒泡排序 + 选择排序（学员自写版）
 *
 * 对应黑马 178 冒泡排序和选择排序（28:41）。
 * 为什么要手写？因为面试会让手写，而且要看懂"为什么是这样"。
 * 两个都要印出每轮结果，用眼睛确认"最大的/最小的在慢慢归位"的过程。
 *
 * ⚠️ 本文件是教练生成的骨架（2026-10-21/22）。3 个方法体全部由你实现。
 */
public class SimpleSorts {

    private SimpleSorts() {
        // 工具类不实例化
    }

    /**
     * 冒泡排序（原地，升序）。返回 null 请改到 main 里打每轮。
     * 核心：每次把未排序区里最大的"冒"到末尾。
     *   for i in [0..n-2]                （外层：要排 n-1 轮）
     *     for j in [0..n-1-i-1]          （内层：未排序区，j 逐对比较）
     *       如果 a[j] > a[j+1] 就交换
     */
    public static void bubbleSort(int[] a) {
        throw new UnsupportedOperationException("TODO 1");
    }

    /**
     * 选择排序（原地，升序）。
     * 核心：每轮找未排序区里最小的下标，跟当前头交换。
     *   for i in [0..n-2]                （轮数）
     *     记 minIdx = i
     *     for j in [i+1..n-1]            （在未排序区找最小）
     *       如果 a[j] < a[minIdx] 就 minIdx = j
     *     如果 minIdx != i，交换
     */
    public static void selectionSort(int[] a) {
        throw new UnsupportedOperationException("TODO 2");
    }

    // ==================================================================
    // 下面是教练写好的 6 条自查（不动），但为了「印出每轮」，请你在 main 里
    // 自己加 for 循环打印每轮的数组。判据是：排完序正确 + 你能讲清过程。
    // ==================================================================

    private static int failed = 0;

    private static void check(String name, boolean ok) {
        if (!ok) failed++;
        System.out.println((ok ? "[PASS] " : "[FAIL] ") + name + (ok ? "" : "  <- 排序结果不对"));
    }

    private static boolean isSorted(int[] a) {
        for (int i = 1; i < a.length; i++) if (a[i-1] > a[i]) return false;
        return true;
    }

    public static void main(String[] args) {
        System.out.println("===== SimpleSorts 自查 =====\n");

        int[] b1 = {5, 3, 8, 1, 9, 2, 7};
        bubbleSort(b1);
        check("冒泡排序升序正确", isSorted(b1));
        System.out.print("  冒泡结果: ");
        for (int x : b1) System.out.print(x + " ");
        System.out.println();

        int[] s1 = {5, 3, 8, 1, 9, 2, 7};
        selectionSort(s1);
        check("选择排序升序正确", isSorted(s1));
        System.out.print("  选择结果: ");
        for (int x : s1) System.out.print(x + " ");
        System.out.println();

        // 空数组 / 单元素边界
        int[] e = {};
        bubbleSort(e); check("空数组不崩", isSorted(e));
        int[] one = {7};
        selectionSort(one); check("单元素不崩", isSorted(one));

        System.out.println();
        System.out.println(failed == 0
                ? "===== ALL GREEN —— 可以提交 ====="
                : "===== 还有 " + failed + " 条没过 =====");
    }
}