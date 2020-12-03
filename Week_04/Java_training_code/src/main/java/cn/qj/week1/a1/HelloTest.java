package cn.qj.week1.a1;

/**
 * @ClassName HelloTest
 * @Description 作业一：
 * 包含基本数据类型、四则运算、if、for、while循环
 * javac -g HelloTest.java 编译
 * javap -c -verbose HelloTest 反编译查看字节码
 * javap -c -verbose HelloTest  > HelloTest.bycode  输出
 * @Author edd1225
 * @Date 2020/10/19 上午10:56
 **/

public class HelloTest {
    /**
     * 查看Java代码
     */
    public static void watchJavaCode() {
        int a = -1;
        int b = 10;
        if (a < b) {
            int c = b * a;
            c = b / a;
            c = b - a;
            System.out.println(c);
        }
        // 自增 和 加 1 的命令不一样
        for (int i = 0; i < 10000; i++) {
            a++; //自增是iinc
            b = a + 1;// 加一是iadd
        }
        // 前++ 和后++ 生成的字节码 顺序不一样
        while (++b < 40000) {
        }
        while (b++ < 40000) {
        }
    }

    public static void main(String[] args) {
        //执行
        watchJavaCode();
    }

}
