package api;

import java.util.regex.Pattern;

/**
 * RegexUtil —— 正则校验工具（学员自写版）
 *
 * 对应黑马 163–168 集：正则表达式。
 * 对照答案在 {@code src/main/java/reference/RegexUtil.java}
 * —— 卡住时只允许看它的「方法签名 + 注释」，**别打开方法体**。
 *
 * 今天只实现下面 3 个校验方法；`extractDateAndMoney`（文本提取）和 `maskPhone`（脱敏）
 * 是进阶，明天再讲。
 *
 * ⚠️ 本文件是教练生成的骨架（2026-10-16）。3 个方法体全部由你实现。
 */
public class RegexUtil {

    /*
     * TODO 0：把三个正则 Pattern 写在这里（写成 static final，能复用，性能差几十倍）。
     *   提示：Java 字符串里要写反斜杠，必须写成两条 \\，例如 \\d 表示「一个数字」。
     *   手机号：1 开头，第二位 3-9，后面 9 位数字
     *   邮箱：xxx@yyy.zzz（@ 前允许字母数字 . _ % + -；域名至少一个点；末尾 2 位以上字母）
     *   身份证：18 位，前 17 位数字，最后一位数字或 X / x
     */

    private RegexUtil() {
        // 工具类不实例化
    }

    /**
     * 校验手机号。
     * <pre>
     * isPhone("13812345678")  == true
     * isPhone("12345678901")  == false   // 第二位不是 3-9
     * </pre>
     */
    public static boolean isPhone(String phone) {
        throw new UnsupportedOperationException("TODO 1");
    }

    /**
     * 校验邮箱。
     * <pre>
     * isEmail("a@b.com")        == true
     * isEmail("a@b")            == false   // 域名没有点
     * isEmail("@b.com")         == false   // 没有用户名
     * </pre>
     */
    public static boolean isEmail(String email) {
        throw new UnsupportedOperationException("TODO 2");
    }

    /**
     * 校验 18 位身份证【格式】。
     * <pre>
     * isIdCard("110101199003071234")  == true
     * isIdCard("11010119900307123X")  == true    // 末位允许大写 X
     * isIdCard("1101011990030")       == false   // 位数不够
     * </pre>
     * 注意：这里只校验格式，不算校验位（校验位是加权求和 mod 11，项目一里再学）。
     */
    public static boolean isIdCard(String idCard) {
        throw new UnsupportedOperationException("TODO 3");
    }

    // ==================================================================
    // 下面是教练写好的 12 条自查，**不要改**。全部 PASS 才算今天过关。
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
        System.out.println("===== RegexUtil 12 条自查 =====\n");

        // 手机号 5 条
        check("1. 手机号 13812345678 合法", isPhone("13812345678"), true);
        check("2. 手机号 19900001111 合法", isPhone("19900001111"), true);
        check("3. 手机号 12345678901 非法(第2位非3-9)", isPhone("12345678901"), false);
        check("4. 手机号 1381234567 非法(少1位)", isPhone("1381234567"), false);
        check("5. 手机号 138123456789 非法(多1位)", isPhone("138123456789"), false);

        // 邮箱 4 条
        check("6. 邮箱 a@b.com 合法", isEmail("a@b.com"), true);
        check("7. 邮箱 zhang.san+test@qq.com 合法", isEmail("zhang.san+test@qq.com"), true);
        check("8. 邮箱 a@b 非法(无域名点)", isEmail("a@b"), false);
        check("9. 邮箱 @b.com 非法(无用户名)", isEmail("@b.com"), false);

        // 身份证 3 条
        check("10. 身份证 110101199003071234 合法", isIdCard("110101199003071234"), true);
        check("11. 身份证 11010119900307123X 合法(末位X)", isIdCard("11010119900307123X"), true);
        check("12. 身份证 1101011990030 非法(位数不够)", isIdCard("1101011990030"), false);

        System.out.println();
        System.out.println(failed == 0
                ? "===== ALL GREEN（12/12）—— 可以提交 ====="
                : "===== 还有 " + failed + " 条没过 =====");
    }
}
