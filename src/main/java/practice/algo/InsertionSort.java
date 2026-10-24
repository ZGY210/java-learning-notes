package practice.algo;

/**
 * InsertionSort —— 插入排序（学员自写版）
 *
 * 对应黑马 179 插入排序和递归算法（34:50）。
 * 插入排序适合「接近有序」的场景，工程里很多小数组都用它。
 * 理解它 = 在已排好的前 k 个元素里，把第 k+1 个元素插到正确位置。
 *
 * ⚠️ 本文件是教练生成的骨架（2026-10-22）。1 个方法体由你实现。
 */
public class InsertionSort {

    private InsertionSort() {
        // 工具类不实例化
    }

    /**
     * 插入排序（原地，升序）。
     * 核心：把 a[i] 拿到手里，跟前面的已排序区一个个比，找到该插的位置。
     *   for i in [1..n-1]                  （从第 2 个元素开始）
     *     记 temp = a[i]
     *     int j = i - 1
     *     while (j >= 0 && a[j] > temp) {  （前面比它大的往后挪）
     *         a[j+1] = a[j];
     *         j--;
     *     }
     *     a[j+1] = temp;                   （插到正确位置）
     */
    public static void insertionSort(int[] a) {
        if (a == null || a.length < 2) return;

        // 1. 跳过已有序的前缀
        int i = 0;
        while (i < a.length - 1 && a[i] <= a[i + 1]) {
            i++;
        }
        i++;

        // 2. 从 i 开始逐个插入
        while (i < a.length) {
            int temp = a[i];
            int j = i - 1;
            while (j >= 0 && a[j] > temp) {
                a[j + 1] = a[j];
                j--;
            }
            a[j + 1] = temp;
            i++;
        }
    }


    // ==================================================================
    // 下面是教练写好的 4 条自查（不动）。判据是：排序正确 + 能讲清过程。
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
        System.out.println("===== InsertionSort 自查 =====\n");

        int[] a1 = {5, 3, 8, 1, 9, 2, 7};
        insertionSort(a1);
        check("随机数组升序", isSorted(a1));
        System.out.print("  结果: ");
        for (int x : a1) System.out.print(x + " ");
        System.out.println();

        int[] a2 = {1, 2, 3, 4, 5};   // 已排序，应该一轮都不用换
        insertionSort(a2);
        check("已排序数组无交换", isSorted(a2));

        int[] a3 = {9, 7, 5, 3, 1};   // 逆序，最坏情况
        insertionSort(a3);
        check("逆序数组升序", isSorted(a3));

        int[] e = {};
        insertionSort(e);
        check("空数组不崩", isSorted(e));

        System.out.println();
        System.out.println(failed == 0
                ? "===== ALL GREEN —— 可以提交 ====="
                : "===== 还有 " + failed + " 条没过 =====");
    }
}