package demo;

import java.util.ArrayList;
import java.util.List;

/**
 * GenericPractice —— 泛型练习（学员自写版）
 *
 * 对应黑马 191 泛型类、泛型方法、泛型接口。
 * 泛型是「编译期检查 + 运行期擦除」，三个 TODO 分别验证它的三个侧面。
 *
 * ⚠️ 本文件是教练生成的骨架（2026-10-31 补建）。3 个 TODO 由你实现。
 */
public class GenericPractice {

    /**
     * TODO 1 · 泛型类 Box<T>：能存任意类型、能取出来。
     * 提示：字段 private T value; 两个方法 put / get。
     */
    public static class Box<T> {
        private T value;

        public void put(T v) {
            throw new UnsupportedOperationException("TODO 1-put");
        }

        public T get() {
            throw new UnsupportedOperationException("TODO 1-get");
        }
    }

    /**
     * TODO 2 · 泛型方法：把数组转成 List<T>。
     * 提示：泛型方法声明里 <T> 要写在返回值前面：
     *   public static <T> List<T> arrayToList(T[] arr)
     */
    public static <T> List<T> arrayToList(T[] arr) {
        throw new UnsupportedOperationException("TODO 2");
    }

    /**
     * TODO 3 · 类型擦除验证：Box<String> 和 Box<Integer> 运行期是同一个 class 吗？
     * 提示：调用 getClass() 比较。预期结果是 true —— 因为泛型在编译后被「擦除」成原始类型。
     */
    public static boolean sameRuntimeClass() {
        Box<String> s = new Box<>();
        Box<Integer> i = new Box<>();
        throw new UnsupportedOperationException("TODO 3");
    }

    // ==================================================================
    // 下面是教练写好的 6 条自查，**不要改**。全部 PASS 才算今天过关。
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
        System.out.println("===== GenericPractice 6 条自查 =====\n");

        // TODO 1：泛型类
        Box<String> boxStr = new Box<>();
        boxStr.put("hello");
        check("1. Box<String> 存取", boxStr.get(), "hello");

        Box<Integer> boxInt = new Box<>();
        boxInt.put(42);
        check("2. Box<Integer> 存取", boxInt.get(), 42);

        // TODO 2：泛型方法
        String[] strArr = {"a", "b", "c"};
        List<String> strList = arrayToList(strArr);
        check("3. arrayToList 大小", strList.size(), 3);
        check("4. arrayToList 内容", strList.get(1), "b");

        Integer[] intArr = {1, 2, 3};
        List<Integer> intList = arrayToList(intArr);
        check("5. arrayToList<Integer>", intList.get(0) + intList.get(1) + intList.get(2), 6);

        // TODO 3：类型擦除
        check("6. 类型擦除：Box<String>==Box<Integer> 是同一个 class", sameRuntimeClass(), true);

        System.out.println();
        System.out.println(failed == 0
                ? "===== ALL GREEN（6/6）—— 可以提交 ====="
                : "===== 还有 " + failed + " 条没过 =====");
    }
}
