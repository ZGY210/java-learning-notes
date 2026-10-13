package api;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 日期时间工具 —— 对应黑马第 169–173 集。
 *
 * <p>本类同时演示两件事：
 * <ol>
 *   <li><b>老 API 的坑</b>：{@link SimpleDateFormat} 不是线程安全的，多线程共用会解析出错乱结果；</li>
 *   <li><b>新 API 的用法</b>：{@link DateTimeFormatter} 是不可变且线程安全的，应当全面替换老 API。</li>
 * </ol>
 *
 * @author 学员自写（2026-10-14）
 */
public class DateUtil {

    /** DateTimeFormatter 是线程安全的，可以放心做成静态常量反复使用。 */
    public static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** 只要日期不要时间。 */
    public static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private DateUtil() {
    }

    // ==================== 字符串 ↔ 日期 ====================

    /**
     * 字符串 → LocalDateTime。
     *
     * @param text 形如 2026-10-14 09:30:00
     * @return 解析结果
     */
    public static LocalDateTime parseDateTime(String text) {
        return LocalDateTime.parse(text, DATE_TIME_FORMATTER);
    }

    /**
     * LocalDateTime → 字符串。
     */
    public static String format(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.format(DATE_TIME_FORMATTER);
    }

    /**
     * 字符串 → LocalDate。
     */
    public static LocalDate parseDate(String text) {
        return LocalDate.parse(text, DATE_FORMATTER);
    }

    // ==================== 时区转换（跨时区业务必须会） ====================

    /**
     * 把某个时区的本地时间，转换成另一个时区的本地时间。
     *
     * <p>例：北京 2026-10-14 09:00 对应 UTC 是 2026-10-14 01:00。</p>
     *
     * @param dateTime      源时区的本地时间
     * @param fromZoneId    源时区，如 ZoneId.of("Asia/Shanghai")
     * @param toZoneId      目标时区，如 ZoneId.of("UTC")
     * @return 目标时区的本地时间
     */
    public static LocalDateTime convertZone(LocalDateTime dateTime, ZoneId fromZoneId, ZoneId toZoneId) {
        ZonedDateTime source = dateTime.atZone(fromZoneId);
        ZonedDateTime target = source.withZoneSameInstant(toZoneId);
        return target.toLocalDateTime();
    }

    // ==================== 时间差 ====================

    /**
     * 计算两个时间之间相差的整天数（忽略时分秒，按日历天算）。
     *
     * <p>为什么用 ChronoUnit.DAYS.between 而不是自己减毫秒再除以 86400000？
     * 因为夏令时切换那天只有 23 小时，自己算会差一天。用库函数才正确。</p>
     *
     * @return 相隔的天数，end 早于 start 时返回负数
     */
    public static long daysBetween(LocalDate start, LocalDate end) {
        return ChronoUnit.DAYS.between(start, end);
    }

    /**
     * 计算两个时间点之间相差多少秒。
     */
    public static long secondsBetween(LocalDateTime start, LocalDateTime end) {
        return Duration.between(start, end).getSeconds();
    }

    /**
     * 距离今天还有多少天。
     */
    public static long daysUntil(LocalDate target) {
        return daysBetween(LocalDate.now(), target);
    }

    // ==================== 老 API 的坑（现场演示） ====================

