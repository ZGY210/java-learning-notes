package demo;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * DatePractice —— JDK7 老时间 API 练习（学员自写版）
 *
 * 对应黑马 169 Date + 170 SimpleDateFormat（+171 Calendar 选看）。
 * 对照答案在 {@code src/main/java/reference/DateUtil.java} —— 但那是 JDK8 新 API 版，
 * 只准看思路，**今天必须用老的 Date / SimpleDateFormat 写**。
 *
 * 今天的定位是「体验坑」：SimpleDateFormat 明天会被你自己跑出来的线程安全问题淘汰，
 * 但面试必问「为什么 SimpleDateFormat 线程不安全」，所以今天每一步都要搞懂。
 *
 * ⚠️ 本文件是教练生成的骨架（2026-10-17）。4 个方法体全部由你实现。
 */
public class DatePractice {

    private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";

    // TODO 0：在这里建一个 SimpleDateFormat 常量
    //   提示：private static final SimpleDateFormat SDF = new SimpleDateFormat(PATTERN);
    //   想一想：写成 static final 和每次方法里 new 一个，有区别吗？（明天的线程安全实验会回答）
    private static final SimpleDateFormat SDF = new SimpleDateFormat(PATTERN);

    private DatePractice() {
        // 工具类不实例化
    }

    /**
     * 字符串 → Date。
     * <pre>
     * parse("1970-01-01 08:00:00").getTime() == 0L   // 本机是东八区，所以是 08:00 不是 00:00
     * </pre>
     */
    public static Date parse(String text) throws Exception {
        return SDF.parse(text);
    }

    /**
     * Date → 字符串。
     * <pre>
     * format(new Date(0L)) == "1970-01-01 08:00:00"   // 注意不是 00:00:00
     * </pre>
     */
    public static String format(Date date) {
        return SDF.format(date);
    }

    /**
     * Date → 毫秒时间戳。提示：Date 有一个 get 开头的无参方法。
     */
    public static long toMillis(Date date) {
        return date.getTime();
    }

    /**
     * 毫秒时间戳 → Date。提示：Date 有一个接收 long 的构造器。
     */
    public static Date fromMillis(long millis) {
        return  new Date(millis);
    }

    // ==================================================================
    // 下面是教练写好的 10 条自查，**不要改**。全部 PASS 才算今天过关。
    // ==================================================================

    private static int failed = 0;

    private static void check(String name, Object actual, Object expect) {
        boolean ok = java.util.Objects.equals(actual, expect);
        if (!ok) {
            failed++;
        }
        System.out.println((ok ? "[PASS] " : "[FAIL] ") + name + " = " + actual
                + (ok ? "" : "   期望: " + expect));
    }

    public static void main(String[] args) throws Exception {
        System.out.println("===== DatePractice 10 条自查 =====\n");

        check("1. parse 纪元(东八区)", parse("1970-01-01 08:00:00").getTime(), 0L);
        check("2. format 纪元", format(new Date(0L)), "1970-01-01 08:00:00");
        check("3. parse->format 往返", format(parse("2026-10-17 09:30:00")), "2026-10-17 09:30:00");
        check("4. toMillis(纪元)", toMillis(parse("1970-01-01 08:00:00")), 0L);
        check("5. toMillis(1 秒)", toMillis(new Date(1000L)), 1000L);
        check("6. fromMillis(0)", fromMillis(0L).getTime(), 0L);
        check("7. fromMillis 一天后", format(fromMillis(86400000L)), "1970-01-02 08:00:00");
        check("8. 两个 Date 相等(equals)", parse("1970-01-02 08:00:00"), new Date(86400000L));
        check("9. 第二次往返", format(parse("2003-01-01 00:00:00")), "2003-01-01 00:00:00");
        check("10. toMillis 往返", toMillis(fromMillis(123456789L)), 123456789L);

        System.out.println();
        System.out.println(failed == 0
                ? "===== ALL GREEN（10/10）—— 可以提交 ====="
                : "===== 还有 " + failed + " 条没过 =====");
    }
}
