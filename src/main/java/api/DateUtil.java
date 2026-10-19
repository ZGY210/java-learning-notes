package api;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * DateUtil —— JDK8 新时间 API 工具（学员自写版）
 *
 * 对应黑马 172–173（标注「必」，原速看）：
 *   172 JDK8时间类-时区、时间和格式化（35:24）
 *   173 JDK8时间类-日历和工具类（22:34）
 *
 * 对照答案在 {@code src/main/java/reference/DateUtil.java} —— 只准看方法签名和注释，别打开方法体。
 *
 * 今天的重点是「替换」：SimpleDateFormat 是线程不安全的，DateTimeFormatter 线程安全、
 * 不可变、可以做成 static final 常量反复用。你昨天写的 DatePractice（JDK7）今天要被这里
 * 的 new API 替换掉 —— 做完就懂「为什么老 API 该被淘汰」。
 *
 * ⚠️ 本文件是教练生成的骨架（2026-10-18）。7 个方法体全部由你实现。
 */
public class DateUtil {

    /**
     * DateTimeFormatter 线程安全，可以放心做成 static final 常量反复用。
     * 注意：和昨天的 SimpleDateFormat 不同，这一个是 immutable 的。
     */
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** 只要日期不要时间。 */
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private DateUtil() {
        // 工具类不实例化
    }

    /**
     * 字符串 → LocalDateTime。
     * <pre>
     * parseDateTime("2026-10-18 09:30:00").toString() 以 "2026-10-18T09:30" 开头
     * </pre>
     * 提示：LocalDateTime.parse(text, DATE_TIME_FORMATTER)
     */
    public static LocalDateTime parseDateTime(String text){
        return LocalDateTime.parse(text, DATE_TIME_FORMATTER);
    }

    /**
     * LocalDateTime → 字符串（yyyy-MM-dd HH:mm:ss）。入参为 null 时返回 null。
     * <pre>
     * format(parseDateTime("2026-10-18 09:30:00")) == "2026-10-18 09:30:00"
     * </pre>
     */
    public static String format(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.format(DATE_TIME_FORMATTER);
    }

    /**
     * 字符串（yyyy-MM-dd）→ LocalDate。
     * <pre>
     * parseDate("2026-10-18").getDayOfMonth() == 18
     * </pre>
     * 提示：LocalDate.parse(text, DATE_FORMATTER)
     */
    public static LocalDate parseDate(String text) {
        return LocalDate.parse(text, DATE_FORMATTER);
    }

    /**
     * LocalDate → 字符串（yyyy-MM-dd）。入参为 null 时返回 null。
     */
    public static String formatDate(LocalDate date) {
        return date == null ? null : date.format(DATE_FORMATTER);
    }

    /**
     * 两个日期相差的整天数（按日历天算，不是简单除 86400000）。
     * <pre>
     * daysBetween(2026-10-01, 2026-10-18) == 17
     * daysBetween(2026-10-18, 2026-10-01) == -17
     * </pre>
     * 提示：java.time.temporal.ChronoUnit.DAYS.between(start, end)
     */
    public static long daysBetween(LocalDate start, LocalDate end) {
        return ChronoUnit.DAYS.between(start, end);
    }

    /**
     * 把某时区的本地时间换算成另一时区的本地时间。
     * <pre>
     * convertZone(2026-10-18T09:00, Asia/Shanghai, UTC)  == 2026-10-18T01:00
     * </pre>
     * 提示：三道手续 —— dateTime.atZone(from).withZoneSameInstant(to).toLocalDateTime()
     */
    public static LocalDateTime convertZone(LocalDateTime dateTime, ZoneId from, ZoneId to) {
        ZonedDateTime source = dateTime.atZone(from);
        ZonedDateTime target = source.withZoneSameInstant(to);
        return target.toLocalDateTime();
    }

    /**
     * 距离目标日期还有几天（今天是 2026-10-18，daysUntil(2027-03-01) 应约等于 134）。
     * 提示：内部调用 daysBetween(LocalDate.now(), target)
     */
    public static long daysUntil(LocalDate target) {
        return ChronoUnit.DAYS.between(target, LocalDate.now());
    }

    // ==================================================================
    // 下面是教练写好的 12 条自查，**不要改**。全部 PASS 才算今天过关。
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

    public static void main(String[] args) {
        System.out.println("===== DateUtil 11 条自查 =====\n");

        // 1-4 格式化与解析
        check("1. parseDateTime 月日", parseDateTime("2026-10-18 09:30:00").getMonthValue(), 10);
        check("2. parseDateTime 日", parseDateTime("2026-10-18 09:30:00").getDayOfMonth(), 18);
        check("3. format 往返", format(parseDateTime("2026-10-18 09:30:00")), "2026-10-18 09:30:00");
        check("4. format(null)", format(null), null);

        // 5-6 LocalDate
        check("5. parseDate 日", parseDate("2026-10-18").getDayOfMonth(), 18);
        check("6. formatDate", formatDate(parseDate("2026-10-18")), "2026-10-18");

        // 7-9 时间差
        check("7. daysBetween 正向", daysBetween(LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 18)), 17L);
        check("8. daysBetween 负向", daysBetween(LocalDate.of(2026, 10, 18), LocalDate.of(2026, 10, 1)), -17L);
        check("9. daysBetween 同一天", daysBetween(LocalDate.of(2026, 10, 18), LocalDate.of(2026, 10, 18)), 0L);

        // 10 时区
        LocalDateTime converted = convertZone(
                LocalDateTime.of(2026, 10, 18, 9, 0),
                ZoneId.of("Asia/Shanghai"), ZoneId.of("UTC"));
        check("10. 上海9点 = UTC1点", converted.toString(), "2026-10-18T01:00");

        // 11-12 距今天
        long until = daysUntil(LocalDate.of(2027, 3, 1));
        check("11. 距 2027-03-01 >130 天", until > 130, true);

        System.out.println();
        System.out.println(failed == 0
                ? "===== ALL GREEN（12/12）—— 可以提交 ====="
                : "===== 还有 " + failed + " 条没过 =====");
    }
}