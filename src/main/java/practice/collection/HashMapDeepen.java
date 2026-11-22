package practice.collection;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * HashMap 源码深挖靶场（2026-11-22）
 *
 * ============================ 为什么有这一个 ============================
 * 你 11/21 把 MyHashMapDemo 打到了 10/10 —— put / get / resize 都自己写出来了。
 * 这说明「怎么做」你已经会了。
 *
 * 但黑马 213、214 讲的是「为什么这么做」，面试官也主要问这个：
 *   - 为什么 hash 要做 `h ^ (h >>> 16)` 扰动？
 *   - 为什么下标用 `(n-1) & hash` 而不是 `hash % n`？
 *   - 为什么容量必须是 2 的幂？
 *   - 扩容时为什么不用重新算哈希，只判断 `hash & oldCap` 就够了？
 *
 * 这 4 个问题，我在这个文件里做成 4 个可运行的方法。
 * 你不是「背答案」，你是把它算出来看一遍 —— 亲手跑出来的结论，面试时讲得比背的稳十倍。
 *
 * ============================ 今天只写 4 个方法 ============================
 * 现在：PASS 0 / FAIL 8
 * 目标：PASS 8 / FAIL 0
 */
public class HashMapDeepen {

    // ==================================================================
    // TODO 1
    // JDK HashMap 的 hash 扰动：
    //     static final int hash(Object key) {
    //         int h;
    //         return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    //     }
    // 你的任务：实现这一行（不考虑 null，传进来的 h 已经是 hashCode）。
    //
    // 写完先自己算一遍：spread(0x12345678) 结果是多少？
    // 提示：>>> 是无符号右移，高 16 位会补 0。
    // 为什么要这么干？（写进 notes/16 第三节）
    // ==================================================================
    static int spread(int h) {
        throw new UnsupportedOperationException("TODO 1");
    }

    // ==================================================================
    // TODO 2
    // JDK 算桶下标的写法：tab[(n - 1) & hash]
    // 你的任务：实现 indexOf(hash, cap)。
    // 注意用位运算，不要用取模。
    //
    // ⚠️ 想想：为什么这里不写 hash % cap？
    //    （提示：负数取模在 Java 里是负数；位运算没有这个问题；而且 & 比 % 快）
    // ==================================================================
    static int indexOf(int hash, int cap) {
        throw new UnsupportedOperationException("TODO 2");
    }

    // ==================================================================
    // TODO 3
    // 给定一批 key 的 hashCode 和容量，返回「桶下标 -> 这个桶里的所有 key」。
    // TreeMap 保证输出按桶下标从小到大排，方便你肉眼看出分布。
    //
    // 用途：跑 bucketOf(new int[]{0, 16, 48}, 16) 你会看到
    //       {0=[0, 16, 48]} —— 三个 key 全撞在 0 号桶。
    //       这就是「为什么需要扰动」和「为什么会树化」的入口。
    // ==================================================================
    static TreeMap<Integer, List<Integer>> bucketOf(int[] keys, int cap) {
        throw new UnsupportedOperationException("TODO 3");
    }

    // ==================================================================
    // TODO 4（最重要的一题，直接对应 214 集的内容）
    // 计算：容量从 16 扩到 32 时，有多少个 key 会换到别的桶里。
    //
    // JDK 的做法：扩容时**不重新算 hash**，只判断
    //      if ((e.hash & oldCap) == 0)  留在原位置 j
    //      else                         挪到 j + oldCap
    //
    // 你的任务：用 indexOf 分别算 cap=16 和 cap=32 时的桶下标，
    //           统计「两个下标不相同」的 key 个数。
    //
    // 跑完你会看到：bucketOf({0,16,48}, 16) 全在 0 号桶，
    //              扩到 32 后 48 和 16 跑到 16 号桶 —— 这就是「下标差一个 oldCap」。
    // ==================================================================
    static int rehashDiff(int[] keys) {
        throw new UnsupportedOperationException("TODO 4");
    }

    // ==================================================================
    // ============================ 自测区 ============================
    // 不要修改。当前 PASS 0 / FAIL 8。
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
        System.out.println("=== HashMapDeepen 自测 ===");

        check("1. spread(0) == 0", () -> spread(0) == 0);

        check("2. spread 只动低 16 位：spread(0x12345678) 高 16 位仍是 0x1234", () ->
                (spread(0x12345678) >>> 16) == 0x1234);

        check("3. indexOf(-1, 16) == 15（负数也不会越界，取模会得到 -1）", () ->
                indexOf(-1, 16) == 15);

        check("4. cap 是 2 的幂时，indexOf(h,16) 等价于 h % 16", () ->
                indexOf(17, 16) == (17 % 16) && indexOf(1023, 16) == (1023 % 16));

        check("5. cap 不是 2 的幂时两者不等价：indexOf(17,15)=1 但 17%15=2", () ->
                indexOf(17, 15) != (17 % 15));

        check("6. bucketOf({0,16,48}, 16) → 三个 key 全挤在 0 号桶", () -> {
            TreeMap<Integer, List<Integer>> m = bucketOf(new int[]{0, 16, 48}, 16);
            return m.size() == 1
                    && m.containsKey(0)
                    && m.get(0).size() == 3;
        });

        check("7. 扩到 32 后：0 号桶只剩 [0]，16 号桶有 [16, 48]", () -> {
            TreeMap<Integer, List<Integer>> m = bucketOf(new int[]{0, 16, 48}, 32);
            return m.size() == 2
                    && m.get(0).equals(List.of(0))
                    && m.get(16).size() == 2
                    && m.get(16).containsAll(List.of(16, 48));
        });

        check("8. rehashDiff({0,16,48}) == 2 且 rehashDiff({1,2,3}) == 0", () ->
                rehashDiff(new int[]{0, 16, 48}) == 2
                        && rehashDiff(new int[]{1, 2, 3}) == 0);

        System.out.println();
        System.out.println("结果：PASS " + pass + " / FAIL " + fail);

        if (fail == 0) {
            System.out.println();
            System.out.println("全绿。现在你能回答这四个面试题了（都要能白板讲 60 秒）：");
            System.out.println("  1. 为什么 hash 要高低 16 位异或？");
            System.out.println("  2. 为什么下标用 & 不用 %？");
            System.out.println("  3. 为什么容量必须是 2 的幂？");
            System.out.println("  4. 扩容时为什么只判断 hash & oldCap？");
            System.out.println();
            System.out.println("把这四个答案写进 notes/collections/16-HashMap源码.md 第三节。");
        }
    }
}
