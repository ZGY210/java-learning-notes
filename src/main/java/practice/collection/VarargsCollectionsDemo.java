package practice.collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 可变参数 + Collections 工具类靶场（2026-11-26 · 对应黑马 217、218）
 *
 * ============================ 为什么两天并一天还能做得完 ============================
 * 11/24 是 217「可变参数」12:22 + 218「Collections」05:49 —— 加起来 18 分钟视频。
 * 11/25 是 219–223「综合练习」，属于「选/快」档，跳着看就行。
 * 两天加起来的内容量，其实不到一天。
 *
 * 所以今天这一张卡就把 11/24 + 11/25 一起收掉，然后 11/27 起进收口周。
 *
 * ============================ 今天只写 5 个方法 ============================
 * 现在：PASS 0 / FAIL 10
 * 目标：PASS 10 / FAIL 0
 */
public class VarargsCollectionsDemo {

    // ==================================================================
    // TODO 1
    // 可变参数求和。没传参数时返回 0。
    //
    // 关键认知：`int... nums` 在字节码层面**就是一个 int[]**。
    // 所以 sum() 等价于 sum(new int[0])，sum(1,2,3) 是编译器帮你打包成数组。
    // 也正因如此，你**不能**同时写 sum(int... nums) 和 sum(int[] nums) —— 它们是同一个签名，编译报错。
    // ==================================================================
    static int sum(int... nums) {
        int total = 0;
        for (int n : nums) {
            total += n;
        }
        return total;
    }

    // ==================================================================
    // TODO 2
    // 求最大值：必传一个 first，后面跟任意多个。
    // 返回 first 和 rest 里最大的那个。
    //
    // 为什么这么设计签名？—— 因为**可变参数必须是形参列表的最后一个**。
    // 不能写 `max(int... rest, int last)`，编译器不允许。
    // 所以「至少要有一个」这种约束，只能靠「前面写一个固定参数」来表达。
    // ==================================================================
    static int max(int first, int... rest) {
        int  max = first;
        for (int n : rest) {
            max = Math.max(max, n);
        }
        return max;
    }

    // ==================================================================
    // TODO 3
    // 返回 true 表示「List.of(...) 造出来的集合是不可变的」。
    //
    // 你的做法：造一个 List.of("a")，尝试 add("b")，捕获异常。
    //   抛 UnsupportedOperationException → 返回 true（说明不可变）
    //   没抛 → 返回 false
    //
    // ⚠️ 这里有个必须搞清的区分（面试常问）：
    //   - List.of(...)                     → 真正的不可变集合，任何修改都抛异常
    //   - Collections.unmodifiableList(x)  → 只是一个**视图**，原 list 改了它跟着变
    //   这两个不是一回事。
    // ==================================================================
    static boolean listOfIsImmutable() {
        List<String> list = List.of("a");
        try {
            list.add("b");
            // 没抛异常 → 说明可修改 → 不是不可变集合
            return false;
        } catch (UnsupportedOperationException e) {
            // 抛了异常 → 说明不可变
            return true;
        }
    }

    // ==================================================================
    // TODO 4
    // 返回一个「排好序的新 list」，**不要改动传进来的 src**。
    //
    // 为什么不能直接 Collections.sort(src)？—— 因为如果 src 是 List.of(...) 出来的，
    // sort 会抛 UnsupportedOperationException（List.of 产出的集合不允许写）。
    // 所以正确做法是：先 new ArrayList<>(src) 复制一份，再排序。
    //
    // 这一题练的是「不可变集合传进工具方法时的防御性拷贝」—— 真实项目里天天遇到。
    // ==================================================================
    static List<String> sortCopy(List<String> src) {
        List<String> dest = new ArrayList<>(src);
        Collections.sort(dest);
        return dest;
    }

    // ==================================================================
    // TODO 5
    // 返回「出现次数最多的那个元素的次数」。空 list 返回 0。
    //
    // 用 Collections.frequency(list, element) 实现：遍历每个元素，看它有几次，取最大。
    // 时间复杂度 O(n^2)，但这里只要你能用对 API。真实业务里会用 Map 统计（你 11/22 写过）。
    //
    // 想一想：如果要求返回「出现次数最多的那个元素」，而有两个元素次数并列，怎么办？
    //   → 写进 notes/18 的问题里。
    // ==================================================================
    static int maxFrequency(List<String> words) {
        int max = 0;
        for (String word : words) {
            max = Math.max(max, Collections.frequency(words, word));
        }
        return max;
    }

    // ==================================================================
    // ============================ 自测区 ============================
    // 不要修改。
    // ==================================================================

    static int pass = 0;
    static int fail = 0;

    static void check(String name, java.util.function.BooleanSupplier body) {
        boolean ok;
        try {
            ok = body.getAsBoolean();
        } catch (Throwable t) {
            fail++;
            System.out.println("  FAIL  " + name + "   → " + t.getClass().getSimpleName() + ": " + t.getMessage());
            return;
        }
        if (ok) {
            pass++;
            System.out.println("  PASS  " + name);
        } else {
            fail++;
            System.out.println("  FAIL  " + name);
        }
    }

    public static void main(String[] args) {
        System.out.println("=== VarargsCollectionsDemo 自测 ===");

        check("1. sum() == 0（空可变参数 = 空数组）", () -> sum() == 0);

        check("2. sum(1,2,3) == 6", () -> sum(1, 2, 3) == 6);

        check("3. sum(new int[]{4,5}) == 9（数组可以直接喂给可变参数）", () ->
                sum(new int[]{4, 5}) == 9);

        check("4. max(5) == 5（只传固定参数）", () -> max(5) == 5);

        check("5. max(1,9,3) == 9", () -> max(1, 9, 3) == 9);

        check("6. List.of(...) 是不可变的", () -> listOfIsImmutable());

        check("7. sortCopy 返回 [a, b, c]", () -> {
            List<String> sorted = sortCopy(List.of("c", "a", "b"));
            return sorted != null && sorted.equals(List.of("a", "b", "c"));
        });

        check("8. sortCopy 没有改动原 list（原 list 第一个还是 c）", () -> {
            List<String> src = List.of("c", "a", "b");
            sortCopy(src);
            return src.get(0).equals("c");
        });

        check("9. maxFrequency([a,b,a,c,a]) == 3", () ->
                maxFrequency(List.of("a", "b", "a", "c", "a")) == 3);

        check("10. maxFrequency([]) == 0", () -> maxFrequency(List.of()) == 0);

        System.out.println();
        System.out.println("结果：PASS " + pass + " / FAIL " + fail);

        if (fail == 0) {
            System.out.println();
            System.out.println("全绿。你现在能回答这四件事：");
            System.out.println("  1. 可变参数本质是什么？为什么必须放最后？");
            System.out.println("  2. List.of 和 Collections.unmodifiableList 差在哪？");
            System.out.println("  3. 为什么不能对 List.of 出来的集合直接 Collections.sort？");
            System.out.println("  4. Arrays.asList 有什么坑？（这条去 notes/18 第三节答）");
        }
    }
}
