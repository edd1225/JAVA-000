## 作业一

    作业要求：使用GCLogAnalysis.java 自己演练一遍串行/并行/CMS/G1的案例。

    用于测试的代码如下：

```JAVA
package com.company;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import ja va.util.concurrent.atomic.LongAdder;

/**
 * GC日志生成演示与解读
 */
public class GCLogAnalysis {

    private static Random random = new Random();

    public static void main(String[] args) {
        // 当前毫秒时间戳
        long startMillis = System.currentTimeMillis();
        // 持续运行毫秒数，可根据需要进行修改
        long timeoutMillis = TimeUnit.SECONDS.toMillis(1);
        // 结束时间戳
        long endMillis = startMillis + timeoutMillis;
        LongAdder counter = new LongAdder();
        System.out.println("正在执行");

        // 缓存一部分对象，进入老年代
        int cacheSize = 2000;
        Object[] cacheGarbege = new Object[cacheSize];

        // 在此时间范围内，持续循环
//        while (true) {
        while (System.currentTimeMillis() < endMillis) {
            // 生成垃圾对象
            Object garbage = generateGarbage(100 * 1024);
            counter.increment();
            int randomIndex = random.nextInt(2 * cacheSize);
            if (randomIndex < cacheSize) {
                cacheGarbege[randomIndex] = garbage;
            }
//            System.out.println("执行中！ 共生成对象次数：" + counter.longValue());
        }

        System.out.println("执行结束！ 共生成对象次数：" + counter.longValue());
    }

    /**
     * 生成对象
     * @param maxSize
     * @return
     */
    private static Object generateGarbage(int maxSize) {
        int randomSize = random.nextInt(maxSize);
        int type = randomSize % 4;
        Object result = null;
        switch (type) {
            case 0:
                result = new int[randomSize];
                break;
            case 1:
                result = new byte[randomSize];
                break;
            case 2:
                result = new double[randomSize];
                break;
            default:
                StringBuilder builder = new StringBuilder();
                String randomString = "randomString-Anything";
                while (builder.length() < randomSize) {
                    builder.append(randomString);
                    builder.append(maxSize);
                    builder.append(randomSize);
                }
                result = builder.toString();
                break;
        }
        return result;
    }
}
```

运行测试脚本：

```PYTHON
"""
GC分析测试脚本
"""
import os
import subprocess

if __name__ == "__main__":
    command = ["java", "xmx", "xms", "gc", "F:\Code\Java\JAVA-000\Week_01\example\src\com\company\GCLogAnalysis.java"]

    for memory in ["128m", "512m", "1g", "2g", "4g", "8g"]:
        for gc in ["-XX:+UseSerialGC", "-XX:+UseParallelGC", "-XX:+UseConcMarkSweepGC", "-XX:+UseG1GC"]:
            xmx = "-Xmx" + memory
            xms = "-Xms" + memory
            command[1] = xmx
            command[2] = xms
            command[3] = gc

            # result = os.system(" ".join(command))
            try:
                spend = 0
                for _ in range(0, 10):
                    spend += int(subprocess.check_output(" ".join(command)))
                print(memory, gc, "::", spend / 10)
            except Exception as e:
                print(memory, gc, "::OOM")
```

    此次分别测试内存级别为：128m、512M、1G、2G、4G、8G  16G，对串行、并行、CMS、G1分别进行测试，测试大致命令如下：

```
java -XX:+UseSerialGC -Xms128m -Xmx128m -Xlog:gc F:\Code\Java\JAVA-000\Week_01\example\src\com\company\GCLogAnalysis.java
java -XX:+UseParallelGC -Xms128m -Xmx128m -Xlog:gc F:\Code\Java\JAVA-000\Week_01\example\src\com\company\GCLogAnalysis.java
java -XX:+UseConcMarkSweepGC -Xms128m -Xmx128m -Xlog:gc F:\Code\Java\JAVA-000\Week_01\example\src\com\company\GCLogAnalysis.java
java -XX:+UseG1GC -Xms128m -Xmx128m -Xlog:gc F:\Code\Java\JAVA-000\Week_01\example\src\com\company\GCLogAnalysis.java
```

    使用Python写脚本，每个运行十次，取平均值作为每次生成对象的数量，均衡时间分配等波动。测试的结果如下图表格所示：

