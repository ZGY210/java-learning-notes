package api;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * MoneyUtil —— 金额计算工具（学员自写版）
 *
 * 对应黑马 162 集：BigDecimal。
 * 对照答案在 {@code src/main/java/reference/MoneyUtil.java}
 * —— 卡住时只允许看它的「方法签名 + 注释」，**别打开方法体**。
 *
 * ⚠️ 本文件是教练生成的骨架（2026-10-15）。下面 5 个方法体全部由你实现。
 *    判断「这个模块学没学」的判据只有一条：
 *        git log --oneline -- src/main/java/api/MoneyUtil.java
 *    在 10/15–10/18 之间必须出现你自己的提交，否则等于没学。
 *
 * ============================================================
 * 动手之前，先把三条铁律用你自己的话写在这里（别抄，抄了记不住）：
 *   铁律 1：
 *   铁律 2：
 *   铁律 3：
 * ============================================================
 */
public class MoneyUtil {

    /** 金额统一保留 2 位小数 */
    private static final int SCALE = 2;

    /** 舍入模式：四舍五入 */
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    /**
     * 分 → 元。
     * <pre>
     * fenToYuan(1234) == 12.34
     * fenToYuan(1)    == 0.01
     * </pre>
     * 提示：long 转 BigDecimal 用 BigDecimal.valueOf(long)，它内部走的是字符串路径。
     */
    public static BigDecimal fenToYuan(long fen) {
        return BigDecimal.valueOf(fen).divide(BigDecimal.valueOf(100), SCALE, ROUNDING_MODE);
    }

    /**
     * 元（字符串）→ 分。
     * <pre>
     * yuanToFen("12.34") == 1234L
     * </pre>
     * 两个要求：① 入参可能带首尾空格，先 trim；② 必须用**字符串构造器**。
     * 提示：setScale(SCALE, ROUNDING_MODE) → multiply(BigDecimal.valueOf(100)) → longValueExact()
     */
    public static long yuanToFen(String yuanStr) {
        BigDecimal yuan = new BigDecimal(yuanStr.trim()).setScale(SCALE, ROUNDING_MODE);
        return yuan.multiply(BigDecimal.valueOf(100)).longValueExact();
    }

    /**
     * 元（double）→ 分。
     * <pre>
     * yuanToFen(0.1 + 0.2) == 30L      ← 不能是 30 以外的任何值
     * </pre>
     * 坑：double 直接塞进 BigDecimal 会把这些二进制误差原样带进去。
     * 想想该先对 double 做什么，再交给上一个方法。
     */
    public static long yuanToFen(double yuan) {
        return yuanToFen(String.valueOf(yuan));
    }

    /**
     * 除法 —— 必须显式指定精度和舍入模式。
     * <pre>
     * divide(new BigDecimal("10"), new BigDecimal("3"), 2) == 3.33
     * </pre>
     */
    public static BigDecimal divide(BigDecimal a, BigDecimal b, int scale) {
        return a.divide(b, scale, ROUNDING_MODE);
    }

    /**
     * 判断两笔金额是否相等（忽略 scale 差异）。
     * <pre>
     * sameAmount(new BigDecimal("1.0"), new BigDecimal("1.00")) == true
     * </pre>
     * 坑：用 equals 比较会得到 false。想清楚为什么，再选正确的方法。
     */
    public static boolean sameAmount(BigDecimal a, BigDecimal b) {
        return a.compareTo(b) == 0;
    }

    // ==================================================================
    // 下面是教练写好的 7 条自查，**不要改**。全部 PASS 才算今天过关。
    // ==================================================================

    private static int failed = 0;

    private static void check(String name, String actual, String expect) {
        boolean ok = expect.equals(actual);
        if (!ok) {
            failed++;
        }
        System.out.println((ok ? "[PASS] " : "[FAIL] ") + name + "  = " + actual
                + (ok ? "" : "   期望: " + expect));
    }

    public static void main(String[] args) {
        System.out.println("===== MoneyUtil 7 条自查 =====\n");

        check("1. fenToYuan(1234)", fenToYuan(1234).toPlainString(), "12.34");
        check("2. fenToYuan(1)", fenToYuan(1).toPlainString(), "0.01");
        check("3. yuanToFen(\"12.34\")", String.valueOf(yuanToFen("12.34")), "1234");
        check("4. yuanToFen(0.1+0.2)", String.valueOf(yuanToFen(0.1 + 0.2)), "30");
        check("5. divide(10, 3, 2)",
                divide(new BigDecimal("10"), new BigDecimal("3"), 2).toPlainString(), "3.33");
        check("6. sameAmount(\"1.0\", \"1.00\")",
                String.valueOf(sameAmount(new BigDecimal("1.0"), new BigDecimal("1.00"))), "true");

        // 第 7 条：不指定精度的除法**【必然】**抛异常，这一条是「坑」的实证
        String actual7;
        try {
            new BigDecimal("10").divide(new BigDecimal("3"));
            actual7 = "没抛异常";
        } catch (ArithmeticException e) {
            actual7 = "抛 ArithmeticException";
            System.out.println("      异常原文: " + e.getMessage());
        } catch (Throwable t) {
            actual7 = "抛 " + t.getClass().getSimpleName();
        }
        check("7. 10/3 不指定精度", actual7, "抛 ArithmeticException");

        System.out.println();
        System.out.println(failed == 0
                ? "===== ALL GREEN（7/7）—— 可以提交 ====="
                : "===== 还有 " + failed + " 条没过 =====");
    }
}
