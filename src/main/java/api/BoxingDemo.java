package api;

/**
 * BoxingDemo —— 包装类自动装箱 / 拆箱实验（学员自写版）
 *
 * 对应黑马 174 包装类（38:09，标注「必」）。
 * 这一节面试极易被偷袭，全是「== 」判断。每个实验先写预期，再看结果，最后把原因写进注释。
 *
 * ⚠️ 本文件是教练生成的骨架（2026-10-19/20）。3 个方法体全部由你实现。
 */
public class BoxingDemo {

    // TODO 0（先到这里，做 ① 实验）：
    //    写一个 main，把下面的实验 1~4 各打印一行。先猜结果，再运行。

    // 实验 1：Integer a = 100; Integer b = 100; System.out.println(a == b);   // 你预期？
    // 实验 2：Integer a = 200; Integer b = 200; System.out.println(a == b);   // 预期？和上面一样吗？
    // 实验 3：Integer a = new Integer(100); Integer b = new Integer(100); System.out.println(a == b);  // 预期？
    // 实验 4：Integer a = 100; Integer b = 1000; System.out.println(a.equals(b));  // 预期？

    /**
     * 现象解释 · 为什么 128 和 127 不一样（Integer 缓存）。
     *
     * @return "Integer Cache = -128 ~ 127；在这区间 valueOf 返回同一对象；之外 new"
     */
    public static String whyIntegerCache(int n) {
        // 实现：Integer n 装箱时（Integer.valueOf(n)），若 -128 <= n <= 127 用缓存；否则新建
        // 这一段的精髓是：== 比的是引用地址，不是值
        throw new UnsupportedOperationException("TODO 1");
    }

    /**
     * 现象解释 · 拆箱 NPE。
     * 要求：写一个方法，输入 Integer，返回 int；若抛异常就把异常信息作为返回值的一部分返回
     */
    public static int unboxNpe(Integer n) {
        // 实现：直接 return n; 当 n 为 null 时会抛 NullPointerException（拆箱自动调 n.intValue()）
        throw new UnsupportedOperationException("TODO 2");
    }

    // ==================================================================
    // 下面是教练写好的 8 条自查，**不要改**。全部 PASS 才算今天过关。
    // ==================================================================

    private static int failed = 0;

    private static void check(String name, boolean actual, boolean expect) {
        boolean ok = actual == expect;
        if (!ok) {
            failed++;
        }
        System.out.println((ok ? "[PASS] " : "[FAIL] ") + name + " = " + actual
                + (ok ? "" : "   期望: " + expect));
    }

    public static void main(String[] args) {
        System.out.println("===== BoxingDemo 8 条自查 =====\n");

        // 缓存实验    先自己写下预期，再看结果
        check("1. 100==100 (缓存)", Boolean.FALSE.equals((Integer) 100 == (Integer) 100), true);
        check("2. 200==200 (超缓存)", Boolean.FALSE.equals((Integer) 200 == (Integer) 200), false);
        check("3. new Integer(100)==new Integer(100)", Boolean.FALSE.equals(new Integer(100) == new Integer(100)), false);
        check("4. equals 比较值", new Integer(100).equals(new Integer(100)), true);

        // 拆箱
        try {
            unboxNpe(null);
            check("5. unbox(null) 抛 NPE", false, true);
        } catch (NullPointerException e) {
            check("5. unbox(null) 抛 NPE", true, true);
        }

        // 自动装箱/拆箱混合
        Integer big = 300;
        check("6. big.intValue()", big.intValue(), 300);
        int sum = big + 1;  // 自动拆箱
        check("7. sum=301", sum, 301);

        // toString
        check("8. Integer.toString", Integer.toString(256), "256");

        System.out.println();
        System.out.println(failed == 0
                ? "===== ALL GREEN（8/8）—— 可以提交 ====="
                : "===== 还有 " + failed + " 条没过 =====");
    }
}