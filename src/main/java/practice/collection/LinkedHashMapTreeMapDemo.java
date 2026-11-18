package practice.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 2026-11-18 任务：LinkedHashMap 保序 + TreeMap 排序与范围查询
 *
 * 视频对应（今天 2 集，共 17:28）：
 *   207 LinkedHashMap                       07:07
 *   208 TreeMap 基本使用和练习1              10:21
 *
 * 今天要打通的一条线：
 *   同样是 Map，三种实现解决三个问题
 *     HashMap       → 只求快（O(1)），不在乎顺序
 *     LinkedHashMap → 既要快，又要「有序」（插入顺序 / 访问顺序）
 *     TreeMap       → 要「按 key 排序」和「范围查询」
 *
 * 而且：LinkedHashMap 的「访问顺序」模式，本身就是现成的 LRU ——
 *      明天 LC146 你就靠这个理解 LRU 的原理。
 *
 * 填完 7 个 TODO，跑 `java practice.collection.LinkedHashMapTreeMapDemo`，10/10 即过关。
 */
public class LinkedHashMapTreeMapDemo {

    // ==================================================================
    // TODO 1（207：证明 LinkedHashMap 保插入顺序）
    // ==================================================================
    /**
     * 依次往 LinkedHashMap 放入 "D","A","C","B"，返回它的 key 列表。
     * 期望结果严格等于 ["D", "A", "C", "B"] —— 插入顺序，跟字母大小无关。
     *
     * 对比：如果放同一个 HashMap，顺序就是散的（11/16 MapDemo 第 9 项你已经验证过）。
     *
     * ⚠️ 这就是 LinkedHashMap 的默认模式：{@code accessOrder = false}（按插入顺序）。
     */
    static List<String> linkedKeepsInsertOrder() {
        throw new UnsupportedOperationException("TODO 1 未实现");
    }

    // ==================================================================
    // TODO 2（207 高阶：访问顺序 + 自动淘汰 = 现成的 LRU）
    // ==================================================================
    /**
     * 用 LinkedHashMap 做一个容量为 3 的 LRU 缓存：
     *
     *   构造时传 {@code accessOrder = true}（第三个参数）—— 这样每次 get/put 都会
     *   把该 key 挪到链表尾部（最近使用）。
     *
     *   然后重写 {@code removeEldestEntry}，当 size() > 3 时返回 true，自动淘汰最旧的。
     *
     * 要求实现的顺序：
     *   put("a", 1) → put("b", 2) → put("c", 3) → get("a")
     *   → put("d", 4)   // 此时该淘汰谁？自己想一想：a 被 get 过，所以 a 变新的了，
     *                   // b 才是最久未使用 → 淘汰 b
     *   最后返回 keySet 的列表，期望 ["c", "a", "d"]（从旧到新）
     *
     * 提示：LinkedHashMap 的迭代顺序就是「从最旧到最新」，
     *      所以淘汰永远发生在**第一个**元素上。
     */
    static List<String> lruByAccessOrder() {
        throw new UnsupportedOperationException("TODO 2 未实现");
    }

    // ==================================================================
    // TODO 3（208：TreeMap 自然排序）
    // ==================================================================
    /**
     * 往 TreeMap<Integer, String> 乱序放入 5 个键，返回 keySet 的列表。
     * 期望：**严格升序**（自然排序），无论插入顺序如何。
     *
     * 用哪些键自己定，但必须包含「插入时是乱序」这个特点，否则证明不了排序。
     *
     * ⚠️ 坑：TreeMap 的 key 必须能比较。Integer 天然可比；如果用自定义对象当 key
     *    而又没实现 Comparable、也没传 Comparator，put 时会抛 ClassCastException。
     */
    static List<Integer> treeMapNaturalOrder() {
        throw new UnsupportedOperationException("TODO 3 未实现");
    }

    // ==================================================================
    // TODO 4（208：自定义 Comparator 让 TreeMap 降序）
    // ==================================================================
    /**
     * 构造一个「按 key 降序」的 TreeMap（构造时传 Comparator.reverseOrder()），
     * 放入任意几个 Integer，返回 keySet 列表 —— 期望是降序。
     *
     * 思考（写进笔记）：如果两个不同的 key 被 Comparator 判为「相等」，
     * TreeMap 会认为它们是同一个 key，后 put 的会**覆盖**前一个。
     * 这一点和 HashMap 完全不同，是 TreeMap 最容易踩的坑。
     */
    static List<Integer> treeMapDescending() {
        throw new UnsupportedOperationException("TODO 4 未实现");
    }

    // ==================================================================
    // TODO 5（208 重点：范围查询 —— HashMap 做不到的事）
    // ==================================================================
    /**
     * 建一个 TreeMap，key 为 10,20,30,40,50（value 随便），然后返回：
     *
     *   [0] floorKey(25)    → 小于等于 25 的最大 key
     *   [1] ceilingKey(25)  → 大于等于 25 的最小 key
     *   [2] floorKey(30)    → 正好命中时返回自己
     *   [3] firstKey()
     *   [4] lastKey()
     *
     * 期望结果：[20, 30, 30, 10, 50]
     *
     * ⚠️ 如果找不到会返回 null（floorKey/ceilingKey 不抛异常），
     *    但 firstKey/lastKey 在空 map 上会抛 NoSuchElementException。
     */
    static List<Integer> floorCeiling() {
        throw new UnsupportedOperationException("TODO 5 未实现");
    }

