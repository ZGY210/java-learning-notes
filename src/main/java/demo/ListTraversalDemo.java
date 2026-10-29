package demo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import practice.mycollection.MyArrayList;

/**
 * ListTraversalDemo —— List 五种遍历 + 迭代器删除演示（学员自写版）
 *
 * 对应黑马 187 List 常见方法和五种遍历方式。
 * 五种遍历各写一个方法，各自返回按顺序拼接的字符串，由 main 统一校验；
 * 最后用你手写的 MyArrayList 演示「边遍历边删除只能靠迭代器」。
 *
 * ⚠️ 本文件是教练补建的骨架：2026-10-27 任务卡里写了这个文件名却没生成，10-30 补上。
 *    6 个方法体全部由你实现。
 */
public class ListTraversalDemo {

    private ListTraversalDemo() {
        // 工具类不实例化
    }

    /**
     * 方式 1：普通 for（按下标）。
     * 场景：需要按下标随机访问、或需要知道「当前是第几个」。
     * 提示：for (int i = 0; i < list.size(); i++) { sb.append(list.get(i)); }
     */
    public static String byIndex(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
        }
        return sb.toString();
    }

    /**
     * 方式 2：增强 for（for-each）。
     * 场景：只读遍历，日常最常用。
     * 提示：for (String s : list) { sb.append(s); }
     */
    public static String byForEach(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (String i:list){
            sb.append(i);
       }
        return sb.toString();
    }

    /**
     * 方式 3：Iterator。
     * 场景：需要边遍历边删除（用 it.remove()）。
     * 提示：for (Iterator<String> it = list.iterator(); it.hasNext(); ) { sb.append(it.next()); }
     */
    public static String byIterator(List<String> list) {
        Iterator<String> it = list.iterator();
        StringBuilder sb = new StringBuilder();
        while (it.hasNext()) {
            sb.append(it.next());
        }
        return sb.toString();
    }

    /**
     * 方式 4：ListIterator。
     * 场景：需要双向移动、或遍历中定位「上一个 / 下一个」。
     * 提示：for (ListIterator<String> it = list.listIterator(); it.hasNext(); ) { sb.append(it.next()); }
     */
    public static String byListIterator(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (ListIterator<String> it = list.listIterator(); it.hasNext(); ) { sb.append(it.next()); }
        return sb.toString();
    }

    /**
     * 方式 5：forEach + lambda。
     * 场景：一行代码快速遍历，不需要下标也不需要删除。
     * 提示：list.forEach(s -> sb.append(s));
     */
    public static String byLambda(List<String> list) {
        StringBuilder sb = new StringBuilder();
        list.forEach(s -> sb.append(s));
        return sb.toString();
    }

    /**
     * 用迭代器边遍历边删除：删掉所有值等于 target 的元素。
     *
     * 关键：必须用 it.remove()，不能用 list.remove(...)。
     * 集合的 remove 会让 modCount++，迭代器的 expectedModCount 没同步，下一次 next() 抛
     * ConcurrentModificationException；it.remove() 删完会把 expectedModCount 重新同步回去。
     *
     * 提示：Iterator<String> it = list.iterator();
     *       while (it.hasNext()) { if (target.equals(it.next())) it.remove(); }
     */
    public static void removeByIterator(MyArrayList<String> list, String target) {
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            if (it.next().equals(target)) {
                it.remove();
            }
        }
    }

    // ==================================================================
    // 下面是教练写好的 7 条自查，**不要改**。全部 PASS 才算今天过关。
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
        System.out.println("===== ListTraversalDemo 7 条自查 =====\n");
        List<String> list = new ArrayList<>(List.of("a", "b", "c"));

        check("1. 普通 for", byIndex(list), "abc");
        check("2. 增强 for", byForEach(list), "abc");
        check("3. Iterator", byIterator(list), "abc");
        check("4. ListIterator", byListIterator(list), "abc");
        check("5. lambda", byLambda(list), "abc");

        // 6-7 迭代器删除：删掉 "b"
        MyArrayList<String> mine = new MyArrayList<>();
        mine.add("a"); mine.add("b"); mine.add("c");
        removeByIterator(mine, "b");
        check("6. 迭代器删除后 size==2", mine.size(), 2);
        check("7. 剩余元素是 a,c", mine.get(0) + mine.get(1), "ac");

        System.out.println();
        System.out.println(failed == 0
                ? "===== ALL GREEN —— 可以提交 ====="
                : "===== 还有 " + failed + " 条没过 =====");
    }
}
