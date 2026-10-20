package api;

/**
 * BoxingDemo —— 包装类自动装箱 / 拆箱实验（学员自写版）
 *
 * 对应黑马 174 包装类（38:09，标注「必」）。
 * 这一节面试极易被偷袭，全是「== 」判断。每个实验先写预期，再看结果，最后把原因写进注释。
 *
 * ⚠️ 本文件是教练生成的骨架（2026-10-19）。2 个方法体全部由你实现。
 */
public class BoxingDemo {

    private BoxingDemo() {
        // 工具类不实例化
    }

    /**
     * 判断 n 是否落在 Integer 缓存的区间。
     * Integer.valueOf(n) 在缓存区间内返回同一个对象，之外 new。
     *
     * <pre>
     * isCached(100)   == true    （缓存区间内）
     * isCached(200)   == false   （超出缓存）
     * isCached(-128)  == true
     * isCached(127)   == true
     * isCached(128)   == false
     * </pre>
     */
    public static boolean isCached(int n) {
        return (n>=-128&&n<=127);
    }

    /**
     * 演示拆箱 NPE：输入一个可能为 null 的 Integer，尝试转成 int。
     * 若 n 为 null，返回 true（抛了 NPE）；否则正常拆箱返回 false。
     */
    public static boolean unboxNullThrows(Integer n) {
        try {
            int v = n.intValue();   // 拆箱核心：n.intValue()
            return v == 0;          // 走到这说明没抛 NPE，正常返回
        } catch (NullPointerException e) {
            return true;            // 说明确实抛了 NPE
        }
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

        // 缓存区间：先自己写下预期，再看结果
        check("1. isCached(100)=true", isCached(100), true);
        check("2. isCached(200)=false", isCached(200), false);
        check("3. isCached(-128)=true", isCached(-128), true);
        check("4. isCached(128)=false", isCached(128), false);

        // 拆箱 NPE
        check("5. unboxNull(null)=抛NPE", unboxNullThrows(null), true);
        check("6. unboxNull(100)=无NPE", unboxNullThrows(100), false);

        // == 原理（直观演示，结果都是 boolean，直接 assert）
        Integer a1 = 100, b1 = 100;
        check("7. 100==100（缓存同一对象）", a1 == b1, true);
        Integer a2 = 200, b2 = 200;
        check("8. 200==200（超缓存不同对象）", a2 == b2, false);

        System.out.println();
        System.out.println(failed == 0
                ? "===== ALL GREEN（8/8）—— 可以提交 ====="
                : "===== 还有 " + failed + " 条没过 =====");
    }
}