    // ==================================================================
    // TODO 6（208：subMap 范围查询）
    // ==================================================================
    /**
     * 基于 TODO 5 的那个 map，返回 subMap(20, true, 40, false) 的 key 列表。
     *
     * 期望：[20, 30]（左闭右开，40 不含）
     *
     * 思考：这个能力在业务里对应什么？（提示：查「价格区间内的商品」、
     *      「某时间段内的订单」—— 这正是数据库索引要干的事，TreeMap 就是内存里的 B+ 树近似）
     */
    static List<Integer> subMapRange() {
        throw new UnsupportedOperationException("TODO 6 未实现");
    }

    // ==================================================================
    // TODO 7（208 练习：自定义对象当 key）
    // ==================================================================
    /**
     * 定义一个 Student 类（见文件末尾），按 **年龄升序、同龄按姓名升序** 排序，
     * 放入 TreeMap<Student, String>，返回按此顺序排列的姓名列表。
     *
     * 两种做法任选其一（推荐都试一下）：
     *   ① 让 Student 实现 Comparable<Student>
     *   ② 在构造 TreeMap 时传 Comparator
     *
     * 期望：按年龄升序，同龄按姓名拼音/字典序 —— 用你自己的测试数据验证。
     *
     * ⚠️ 记住：TreeMap 判「相等」用的是 compareTo/compare 返回 0，
     *    **不是** equals！所以两个年龄姓名都一样的学生，后 put 的会覆盖前一个。
     */
    static List<String> treeMapCustomKey() {
        throw new UnsupportedOperationException("TODO 7 未实现");
    }

    // ==================================================================
    // 自测（填完 TODO 直接跑，不许改）
    // ==================================================================
    private static int pass = 0;
    private static int fail = 0;

    private static void t(String name, java.util.function.Supplier<Boolean> body) {
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
        System.out.println("========= LinkedHashMap / TreeMap 自测 =========");

        t("1  LinkedHashMap 严格保持插入顺序 [D,A,C,B]", () ->
                linkedKeepsInsertOrder().equals(Arrays.asList("D", "A", "C", "B")));

        t("2  accessOrder + removeEldestEntry = LRU，淘汰最久未用的 b", () ->
                lruByAccessOrder().equals(Arrays.asList("c", "a", "d")));

        t("3  TreeMap 自然排序：keySet 严格升序", () -> {
            List<Integer> keys = treeMapNaturalOrder();
            List<Integer> copy = new ArrayList<>(keys);
            Collections.sort(copy);
            return keys.equals(copy) && new java.util.HashSet<>(keys).size() == keys.size();
        });

        t("4  TreeMap 传 reverseOrder：keySet 严格降序", () -> {
            List<Integer> keys = treeMapDescending();
            List<Integer> copy = new ArrayList<>(keys);
            copy.sort(Comparator.reverseOrder());
            return keys.equals(copy) && keys.size() >= 3;
        });

        t("5  floor / ceiling / first / last = [20, 30, 30, 10, 50]", () ->
                floorCeiling().equals(Arrays.asList(20, 30, 30, 10, 50)));

        t("6  subMap(20,true,40,false) = [20, 30]", () ->
                subMapRange().equals(Arrays.asList(20, 30)));

        t("7  TreeMap 自定义对象 key：按年龄升序", () -> {
            List<String> names = treeMapCustomKey();
            return names != null && names.size() >= 3;
        });

        t("8  LinkedHashMap 的迭代顺序 = 从最旧到最新（LRU 淘汰删第一个）", () -> {
            LinkedHashMap<String, Integer> m = new LinkedHashMap<>(16, 0.75f, true);
            m.put("x", 1);
            m.put("y", 2);
            m.get("x");                  // x 变最新
            List<String> keys = new ArrayList<>(m.keySet());
            return keys.equals(Arrays.asList("y", "x"));
        });

        t("9  HashMap 的 keySet 顺序不可依赖（同一批数据多次插入顺序可能不同）", () -> {
            // 这条不 assert 具体顺序，只验证「它不是插入顺序」，用足够多的 key 提高概率
            Map<Integer, Integer> m = new java.util.HashMap<>();
            int[] in = {5, 3, 9, 1, 7, 2, 8, 4, 6, 0};
            for (int i : in) {
                m.put(i, i);
            }
            List<Integer> keys = new ArrayList<>(m.keySet());
            List<Integer> insertion = new ArrayList<>();
            for (int i : in) {
                insertion.add(i);
            }
            // 只要不等于插入顺序，就说明 HashMap 确实不保序（极小概率相等，忽略）
            return !keys.equals(insertion) || keys.size() == in.length;
        });

        t("10 TreeMap 的 size 只由 comparator 的「相等」决定，不是 equals", () -> {
            TreeMap<String, Integer> m = new TreeMap<>((a, b) -> 0);   // 所有 key 都"相等"
            m.put("a", 1);
            m.put("b", 2);
            m.put("c", 3);
            return m.size() == 1;
        });

        System.out.println("-----------------------------------------------");
        System.out.printf("结果：PASS %d / FAIL %d%n", pass, fail);
        if (fail == 0) {
            System.out.println("ALL GREEN（10/10）—— 明天 LC146 LRU 你已经有底了");
        } else {
            System.out.println("还有 " + fail + " 项没过");
        }
        System.out.println("===============================================");
    }
}

/**
 * TODO 7 用到的自定义 key。
 * 你可以选择让它 implements Comparable<Student>（按年龄升序、同龄按姓名升序），
 * 或者在 TreeMap 构造时传 Comparator —— 两种都试一遍最好。
 */
class Student2 implements Comparable<Student2> {
    private final String name;
    private final int age;

    Student2(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    @Override
    public String toString() {
        return name + "(" + age + ")";
    }

    @Override
    public int compareTo(Student2 o) {
        throw new UnsupportedOperationException("TODO 7 未实现：compareTo —— 先比 age，再比 name");
    }
}
