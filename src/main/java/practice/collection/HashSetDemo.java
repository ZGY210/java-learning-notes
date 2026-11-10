package practice.collection;

import java.util.*;
import java.util.function.Supplier;

/**
 * 2026-11-04 任务：HashSet / LinkedHashSet
 *
 * 视频对应：黑马 196「HashSet、LinkedHashSet 详解」
 *
 * 这一天的核心不是背 API，而是回答三个问题：
 *   1. HashSet 凭什么能做到「加进去就不重复」？—— hashCode 定位 + equals 判等
 *   2. 为什么「只重写 equals 不重写 hashCode」会出问题？
 *   3. HashSet 和 LinkedHashSet 差在哪？—— 底层一个 HashMap、一个 LinkedHashMap
 *
 * 填完 9 个 TODO，`java practice.collection.HashSetDemo` 打印 ALL GREEN 即过关。
 */
public class HashSetDemo {

    // ==================================================================
    // 第一组：HashSet 的基本行为（TODO 1 ~ 5）
    // ==================================================================

    /**
     * TODO 1
     * 把 1, 2, 3, 2, 1, 4 依次 add 进一个 HashSet，返回最终的 size。
     * 期望：4（重复的 1 和 2 只留一份）
     */
    static int collectDuplicates() {
        Set<Integer> set = new HashSet<>();
        int[] nums = {1, 2, 3, 2, 1, 4};
        for (int n : nums) {
            set.add(n);
        }
        return set.size();
    }

    /**
     * TODO 2
     * 对同一个 HashSet<String> 依次执行 add("a")、add("a")、add("b")，
     * 把三次 add 的返回值按顺序放进 boolean[] 返回。
     *
     * 关键：Set.add() 返回 boolean —— true 表示真的加进去了，false 表示已存在没加。
     * 这个返回值是很多业务幂等逻辑的基础（插入成功才发通知）。
     */
    static boolean[] addReturnValues() {
        Set<String> set = new HashSet<>();
        String[] words = {"a", "a", "b"};
        boolean[] result = new boolean[words.length];
        for (int i = 0; i < words.length; i++) {
            result[i] = set.add(words[i]);
        }
        return result;
    }

    /**
     * TODO 3
     * 对同一个 HashSet 连续 add(null) 两次，返回 size。
     * 期望：1（HashSet 允许一个 null，不允许两个）
     */
    static int nullCount() {
        Set<String> set = new HashSet<>();
        String[] nums = {null, null};
        for (String n : nums) {
            set.add(n);
        }
        return set.size();
    }

    /**
     * TODO 4
     * 依次把 100、2、55 add 进一个 HashSet（注意：用 new HashSet<>() 空构造，
     * 不要用 new HashSet<>(Arrays.asList(...))），返回迭代顺序。
     *
     * 这题的目的不是「得到某个固定顺序」，而是让你亲眼看到：
     * HashSet 的遍历顺序**不是插入顺序，也不是大小顺序**，它由 hash 决定的桶位置决定。
     * 很多人被「1,2,3 加进去遍历出来也是 1,2,3」骗过，以为 HashSet 有序。
     */
    static List<Integer> hashSetOrderBig() {
        Set<Integer> set = new HashSet<>();
        set.add(100);
        set.add(2);
        set.add(55);
        return new ArrayList<>(set);
    }

    /**
     * TODO 5
     * 同样依次 add 3、1、2，但这次用 LinkedHashSet，返回迭代顺序。
     * 期望：[3, 1, 2]（LinkedHashSet 额外维护了一条双向链表记录插入顺序）
     */
    static List<Integer> linkedSetOrder() {
        Set<Integer> set = new LinkedHashSet<>();
        set.add(3);
        set.add(1);
        set.add(2);
        return new ArrayList<>(set);
    }

    // ==================================================================
    // 第二组：Set 的实战用法（TODO 6 ~ 7）
    // 这两个是真会写进业务代码的：去重、求交集 / 差集。
    // ==================================================================

    /**
     * TODO 6
     * 求两个数组的交集，用 Set 实现，结果本身也是 Set。
     * 例：a = {1,2,2,3}, b = {2,3,4}  →  {2,3}
     *
     * 提示：先把 a 灌进一个 HashSet，再遍历 b 看 contains。
     * 复杂度：O(n + m)，比双重 for 的 O(n*m) 快一个量级。
     */
    static Set<Integer> intersection(int[] a, int[] b) {
        Set<Integer> setA = new HashSet<>();
        for (int x : a) {
            setA.add(x);          // a 里有重复也会被自动去重
        }

        Set<Integer> result = new HashSet<>();
        for (int y : b) {
            if (setA.contains(y)) {
                result.add(y);    // b 里的重复元素也只进一次
            }
        }
        return result;
    }

    /**
     * TODO 7
     * 数组去重，并且保持「第一次出现的顺序」。
     * 例：{"b","a","b","c","a"}  →  ["b","a","c"]
     *
     * 提示：这里必须用 LinkedHashSet —— 用 HashSet 顺序就乱了。
     */
    static List<String> uniqueInOrder(String[] words) {
        Set<String> set = new LinkedHashSet<>();
        for (String w : words) {
            set.add(w);
        }
        return new ArrayList<>(set);
    }

    // ==================================================================
    // 第三组：为什么必须「同时」重写 hashCode 和 equals（TODO 8 ~ 9）
    // 这是这一天最值钱的部分，也是面试最爱追问的点。
    // ==================================================================

    /**
     * 对照组：故意不重写 hashCode / equals。加两个「内容相同」的对象，去不了重。
     */
       /** record UserRaw(String name) {

        @Override
            public String toString() {
                return "UserRaw(" + name + ")";
            }
        }**/
       static class UserRaw {
           private final String name;

