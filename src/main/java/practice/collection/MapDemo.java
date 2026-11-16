package practice.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 2026-11-16 任务：Map 接口的常用 API + **三种遍历方式**
 *
 * 视频对应（今天 5 集，共 47:45）：
 *   199 双列集合的特点              05:39
 *   200 Map 集合常用的 API          15:36
 *   201 Map 第一种遍历（键找值）      07:58
 *   202 Map 第二种遍历（键值对）      10:48
 *   203 Map 第三种遍历（lambda）      07:44
 *
 * 今天只回答三个问题：
 *   1. Map 和 Collection 是什么关系？—— 答案是「没关系」，它们是两套体系
 *   2. 三种遍历分别靠什么？—— keySet / entrySet / forEach
 *   3. 为什么 HashMap 的遍历顺序「看起来没规律」？
 *
 * 填完 6 个 TODO，跑 `java practice.collection.MapDemo`，10/10 即过关。
 */
public class MapDemo {

    // ==================================================================
    // TODO 1（热身：熟悉 put / get）
    // ==================================================================
    /**
     * 建一个 HashMap<String, Integer>，依次放入：
     *   语文 → 90，数学 → 95，英语 → 88
     * 然后返回它。
     *
     * 注意：HashMap 的迭代顺序**不是插入顺序**，所以下面的测试不会比较顺序，
     *      而是把结果排序后再比。这一点（无序）今天要写进笔记。
     */
    static Map<String, Integer> build() {
        Map<String, Integer> map = new HashMap<>();
        map.put("语文", 90);
        map.put("数学", 95);
        map.put("英语", 88);
        return map;
    }

    // ==================================================================
    // TODO 2（第一种遍历：键找值）
    // ==================================================================
    /**
     * 用 **keySet() + get(key)** 遍历，返回形如 ["语文=90", "数学=95", "英语=88"] 的列表。
     *
     * 返回 List<String>，每个元素是 "key=value"。
     * 顺序不用管，测试会排序。
     *
     * 提醒：这种写法每次 get 都要再查一次哈希表，**是最慢的一种遍历**。
     *      能说出「为什么慢」是今天的加分点。
     */
    static List<String> traverseByKeySet(Map<String, Integer> map) {
        List<String> res = new ArrayList<>();
        for(String key : map.keySet()) {
            res.add(key + "=" + map.get(key));
        }
        return res;
    }

    // ==================================================================
    // TODO 3（第二种遍历：键值对，推荐写法）
    // ==================================================================
    /**
     * 用 **entrySet()** 遍历，同样返回 ["语文=90", ...] 的列表。
     * 每个元素是 Map.Entry<String,Integer>，用 getKey() / getValue() 取值。
     *
     * 这是**实际开发最推荐**的写法：一次遍历同时拿到 key 和 value，不用二次查找。
     */
    static List<String> traverseByEntry(Map<String, Integer> map) {
        List<String> res = new ArrayList<>();
        for(Map.Entry<String, Integer> entry : map.entrySet()) {
            res.add(entry.getKey() + "=" + entry.getValue());
        }
        return res;
    }

    // ==================================================================
    // TODO 4（第三种遍历：forEach + lambda）
    // ==================================================================
    /**
     * 用 **map.forEach((k, v) -> ...)** 遍历，同样返回 ["语文=90", ...] 的列表。
     *
     * 注意：forEach 里的 lambda 不能直接修改外部方法的局部变量（effectively final），
     *      所以你要往一个**已经在外面创建好**的 List 里 add。
     */
    static List<String> traverseByLambda(Map<String, Integer> map) {
        List<String> res = new ArrayList<>();
        map.forEach((s, integer) -> res.add(s + "=" + integer));
        return res;
    }

    // ==================================================================
    // TODO 5（常用 API 的返回值，这块全是坑）
    // ==================================================================
    /**
     * 依次验证下面 5 件事，**全部成立才返回 true**：
     *
     *   1. map.put("新key", 1) 在 key 不存在时返回 null
     *   2. map.put("新key", 2) 在 key 已存在时返回**旧值 1**（不是 null！这是坑）
     *   3. map.getOrDefault("不存在", -1) 返回 -1
     *   4. map.containsKey("新key") 为 true，containsValue(2) 为 true
     *   5. map.remove("新key") 返回被删掉的值 2，删完 containsKey 为 false
     *
     * 用一个新的 HashMap 来做，别污染 build() 的结果。
     */
    static boolean apiBehaviors() {
       Map<String, Integer> map = new HashMap<>();
        Object r1 = map.put("新key", 1);
        boolean c1 = (r1 == null);

        // 2. key 已存在时 put 返回旧值 1（不是 null）
        Integer r2 = map.put("新key", 2);
        boolean c2 = (r2 != null && r2 == 1);

        // 3. getOrDefault 对不存在的 key 返回默认值 -1
        Integer r3 = map.getOrDefault("不存在", -1);
        boolean c3 = (r3 == -1);

        // 4. containsKey / containsValue
        boolean c4 = map.containsKey("新key") && map.containsValue(2);

        // 5. remove 返回被删的值，删完 containsKey 为 false
        Integer r5 = map.remove("新key");
        boolean c5 = (r5 != null && r5 == 2) && !map.containsKey("新key");

        return c1 && c2 && c3 && c4 && c5;
    }

