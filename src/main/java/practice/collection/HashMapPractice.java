package practice.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 2026-11-17 任务：HashMap 基本使用 + 自定义对象当 key + 用 Map 做统计
 * <p>
 * 视频对应（今天 3 集，共 30:49）：
 * 204 HashMap 基本的使用                 04:54
 * 205 HashMap 练习一（存储自定义对象）      09:23
 * 206 HashMap 练习二（利用 Map 进行统计）   16:32
 * <p>
 * 今天要打通的一条线：
 * Map 能存 → 能存「对象」→ 能用 Map 做「计数 / 分组」
 * 这条线直接对应面试里的两类题：①词频统计 ②按 key 分组
 * <p>
 * 填完 6 个 TODO，跑 `java practice.collection.HashMapPractice`，10/10 即过关。
 */
public class HashMapPractice {

    // ==================================================================
    // TODO 1（204：最基础的计数写法）
    // ==================================================================

    /**
     * 统计每个单词出现次数，返回 Map<单词, 次数>。
     * <p>
     * 例子：["a","b","a","c","a"] → {a=3, b=1, c=1}
     * []                   → {}（空 Map，不是 null）
     * <p>
     * ⚠️ 必须用「先 get 再判 null」的老写法，**不许用 Stream**。
     * 因为 206 那集讲的就是这个写法，你要亲手写一遍才知道 `get` 返回 null 的坑。
     * 提示：
     * Integer count = map.get(word);
     * if (count == null) { map.put(word, 1); } else { map.put(word, count + 1); }
     */
    static Map<String, Integer> countWords(String[] words) {
        Map<String, Integer> map = new HashMap<>();
        for (String word : words) {
            if (map.containsKey(word)) {
                map.put(word, map.get(word) + 1);
            } else {
                map.put(word, 1);
            }
        }
        return map;
    }

    // ==================================================================
    // TODO 2（204：同样的事，换一种更短的写法）
    // ==================================================================

    /**
     * 用 **getOrDefault** 或 **merge** 重写上面的逻辑，统计字符出现次数。
     * <p>
     * 例子："hello" → {h=1, e=1, l=2, o=1}
     * ""      → {}（空串返回空 Map）
     * <p>
     * ⚠️ 区分大小写："Aa" → {A=1, a=1}（不要 toLowerCase）
     * <p>
     * 两种写法都可以，但你笔记里必须写清：
     * `merge(ch, 1, Integer::sum)` 这一行到底做了什么？
     */
    static Map<Character, Integer> countChars(String s) {
        Map<Character, Integer> map = new HashMap<>();
        for (char c : s.toCharArray()) {
            map.put(c, map.getOrDefault(c, 0) + 1);
        }
        return map;
    }

    // ==================================================================
    // TODO 3（205：自定义对象当 key）
    // ==================================================================

    /**
     * 定义一个类 Student 作为 Map 的 key（见文件末尾），然后：
     * 1. 往一个 HashMap<Student, Integer> 里放 3 个学生（分数）
     * 2. 用 **new 出来的、id 和 name 都相同的新对象** 去 get
     * 3. 如果能查到，返回 true
     * <p>
     * ⚠️ 这一步在你补完 Student 的 equals/hashCode 之前**一定是 false**。
     * 这就是 205 那集的核心：对象当 key，不重写 equals/hashCode 就查不到。
     * <p>
     * 验证方法：先不写 equals/hashCode 跑一次看 false，写完再跑一次看 true，
     * 两次结果都要记进笔记（这是面试常被追问的点）。
     */
    static boolean customKeyWorks() {
        Map<Student, Integer> map = new HashMap<>();
        map.put(new Student(1, "Tom"), 90);
        map.put(new Student(2, "Jerry"), 85);
        map.put(new Student(3, "Lucy"), 95);

        // 2. 用 new 出来的、id 和 name 都相同的新对象去 get
        Student probe = new Student(1, "Tom");

        // 3. 能查到就返回 true
        boolean b = map.get(probe) != null;
        return b;
    }

    // ==================================================================
    // TODO 4（206：找最大值 —— Map 遍历的经典用法）
    // ==================================================================

