package practice.collection;

/**
 * 2026-11-20 → 11-21 任务：手写一个能跑的哈希表（HashMap 的骨架版）
 *
 * 视频对应：黑马 211「HashMap 源码超详细解析（一）」（哈希表原理 + put 流程）
 *          + 212「HashMap 源码超详细解析（二）」（扩容 / 红黑树）
 * 这不是让你复刻 JDK —— 是让你亲手把下面这五个问题回答一遍：
 *   1. 哈希值怎么从 key 算出来？为什么要做「扰动」（h ^ h >>> 16）？
 *   2. 哈希值算出来了，怎么变成数组下标？为什么容量必须是 2 的幂？
 *   3. 两个 key 算到同一个下标（哈希冲突）怎么办？—— 链表（链地址法）
 *   4. 同一个 key 再 put 一次，会怎样？—— 覆盖，size 不变
 *   5. 装不下了怎么办？—— 扩容到 2 倍，所有元素重新算下标（rehash）
 *
 * 填完 5 个 TODO，跑 `java practice.collection.MyHashMapDemo`，10/10 即过关。
 */
public class MyHashMapDemo {

    // ==================================================================
    // 哈希桶里的节点：这就是「链表」的一个节点
    // ==================================================================
    static class Node {
        final int hash;   // 缓存 hash，扩容时不用重算（JDK 也这么干）
        final int key;
        int value;
        Node next;        // 指向同桶的下一个节点

