package demo;

import java.util.Arrays;
import java.util.Comparator;

/**
 * ArraysPractice —— Arrays 工具类练习（学员自写版）
 *
 * 对应黑马 181 常用算法的 API-Arrays（27:15，标注「必」）。
 * Arrays 是写 Java 绕不开的工具类，面试会问「Arrays.asList 为什么不能 add」。
 *
 * ⚠️ 本文件是教练生成的骨架（2026-10-23）。2 个方法体由你实现。
 */
public class ArraysPractice {

    private ArraysPractice() {
        // 工具类不实例化
    }

    /**
     * 用 Comparator 按「字符串长度升序」排序。
     * 提示：Arrays.sort(arr, Comparator.comparingInt(String::length))
     */
    public static void sortByLength(String[] arr) {
        Arrays.sort(arr, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.length() - o2.length();
            }
        });

    }

    /**
     * 用 Comparator 按「年龄升序，年龄相同按名字字典序」排序。
     * 提示：Comparator.comparingInt(...).thenComparing(...)
     */
    public static void sortByAgeThenName(Person[] arr) {
        Arrays.sort(arr, Comparator.comparingInt((Person p) -> p.age)
                                   .thenComparing(p -> p.name));
    }

    /** 内部类，别改 */
    public static class Person {
        public String name;
        public int age;
        public Person(String name, int age) { this.name = name; this.age = age; }
        @Override public String toString() { return name + "(" + age + ")"; }
    }

    // ==================================================================
    // 下面是教练写好的 6 条自查（不动）。
    // ==================================================================

    private static int failed = 0;

    private static void check(String name, boolean ok) {
        if (!ok) failed++;
        System.out.println((ok ? "[PASS] " : "[FAIL] ") + name);
    }

    public static void main(String[] args) {
        System.out.println("===== ArraysPractice 自查 =====\n");

        // 实验 1：Arrays.asList 的坑（必背）
        String[] arr = {"a", "bb", "ccc"};
        try {
            java.util.List<String> list = Arrays.asList(arr);
            list.add("dddd");
            check("1. Arrays.asList 能 add", false);
        } catch (UnsupportedOperationException e) {
            check("1. Arrays.asList 不能 add（抛 UnsupportedOperationException）", true);
        }

        // 实验 2：排序
        String[] strs = {"ccc", "a", "bb"};
        sortByLength(strs);
        check("2. 按长度排序", strs[0].equals("a") && strs[2].equals("ccc"));

        // 实验 3：toString 转打印
        check("3. Arrays.toString", Arrays.toString(new int[]{1,2,3}).equals("[1, 2, 3]"));

        // 实验 4：fill
        int[] fill = new int[5];
        Arrays.fill(fill, 7);
        check("4. Arrays.fill", fill[0] == 7 && fill[4] == 7);

        // 实验 5：多字段排序
        Person[] people = {
            new Person("张三", 30), new Person("李四", 25), new Person("王五", 25)
        };
        sortByAgeThenName(people);
        check("5. 年龄升序", people[0].age == 25 && people[2].age == 30);
        check("6. 同年龄按名字", people[0].name.equals("李四") && people[1].name.equals("王五"));

        System.out.println();
        System.out.println(failed == 0
                ? "===== ALL GREEN —— 可以提交 ====="
                : "===== 还有 " + failed + " 条没过 =====");
    }
}