| GC/MEM             | 128M | 512M    | 1G      | 2G      | 4G      | 8G      |
| ------------------ | ---- | ------- | ------- | ------- | ------- | ------- |
| UseSerialGC        | OOM  | 13908.4 | 18831.2 | 17942.7 | 15903.8 | 10634.6 |
| UseParallelGC      | OOM  | 10730.8 | 19241.3 | 21413.5 | 21765.2 | 14347.6 |
| UseConcMarkSweepGC | OOM  | 13729.9 | 18409.2 | 17671.8 | 17331.6 | 16474.6 |
| UseG1GC            | OOM  | 15307.3 | 23348.2 | 23697.0 | 21144.1 | 23816.7 |

    先从水平方向来研究一波：

    先从内存不断增加来看，可以看到串行、并行、CMS都有一个先上升后下降的现象，我们从GC的原理来看，内存越大，需要进行标记和整理的对象就越多，而GC的处理能力有一个上限，在能力范围内，也就是内存较小的时候，它能在较短时间内标记和清理完全；但随着内存增大，超过能力范围，其标记和清除的负担加重，性能就表现出下降的趋势了

    为了更具体的验证我们的想法，对每个GC做内存递增测试，测试数据大致如下（每个内存跑十次，取平局值）：

    下面这个是串行GC的测试数据，从328M逐步递增到4G，观察下面的数据可以看出，串行是存在一个处理上限的，700-3500M之间好像性能是差不的，但后面就开始下降了。

| 内存M | 328  | 428   | 528   | 628   | 728   | 828   | 928   | 1028  | 1128  | 1228  | 1328  | 1428  | 1528  | 1628  | 1728  | 1828  | 1928  | 2028  | 2128  | 2228  | 2328  | 2428  | 2528 | 2628 | 2728  | 2828  | 2928  | 3028  | 3128  | 3228  | 3328  | 3428  | 3528  | 3628  | 3728  | 3828  | 3928  |
| ----- | ---- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ---- | ---- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- |
| 数量  | 8723 | 12440 | 15624 | 18966 | 20527 | 21462 | 20745 | 21513 | 21560 | 21394 | 20535 | 20152 | 19518 | 20273 | 19393 | 19979 | 20114 | 19766 | 19728 | 19286 | 19006 | 19194 | 1917 | 1901 | 19063 | 19735 | 19732 | 19298 | 19084 | 18631 | 18772 | 18784 | 19467 | 18784 | 18572 | 18389 | 17629 |

    关于为啥提高内存能提升性能，我们可以看下面的测试数据，分别测试了328、528、728的内存数据，可以看到由于内存扩大，每次触发GC的阙值是不断提高的，阙值的不断提高，而总的GC次数下降；而且可以观察到内存增大，但单词的GC时间并没有增加，大致可以看出不同内存的平均单次GC时间应该是差不多的。GC次数的减少，GC时间就相应的减少，业务时间就增加了，所以性能就提升了。

