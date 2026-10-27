package demo;

import java.math.BigInteger;

public class BigIntegerDemo {
    public static void main(String[] args) {
        // ---------- 1) 20! 用 long 可以精确表示 ----------
        // long 是 8 个字节，能表示的最大值是 2^63-1 = 9223372036854775807
        long fact20 = 1;
        for (int i = 1; i <= 20; i++) {
            fact20 *= i;
        }
        System.out.println("20! 用 long 算 = " + fact20); // 2432902008176640000

        // ---------- 2) 21! 超出了 long 的范围，会静默溢出 ----------
        // 21! = 51090942171709440000 > 9223372036854775807，装不进 long
        // 注意：long 乘法溢出不会报错，也不抛异常，只会得到一个没意义的数字（这里变成了负数）
        long overflow = 21L * fact20;
        System.out.println("21! 用 long 算 = " + overflow); // -4249290049419214848，结果无效
        System.out.println("是否溢出 = " + (overflow < 0));  // true，所以 21! 只能用 BigInteger 表示

        // ---------- 3) 用 BigInteger 得到 100! 的正确结果 ----------
        // BigInteger 是不可变对象，每次运算都返回一个新的 BigInteger，所以必须接收返回值
        BigInteger result = BigInteger.ONE;
        for (int i = 2; i <= 100; i++) {
            result = result.multiply(BigInteger.valueOf(i));
        }
        System.out.println("100! = " + result);
        System.out.println("100! 的位数 = " + result.toString().length()); // 158

        // ---------- 补充说明 ----------
        // 理论上 BigInteger 可以表示任意大的整数，实际大小受内存限制
        // 更准确地说：BigInteger 内部用 int[] 数组存放数值（符号单独用一个 int 表示），
        // bitLength() 返回 int，所以能表示的位数上限受 Integer.MAX_VALUE 约束；
        // 这个上限远超内存能承载的范围，而且真的超限时它会抛异常，而不是像 long 那样静默溢出：
        // ArithmeticException: BigInteger would overflow supported range
    }
}
