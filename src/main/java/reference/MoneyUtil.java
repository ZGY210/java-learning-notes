package reference;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 金额计算工具 —— 对应黑马第 161 集（BigInteger）与第 162 集（BigDecimal）。
 *
 * <p>这个类存在的唯一理由：{@code double} 不能用来表示钱。
 * 你在 main 里会亲眼看到 {@code 0.1 + 0.2} 的结果不是 0.3。</p>
 *
 * <p>三条铁律（面试也会问）：
 * <ol>
 *   <li>new BigDecimal 一定要用【字符串】构造器，不能用 double 构造器；</li>
 *   <li>除法必须显式指定 scale 和 RoundingMode，否则除不尽会抛 ArithmeticException；</li>
 *   <li>比较大小时用 compareTo，不要用 equals —— 1.0 和 1.00 的 equals 是 false。</li>
 * </ol>
 *
 * <p>⚠️ 本文件是【参考答案】，由教练预先写好。规则：**先关掉它、从空白文件自己写一遍**，
 * 写完再回来对照 —— 看懂的 ≠ 会写的 ≠ 面试能讲的。对照时重点看第 3 条铁律的
 * 代码落地位置（{@link #yuanToFen(double)} 里那个 {@code Double.toString}）。</p>
 *
 * @author 教练（参考答案，2026-10-14）
 */
public class MoneyUtil {

    /** 金额固定保留 2 位小数。金额的精度不该由调用方随心情决定。 */
    private static final int SCALE = 2;

    /** 默认舍入模式：四舍五入。银行场景会换成 HALF_EVEN，这里先按最常见的业务来。 */
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    /**
     * 分 → 元。
     *
     * @param fen 以分表示的整数金额，例如 1234 表示 12.34 元
     * @return 以元表示、保留 2 位小数的金额
     */
    public static BigDecimal fenToYuan(long fen) {
        // 用 BigDecimal.valueOf(fen) 而不是 new BigDecimal(fen)：
        // 对 long 来说两者等价，但 valueOf 是官方推荐的入口，语义更清楚。
        return BigDecimal.valueOf(fen)
                .divide(BigDecimal.valueOf(100), SCALE, ROUNDING_MODE);
    }

    /**
     * 元（字符串形式）→ 分。
     * 入参用 String 而不是 double，就是为了逼调用方交出精确的文本表示。
     *
     * @param yuanStr 以元为单位的金额字符串，例如 "12.34"
     * @return 以分表示的整数金额
     */
    public static long yuanToFen(String yuanStr) {
        if (yuanStr == null || yuanStr.trim().isEmpty()) {
            throw new IllegalArgumentException("金额不能为空");
        }
        BigDecimal yuan = new BigDecimal(yuanStr.trim())
                .setScale(SCALE, ROUNDING_MODE);
        return yuan.multiply(BigDecimal.valueOf(100)).longValueExact();
    }

    /**
     * 元（数字形式）→ 分。
     * 特意写成接受 double，是为了在内部把它转成字符串再构造 BigDecimal ——
     * 这是「必须用字符串构造器」这条铁律在代码里的落地方式。
     *
     * @param yuan 以元为单位的金额
     * @return 以分表示的整数金额
     */
    public static long yuanToFen(double yuan) {
        if (Double.isNaN(yuan) || Double.isInfinite(yuan)) {
            throw new IllegalArgumentException("金额必须是有限数字：" + yuan);
        }
        // 关键点：Double.toString(12.34) 会得到 "12.34"，而 new BigDecimal(12.34)
        // 会得到 12.339999999999999857891452847979962825775146484375，两者天差地别。
        return yuanToFen(Double.toString(yuan));
    }

    /**
     * 金额除法。
     * 除不尽时必须由调用方指定精度，否则 BigDecimal 会直接抛异常 —— 这是它的保护机制。
     *
     * @param dividend 被除数
     * @param divisor  除数
     * @param scale    保留几位小数
     * @return 保留 scale 位小数的商
     */
    public static BigDecimal divide(BigDecimal dividend, BigDecimal divisor, int scale) {
        if (dividend == null || divisor == null) {
            throw new IllegalArgumentException("被除数和除数都不能为 null");
        }
        if (divisor.compareTo(BigDecimal.ZERO) == 0) {
            throw new ArithmeticException("除数不能为 0");
        }
        if (scale < 0) {
            throw new IllegalArgumentException("精度不能为负数：" + scale);
        }
        return dividend.divide(divisor, scale, ROUNDING_MODE);
    }

    /**
     * 金额除法（默认保留 2 位，用于算单价、折扣这类场景）。
     */
    public static BigDecimal divide(BigDecimal dividend, BigDecimal divisor) {
        return divide(dividend, divisor, SCALE);
    }

    /**
     * 金额加法，返回保留 2 位小数的结果。
     */
    public static BigDecimal add(BigDecimal a, BigDecimal b) {
        return a.add(b).setScale(SCALE, ROUNDING_MODE);
    }

    /**
     * 比较两个金额是否相等（用 compareTo，不用 equals）。
     */
    public static boolean sameAmount(BigDecimal a, BigDecimal b) {
        return a.compareTo(b) == 0;
    }

    // ==================== 自测入口：直接运行本类的 main ====================

    public static void main(String[] args) {
        System.out.println("========== 1. double 为什么不能表示钱 ==========");
        double d = 0.1 + 0.2;
        System.out.println("0.1 + 0.2 = " + d);
        System.out.println("0.1 + 0.2 == 0.3 ? " + (d == 0.3));

        // 用字符串构造器：得到精确的 0.3
        BigDecimal bd = new BigDecimal("0.1").add(new BigDecimal("0.2"));
        System.out.println("BigDecimal(\"0.1\").add(BigDecimal(\"0.2\")) = " + bd);
        System.out.println("精确比较结果 = " + sameAmount(bd, new BigDecimal("0.3")));

        // 用 double 构造器：把上面的误差原封不动搬进来（反例）
        System.out.println("反例 new BigDecimal(0.1 + 0.2) = " + new BigDecimal(d));

        System.out.println("\n========== 2. 分与元互转 ==========");
        System.out.println("fenToYuan(1234)      = " + fenToYuan(1234));
        System.out.println("fenToYuan(1)         = " + fenToYuan(1));
        System.out.println("yuanToFen(\"12.34\")   = " + yuanToFen("12.34"));
        System.out.println("yuanToFen(0.1 + 0.2) = " + yuanToFen(0.1 + 0.2));

        System.out.println("\n========== 3. 除不尽必须指定精度 ==========");
        System.out.println("10 / 3 保留 2 位 = " + divide(new BigDecimal("10"), new BigDecimal("3")));
        System.out.println("10 / 3 保留 6 位 = " + divide(new BigDecimal("10"), new BigDecimal("3"), 6));
        try {
            new BigDecimal("10").divide(new BigDecimal("3"));
        } catch (ArithmeticException e) {
            System.out.println("不指定精度时抛异常: " + e.getMessage());
        }
        try {
            divide(new BigDecimal("1"), BigDecimal.ZERO);
        } catch (ArithmeticException e) {
            System.out.println("除零时抛异常: " + e.getMessage());
        }

        System.out.println("\n========== 4. equals 与 compareTo 的差别 ==========");
        BigDecimal one = new BigDecimal("1.0");
        BigDecimal two = new BigDecimal("1.00");
        System.out.println("1.0.equals(1.00)    = " + one.equals(two));
        System.out.println("1.0.compareTo(1.00) = " + one.compareTo(two));
        System.out.println("结论：判金额相等必须用 compareTo");

        System.out.println("\n========== 5. 一个真实的业务场景：购物车分摊 ==========");
        BigDecimal total = new BigDecimal("100.00");
        int people = 3;
        BigDecimal each = divide(total, BigDecimal.valueOf(people));
        System.out.println("100 元 3 人平分，每人 = " + each + " 元");
        System.out.println("按每人 " + each + " 元收，总共收到 = " + each.multiply(BigDecimal.valueOf(people)));
        System.out.println("差额（这就是对不上账的原因）= " + total.subtract(each.multiply(BigDecimal.valueOf(people))));
    }
}