           UserRaw(String name) {
               this.name = name;
           }

           @Override
           public String toString() {
               return "UserRaw(" + name + ")";
           }
       }

    /**
     * 实验组：你来补 hashCode 和 equals，补完后同内容对象就能去重。
     */
        record User(String name) {

        //
            // 在这里补：@Override public int hashCode()
            //   要求用 Objects.hash(name) 实现。
            //   为什么需要它：HashSet 先用 hashCode 算出桶下标，桶里才比较 equals。
            //   如果两个「相等」的对象 hashCode 不同，它们会被放进不同桶，永远碰不到面。
        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

            //
            // 在这里补：@Override public boolean equals(Object o)
            //   要求：
            //     - o == this 直接返回 true（性能优化，也是规范）
            //     - o == null 或 getClass() != o.getClass() 返回 false
            //     - 强转后比较 name，用 Objects.equals(this.name, ((User) o).name)
            @Override
            public boolean equals(Object o) {
                if (o == this) {
                    return true;                       // 同一引用，直接 true
                }
                if (o == null || getClass() != o.getClass()) {
                    return false;                      // null 或类型不同
                }
                User other = (User) o;                 // 强转
                return Objects.equals(this.name, other.name);
            }

            @Override
            public String toString() {
                return "User(" + name + ")";
            }
        }

    /** 不重写的情况：两个 UserRaw("张三") 都会留在 Set 里 → size == 2 */
    static int dedupRaw() {
        Set<UserRaw> set = new HashSet<>();
        set.add(new UserRaw("张三"));
        set.add(new UserRaw("张三"));
        return set.size();
    }

    /**
     * 重写之后的情况：
     * 你还没补 TODO 8/9 的时候，这里会返回 2（说明去重失败）；
     * 补完之后应该变成 1 —— 这就是「同时重写」的必要性。
     */
    static int dedupFixed() {
        Set<User> set = new HashSet<>();
        set.add(new User("张三"));
        set.add(new User("张三"));
        return set.size();
    }

    // ------------------------------------------------------------------
    // 第三组：record 为什么不能当「不重写」的对照组（你 11/04 提出的问题）
    // ------------------------------------------------------------------

    /**
     * record 会自动生成 equals / hashCode / toString，规则是：
     *   「所有组件（这里是 name）都相等 → equals 返回 true」
     *   而且 hashCode 用组件算，所以两个内容相同的 record 天然落在同一个桶。
     *
     * 结论：record 天生就能被 HashSet 去重，因此**不能**用来演示「不重写会怎样」。
     * 你当时把 record UserRaw 注释掉、改回普通 class，这个判断是完全正确的。
     */
    record UserRecord(String name) {
    }

    /** record 不写任何方法，也能去重 → size == 1 */
    static int dedupRecord() {
        Set<UserRecord> set = new HashSet<>();
        set.add(new UserRecord("张三"));
        set.add(new UserRecord("张三"));
        return set.size();
    }

    /** 顺手验一下 record 的 equals：两个内容相同的 record，equals 为 true */
    static boolean recordEqualsWorks() {
        return new UserRecord("张三").equals(new UserRecord("张三"));
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
        System.out.println("================ HashSet / LinkedHashSet 自测 ================");

        t("1  重复元素只存一份（期望 size == 4）", () -> collectDuplicates() == 4);

        t("2  add 的返回值（期望 true / false / true）",
                () -> Arrays.equals(addReturnValues(), new boolean[]{true, false, true}));

        t("3  null 只允许一个（期望 size == 1）", () -> nullCount() == 1);

        t("4  HashSet 不保证顺序（100/2/55 遍历结果 != 升序 [2,55,100]）",
                () -> !hashSetOrderBig().equals(Arrays.asList(2, 55, 100)));

        t("5  LinkedHashSet 保持插入顺序（期望 [3, 1, 2]）",
                () -> linkedSetOrder().equals(Arrays.asList(3, 1, 2)));

        t("6  交集 {1,2,2,3} ∩ {2,3,4}（期望 {2,3}）",
                () -> intersection(new int[]{1, 2, 2, 3}, new int[]{2, 3, 4})
                        .equals(new HashSet<>(Arrays.asList(2, 3))));

        t("7  去重并保序 {\"b\",\"a\",\"b\",\"c\",\"a\"}（期望 [b, a, c]）",
                () -> uniqueInOrder(new String[]{"b", "a", "b", "c", "a"})
                        .equals(Arrays.asList("b", "a", "c")));

        t("8  不重写 hashCode/equals 时同内容对象不去重（期望 size == 2）",
                () -> dedupRaw() == 2);

        t("9  补完 hashCode + equals 后能去重（期望 size == 1）",
                () -> dedupFixed() == 1);

        t("10 contains / remove / isEmpty 行为正确", () -> {
            Set<String> s = new HashSet<>();
            s.add("x");
            boolean before = s.contains("x");
            s.remove("x");
            return before && !s.contains("x") && s.isEmpty();
        });

        t("11 record 不写任何方法也能去重（期望 size == 1）", () -> dedupRecord() == 1);

        t("12 record 的 equals 自动可用（期望 true）", () -> recordEqualsWorks());

        System.out.println("------------------------------------------------------------");
        System.out.printf("结果：PASS %d / FAIL %d%n", pass, fail);
        if (fail == 0) {
            System.out.println("ALL GREEN（12/12）—— 可以去写笔记和 LeetCode 了");
        } else {
            System.out.println("还有 " + fail + " 项没过，先别 commit");
        }
        System.out.println("============================================================");
    }
}