    /**
     * 找出分数最高的学生姓名。如果有并列最高，返回**任意一个**即可（测试只校验分数）。
     * 如果 map 为空，返回 null。
     * <p>
     * 例子：{张三=88, 李四=95, 王五=95} → "李四" 或 "王五"
     * <p>
     * 提示：用 entrySet 遍历，别用 keySet + get（那是两倍开销）。
     */
    static String bestStudent(Map<String, Integer> scores) {
        if (scores == null || scores.isEmpty()) return null;
        int max = Integer.MIN_VALUE;;
        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
            }
        }
        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            if (entry.getValue() == max) {
                return entry.getKey();
            }
        }
        return null;
    }

    // ==================================================================
    // TODO 5（206 进阶：Top-K —— 面试高频）
    // ==================================================================

    /**
     * 返回出现次数最多的前 k 个单词，**按次数从大到小**排列。
     * 如果 k 大于 map 大小，返回全部。如果 k <= 0，返回空 List。
     * <p>
     * 例子：{a=3, b=2, c=1}, k=2 → ["a", "b"]
     * <p>
     * 这个 TODO **允许且推荐用 Stream**：
     * map.entrySet().stream()
     * .sorted(...)      // 按 value 降序，相等时按 key 升序保证结果稳定
     * .limit(k)
     * .map(Map.Entry::getKey)
     * .collect(Collectors.toList());
     * <p>
     * 注意：Comparator 里比较 Integer 要用 compareTo，别用减号（虽然能跑，但不规范）。
     */
    static List<String> topK(Map<String, Integer> counts, int k) {
        if (k <= 0 || counts.isEmpty()) {
            return new ArrayList<>();
        }

        return counts.entrySet().stream()
                .sorted((a, b) -> {
                    int cmp = b.getValue().compareTo(a.getValue());  // value 降序
                    if (cmp != 0) return cmp;
                    return a.getKey().compareTo(b.getKey());          // 相等时 key 升序
                })
                .limit(k)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

    }

    // ==================================================================
    // TODO 6（206 延伸：分组 —— 和 LC49 字母异位词分组同一个套路）
    // ==================================================================

    /**
     * 按单词长度分组，返回 Map<长度, 该长度的所有单词>。
     * <p>
     * 例子：["ab","cde","f","gh"] → {1=[f], 2=[ab, gh], 3=[cde]}
     * <p>
     * ⚠️ 组内顺序必须保持**原数组出现顺序**（测试会校验）。
     * ⚠️ 用 computeIfAbsent 写最优雅：
     * map.computeIfAbsent(len, x -> new ArrayList<>()).add(word);
     * <p>
     * 这个套路就是 LC49「字母异位词分组」的骨架 —— 今天写完，明天做 LC49 会很快。
     */
    static Map<Integer, List<String>> groupByLength(String[] words) {
        Map<Integer, List<String>> map = new HashMap<>();
        for (String word : words) {
            int len = word.length();
            map.computeIfAbsent(len, x -> new ArrayList<>()).add(word);
        }
        return map;
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
        System.out.println("================ HashMap 统计 自测 ================");

        t("1  countWords([a,b,a,c,a]) → a=3,b=1,c=1", () -> {
            Map<String, Integer> m = countWords(new String[]{"a", "b", "a", "c", "a"});
            return m.size() == 3 && m.get("a") == 3 && m.get("b") == 1 && m.get("c") == 1;
        });

        t("2  countWords([]) → 空 Map（不是 null）", () -> {
            Map<String, Integer> m = countWords(new String[0]);
            return m != null && m.isEmpty();
        });

        t("3  countChars(\"hello\") → l=2，其余各 1", () -> {
            Map<Character, Integer> m = countChars("hello");
            return m.size() == 4 && m.get('l') == 2 && m.get('h') == 1
                    && m.get('e') == 1 && m.get('o') == 1;
        });

        t("4  countChars(\"Aa\") → 区分大小写：A=1,a=1", () -> {
            Map<Character, Integer> m = countChars("Aa");
            return m.size() == 2 && m.get('A') == 1 && m.get('a') == 1;
        });

        t("5  bestStudent 取到最高分（并列取谁都行）", () -> {
            Map<String, Integer> s = new HashMap<>();
            s.put("张三", 88);
            s.put("李四", 95);
            s.put("王五", 95);
            String best = bestStudent(s);
            return best != null && s.get(best) == 95;
        });

        t("6  bestStudent(空 map) → null", () -> bestStudent(new HashMap<>()) == null);

        t("7  topK({a=3,b=2,c=1}, 2) → [a, b]", () -> {
            Map<String, Integer> m = new HashMap<>();
            m.put("a", 3);
            m.put("b", 2);
            m.put("c", 1);
            return topK(m, 2).equals(Arrays.asList("a", "b"));
        });

        t("8  topK(k 超过 size) → 返回全部", () -> {
            Map<String, Integer> m = new HashMap<>();
            m.put("x", 1);
            return topK(m, 99).size() == 1;
        });

        t("9  groupByLength 分组正确且组内保序", () -> {
            Map<Integer, List<String>> g = groupByLength(new String[]{"ab", "cde", "f", "gh"});
            return g.get(1).equals(Collections.singletonList("f"))
                    && g.get(2).equals(Arrays.asList("ab", "gh"))
                    && g.get(3).equals(Collections.singletonList("cde"));
        });

        t("10 customKeyWorks → 重写 equals/hashCode 后必须是 true", () -> customKeyWorks());

        System.out.println("--------------------------------------------------");
        System.out.printf("结果：PASS %d / FAIL %d%n", pass, fail);
        if (fail == 0) {
            System.out.println("ALL GREEN（10/10）—— 可以去做 LC49 了");
        } else {
            System.out.println("还有 " + fail + " 项没过。如果只有第 10 项没过，说明你还没写 Student 的 equals/hashCode，那是今天最该亲手写的两行代码。");
        }
        System.out.println("==================================================");
    }
}

/**
 * 205 那集的自定义对象。
 * <p>
 * TODO 3 需要你补两件事：
 *   1. 重写 equals：两个 Student 的 id 和 name 都相同就算相等
 *   2. 重写 hashCode：必须和 equals 一致（用 Objects.hash(id, name) 最省事）
 * <p>
 * 不写会怎样？customKeyWorks() 返回 false —— 因为 HashMap 先用 hashCode 定位桶，
 * 再用 equals 比较。不重写就是「两个不同对象」，永远查不到。
 */
class Student {
    private final int id;
    private final String name;

    Student(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Student{id=" + id + ", name='" + name + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return id == student.id && Objects.equals(name, student.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
