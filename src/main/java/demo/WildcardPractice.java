package demo;

import java.util.ArrayList;
import java.util.List;

/**
 * WildcardPractice —— 泛型通配符练习（学员自写版）
 *
 * 对应黑马 192 泛型的通配符和综合练习。
 * 通配符是泛型的另一半：`? extends` 上界、`? super` 下界。
 * 核心记忆是 PECS：Producer Extends, Consumer Super。
 *
 * ⚠️ 本文件是教练生成的骨架（2026-11-01 补建）。3 个 TODO 由你实现。
 */
public class WildcardPractice {

    /**
     * TODO 1 · `? extends` 只能读：接收 List<? extends Number>，求和返回 double。
     * 提示：里面只能 get（读），不能 add（写）。get 出来的类型是 Number。
     */
    public static double sum(List<? extends Number> list) {
        double sum = 0;
        for (Number n : list) {
            sum += n.doubleValue();
        }
        return sum;
    }

    /**
     * TODO 2 · `? super` 只能写：往 List<? super Integer> 里添加元素。
     * 提示：里面只能 add（写），不能 get 出具体类型（get 出来是 Object）。
     * 把 list 里现有元素个数作为返回值，方便自查。
     */
    public static int addIntegers(List<? super Integer> list) {
        list.add(1);
        list.add(2);
        return list.size();
    }

    /**
     * TODO 3 · PECS 记忆：把下面这句话补完整，作为方法注释。
     * Producer Extends, Consumer Super ——
     * 方法「往外拿」数据（生产）用 extends；方法「往里放」数据（消费）用 super。
     */
    public static String pecsRule() {
        return "PECS: Producer Extends, Consumer Super —— 往外拿数据（生产）用 extends，往里放数据（消费）用 super。";
    }

    // ==================================================================
    // 下面是教练写好的 8 条自查，**不要改**。全部 PASS 才算今天过关。
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
        System.out.println("===== WildcardPractice 8 条自查 =====\n");

        // TODO 1：? extends 只能读
        List<Integer> ints = new ArrayList<>(List.of(1, 2, 3));
        check("1. sum<Integer> 求和", sum(ints), 6.0);
        List<Double> doubles = new ArrayList<>(List.of(1.5, 2.5));
        check("2. sum<Double> 求和", sum(doubles), 4.0);

        // TODO 2：? super 只能写
        List<Object> objs = new ArrayList<>();
        int before = objs.size();
        int after = addIntegers(objs);
        check("3. addIntegers 后 size+2", after - before, 2);
        check("4. 元素能写进去", objs.get(0), 1);
        check("5. 元素能写进去", objs.get(1), 2);

        // TODO 3：PECS 记忆
        String rule = pecsRule();
        check("6. PECS 包含 Producer", rule.contains("Producer"), true);
        check("7. PECS 包含 Consumer", rule.contains("Consumer"), true);
        check("8. PECS 包含 Extends/Super", rule.contains("extends") || rule.contains("super"), true);

        System.out.println();
        System.out.println(failed == 0
                ? "===== ALL GREEN（8/8）—— 可以提交 ====="
                : "===== 还有 " + failed + " 条没过 =====");
    }
}
