package practice.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.function.Supplier;

/**
 * 2026-11-11 任务：TreeSet 两种排序
 *
 * 视频对应：黑马 197「TreeSet 第一种排序方式」（40:47）+ 198「TreeSet 第二种排序方式和综合练习」（30:35）
 *
 * 今天只回答三个问题：
 *   1. 「自然排序」和「比较器排序」各是什么？什么时候用哪个？
 *   2. TreeSet 凭什么判重？—— 答案是 compareTo / compare 返回 0，**不是 equals**
 *   3. compareTo 返回 0 但 equals 为 false 时，TreeSet 和 HashSet 的判断会不一样吗？
 *
 * 填完 8 个 TODO，`java practice.collection.TreeSetDemo` 打印 ALL GREEN 即过关。
 */
public class TreeSetDemo {

    // ==================================================================
    // 学生类：给它实现 Comparable，就是「自然排序」
    // ==================================================================

    static class Student implements Comparable<Student> {
        final String name;
        final int age;

        Student(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public String toString() {
            return name + "(" + age + ")";
        }

        /**
         * TODO 1
         * 排序规则：**年龄升序**；年龄相同时按**姓名字典序升序**。
         *
         * 提示：先比 age（int 相减即可），相减结果为 0 时再用 name.compareTo(...)。
         * 注意：这里返回 0 的含义是「这两个对象在我眼里是同一个」——
         *      TreeSet 就是靠这个 0 来判重的。
         */
        @Override
        public int compareTo(Student o) {
            throw new UnsupportedOperationException("TODO 1 未实现");
        }
    }

    // ==================================================================
    // 第一组：两种排序方式（TODO 2 ~ 3）
    // ==================================================================

    /**
     * TODO 2 · 自然排序
     * 把 张三(22)、李四(20)、王五(25)、赵六(20) 加进 TreeSet<Student>（用无参构造，靠 compareTo），
     * 返回按迭代顺序的 toString 列表。
     *
     * 期望：["李四(20)", "赵六(20)", "张三(22)", "王五(25)"]
     */
    static List<String> naturalOrder() {
        throw new UnsupportedOperationException("TODO 2 未实现");
    }

    /**
     * TODO 3 · 比较器排序
     * 同样这四个学生，但改用 `new TreeSet<>(比较器)`，比较器规则：
     * **年龄降序**，年龄相同时**姓名字典序升序**。
     *
     * 提示：Comparator.comparingInt((Student s) -> s.age).reversed().thenComparing(s -> s.name)
     * 期望：["王五(25)", "张三(22)", "李四(20)", "赵六(20)"]
     */
    static List<String> comparatorOrder() {
        throw new UnsupportedOperationException("TODO 3 未实现");
    }

    // ==================================================================
    // 第二组：TreeSet 判重靠什么（TODO 4 ~ 5）
    // 这是今天最值钱的两条，也是面试常问的「TreeSet 和 HashSet 去重有什么区别」
    // ==================================================================

    /**
     * TODO 4
     * 往 TreeSet<Student> 里加两个「内容完全相同」的 Student("张三", 20)，
     * 返回最终 size。
     *
     * 期望：1
     * 关键：这两个对象用 == 和默认 equals 比较都是 false（Student 没重写 equals），
     *      但 TreeSet 仍然只留了一个 —— 因为 compareTo 返回了 0。
     *      所以 TreeSet 的「重复」定义是 **compareTo == 0**，与 equals 无关。
     */
    static int dedupByCompareTo() {
        throw new UnsupportedOperationException("TODO 4 未实现");
    }

    /**
     * TODO 5
     * 先往 TreeSet<Student> 加一个 Student("张三", 20)，
     * 再调用 set.contains(new Student("张三", 20))，返回结果。
     *
     * 期望：true
     * 关键：新对象和集合里的对象 equals 是 false（引用不同），
     *      但 contains 依然返回 true —— TreeSet 的查找走的是 compareTo 的二叉搜索，不是 equals。
     */
    static boolean containsByCompareTo() {
        throw new UnsupportedOperationException("TODO 5 未实现");
    }

    // ==================================================================
    // 第三组：TreeSet 的其他行为（TODO 6 ~ 8）
    // ==================================================================

    /**
     * TODO 6
     * 往 `new TreeSet<Integer>()` 里 add(null)，捕获异常，
     * 返回 true 表示「确实抛了异常」。
     *
     * 期望：true（NullPointerException）
     * 原因：TreeSet 插入时要调用 compareTo 排序，null 身上没有 compareTo。
     *      （对照：HashSet 允许一个 null，因为它是算 hashCode。）
     */
    static boolean nullRejected() {
        throw new UnsupportedOperationException("TODO 6 未实现");
    }

