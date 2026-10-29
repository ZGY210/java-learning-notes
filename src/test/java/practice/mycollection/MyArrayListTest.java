package practice.mycollection;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * MyArrayList 自测脚本（纯 JDK，无任何依赖，直接 {@code java} 运行即可）。
 *
 * <p>用法：在 IDEA 里右键 Run，或命令行：
 * <pre>
 * javac -encoding UTF-8 -d out src/main/java/practice/mycollection/*.java
 * java -cp out practice.mycollection.MyArrayListTest
 * </pre>
 *
 * <p><b>预期结果：全部 PASS（当前共 39 项）。</b>
 * D1 时其中 5 项会失败（remove 搬移相关 3 项、迭代器耗尽 1 项、fail-fast 1 项），
 * 补齐 remove 与 modCount 之后应当全绿。D3 新增迭代器 remove() 相关 8 项。
 * 只要有 FAIL，就不算过。</p>
 *
 * @author 学员自测用
 */
public class MyArrayListTest {

    /** 用来把「应该抛异常」的代码片段传进断言方法。 */
    private interface ThrowingRunnable {
        void run() throws Exception;
    }

    private static int passed;
    private static int failed;

    public static void main(String[] args) {
        System.out.println("===== MyArrayList 自测开始 =====");

        testLazyInit();
        testAddAndGrow();
        testGetAndIndexCheck();
        testSetReturnsOldValue();
        testRemoveShiftsLeft();
        testRemoveReturnValue();
        testRemoveOutOfBounds();
        testSizeNotCapacity();
        testIteratorOrder();
        testIteratorExhausted();
        testFailFastOnAddDuringIteration();
        testFailFastOnRemoveDuringIteration();
        testSetDoesNotTriggerFailFast();
        testIteratorRemove();
        testIteratorRemoveIllegalStates();
        testIteratorRemoveNoSkip();

        System.out.println("\n===== 结果：" + passed + " PASS / " + failed + " FAIL =====");
        if (failed == 0) {
            System.out.println("全绿。可以把这一屏截图丢进今天的日志了。");
        } else {
            System.out.println("还有 " + failed + " 项没过，逐条看上面的 expected / actual。");
        }
    }

    // ---------------- 各项测试 ----------------

    private static void testLazyInit() {
        MyArrayList<String> list = new MyArrayList<>();
        check("无参构造：size()==0", list.size() == 0);
        check("无参构造：isEmpty()==true", list.isEmpty());
    }