```
lw@DESKTOP-1JVUVP4  F:\Code\Java\JAVA-000   main ≣ +1 ~2 -0 !                                         [10:07]
❯ java -XX:+UseSerialGC -Xms328m -Xmx328m -Xlog:gc F:\Code\Java\JAVA-000\Week_01\example\src\com\company\GCLogAnalysis.java
[0.008s][info][gc] Using Serial
[0.722s][info][gc] GC(0) Pause Young (Allocation Failure) 87M->28M(317M) 20.131ms
[0.770s][info][gc] GC(1) Pause Young (Allocation Failure) 116M->62M(317M) 30.488ms
[0.823s][info][gc] GC(2) Pause Young (Allocation Failure) 150M->94M(317M) 41.132ms
[0.926s][info][gc] GC(3) Pause Young (Allocation Failure) 181M->127M(317M) 90.829ms
[0.963s][info][gc] GC(4) Pause Young (Allocation Failure) 214M->156M(317M) 23.138ms
[1.015s][info][gc] GC(5) Pause Young (Allocation Failure) 243M->186M(317M) 37.662ms
[1.064s][info][gc] GC(6) Pause Young (Allocation Failure) 274M->216M(317M) 37.775ms
[1.115s][info][gc] GC(8) Pause Full (Allocation Failure) 303M->197M(317M) 37.808ms
[1.115s][info][gc] GC(7) Pause Young (Allocation Failure) 303M->197M(317M) 38.489ms
[1.170s][info][gc] GC(10) Pause Full (Allocation Failure) 284M->207M(317M) 40.868ms
[1.170s][info][gc] GC(9) Pause Young (Allocation Failure) 284M->207M(317M) 41.245ms
[1.234s][info][gc] GC(12) Pause Full (Allocation Failure) 295M->224M(317M) 49.322ms
[1.234s][info][gc] GC(11) Pause Young (Allocation Failure) 295M->224M(317M) 49.783ms
[1.294s][info][gc] GC(13) Pause Full (Allocation Failure) 316M->224M(317M) 47.789ms
[1.334s][info][gc] GC(14) Pause Full (Allocation Failure) 317M->250M(317M) 27.350ms
[1.386s][info][gc] GC(15) Pause Full (Allocation Failure) 316M->260M(317M) 43.286ms
[1.440s][info][gc] GC(16) Pause Full (Allocation Failure) 316M->265M(317M) 45.928ms
[1.506s][info][gc] GC(17) Pause Full (Allocation Failure) 316M->261M(317M) 54.715ms
[1.534s][info][gc] GC(18) Pause Full (Allocation Failure) 316M->284M(317M) 20.048ms
[1.574s][info][gc] GC(19) Pause Full (Allocation Failure) 316M->285M(317M) 35.890ms
[1.624s][info][gc] GC(20) Pause Full (Allocation Failure) 317M->286M(317M) 43.767ms
[1.686s][info][gc] GC(21) Pause Full (Allocation Failure) 316M->277M(317M) 57.332ms
6588
lw@DESKTOP-1JVUVP4  F:\Code\Java\JAVA-000   main ≣ +1 ~2 -0 !                                         [10:08]
❯ java -XX:+UseSerialGC -Xms528m -Xmx528m -Xlog:gc F:\Code\Java\JAVA-000\Week_01\example\src\com\company\GCLogAnalysis.java
[0.011s][info][gc] Using Serial
[0.778s][info][gc] GC(0) Pause Young (Allocation Failure) 140M->50M(510M) 33.116ms
[0.847s][info][gc] GC(1) Pause Young (Allocation Failure) 190M->107M(510M) 48.562ms
[0.910s][info][gc] GC(2) Pause Young (Allocation Failure) 248M->157M(510M) 40.834ms
[0.971s][info][gc] GC(3) Pause Young (Allocation Failure) 297M->210M(510M) 44.013ms
[1.037s][info][gc] GC(4) Pause Young (Allocation Failure) 350M->262M(510M) 45.540ms
[1.101s][info][gc] GC(5) Pause Young (Allocation Failure) 403M->313M(510M) 43.860ms
[1.171s][info][gc] GC(7) Pause Full (Allocation Failure) 454M->256M(510M) 47.008ms
[1.171s][info][gc] GC(6) Pause Young (Allocation Failure) 454M->256M(510M) 47.533ms
[1.199s][info][gc] GC(8) Pause Young (Allocation Failure) 397M->309M(510M) 8.019ms
[1.252s][info][gc] GC(9) Pause Young (Allocation Failure) 449M->355M(510M) 33.679ms
[1.323s][info][gc] GC(11) Pause Full (Allocation Failure) 496M->297M(510M) 48.970ms
[1.324s][info][gc] GC(10) Pause Young (Allocation Failure) 496M->297M(510M) 49.769ms
[1.354s][info][gc] GC(12) Pause Young (Allocation Failure) 438M->354M(510M) 8.625ms
[1.428s][info][gc] GC(14) Pause Full (Allocation Failure) 495M->322M(510M) 53.364ms
[1.429s][info][gc] GC(13) Pause Young (Allocation Failure) 495M->322M(510M) 53.801ms
[1.506s][info][gc] GC(16) Pause Full (Allocation Failure) 463M->309M(510M) 59.483ms
[1.507s][info][gc] GC(15) Pause Young (Allocation Failure) 463M->309M(510M) 60.014ms
[1.575s][info][gc] GC(18) Pause Full (Allocation Failure) 449M->326M(510M) 43.394ms
[1.575s][info][gc] GC(17) Pause Young (Allocation Failure) 449M->326M(510M) 43.952ms
[1.656s][info][gc] GC(20) Pause Full (Allocation Failure) 467M->331M(510M) 62.490ms
[1.657s][info][gc] GC(19) Pause Young (Allocation Failure) 467M->331M(510M) 63.235ms
10239
lw@DESKTOP-1JVUVP4  F:\Code\Java\JAVA-000   main ≣ +1 ~2 -0 !                                         [10:08]
 java -XX:+UseSerialGC -Xms728m -Xmx728m -Xlog:gc F:\Code\Java\JAVA-000\Week_01\example\src\com\company\GCLogAnalysis.java
[0.007s][info][gc] Using Serial
[0.594s][info][gc] GC(0) Pause Young (Allocation Failure) 194M->60M(703M) 31.024ms
[0.658s][info][gc] GC(1) Pause Young (Allocation Failure) 254M->128M(703M) 38.228ms
[0.715s][info][gc] GC(2) Pause Young (Allocation Failure) 322M->188M(703M) 29.649ms
[0.771s][info][gc] GC(3) Pause Young (Allocation Failure) 382M->250M(703M) 30.623ms
[0.830s][info][gc] GC(4) Pause Young (Allocation Failure) 444M->318M(703M) 31.502ms
[0.882s][info][gc] GC(5) Pause Young (Allocation Failure) 513M->381M(703M) 26.823ms
[0.937s][info][gc] GC(6) Pause Young (Allocation Failure) 575M->442M(703M) 29.273ms
[1.006s][info][gc] GC(8) Pause Full (Allocation Failure) 636M->302M(703M) 40.899ms
[1.007s][info][gc] GC(7) Pause Young (Allocation Failure) 636M->302M(703M) 41.564ms
[1.045s][info][gc] GC(9) Pause Young (Allocation Failure) 496M->371M(703M) 8.334ms
[1.086s][info][gc] GC(10) Pause Young (Allocation Failure) 565M->430M(703M) 13.109ms
[1.142s][info][gc] GC(11) Pause Young (Allocation Failure) 625M->491M(703M) 27.410ms
[1.220s][info][gc] GC(13) Pause Full (Allocation Failure) 685M->340M(703M) 55.208ms
[1.221s][info][gc] GC(12) Pause Young (Allocation Failure) 685M->340M(703M) 55.851ms
[1.256s][info][gc] GC(14) Pause Young (Allocation Failure) 534M->405M(703M) 7.640ms
[1.297s][info][gc] GC(15) Pause Young (Allocation Failure) 599M->476M(703M) 16.520ms
[1.375s][info][gc] GC(17) Pause Full (Allocation Failure) 670M->360M(703M) 55.488ms
[1.376s][info][gc] GC(16) Pause Young (Allocation Failure) 670M->360M(703M) 56.010ms
[1.414s][info][gc] GC(18) Pause Young (Allocation Failure) 554M->435M(703M) 13.413ms
[1.460s][info][gc] GC(19) Pause Young (Allocation Failure) 629M->501M(703M) 18.806ms
[1.541s][info][gc] GC(21) Pause Full (Allocation Failure) 695M->338M(703M) 53.498ms
[1.542s][info][gc] GC(20) Pause Young (Allocation Failure) 695M->338M(703M) 54.111ms
16599
```

    关于后面下降趋势的验证可以看下面的测试数据，就简单的测试8G的内存，正常来说，其性能是要小于728M内存的，结果也符合我们的预期。但有下面的疑问：

    这里有个矛盾：8G内存生成对象比728M少（已排除其他条件抖动，平均值来说），但4G的GC总时间却比728M的GC总时间少，有点不太符合预期。目前还没找到原因