    /**
     * TODO 7
     * 把 10、20、30、40 放进 TreeSet<Integer>，求 25 的 floor 和 ceiling，
     * 返回 {floor, ceiling}。
     *
     * 期望：{20, 30}
     * 补充理解：floor = ≤ 目标的最大元素；ceiling = ≥ 目标的最小元素；
     *          lower / higher 是严格小于 / 严格大于，边界上会和你以为的不一样。
     */
    static int[] floorCeiling() {
        throw new UnsupportedOperationException("TODO 7 未实现");
    }

    /**
     * TODO 8
     * 用 TreeSet 对 int[] 做「去重 + 排序」，返回 List<Integer>。
     * 例：{3,1,2,3,1} → [1,2,3]
     *
     * 提示：TreeSet 天生就去重 + 排序，这是它比「先 HashSet 再去 Collections.sort」更省事的地方。
     */
    static List<Integer> dedupAndSort(int[] arr) {
        throw new UnsupportedOperationException("TODO 8 未实现");
    }

    // ==================================================================
    // 自测（不用改，填完 TODO 直接跑）
    // ==================================================================

    private static int pass = 0;
    private static int fail = 0;

    private static void t(String name, Supplier<Boolean> body) {
        try {
            if (Boolean.TRUE.equals(body.get())) {
                pass++;
                System.out.println("[PASS] " + name);
            } else {
                fail++;
                System.out.println("[FAIL] " + name);
            }
        } catch (UnsupportedOperationException e) {
            fail++;
            System.out.println("[TODO] " + name + "  --> " + e.getMessage());
        } catch (Exception e) {
            fail++;
            System.out.println("[ERR ] " + name + "  --> " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        System.out.println("================== TreeSet 两种排序 自测 ==================");

        t("1  自然排序：年龄升序、同龄按姓名（期望 [李四(20), 赵六(20), 张三(22), 王五(25)]）",
                () -> naturalOrder().equals(Arrays.asList("李四(20)", "赵六(20)", "张三(22)", "王五(25)")));

        t("2  比较器排序：年龄降序、同龄按姓名（期望 [王五(25), 张三(22), 李四(20), 赵六(20)]）",
                () -> comparatorOrder().equals(Arrays.asList("王五(25)", "张三(22)", "李四(20)", "赵六(20)")));

        t("3  compareTo 能正常工作（升序第一个是年龄最小的 李四(20)）",
                () -> !naturalOrder().isEmpty() && naturalOrder().get(0).equals("李四(20)"));

        t("4  判重靠 compareTo 返回 0，不是 equals（期望 size == 1）",
                () -> dedupByCompareTo() == 1);

        t("5  contains 也走 compareTo（equals 为 false 却返回 true）",
                () -> containsByCompareTo());

        t("6  TreeSet 不允许 null（期望抛 NPE）", () -> nullRejected());

        t("7  floor(25)=20、ceiling(25)=30（期望 {20, 30}）",
                () -> Arrays.equals(floorCeiling(), new int[]{20, 30}));

        t("8  去重 + 排序 {3,1,2,3,1}（期望 [1, 2, 3]）",
                () -> dedupAndSort(new int[]{3, 1, 2, 3, 1}).equals(Arrays.asList(1, 2, 3)));

        t("9  TreeSet 迭代出来一定是升序（自动检查 last 最大）", () -> {
            TreeSet<Integer> set = new TreeSet<>(Arrays.asList(5, 3, 9, 1));
            List<Integer> ordered = new ArrayList<>(set);
            List<Integer> sorted = new ArrayList<>(set);
            sorted.sort(Comparator.naturalOrder());
            return ordered.equals(sorted) && set.last() == 9 && set.first() == 1;
        });

        t("10 空 TreeSet 的 first/last 会抛 NoSuchElementException（不是返回 null）", () -> {
            try {
                new TreeSet<Integer>().first();
                return false;
            } catch (java.util.NoSuchElementException e) {
                return true;
            }
        });

        System.out.println("----------------------------------------------------------");
        System.out.printf("结果：PASS %d / FAIL %d%n", pass, fail);
        if (fail == 0) {
            System.out.println("ALL GREEN（10/10）—— 可以去写笔记和 LeetCode 了");
        } else {
            System.out.println("还有 " + fail + " 项没过，先别 commit");
        }
        System.out.println("==========================================================");
    }
}