    /**
     * 演示 {@link SimpleDateFormat} 在多线程下会算错。
     *
     * <p>原理：SimpleDateFormat 内部有一个可变的 Calendar 字段用来暂存解析中间结果，
     * 多个线程同时进这个方法，就会互相覆盖对方的数据，于是解析出乱七八糟的结果甚至抛异常。</p>
     *
     * @param threads 并发线程数
     * @param timesPerThread 每线程解析次数
     * @return 出错次数
     */
    public static int demoSimpleDateFormatIsNotThreadSafe(int threads, int timesPerThread) throws InterruptedException {
        SimpleDateFormat shared = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 故意设为共享
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch endGate = new CountDownLatch(threads);
        AtomicInteger errors = new AtomicInteger();

        for (int i = 0; i < threads; i++) {
            pool.submit(() -> {
                try {
                    startGate.await(); // 所有线程卡在这里，一声令下同时冲
                    for (int j = 0; j < timesPerThread; j++) {
                        try {
                            Date date = shared.parse("2026-10-14 09:30:00");
                            if (!"2026-10-14 09:30:00".equals(shared.format(date))) {
                                errors.incrementAndGet();
                            }
                        } catch (ParseException | RuntimeException e) {
                            // 并发下甚至可能直接抛 NumberFormatException / ArrayIndexOutOfBoundsException
                            errors.incrementAndGet();
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endGate.countDown();
                }
            });
        }

        startGate.countDown();
        endGate.await();
        pool.shutdown();
        return errors.get();
    }

    /**
     * 对照实验：用线程安全的 {@link DateTimeFormatter} 做同样的事，错误数应为 0。
     */
    public static int demoDateTimeFormatterIsSafe(int threads, int timesPerThread) throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch endGate = new CountDownLatch(threads);
        AtomicInteger errors = new AtomicInteger();

        for (int i = 0; i < threads; i++) {
            pool.submit(() -> {
                try {
                    startGate.await();
                    for (int j = 0; j < timesPerThread; j++) {
                        try {
                            LocalDateTime dt = parseDateTime("2026-10-14 09:30:00");
                            if (!"2026-10-14 09:30:00".equals(format(dt))) {
                                errors.incrementAndGet();
                            }
                        } catch (RuntimeException e) {
                            errors.incrementAndGet();
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endGate.countDown();
                }
            });
        }

        startGate.countDown();
        endGate.await();
        pool.shutdown();
        return errors.get();
    }

    // ==================== 自测入口 ====================

    public static void main(String[] args) throws InterruptedException {
        System.out.println("========== 1. 字符串与日期互转 ==========");
        LocalDateTime dt = parseDateTime("2026-10-14 09:30:00");
        System.out.println("解析结果      = " + dt);
        System.out.println("格式化回去    = " + format(dt));
        System.out.println("只取日期      = " + parseDate("2026-10-14"));
        System.out.println("今天          = " + LocalDate.now() + "（" + LocalDate.now().getDayOfWeek() + "）");

        System.out.println("\n========== 2. 时区换算 ==========");
        LocalDateTime beijing = parseDateTime("2026-10-14 09:00:00");
        System.out.println("北京 " + beijing
                + "  =  UTC " + convertZone(beijing, ZoneId.of("Asia/Shanghai"), ZoneId.of("UTC"))
                + "  =  纽约 " + convertZone(beijing, ZoneId.of("Asia/Shanghai"), ZoneId.of("America/New_York")));

        System.out.println("\n========== 3. 时间差（排期表就靠这个） ==========");
        LocalDate today = LocalDate.now();
        LocalDate deadline = LocalDate.of(2027, 3, 1);
        System.out.println("今天 -> 2027-03-01 投递日，还剩 " + daysUntil(deadline) + " 天");
        System.out.println("今天 -> 2026-12-20 Java SE 收尾，还剩 " + daysUntil(LocalDate.of(2026, 12, 20)) + " 天");
        System.out.println("两句时间相隔秒数 = " + secondsBetween(parseDateTime("2026-10-14 09:00:00"), parseDateTime("2026-10-14 10:30:00")));
        System.out.println("跨夏令时算天数（这就是不能自己减毫秒的原因）= "
                + daysBetween(LocalDate.of(2026, 3, 7), LocalDate.of(2026, 3, 9)) + " 天");

        System.out.println("\n========== 4. 老 API 的坑：SimpleDateFormat 多线程 ==========");
        int threads = 8;
        int times = 2000;
        int badErrors = demoSimpleDateFormatIsNotThreadSafe(threads, times);
        int goodErrors = demoDateTimeFormatterIsSafe(threads, times);
        System.out.println("共享一个 SimpleDateFormat，" + threads + " 线程各解析 " + times + " 次 -> 出错 " + badErrors + " 次");
        System.out.println("改用 DateTimeFormatter            ，同样压力           -> 出错 " + goodErrors + " 次");
        System.out.println("结论：字段型工具类（SimpleDateFormat / Date）不要做成 static 共享，");
        System.out.println("      新代码一律用 java.time 包（LocalDateTime / DateTimeFormatter）。");

        System.out.println("\n========== 5. 小练习：算一算你的学习进度 ==========");
        LocalDate start = LocalDate.of(2026, 10, 14);
        long total = daysBetween(start, deadline);
        long passed = daysBetween(start, today);
        System.out.printf("从 10/14 到 3/1 共 %d 天，已过 %d 天，还剩 %d 天，进度 %.1f%%%n",
                total, passed, total - passed, passed * 100.0 / total);
        System.out.println("按每天 2.5 小时算，剩余可投入 " + (total - passed) * 2.5 + " 小时");
    }
}