```
 java -XX:+UseSerialGC -Xms8g -Xmx8g -Xlog:gc F:\Code\Java\JAVA-000\Week_01\example\src\com\company\GCLogAnalysis.java
[0.023s][info][gc] Using Serial
[1.543s][info][gc] GC(0) Pause Young (Allocation Failure) 2184M->330M(7918M) 137.072ms
10491
```

    并行GC的数据和串行GC大同小异，也是有一个先上升再下降的过程，同样有最后GC总时间矛盾

    CMS测试数据（设置最大约16G）也有先上升后下降的趋势，但较为平稳，从这里可以看出，CMS在这个程序上确实比串行和并行GC好

| 内存M | 528   | 928   | 1328  | 1728  | 2128  | 2528  | 2928 | 3328  | 3728 | 4128 | 4528  | 4928  | 5328  | 5728  | 6128  | 6528  | 6928  | 7328  | 7728  | 8128  | 8528  | 8928  | 9328 | 9728 | 10128 | 10528 | 10928 | 11328 | 11728 | 12128 | 12528 | 12928 | 13328 | 13728 | 14128 | 14528 | 14928 | 15328 | 15728 |
| ----- | ----- | ----- | ----- | ----- | ----- | ----- | ---- | ----- | ---- | ---- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ---- | ---- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- |
| 数量  | 15763 | 20869 | 18210 | 17386 | 16780 | 16953 | 1674 | 17031 | 1670 | 1682 | 16972 | 16771 | 16816 | 16763 | 17021 | 16771 | 16785 | 16782 | 16646 | 16768 | 16536 | 16634 | 1621 | 1609 | 16463 | 16343 | 16127 | 15725 | 15705 | 15817 | 15726 | 14629 | 14924 | 14234 | 14131 | 13642 | 14288 | 14249 | 14479 |

    我们再来看下GC的测试数据：从下面的数据可以看出，G1的性能是要好于前面三种GC的，均值基本都2万以上；在16G内存范围来看，表现较为稳定和平稳；两个字，优秀！

