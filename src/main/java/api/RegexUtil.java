package api;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式工具 —— 对应黑马第 163–168 集。
 *
 * <p>只放真实用得到的三个校验 + 一个提取：手机号、邮箱、身份证、以及从文本里抽金额和日期。
 * 这三个校验在项目一（校园二手交易平台）的注册接口里会原样用上。</p>
 *
 * <p>面试常问：{@link Pattern} 是线程安全的、可以复用；{@link Matcher} 不是，每次都要新建。
 * 所以本类把 Pattern 全部预编译成 static final 常量，避免每次调用重新编译正则（性能差几十倍）。</p>
 *
 * @author 学员自写（2026-10-14）
 */
public class RegexUtil {

    /** 大陆手机号：1 开头，第二位 3-9，后面 9 位数字。 */
    private static final Pattern PHONE = Pattern.compile("^1[3-9]\\d{9}$");

    /** 邮箱：允许大小写字母、数字、下划线、点、加号、减号；域名部分至少一个点。 */
    private static final Pattern EMAIL =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    /** 18 位身份证：前 17 位数字，最后一位数字或 X/x。 */
    private static final Pattern ID_CARD =
            Pattern.compile("^\\d{17}[\\dXx]$");

    /** 金额：形如 12 或 12.3 或 12.34，可选带「元」字。 */
    private static final Pattern MONEY =
            Pattern.compile("(\\d+(?:\\.\\d{1,2})?)\\s*元?");

    /** 日期：形如 2026-10-14、2026/10/14、2026.10.14。用捕获分组把年月日分开。 */
    private static final Pattern DATE =
            Pattern.compile("(\\d{4})[-/.](\\d{1,2})[-/.](\\d{1,2})");

    private RegexUtil() {
        // 工具类不需要被实例化
    }

    /**
     * 校验手机号。
     *
     * @param phone 待校验字符串
     * @return 合法返回 true
     */
    public static boolean isPhone(String phone) {
        return phone != null && PHONE.matcher(phone).matches();
    }

    /**
     * 校验邮箱。
     */
    public static boolean isEmail(String email) {
        return email != null && EMAIL.matcher(email).matches();
    }

    /**
     * 校验 18 位身份证号。
     *
     * <p>注意：这里只校验【格式】，不校验校验位。
     * 真实项目要做校验位运算（前 17 位加权求和 mod 11 查表），那是项目一的正经需求。</p>
     */
    public static boolean isIdCard(String idCard) {
        return idCard != null && ID_CARD.matcher(idCard).matches();
    }

    /**
     * 从一段文本中提取所有出现的「日期 + 该日期后面最近的金额」。
     *
     * <p>这是爬虫和文档解析最常用的套路：先定位锚点（日期），再在它后面找目标（金额）。</p>
     *
     * @param text 原文
     * @return 每条形如 {@code 2026-10-14 -> 88.50} 的字符串
     */
    public static List<String> extractDateAndMoney(String text) {
        List<String> result = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return result;
        }
        Matcher dateMatcher = DATE.matcher(text);
        while (dateMatcher.find()) {
            String date = dateMatcher.group(1) + "-"
                    + padLeft(dateMatcher.group(2)) + "-"
                    + padLeft(dateMatcher.group(3));

            // 只在这条日期之后的 50 个字符里找金额，避免串到下一条记录上
            int from = dateMatcher.end();
            int to = Math.min(text.length(), from + 50);
            Matcher moneyMatcher = MONEY.matcher(text.substring(from, to));
            if (moneyMatcher.find()) {
                result.add(date + " -> " + moneyMatcher.group(1));
            }
        }
        return result;
    }

    /**
     * 手机号脱敏：13812345678 → 138****5678。
     */
    public static String maskPhone(String phone) {
        if (!isPhone(phone)) {
            throw new IllegalArgumentException("不是合法手机号：" + phone);
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }

    private static String padLeft(String s) {
        return s.length() == 1 ? "0" + s : s;
    }

    // ==================== 自测入口 ====================

    public static void main(String[] args) {
        System.out.println("========== 1. 手机号校验 ==========");
        String[] phones = {"13812345678", "19900001111", "12345678901", "1381234567", "138123456789"};
        for (String p : phones) {
            System.out.printf("%-14s -> %s%n", p, isPhone(p));
        }

        System.out.println("\n========== 2. 邮箱校验 ==========");
        String[] emails = {"a@b.com", "zhang.san+test@qq.com", "a@b", "@b.com", "a b@c.com"};
        for (String e : emails) {
            System.out.printf("%-28s -> %s%n", e, isEmail(e));
        }

        System.out.println("\n========== 3. 身份证格式校验 ==========");
        String[] ids = {"110101199003071234", "11010119900307123X", "11010119900307123x", "1101011990030"};
        for (String id : ids) {
            System.out.printf("%-20s -> %s%n", id, isIdCard(id));
        }

        System.out.println("\n========== 4. 提取文本里的日期与金额 ==========");
        String text = "2026/10/01 卖出二手键盘 收入 88.50 元；"
                + "2026-10-05 卖出显示器 收入 599 元；"
                + "2026.10.12 买入鼠标 支出 129.9 元。";
        List<String> rows = extractDateAndMoney(text);
        rows.forEach(r -> System.out.println("  " + r));
        System.out.println("共提取 " + rows.size() + " 条（注意最后一条是支出，正则本身分不清收支）");

        System.out.println("\n========== 5. 手机号脱敏 ==========");
        System.out.println(maskPhone("13812345678"));

        System.out.println("\n========== 6. 分组：自己把年月日拆开 ==========");
        Matcher m = DATE.matcher(text);
        while (m.find()) {
            System.out.printf("年=%s 月=%s 日=%s%n", m.group(1), m.group(2), m.group(3));
        }
    }
}
