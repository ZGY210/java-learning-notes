package practice.collection;

import java.util.*;
import java.util.function.Supplier;

/**
 * 2026-11-19 手写任务 · TreeMap 练习二 / 练习三（对应黑马「209 键位置添加自定义对象」「210 利用 TreeMap 进行统计」）
 *
 * 规则：
 *   1. 只写 6 个 TODO + Student3.compareTo，不许改 main 里的断言（断言是你唯一的裁判）。
 *   2. 不许抄网上的答案；卡住超过 15 分钟就写一行注释说明卡在哪，明天问我。
 *   3. 全部写完，跑 main，绿字必须是 PASS 10 / FAIL 0。
 *
 * 跑法：
 *   javac -encoding UTF-8 --release 17 -d target\check (所有源文件)
 *   java -cp target\check practice.collection.TreeMapPractice
 */
public class TreeMapPractice {

    // ==================== 6 个 TODO ====================

    /**
     * TODO 1（209 集）：TreeMap 的 key 是自定义对象，用【外部 Comparator】排序。
     * 规则：年龄升序；同龄时按姓名字典序升序。
     * 返回排好序的姓名列表（每项形如 "张三(20)"）。
     */
    static List<String> sortByAgeThenName(List<Student3> list) {
        throw new UnsupportedOperationException("TODO 1");
    }

    /**
     * TODO 2（209 集）：TreeMap 的 key 是自定义对象，用【Student3 自己的 compareTo】排序。
     * 也就是 new TreeMap<>() 不传 comparator。
     * 返回排好序的姓名列表（每项形如 "张三(20)"）。
     */
    static List<String> naturalOrderCustomKey() {
        throw new UnsupportedOperationException("TODO 2");
    }

    /**
     * TODO 3（209 集，这题是陷阱）：证明 TreeMap 判「相等」看的是 compareTo / comparator，
     * 和 equals、hashCode 一点关系都没有。
     *
     * 要求：建一个 TreeMap，comparator 只比年龄（不看姓名）；
     *       依次 put 张三(20) 和 王五(20)；
     *       返回这个 map 的 size()。
     * 想一想：两个不同的人，为什么进去只剩一个？谁被谁覆盖了？
     */
    static int dedupByCompareTo() {
        throw new UnsupportedOperationException("TODO 3");
    }

    /**
     * TODO 4（210 集）：用 TreeMap 做词频统计，天然按单词字典序输出。
     * 输入 "the cat the dog the"，返回 ["cat=1", "dog=1", "the=3"]。
     * 提示：按非字母数字切分、转小写、忽略空串。
     */
    static List<String> wordFrequencySorted(String text) {
        throw new UnsupportedOperationException("TODO 4");
    }

    /**
     * TODO 5（210 集）：用 TreeMap 做字符频次统计，按字符升序输出。
     * 输入 "banana"，返回 ["a=3", "b=1", "n=2"]。
     */
    static List<String> charFrequencySorted(String text) {
        throw new UnsupportedOperationException("TODO 5");
    }

    /**
     * TODO 6（209 集，范围查询 —— 面试爱问）：
     * 把学生按年龄分桶（TreeMap&lt;Integer, List&lt;String&gt;&gt;：年龄 -> 该年龄的人名），
     * 再用 subMap 取出年龄在 [from, to) 区间的所有桶，
     * 按年龄升序把 "姓名(年龄)" 拍平成一个列表返回。
     *
     * 例：20 岁的有 张三、王五；22 岁的有 李四 → 取 [20,22) 得 ["张三(20)", "王五(20)"]。
     * 提示：subMap(from, true, to, false) 是左闭右开，返回的是 Map 视图，遍历它即可。
     * 想一想：为什么用 TreeMap&lt;Integer,...&gt; 而不是 TreeMap&lt;Student3,...&gt; 做这件事？
     */
    static List<String> ageBetween(List<Student3> list, int from, int to) {
        throw new UnsupportedOperationException("TODO 6");
    }

