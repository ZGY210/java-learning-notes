package api;


import java.util.Arrays;

/**
 * LambdaPractice —— Lambda 表达式练习（学员自写版）
 *
 * 对应黑马 182 lambda 表达式（29:08，标注「必」）。
 * Lambda 是 Java 8 的入场券，Stream/集合排序/线程里天天用。
 * 今天只练三件事：把匿名内部类改成 lambda、写几个函数式接口、用 Arrays.sort + lambda 排序。
 *
 * ⚠️ 本文件是教练生成的骨架（2026-10-23）。3 个方法体由你实现。
 */
public class LambdaPractice {

    private LambdaPractice() {
        // 工具类不实例化
    }

    /**
     * 用 lambda 把匿名内部类改成一行。
     * 原样（匿名内部类）：
     *   Runnable r = new Runnable() { @Override public void run() { System.out.println("hello"); } };
     * 改成：
     *   Runnable r = () -> System.out.println("hello");
     */
    public static Runnable makeRunnable() {
        return () -> System.out.println("hello");
    }

    /**
     * 自定义函数式接口 Calculator（加、减），用 lambda 分别实现。
     * 提示：Calculator add = (a, b) -> a + b;
     */
    public static int calculate(String op, int a, int b) {
        // op 是 "+" / "-"，返回对应结果
        Calculator add = (x, y) -> x + y;
        Calculator sub = (x, y) -> x - y;

        if ("+".equals(op)) {
            return add.calc(a, b);
        } else if ("-".equals(op)) {
            return sub.calc(a, b);
        }
        throw new IllegalArgumentException("不支持的运算符: " + op);
    }
        /**
     * 用 lambda + Arrays.sort 对 int[] 按绝对值从小到大排序。
     * 提示：Arrays.sort(arr, (x, y) -> Math.abs(x) - Math.abs(y));
     */
    public static void sortByAbs(int[] arr) {
        Integer[] boxed = Arrays.stream(arr).boxed().toArray(Integer[]::new);
        Arrays.sort(boxed, (x, y) -> Math.abs(x) - Math.abs(y));
        // 写回原数组
        for (int i = 0; i < arr.length; i++) {
            arr[i] = boxed[i];
        }
    }

    @FunctionalInterface
    interface Calculator {
        int calc(int a, int b);
    }

    // ==================================================================
    // 下面是教练写好的 6 条自查（不动）。
    // ==================================================================

    private static int failed = 0;

    private static void check(String name, boolean ok) {
        if (!ok) failed++;
        System.out.println((ok ? "[PASS] " : "[FAIL] ") + name);
    }

    public static void main(String[] args) {
        System.out.println("===== LambdaPractice 自查 =====\n");

        // 实验 1：Runnable lambda
        try {
            makeRunnable().run();
            check("1. lambda Runnable 能跑", true);
        } catch (Throwable e) {
            check("1. lambda Runnable 能跑", false);
        }

        // 实验 2：自定义函数式接口
        check("2. calculate(+)", calculate("+", 3, 5) == 8);
        check("3. calculate(-)", calculate("-", 3, 5) == -2);

        // 实验 3：lambda + Arrays.sort
        int[] nums = {-5, 2, -9, 1, 3};
        sortByAbs(nums);
        check("4. 按绝对值排序", nums[0] == 1 && nums[4] == -9);

        // 实验 4：Comparator 的 lambda 简写
        String[] words = {"banana", "apple", "fig", "grape"};
        java.util.Arrays.sort(words, (s1, s2) -> s1.length() - s2.length());
        check("5. 按长度排序", words[0].equals("fig") && words[3].equals("banana"));

        // 实验 5：forEach + lambda
        java.util.List<String> list = java.util.Arrays.asList("a", "b", "c");
        StringBuilder sb = new StringBuilder();
        list.forEach(s -> sb.append(s));
        check("6. forEach + lambda", sb.toString().equals("abc"));

        System.out.println();
        System.out.println(failed == 0
                ? "===== ALL GREEN —— 可以提交 ====="
                : "===== 还有 " + failed + " 条没过 =====");
    }
}