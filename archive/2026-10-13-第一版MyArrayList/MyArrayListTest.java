package practice.mycollection;

import java.util.ConcurrentModificationException;
import java.util.Iterator;

/**
 * MyArrayList 的验收脚本（P1 集合框架 D1–D2）。
 * 用法：把本文件和 MyArrayList.java 放在同一个包下，直接运行 main。
 * 判据：所有输出都是 [PASS]，出现 [FAIL] 说明该方法还有问题。
 *
 * 注意：不要修改本文件里的断言，改了就等于改考卷。
 */
public class MyArrayListTest {

    private static int failed = 0;

    public static void main(String[] args) {
        testAddAndGet();
        testSet();
        testRemoveAndShift();
        testIndexOutOfBounds();
        testIterator();
        testFailFast();
        System.out.println(failed == 0
                ? "\n全部通过 ✅  MyArrayList 可以 push 了"
                : "\n有 " + failed + " 项未通过 ❌  修完再 push");
    }

    private static void testAddAndGet() {
        MyArrayList<Integer> list = new MyArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add(i);                 // 触发多次 1.5 倍扩容
        }
        check("add 100 个元素后 size=100", list.size() == 100);
        check("get(0)=0", list.get(0) == 0);
        check("get(99)=99", list.get(99) == 99);
        check("扩容后顺序不错乱", list.get(37) == 37 && list.get(55) == 55);
    }

    private static void testSet() {
        MyArrayList<String> list = new MyArrayList<>();
        list.add("a");
        String old = list.set(0, "b");
        check("set 返回被覆盖的旧值", "a".equals(old));
        check("set 后 get 到新值", "b".equals(list.get(0)));
    }

    private static void testRemoveAndShift() {
        MyArrayList<Integer> list = new MyArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add(i);                 // [0,1,2,3,4]
        }
        int removed = list.remove(1);    // 删掉 1，后面的元素必须整体前移
        check("remove 返回被删元素", removed == 1);
        check("remove 后 size=4", list.size() == 4);
        check("remove 后元素前移正确", list.get(1) == 2 && list.get(2) == 3 && list.get(3) == 4);
        check("删尾巴", list.remove(3) == 4 && list.size() == 3);
    }

    private static void testIndexOutOfBounds() {
        MyArrayList<Integer> list = new MyArrayList<>();
        list.add(1);
        check("get(-1) 抛 IndexOutOfBoundsException", throwsIOOBE(() -> list.get(-1)));
        check("get(size) 抛 IndexOutOfBoundsException", throwsIOOBE(() -> list.get(1)));
        check("remove(99) 抛 IndexOutOfBoundsException", throwsIOOBE(() -> list.remove(99)));
    }

    private static void testIterator() {
        MyArrayList<Integer> list = new MyArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add(i);
        }
        int sum = 0;
        for (int v : list) {
            sum += v;
        }
        check("for-each 遍历求和 = 10", sum == 10);

        Iterator<Integer> it = list.iterator();
        check("hasNext 初始为 true", it.hasNext());
        check("next() 依次返回", it.next() == 0 && it.next() == 1);
        boolean threw = false;
        try {
            it.next();
            it.next();
            it.next();
            it.next();                   // 越界，应该抛 NoSuchElementException
        } catch (java.util.NoSuchElementException e) {
            threw = true;
        }
        check("迭代器耗尽后 next 抛 NoSuchElementException", threw);
    }

    private static void testFailFast() {
        MyArrayList<Integer> list = new MyArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add(i);
        }
        boolean threw = false;
        try {
            for (Integer v : list) {
                if (v == 2) {
                    list.remove(2);      // 遍历中修改结构 → 必须 fail-fast
                }
            }
        } catch (ConcurrentModificationException e) {
            threw = true;
        }
        check("遍历中 remove 触发 ConcurrentModificationException", threw);
    }

    private interface ThrowingRunnable {
        void run();
    }

    private static boolean throwsIOOBE(ThrowingRunnable r) {
        try {
            r.run();
            return false;
        } catch (IndexOutOfBoundsException e) {
            return true;
        }
    }

    private static void check(String name, boolean ok) {
        if (!ok) {
            failed++;
        }
        System.out.println((ok ? "[PASS] " : "[FAIL] ") + name);
    }
}
