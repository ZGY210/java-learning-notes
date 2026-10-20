package practice.algo;

/**
 * BinarySearch —— 二分查找练习（学员自写版）
 *
 * 对应黑马 176 基本、二分、插值和斐波那契查找（46:05）。
 * 二分是面试手写题里最常考的一种，必须"能默写、不被边界条件绊倒"。
 *
 * 今天的重点是**边界**——这题 90% 的人都栽在边界上。
 * 要求用**左闭右闭** [left, right] 的写法，因为在有序数组里它最直观。
 *
 * ⚠️ 本文件是教练生成的骨架（2026-10-20/21）。2 个方法体全部由你实现。
 */
public class BinarySearch {

    private BinarySearch() {
        // 工具类不实例化
    }

    /**
     * 在有序数组 arr 里查找 target，返回它的下标；找不到返回 -1。
     * 采用【左闭右闭】[left, right] 写法。
     *
     * <pre>
     * binarySearch([1,3,5,7,9], 5) == 2
     * binarySearch([1,3,5,7,9], 4) == -1
     * binarySearch([1,3,5,7,9], 1) == 0    （首）
     * binarySearch([1,3,5,7,9], 9) == 4    （尾）
     * </pre>
     *
     * 三件套（背下来，别抄）：
     *   int left = 0, right = arr.length - 1;      // 左闭右闭：right 是最后一个有效下标
     *   while (left <= right) {                    // 闭区间用 <=，因为 left==right 时仍要查
     *       int mid = left + (right - left) / 2;   // 防溢出的 mid
     *       if      (arr[mid] == target) return mid;
     *       else if (arr[mid] <  target) left  = mid + 1;
     *       else                       right = mid - 1;
     *   }
     *   return -1;
     */
    public static int binarySearch(int[] arr, int target) {
        throw new UnsupportedOperationException("TODO 1");
    }

    /**
     * 查找【第一个 ≥ target】的下标（左边界变体）。找不到（所有数都 < target）返回 arr.length。
     *
     * <pre>
     * lowerBound([1,3,5,7,9], 6) == 3    （第一个 ≥6 的是 7，下标 3）
     * lowerBound([1,3,5,7,9], 0) == 0
     * lowerBound([1,3,5,7,9], 10) == 5   （没有 ≥10 的，返回 length）
     * </pre>
     *
     * 思路和标准二分几乎一样，只有两处不同：
     *   命中时不 return，而是收缩右界（right = mid - 1），把「答案」往左压；
     *   循环结束后 left 就是第一个 ≥ target 的位置。
     */
    public static int lowerBound(int[] arr, int target) {
        throw new UnsupportedOperationException("TODO 2");
    }

    // ==================================================================
    // 下面是教练写好的 8 条自查，**不要改**。全部 PASS 才算今天过关。
    // ==================================================================

    private static int failed = 0;

    private static void check(String name, int actual, int expect) {
        boolean ok = actual == expect;
        if (!ok) {
            failed++;
        }
        System.out.println((ok ? "[PASS] " : "[FAIL] ") + name + " = " + actual
                + (ok ? "" : "   期望: " + expect));
    }

    public static void main(String[] args) {
        System.out.println("===== BinarySearch 8 条自查 =====\n");
        int[] a = {1, 3, 5, 7, 9};
        int[] b = {1, 3, 3, 3, 9};   // 有重复

        // 标准二分
        check("1. binarySearch(...,5)=2", binarySearch(a, 5), 2);
        check("2. binarySearch(...,4)=-1", binarySearch(a, 4), -1);
        check("3. binarySearch(...,1)=0(首)", binarySearch(a, 1), 0);
        check("4. binarySearch(...,9)=4(尾)", binarySearch(a, 9), 4);
        check("5. binarySearch([])=-1", binarySearch(new int[]{}, 5), -1);

        // 左边界
        check("6. lowerBound(...,6)=3", lowerBound(a, 6), 3);
        check("7. lowerBound(...,9)=4(尾部命中)", lowerBound(a, 9), 4);
        check("8. lowerBound(...,10)=5(超界)", lowerBound(a, 10), 5);

        System.out.println();
        System.out.println(failed == 0
                ? "===== ALL GREEN（8/8）—— 可以提交 ====="
                : "===== 还有 " + failed + " 条没过 =====");
    }
}