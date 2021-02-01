
/**
 * @ClassName Hello
 * @Description 包含基本数据类型、四则运算、if、for、while循环
 * javac -g Hello.java编译
 * javap -c -verbose Hello反编译查看字节码
 * @Author edd1225
 * @Date 2020/10/17 上午10:56
 **/
public class Hello {

    public void catJavaCode() {
        int num_1 = -1;
        int num5 = 5;
        int num1282 = 1282;
        int num32767 = 32767;//常量池中看不见
        int num32768 = 32768;//在常量池中可以看见

        double d12 = 12.0; 
        double d100000 = 100000;
        // double、long 与 int 比较助记符是不一样 前者用dcmpl lcmp ifeq，后者用if_icmpeq
        while (d100000 > d12) {
            d12 = (++d12 + 10) * 4 / 2 - 1;
        }

        long l12 = 12;
        long l100000 = 100000;
        while (l100000 > l12) {
            l12++;
            l12 = l12 + 10;
            l12 = l12 - 1;
            l12 = l12 * 4;
            l12 = l12 / 2;
        }

        if (num_1 < num5) {
            int num = num5 * num_1;
            num = num5 / num_1;
            num = num5 - num_1;
            System.out.println(num);
        }
        // 自增 和 加 1 的命令不一样 自增是iinc 加一是iadd
        for (int i = 0; i < 10000; i++) {
            num1282++;
            num1282 = num1282 + 1;
        }
        // 前++ 和后++ 生成的字节码 顺序不一样
        while (++num32767 < 40000) {
        }
        while (num32767++ < 40000) {
        }
    }

    public static void main(String[] args) {
        new Hello().catJavaCode();
    }

}