    // ==================================================================
    // TODO 6（用 entrySet 做一次真正的计算）
    // ==================================================================
    /**
     * 把 map 里所有 value 加起来返回（用 entrySet 遍历，不许用 values().stream()）。
     * 例：{语文=90, 数学=95, 英语=88} → 273
     *
     * 提示：如果某个 value 是 null 会 NPE —— 想想为什么实际业务里很少往 Map 里放 null。
     */
    static int sumValues(Map<String, Integer> map) {
        int sum = 0;
        for(Map.Entry<String, Integer> entry : map.entrySet()) {
            sum += entry.getValue();
        }
        return sum;
    }

    // ==================================================================
    // 自测（填完 TODO 直接跑，不许改）
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

    /** 把 List 排序后返回，屏蔽 HashMap 无序带来的干扰 */
    private static List<String> sorted(List<String> list) {
        List<String> copy = new ArrayList<>(list);
        Collections.sort(copy);
        return copy;
    }

    public static void main(String[] args) {
        System.out.println("================== Map 三种遍历 自测 ==================");

        t("1  build() 有 3 个元素且值正确", () -> {
            Map<String, Integer> m = build();
            return m.size() == 3 && m.get("语文") == 90 && m.get("数学") == 95 && m.get("英语") == 88;
        });

        t("2  遍历一（keySet + get）结果正确", () ->
                sorted(traverseByKeySet(build())).equals(Arrays.asList("数学=95", "英语=88", "语文=90")));

        t("3  遍历二（entrySet）结果正确", () ->
                sorted(traverseByEntry(build())).equals(Arrays.asList("数学=95", "英语=88", "语文=90")));

        t("4  遍历三（forEach + lambda）结果正确", () ->
                sorted(traverseByLambda(build())).equals(Arrays.asList("数学=95", "英语=88", "语文=90")));

        t("5  三种遍历结果完全一致（元素集合相同）", () -> {
            Map<String, Integer> m = build();
            return sorted(traverseByKeySet(m)).equals(sorted(traverseByEntry(m)))
                    && sorted(traverseByEntry(m)).equals(sorted(traverseByLambda(m)));
        });

        t("6  五个 API 行为全部正确（put 返回旧值是重点）", () -> apiBehaviors());

        t("7  sumValues 求和 = 273", () -> sumValues(build()) == 273);

        t("8  get 不存在的 key 返回 null", () -> build().get("物理") == null);

        t("9  HashMap 无序 vs LinkedHashMap 保插入顺序", () -> {
            Map<String, Integer> hash = new HashMap<>();
            Map<String, Integer> linked = new LinkedHashMap<>();
            for (String s : Arrays.asList("香蕉", "苹果", "西瓜", "梨")) {
                hash.put(s, 1);
                linked.put(s, 1);
            }
            // LinkedHashMap 必须严格保持插入顺序
            List<String> linkedKeys = new ArrayList<>(linked.keySet());
            return linkedKeys.equals(Arrays.asList("香蕉", "苹果", "西瓜", "梨"));
        });

        t("10 containsKey 对自定义对象 key 需要重写 equals/hashCode（String 已重写，所以能查到）", () -> {
            Map<String, Integer> m = new HashMap<>();
            m.put(new String("语文"), 90);
            // new String 是两个不同对象，但 String 重写了 equals/hashCode → 能查到
            return m.get(new String("语文")) != null;
        });

        System.out.println("------------------------------------------------------");
        System.out.printf("结果：PASS %d / FAIL %d%n", pass, fail);
        if (fail == 0) {
            System.out.println("ALL GREEN（10/10）—— 可以去做 LC41 了");
        } else {
            System.out.println("还有 " + fail + " 项没过");
        }
        System.out.println("======================================================");
    }
}