    // ==================== Student3：compareTo 留给你 ====================

    static class Student3 implements Comparable<Student3> {
        private final String name;
        private final int age;

        Student3(String name, int age) {
            this.name = name;
            this.age = age;
        }

        String getName() { return name; }
        int getAge() { return age; }

        @Override
        public String toString() {
            return name + "(" + age + ")";
        }

        /**
         * TODO（209 集）：写出 compareTo —— 年龄升序；同龄时比姓名（String 自带 compareTo）。
         * 注意：TreeMap 用它判断「相等」，返回 0 就会被当成同一个 key。
         */
        @Override
        public int compareTo(Student3 o) {
            throw new UnsupportedOperationException("TODO：写出 Student3.compareTo");
        }
    }

    // ==================== 下面不要改 ====================

    private static int pass = 0;
    private static int fail = 0;

    private static void check(String label, Supplier<Boolean> body) {
        boolean r;
        try {
            r = Boolean.TRUE.equals(body.get());
        } catch (Exception e) {
            r = false;
            System.out.println("      异常: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
        if (r) {
            pass++;
            System.out.println("[PASS] " + label);
        } else {
            fail++;
            System.out.println("[FAIL] " + label);
        }
    }

    private static boolean expectNpe(Runnable r) {
        try {
            r.run();
            return false;
        } catch (NullPointerException e) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static List<Student3> students() {
        return new ArrayList<>(List.of(
                new Student3("张三", 20),
                new Student3("李四", 22),
                new Student3("王五", 20)
        ));
    }

    public static void main(String[] args) {
        System.out.println("================ TreeMap 练习二 / 三 ================");

        check("1 sortByAgeThenName 年龄升序 + 同龄按姓名", () ->
                sortByAgeThenName(students()).equals(List.of("张三(20)", "王五(20)", "李四(22)")));

        check("2 naturalOrderCustomKey 走 compareTo，不用 comparator", () ->
                naturalOrderCustomKey().equals(List.of("张三(20)", "王五(20)", "李四(22)")));

        check("3 dedupByCompareTo：两个同龄的人只留一个（size=1）", () ->
                dedupByCompareTo() == 1);

        check("4 wordFrequencySorted(\"the cat the dog the\")", () ->
                wordFrequencySorted("the cat the dog the").equals(List.of("cat=1", "dog=1", "the=3")));

        check("5 wordFrequencySorted(\"\") 返回空列表", () ->
                wordFrequencySorted("").isEmpty());

        check("6 charFrequencySorted(\"cab\") 按字符升序", () ->
                charFrequencySorted("cab").equals(List.of("a=1", "b=1", "c=1")));

        check("7 charFrequencySorted(\"banana\")", () ->
                charFrequencySorted("banana").equals(List.of("a=3", "b=1", "n=2")));

        check("8 ageBetween(students, 20, 22) 左闭右开", () ->
                ageBetween(students(), 20, 22).equals(List.of("张三(20)", "王五(20)")));

        check("9 TreeMap 不允许 null key（抛 NPE）", () ->
                expectNpe(() -> {
                    Map<String, String> m = new TreeMap<>();
                    m.put(null, "x");
                }));

        check("10 对照组：HashMap 允许 null key", () -> {
            Map<String, String> m = new HashMap<>();
            m.put(null, "x");
            return m.size() == 1;
        });

        System.out.println("-----------------------------------------------");
        System.out.println("结果：PASS " + pass + " / FAIL " + fail);
        if (fail == 0) {
            System.out.println("===============================================");
            System.out.println("  全绿。TreeMap 你拿下了。");
            System.out.println("  今天欠的两笔账：notes/collections/15 + LC146。");
            System.out.println("===============================================");
        } else {
            System.out.println("还有 " + fail + " 项没过");
        }
    }
}