| 内存M | 528  | 928   | 1328  | 1728  | 2128  | 2528  | 2928  | 3328  | 3728  | 4128  | 4528  | 4928  | 5328  | 5728 | 6128  | 6528  | 6928  | 7328  | 7728  | 8128  | 8528  | 8928  | 9328  | 9728  | 10128 | 10528 | 10928 | 11328 | 11728 | 12128 | 12528 | 12928 | 13328 | 13728 | 14128 | 14528 | 14928 | 15328 |
| ----- | ---- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ---- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- |
| 数量  | 1817 | 25974 | 27909 | 25241 | 23936 | 22951 | 21251 | 21932 | 21455 | 22793 | 21814 | 22870 | 22611 | 2485 | 25148 | 24337 | 24010 | 25259 | 25966 | 25012 | 24570 | 23426 | 23547 | 24961 | 24840 | 25256 | 25623 | 25497 | 23764 | 23444 | 24347 | 24468 | 22766 | 24960 | 2460  | 24151 | 24411 | 22070 |

    下面我们从垂直方向来分析一波：

    在512M总有个有趣的现象，串行的性能和CMS相等，并行竟然是垫底。感觉有点怪，于是多次使用512M内存测试。

    串行和CMS均稳定在1万2左右，而串行只用1万左右。从日志中看出，并行的GC次数比串行多了近一半，从这里可以验证小内存时用串行（CMS好像也行）性能也不错。

    另外一个现象是：在1G到4G的时候，并行GC的性能比CMS要好，甚至比较G1 GC。这边为了验证，又详细测试了一下，第一个是串行的，第二个是CMS,可以看到在大于1G的时候，串行的性能总体还是比CMS好。不得不说这个有点不符合想象，后面在测试网关的时候再看看，是不是CMS在WEB上能超过串行。这也从另一个方面证明了官方默认配置是串行是有一定道理的。

| 内存M | 328  | 428  | 528   | 628   | 728   | 828   | 928   | 1028  | 1128  | 1228  | 1328  | 1428  | 1528  | 1628  | 1728  | 1828  | 1928 | 2028  | 2128  | 2228  | 2328  | 2428  | 2528  | 2628 | 2728  | 2828  | 2928  | 3028  | 3128  | 3228  | 3328  | 3428  | 3528  | 3628  | 3728  | 3828  | 3928  |
| ----- | ---- | ---- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ---- | ----- | ----- | ----- | ----- | ----- | ----- | ---- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- |
| 数量  | 5782 | 9006 | 13157 | 16321 | 17903 | 19083 | 20112 | 20951 | 20743 | 21344 | 22640 | 22648 | 22290 | 22511 | 22790 | 23910 | 2417 | 23983 | 23378 | 23977 | 24040 | 24408 | 24659 | 2404 | 22606 | 22745 | 23834 | 22843 | 22775 | 22462 | 23450 | 24009 | 22020 | 23630 | 23478 | 24336 | 23671 |

| 内存M | 328  | 428   | 528   | 628  | 728   | 828   | 928   | 1028  | 1128  | 1228  | 1328  | 1428 | 1528  | 1628  | 1728 | 1828  | 1928  | 2028  | 2128  | 2228  | 2328  | 2428  | 2528  | 2628  | 2728  | 2828 | 2928  | 3028  | 3128  | 3228  | 3328  | 3428  | 3528  | 3628  | 3728  | 3828  | 3928  |
| ----- | ---- | ----- | ----- | ---- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ---- | ----- | ----- | ---- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ---- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- |
| 数量  | 8313 | 11989 | 15102 | 1917 | 19657 | 19779 | 19938 | 20095 | 19149 | 18585 | 18403 | 1870 | 19496 | 19893 | 1930 | 18722 | 18722 | 19325 | 18393 | 19068 | 19037 | 19264 | 19471 | 18264 | 19157 | 1931 | 17937 | 18339 | 17614 | 18433 | 18996 | 19398 | 19444 | 19380 | 19315 | 19468 | 19235 |

    另外从表格中，我们可以看到G1是一骑绝尘，一直领先于其他的GC，测试的数据如下：对比串行发现在内存小于512M的时候串行有时候略胜一筹，但不是太多，但在后面G1是完全碾压。从这里可以看出G1不亏是称为里程牌式的，确实厉害。所以运用内存大于512M时，感觉直接使用G1就行了