    private static void testAddAndGrow() {
        MyArrayList<Integer> list = new MyArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add(i);
        }
        check("add 100 个后 size()==100", list.size() == 100);
        check("多次扩容后 get(0)==0", list.get(0) == 0);
        check("多次扩容后 get(99)==99", list.get(99) == 99);
        check("扩容后中间元素不错乱 get(37)==37 && get(55)==55",
                list.get(37) == 37 && list.get(55) == 55);
        check("扩容后容量 >= 100（实际 " + list.capacity() + "）", list.capacity() >= 100);
    }

    private static void testGetAndIndexCheck() {
        MyArrayList<String> list = new MyArrayList<>();
        list.add("a");
        check("get(-1) 抛 IndexOutOfBoundsException",
                throwsIOOBE(() -> list.get(-1)));
        check("get(size()) 抛 IndexOutOfBoundsException",
                throwsIOOBE(() -> list.get(list.size())));
    }

    private static void testSetReturnsOldValue() {
        MyArrayList<String> list = new MyArrayList<>();
        list.add("a");
        list.add("b");
        String old = list.set(1, "c");
        check("set 返回旧值 \"b\"（实际 " + old + "）", "b".equals(old));
        check("set 后 get(1) 为 \"c\"", "c".equals(list.get(1)));
        check("set 不改变 size", list.size() == 2);
        check("set(-1) 抛 IndexOutOfBoundsException",
                throwsIOOBE(() -> list.set(-1, "x")));
    }

    private static void testRemoveShiftsLeft() {
        MyArrayList<Integer> list = new MyArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add(i);   // [0,1,2,3,4]
        }
        list.remove(1);     // 期望 [0,2,3,4]
        check("remove(1) 后 get(0)==0", list.get(0) == 0);
        check("remove(1) 后 get(1)==2（元素已前移）", list.get(1) == 2);
        check("remove(1) 后 get(2)==3", list.get(2) == 3);
        check("remove(1) 后 size==4", list.size() == 4);
    }

    private static void testRemoveReturnValue() {
        MyArrayList<Integer> list = new MyArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add(i);
        }
        Integer removed = list.remove(1);
        check("remove(1) 返回被删元素 1（实际 " + removed + "）", Integer.valueOf(1).equals(removed));
    }

    private static void testRemoveOutOfBounds() {
        MyArrayList<Integer> list = new MyArrayList<>();
        for (int i = 0; i < 3; i++) {
            list.add(i);
        }
        check("remove(99) 抛 IndexOutOfBoundsException",
                throwsIOOBE(() -> list.remove(99)));
        check("remove(-1) 抛 IndexOutOfBoundsException",
                throwsIOOBE(() -> list.remove(-1)));

        // 首尾删除的边界
        list.remove(0);
        list.remove(list.size() - 1);
        check("删头删尾后 size==1 且 get(0)==1", list.size() == 1 && list.get(0) == 1);
    }

    private static void testSizeNotCapacity() {
        MyArrayList<Integer> list = new MyArrayList<>();
        for (int i = 0; i < 7; i++) {
            list.add(i);
        }
        check("size() 是元素个数 7（实际 " + list.size() + "）", list.size() == 7);
        check("容量大于 size（实际容量 " + list.capacity() + "）", list.capacity() > list.size());
    }

    private static void testIteratorOrder() {
        MyArrayList<Integer> list = new MyArrayList<>();
        for (int i = 0; i < 4; i++) {
            list.add(i);
        }
        int sum = 0;
        StringBuilder order = new StringBuilder();
        for (Integer v : list) {
            sum += v;
            order.append(v);
        }
        check("for-each 求和 == 6（实际 " + sum + "）", sum == 6);
        check("for-each 顺序 == 0123（实际 " + order + "）", "0123".contentEquals(order));
    }

    private static void testIteratorExhausted() {
        MyArrayList<Integer> list = new MyArrayList<>();
        list.add(1);
        Iterator<Integer> it = list.iterator();
        it.next();
        check("迭代器耗尽后 hasNext()==false", !it.hasNext());
        check("迭代器耗尽后再 next() 抛 NoSuchElementException",
                throwsNSEE(it::next));
    }

    private static void testFailFastOnAddDuringIteration() {
        MyArrayList<Integer> list = new MyArrayList<>();
        for (int i = 0; i < 3; i++) {
            list.add(i);
        }
        check("遍历中 add 触发 ConcurrentModificationException", throwsCME(() -> {
            for (Integer ignored : list) {
                list.add(9);
            }
        }));
    }

    private static void testFailFastOnRemoveDuringIteration() {
        MyArrayList<Integer> list = new MyArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add(i);
        }
        check("遍历中 remove(2) 触发 ConcurrentModificationException", throwsCME(() -> {
            for (Integer v : list) {
                if (v == 1) {
                    list.remove(2);
                }
            }
        }));
    }

    private static void testSetDoesNotTriggerFailFast() {
        MyArrayList<Integer> list = new MyArrayList<>();
        for (int i = 0; i < 3; i++) {
            list.add(i);
        }
        // set 只是换值，没有结构变化，JDK 也不抛异常，因此这里应当正常跑完
        boolean ok = true;
        try {
            for (Integer ignored : list) {
                list.set(0, 100);
            }
        } catch (ConcurrentModificationException e) {
            ok = false;
        }
        check("遍历中 set 不触发 ConcurrentModificationException（符合 JDK 行为）", ok);
    }

    private static void testIteratorRemove() {
        MyArrayList<String> list = new MyArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        // 验收场景：用迭代器把 b 删掉，不抛异常，size==2，剩下 a、c
        boolean noThrow = true;
        try {
            Iterator<String> it = list.iterator();
            while (it.hasNext()) {
                if ("b".equals(it.next())) {
                    it.remove();
                }
            }
        } catch (Exception e) {
            noThrow = false;
        }
        check("迭代器 remove() 删 b 不抛异常", noThrow);
        check("迭代器 remove() 后 size()==2（实际 " + list.size() + "）", list.size() == 2);
        check("迭代器 remove() 后剩下 a,c", "a".equals(list.get(0)) && "c".equals(list.get(1)));
    }

    private static void testIteratorRemoveIllegalStates() {
        MyArrayList<Integer> list = new MyArrayList<>();
        list.add(1);
        list.add(2);
        Iterator<Integer> it = list.iterator();
        // 还没 next 就 remove → IllegalStateException
        check("未 next 就 remove() 抛 IllegalStateException", throwsISE(it::remove));
        it.next();
        it.remove();
        // 连续 remove 两次 → IllegalStateException（lastRet 已被重置为 -1）
        check("连续 remove() 两次抛 IllegalStateException", throwsISE(it::remove));
        // remove 之后迭代还能正常继续（expectedModCount 已同步，不误杀自己）
        check("迭代器 remove() 后还能继续 next()（不抛 CME）", it.next() == 2);
        check("删掉 1 之后 size()==1", list.size() == 1);
    }

    private static void testIteratorRemoveNoSkip() {
        // cursor = lastRet 的意义：删除点后面的元素左移一格，cursor 退回去才不会跳过元素
        MyArrayList<Integer> list = new MyArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add(i);   // [0,1,2,3,4]
        }
        Iterator<Integer> it = list.iterator();
        StringBuilder seen = new StringBuilder();
        while (it.hasNext()) {
            Integer v = it.next();
            seen.append(v);
            if (v % 2 == 0) {
                it.remove();   // 删 0、2、4，奇数一个都不能跳过
            }
        }
        check("隔一个删一个：每个元素都被访问到（实际 " + seen + "）", "01234".contentEquals(seen));
        check("隔一个删一个：剩下 1,3（size==" + list.size() + "）",
                list.size() == 2 && list.get(0) == 1 && list.get(1) == 3);
    }

    // ---------------- 断言工具 ----------------

    private static boolean throwsIOOBE(ThrowingRunnable runnable) {
        try {
            runnable.run();
            return false;
        } catch (IndexOutOfBoundsException e) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean throwsCME(ThrowingRunnable runnable) {
        try {
            runnable.run();
            return false;
        } catch (ConcurrentModificationException e) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean throwsNSEE(ThrowingRunnable runnable) {
        try {
            runnable.run();
            return false;
        } catch (NoSuchElementException e) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean throwsISE(ThrowingRunnable runnable) {
        try {
            runnable.run();
            return false;
        } catch (IllegalStateException e) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static void check(String name, boolean ok) {
        if (ok) {
            passed++;
            System.out.println("[PASS] " + name);
        } else {
            failed++;
            System.out.println("[FAIL] " + name);
        }
    }
}