        Node(int hash, int key, int value, Node next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    // ==================================================================
    // 常量：这两个数字是 HashMap 的灵魂，背下来
    // ==================================================================
    static final int DEFAULT_CAPACITY = 16;    // 默认容量，2 的 4 次方
    static final float LOAD_FACTOR = 0.75f;    // 负载因子

    Node[] table;
    int size;        // 实际元素个数
    int threshold;   // 扩容阈值 = capacity * LOAD_FACTOR

    MyHashMapDemo() {
        table = new Node[DEFAULT_CAPACITY];
        threshold = (int) (DEFAULT_CAPACITY * LOAD_FACTOR);   // 16 * 0.75 = 12
    }

    // ==================================================================
    // TODO 1（最简单，但不能写错）
    // ==================================================================
    /**
     * 计算扰动后的 hash。
     *
     * 标准写法（JDK 8）：
     *     h = key（对 int 来说 key 本身就是 hashCode）
     *     return h ^ (h >>> 16);
     *
     * 为什么要右移 16 位再异或？
     *   —— 因为 indexFor 只用了 hash 的低几位（见 TODO 2），
     *      如果两个 key 只有高位不同、低位相同，它们会一直撞在同一个桶里。
     *      把高 16 位「掺」到低 16 位，就等于让高位也参与定位，减少冲突。
     */
    static int hash(int key) {
        return key^(key>>>16);
    }

    // ==================================================================
    // TODO 2（这一行是「为什么容量必须是 2 的幂」的答案）
    // ==================================================================
    /**
     * 用 hash 算出数组下标。
     *
     * 标准写法：return hash & (capacity - 1);
     *
     *
     * 为什么能用 & 代替 % ？
     *   —— 当 capacity 是 2 的幂时，capacity-1 的二进制是「全 1」，
     *      此时 hash & (capacity-1) 与 hash % capacity 结果完全相同，但位运算快得多。
     *      比如 capacity=16 → 15 = 0b1111 → 等价于取 hash 的低 4 位。
     *      如果 capacity=10 → 9 = 0b1001 → 取出来的下标只可能是 0/1/8/9，
     *      2、3、4、5、6、7 这几个桶永远用不到，冲突率暴涨。这就是「2 的幂」的意义。
     */
    static int indexFor(int hash, int capacity) {
        return hash & (capacity - 1);
    }

    // ==================================================================
    // TODO 3（本题的重点）
    // ==================================================================
    /**
     * 存一个键值对。步骤（照着写，每一步都别省）：
     *
     *   1. 算 hash：      int h = hash(key);
     *   2. 算下标：       int i = indexFor(h, table.length);
     *   3. 从 table[i] 开始遍历这条链表：
     *        - 如果找到 key 相同的节点 → 覆盖 value，直接 return（size 不变！）
     *   4. 没找到 → 头插/尾插一个新节点（JDK 8 是尾插，你就用头插也行，但要能说出差别）
     *   5. size++
     *   6. 如果 size > threshold → 扩容（调 resize()）
     *
     * 注意顺序：判断「已存在」必须发生在「新建节点」之前，
     *          否则同一个 key 会真的插两条，size 也会加错。
     */
    void put(int key, int value) {
        int hash = hash(key);
        int index = indexFor(hash, table.length);
        Node x= table[index];
        while (x != null) {
            if (x.key == key) {        // 比 key，不是比 hash
                x.value = value;       // 覆盖，size 不变
                return;
            }
            x = x.next;
        }
        Node newNode = new Node(hash, key, value, x.next);
        table[index] = newNode;
        size++;

        if (size > threshold) {
            resize();
        }
    }

    // ==================================================================
    // TODO 4（和 put 的前半段几乎一样）
    // ==================================================================
    /**
     * 取一个值。步骤：
     *   1. 算 hash
     *   2. 算下标
     *   3. 沿着链表找 key 相同的节点 → 返回它的 value
     *   4. 找不到 → 返回 null（注意返回类型是 Integer 不是 int，这样才能表示「不存在」）
     */
    Integer get(int key) {
        throw new UnsupportedOperationException("TODO 4 未实现");
    }

    // ==================================================================
    // TODO 5（理解「扩容为什么贵」）
    // ==================================================================
    /**
     * 扩容到原来的 2 倍，并把所有元素迁移过去。
     *
     * 步骤：
     *   1. 新数组 newCap = table.length * 2
     *   2. Node[] newTable = new Node[newCap]
     *   3. 遍历旧数组的每一个桶、每一条链表上的每个节点：
     *        因为容量翻倍，同一个节点的新下标只有两种可能：
     *          - 还在原下标：      indexFor(hash, newCap)
     *          - 原下标 + 旧容量： indexFor(hash, newCap) 的结果正好是 旧下标 + oldCap
     *        （这就是 JDK 8 里那句 `(e.hash & oldCap) == 0` 的由来，你算一次就懂了）
     *      简单做法：直接重新算 indexFor(hash, newCap) 然后挂到新桶上，不用管上面那个优化。
     *   4. table = newTable
     *   5. threshold = newCap * LOAD_FACTOR   ← 忘了这行会一直扩容
     */
    void resize() {
        throw new UnsupportedOperationException("TODO 5 未实现");
    }

    // ==================================================================
    // 下面不用改
    // ==================================================================
    int size() {
        return size;
    }

    int capacity() {
        return table.length;
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
        System.out.println("================== 手写哈希表 自测 ==================");

        t("1  hash 扰动：hash(0) == 0", () -> hash(0) == 0);

        t("2  hash 扰动：高位参与了低位（hash(0x00010000) != 0x00010000）",
                () -> hash(0x00010000) != 0x00010000);

        t("3  indexFor：cap=16 时 1→1、16→0、17→1（等价于 % 16）",
                () -> indexFor(1, 16) == 1 && indexFor(16, 16) == 0 && indexFor(17, 16) == 1);

        t("4  indexFor 永远落在 [0, capacity) 内", () -> {
            for (int h = -1000; h < 1000; h++) {
                int i = indexFor(h, 16);
                if (i < 0 || i >= 16) return false;
            }
            return true;
        });

        t("5  put/get 基本可用", () -> {
            MyHashMapDemo m = new MyHashMapDemo();
            m.put(1, 100);
            m.put(2, 200);
            return m.size() == 2 && m.get(1) == 100 && m.get(2) == 200;
        });

        t("6  同一个 key 重复 put 只覆盖，size 不变", () -> {
            MyHashMapDemo m = new MyHashMapDemo();
            m.put(1, 100);
            m.put(1, 999);
            return m.size() == 1 && m.get(1) == 999;
        });

        t("7  哈希冲突：1 和 17 同桶（cap=16），两个都能取到", () -> {
            MyHashMapDemo m = new MyHashMapDemo();
            m.put(1, 11);
            m.put(17, 1717);
            return m.size() == 2 && m.get(1) == 11 && m.get(17) == 1717;
        });

        t("8  扩容：塞 13 个后容量从 16 变 32，且元素没丢", () -> {
            MyHashMapDemo m = new MyHashMapDemo();
            for (int i = 0; i < 13; i++) {
                m.put(i, i * 10);
            }
            if (m.capacity() != 32) return false;
            for (int i = 0; i < 13; i++) {
                if (m.get(i) != i * 10) return false;
            }
            return m.size() == 13;
        });

        t("9  get 不存在的 key 返回 null", () -> {
            MyHashMapDemo m = new MyHashMapDemo();
            m.put(1, 1);
            return m.get(2) == null;
        });

        t("10 负数 key 也不会报错（下标必须非负）", () -> {
            MyHashMapDemo m = new MyHashMapDemo();
            m.put(-5, 123);
            return m.get(-5) == 123;
        });

        System.out.println("----------------------------------------------------");
        System.out.printf("结果：PASS %d / FAIL %d%n", pass, fail);
        if (fail == 0) {
            System.out.println("ALL GREEN（10/10）—— 可以去看源码了（211 集）");
        } else {
            System.out.println("还有 " + fail + " 项没过");
        }
        System.out.println("====================================================");
    }
}
