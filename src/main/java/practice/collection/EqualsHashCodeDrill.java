package practice.collection;

import java.util.*;

/**
 * 2026-11-18 加练：手写 equals 与 hashCode（不许用 IDEA 生成）
 *
 * 为什么加这一课：
 *   你 11/17 用 IDEA 一键生成了 Student 的 equals/hashCode，代码是对的，
 *   但面试官问「为什么是 31」的时候，一键生成救不了你。
 *   所以今天用 20 分钟，脱离 IDE，自己把它敲一遍，并且能讲清 31 的来历。
 *
 * 规矩：
 *   1. 不许用 IDEA 的 Generate → equals() and hashCode()
 *   2. 不许用 Objects.hash(...)（那是把 31 藏起来了）
 *   3. 必须用 result = 31 * result + xxx 的写法，手打
 *
 * 填完 2 个 TODO，跑 `java practice.collection.EqualsHashCodeDrill`，10/10 过关。
 */
public class EqualsHashCodeDrill {

    // ==================================================================
    // TODO 1：手写 equals
    // ==================================================================
    /**
     * 规则：id 和 name 都相同，就算同一个人。
     *
     * 必须包含这 5 步（缺一步就错）：
     *   ① 地址相同直接返回 true（this == o）—— 性能优化，也是自反性
     *   ② o 为 null 返回 false
     *   ③ 类型不同返回 false（用 instanceof，别用 == getClass()）
     *   ④ 强转成 Employee
     *   ⑤ 逐字段比较：int 用 ==，String 用 Objects.equals（因为可能是 null）
     *
     * ⚠️ 别忘了 @Override —— 不加 annotation 只是少了编译期检查，逻辑仍然生效，
     *    但面试官会看出来你不熟。
     */
    static boolean EQUALS_TODO(Employee a, Object o) {
        if (a == o) return true;
        if (o == null || !(o instanceof Employee)) return false;
        Employee other = (Employee) o;
        return a.id == other.id && Objects.equals(a.name, other.name);
    }

    // ==================================================================
    // TODO 2：手写 hashCode
    // ==================================================================
    /**
     * 必须用 31 这个魔数的经典写法：
     *
     *   int result = id;
     *   result = 31 * result + (name == null ? 0 : name.hashCode());
     *   return result;
     *
     * 要求：
     *   - id 是 int，直接作为初始值
     *   - name 可能为 null，用三元运算符兜底成 0
     *   - 不许用 Objects.hash(...)
     *
     * ⚠️ 必须和 equals 的字段保持一致：equals 比了 id 和 name，hashCode 就只算这两个。
     *    多算一个少算一个，都会让 HashSet/HashMap 出错。
     */
    static int HASHCODE_TODO(Employee e) {
        int result = e.id;
        result = 31 * result + (e.name == null ? 0 : e.name.hashCode());
        return result;
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
        System.out.println("============ 手写 equals / hashCode 自测 ============");

        t("1  同 id 同 name 的两个对象 → equals true", () ->
                EQUALS_TODO(new Employee(1, "张三", 20), new Employee(1, "张三", 22)));

        t("2  同 id 同 name → hashCode 相等（一致性契约）", () ->
                HASHCODE_TODO(new Employee(1, "张三", 20)) == HASHCODE_TODO(new Employee(1, "张三", 22)));

        t("3  id 不同 → equals false", () ->
                !EQUALS_TODO(new Employee(1, "张三", 20), new Employee(2, "张三", 20)));

        t("4  name 不同 → equals false", () ->
                !EQUALS_TODO(new Employee(1, "张三", 20), new Employee(1, "李四", 20)));

        t("5  equals(null) → false（不能抛 NPE）", () ->
                !EQUALS_TODO(new Employee(1, "张三", 20), null));

        t("6  equals 自己 → true（自反性）", () -> {
            Employee e = new Employee(1, "张三", 20);
            return EQUALS_TODO(e, e);
        });

        t("7  equals 一个 String → false（类型检查）", () ->
                !EQUALS_TODO(new Employee(1, "张三", 20), "张三"));

        t("8  name 为 null 时不抛异常，且两个 null name 相等", () -> {
            Employee a = new Employee(1, null, 20);
            Employee b = new Employee(1, null, 20);
            return EQUALS_TODO(a, b) && HASHCODE_TODO(a) == HASHCODE_TODO(b);
        });

        t("9  放进 HashSet 能去重", () -> {
            Set<Employee> set = new HashSet<>();
            for (Employee e : new Employee[]{new Employee(1, "张三", 20), new Employee(1, "张三", 22)}) {
                // 用自己写的 equals/hashCode 来判等：HashSet 内部会调对象的 equals/hashCode，
                // 而 Employee 类本身没重写，所以这里改成用 Map 手工去重来验证你的两个方法
                boolean exists = false;
                for (Employee old : set) {
                    if (EQUALS_TODO(old, e) && HASHCODE_TODO(old) == HASHCODE_TODO(e)) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    set.add(e);
                }
            }
            return set.size() == 1;
        });

        t("10 放进 HashMap 能用「等价的新对象」查到", () -> {
            Map<Employee, Integer> map = new HashMap<>();
            map.put(new Employee(1, "张三", 20), 90);
            // HashMap 靠 Employee 自己的 equals/hashCode，类里没有重写 → 查不到；
            // 所以这里同样用你的方法手工查找，验证的是「你的 equals 逻辑能否定位到」
            for (Map.Entry<Employee, Integer> en : map.entrySet()) {
                if (EQUALS_TODO(en.getKey(), new Employee(1, "张三", 99))
                        && HASHCODE_TODO(en.getKey()) == HASHCODE_TODO(new Employee(1, "张三", 99))) {
                    return en.getValue() == 90;
                }
            }
            return false;
        });

        System.out.println("----------------------------------------------------");
        System.out.printf("结果：PASS %d / FAIL %d%n", pass, fail);
        if (fail == 0) {
            System.out.println("ALL GREEN（10/10）—— 现在你能给面试官讲 31 了");
        } else {
            System.out.println("还有 " + fail + " 项没过");
        }
        System.out.println("====================================================");
        System.out.println();
        System.out.println("【必答】为什么是 31？把你的答案写进 notes/collections/14 的第七节：");
        System.out.println("  提示 1：31 是质数，为什么质数好？");
        System.out.println("  提示 2：31 * i 可以被 JVM 优化成 (i << 5) - i，省一次乘法");
        System.out.println("  提示 3：实测 31 在英文常用单词上的碰撞率几乎最低（Effective Java 的结论）");
    }
}

/**
 * 刻意「没有任何 equals / hashCode」的类 —— 就是为了让你在外面手写。
 * 注意：这不是最终形态。真实项目里你应该重写进 Employee 内部，
 *      但今天为了脱离 IDE、强制手打，故意放在外面。
 */
class Employee {
    int id;
    String name;
    int age;

    Employee(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return "Employee{id=" + id + ", name='" + name + "', age=" + age + "}";
    }
}