| 内存M | 428   | 528   | 628   | 728   | 828   | 928  | 1028  | 1128  | 1228  | 1328  | 1428  | 1528  | 1628  | 1728  | 1828  | 1928  | 2028 | 2128  | 2228  | 2328  | 2428  | 2528  | 2628  | 2728  | 2828  | 2928  | 3028  | 3128  | 3228  | 3328  | 3428  | 3528  | 3628  | 3728  | 3828  | 3928  |
| ----- | ----- | ----- | ----- | ----- | ----- | ---- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ---- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- |
| 数量  | 11840 | 19048 | 23225 | 24982 | 25455 | 2681 | 26974 | 27209 | 27744 | 27333 | 27611 | 27084 | 26887 | 27057 | 27479 | 26289 | 2603 | 26001 | 26040 | 26389 | 25468 | 24887 | 24729 | 22899 | 24511 | 24811 | 24373 | 23963 | 23070 | 24924 | 25928 | 25207 | 25343 | 24690 | 25501 | 25557 |

## 作业二

    使用压测工具（wrk或sb），演练gateway-server-0.0.1-SNAPSHOT.jar 示例。

    分别测试串行、并行、CMS、G1等GC，内存测试分别为512M、1G、2G、4G、8G

    测试的数据如下：sb -u http://localhost:8088/api/hello -c 20 -N 60

| GC/内存 | 512M | 1G   | 2G   | 4G   | 8G   |
| ------- | ---- | ---- | ---- | ---- | ---- |
| 串行    | 5120 | 4488 | 4696 | 4965 | 4483 |
| 并行    | 5137 | 5289 | 5334 | 5228 | 5081 |
| CMS     | 5083 | 5225 | 5526 | 5131 | 5293 |
| G1      | 5400 | 4947 | 5163 | 5158 | 5050 |

    测试的结果发现大家的差距都不是太大，有点出乎意料。通过调节sb的参数，测试结果也没有太大的变化。

    只能从上面简单的看出串行GC是不适合Web场景的，越到后面性能越低，但并行、CMS、G1在测试中性能差距不是太大。猜测性能没有提升的原因是GC能应对当前的场景，性能的瓶颈现在是在网络请求处理上。

## 作业三（先跳过）

    如果自己本地有可以运行的项目，可以按照2的方式进行演练。

## 作业四

    运行课上的例子，以及Netty的例子，分析相关现象

    测试命令：sb -u [http://localhost:8080](http://localhost:8080/) -c 40 -N 60

    测试数据如下：可以看到性能的提升还是比较明显的

- 简朴HTTP server：32.3
- 多线程版本：1091.7
- 线程池版本：1163
- netty：5938.6

## 作业五

    写一段代码，使用 HttpClient 或 OkHttp 访问[http://localhost:8801，代码提交到](http://localhost:8801，代码提交到/) github

    这里使用OKHttp

    需要添加下面两个Maven依赖：

```
        <dependency>
            <groupId>com.squareup.okhttp3</groupId>
            <artifactId>okhttp</artifactId>
            <version>4.9.0</version>
        </dependency>

        <dependency>
            <groupId>org.jetbrains.kotlin</groupId>
            <artifactId>kotlin-stdlib</artifactId>
            <version>1.3.70</version>
        </dependency>
```

    代码如下：

```
package io.github.kimmking.netty.server;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class OkHttpDemo {
    public static void main(String[] args) throws IOException {
        OkHttpClient client = new OkHttpClient();
        try {
            Request request = new Request.Builder().url("http://localhost:8808/test").build();
            Response response = client.newCall(request).execute();
            System.out.println(response.body().string());
        } finally {
            client.clone();
        }
    }
}
```

    返回结果：

```
hello,kimmking

Process finished with exit code 0
```