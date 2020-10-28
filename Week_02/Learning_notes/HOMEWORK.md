

## 作业一

    作业要求：使用GCLogAnalysis.java 自己演练一遍串行/并行/CMS/G1的案例。

    用于测试的代码如下：

```java
package  cn.qj.week2;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

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

分别按照堆大小256M, 512M, 1G, 2G, 4G 五种规格对串行GC，并行GC，CMS GC和G1 GC进行GC日志的记录和分析。

测试 

```
cd  /Users/qianjiang/tmp/JAVA-000/Week_02/Learning_notes

java GCLogAnalysis.java
```

串行GC，并行GC，CMS GC和G1 GC 命令集

```
#串行GC
java -XX:+UseSerialGC -Xms256m -Xmx256m -XX:+PrintGCDetails -XX:+PrintGCDateStamps  GCLogAnalysis
java -XX:+UseSerialGC -Xms512m -Xmx512m -XX:+PrintGCDetails -XX:+PrintGCDateStamps  GCLogAnalysis
java -XX:+UseSerialGC -Xms1g -Xmx1g -XX:+PrintGCDetails -XX:+PrintGCDateStamps  GCLogAnalysis
java -XX:+UseSerialGC -Xms2g -Xmx2g -XX:+PrintGCDetails -XX:+PrintGCDateStamps  GCLogAnalysis
java -XX:+UseSerialGC -Xms4g -Xmx4g -XX:+PrintGCDetails -XX:+PrintGCDateStamps  GCLogAnalysis
java -XX:+UseSerialGC -Xms8g -Xmx8g -XX:+PrintGCDetails -XX:+PrintGCDateStamps  GCLogAnalysis

#并行GC
java -XX:+UseParallelGC -Xms256m -Xmx256m -XX:+PrintGCDetails -XX:+PrintGCDateStamps  GCLogAnalysis
java -XX:+UseParallelGC -Xms512m -Xmx512m -XX:+PrintGCDetails -XX:+PrintGCDateStamps  GCLogAnalysis
java -XX:+UseParallelGC -Xms1g -Xmx1g -XX:+PrintGCDetails -XX:+PrintGCDateStamps  GCLogAnalysis
java -XX:+UseParallelGC -Xms2g -Xmx2g -XX:+PrintGCDetails -XX:+PrintGCDateStamps  GCLogAnalysis
java -XX:+UseParallelGC -Xms4g -Xmx4g -XX:+PrintGCDetails -XX:+PrintGCDateStamps  GCLogAnalysis
java -XX:+UseParallelGC -Xms8g -Xmx8g -XX:+PrintGCDetails -XX:+PrintGCDateStamps  GCLogAnalysis

#CMS GC
java -XX:+UseConcMarkSweepGC -Xms256m -Xmx256m -XX:+PrintGCDetails -XX:+PrintGCDateStamps  GCLogAnalysis
java -XX:+UseConcMarkSweepGC -Xms512m -Xmx512m -XX:+PrintGCDetails -XX:+PrintGCDateStamps  GCLogAnalysis
java -XX:+UseConcMarkSweepGC -Xms1g -Xmx1g -XX:+PrintGCDetails -XX:+PrintGCDateStamps  GCLogAnalysis
java -XX:+UseConcMarkSweepGC -Xms2g -Xmx2g -XX:+PrintGCDetails -XX:+PrintGCDateStamps  GCLogAnalysis
java -XX:+UseConcMarkSweepGC -Xms4g -Xmx4g -XX:+PrintGCDetails -XX:+PrintGCDateStamps  GCLogAnalysis
java -XX:+UseConcMarkSweepGC -Xms8g -Xmx8g -XX:+PrintGCDetails -XX:+PrintGCDateStamps  GCLogAnalysis


#G1 GC
java -XX:+UseG1GC -Xms256m -Xmx256m -XX:+PrintGC -XX:+PrintGCDateStamps  GCLogAnalysis
java -XX:+UseG1GC -Xms512m -Xmx512m -XX:+PrintGC -XX:+PrintGCDateStamps  GCLogAnalysis
java -XX:+UseG1GC -Xms1g -Xmx1g -XX:+PrintGC -XX:+PrintGCDateStamps  GCLogAnalysis
java -XX:+UseG1GC -Xms2g -Xmx2g -XX:+PrintGC -XX:+PrintGCDateStamps  GCLogAnalysis
java -XX:+UseG1GC -Xms4g -Xmx4g -XX:+PrintGC -XX:+PrintGCDateStamps  GCLogAnalysis
java -XX:+UseG1GC -Xms8g -Xmx8g -XX:+PrintGC -XX:+PrintGCDateStamps  GCLogAnalysis

```



## 串行GC日志

### 256M堆内存 串行GC日志：

```
➜  Learning_notes (main) ✗ java -XX:+UseSerialGC -Xms256m -Xmx256m -XX:+PrintGCDetails -XX:+PrintGCDateStamps  GCLogAnalysis
正在执行
2020-10-28T17:23:25.590-0800: [GC (Allocation Failure) 2020-10-28T17:23:25.590-0800: [DefNew: 69523K->8703K(78656K), 0.0147241 secs] 69523K->25538K(253440K), 0.0147633 secs] [Times: user=0.02 sys=0.01, real=0.01 secs]
2020-10-28T17:23:25.620-0800: [GC (Allocation Failure) 2020-10-28T17:23:25.620-0800: [DefNew: 78655K->8698K(78656K), 0.0202195 secs] 95490K->49508K(253440K), 0.0202546 secs] [Times: user=0.02 sys=0.01, real=0.02 secs]
2020-10-28T17:23:25.653-0800: [GC (Allocation Failure) 2020-10-28T17:23:25.654-0800: [DefNew: 78249K->8703K(78656K), 0.0183657 secs] 119059K->77990K(253440K), 0.0183994 secs] [Times: user=0.02 sys=0.01, real=0.02 secs]
2020-10-28T17:23:25.685-0800: [GC (Allocation Failure) 2020-10-28T17:23:25.685-0800: [DefNew: 78655K->8702K(78656K), 0.0168851 secs] 147942K->104716K(253440K), 0.0169194 secs] [Times: user=0.03 sys=0.00, real=0.02 secs]
2020-10-28T17:23:25.713-0800: [GC (Allocation Failure) 2020-10-28T17:23:25.713-0800: [DefNew: 78634K->8699K(78656K), 0.0127064 secs] 174648K->124778K(253440K), 0.0127419 secs] [Times: user=0.01 sys=0.00, real=0.01 secs]
2020-10-28T17:23:25.737-0800: [GC (Allocation Failure) 2020-10-28T17:23:25.737-0800: [DefNew: 78651K->8701K(78656K), 0.0137780 secs] 194730K->146019K(253440K), 0.0138133 secs] [Times: user=0.00 sys=0.01, real=0.02 secs]
2020-10-28T17:23:25.764-0800: [GC (Allocation Failure) 2020-10-28T17:23:25.764-0800: [DefNew: 78552K->8701K(78656K), 0.0148442 secs] 215871K->168729K(253440K), 0.0148833 secs] [Times: user=0.01 sys=0.00, real=0.01 secs]
2020-10-28T17:23:25.790-0800: [GC (Allocation Failure) 2020-10-28T17:23:25.790-0800: [DefNew: 78470K->78470K(78656K), 0.0000174 secs]2020-10-28T17:23:25.790-0800: [Tenured: 160028K->168148K(174784K), 0.0273177 secs] 238499K->168148K(253440K), [Metaspace: 2677K->2677K(1056768K)], 0.0273971 secs] [Times: user=0.02 sys=0.01, real=0.02 secs]
2020-10-28T17:23:25.829-0800: [GC (Allocation Failure) 2020-10-28T17:23:25.829-0800: [DefNew: 69952K->69952K(78656K), 0.0000248 secs]2020-10-28T17:23:25.829-0800: [Tenured: 168148K->174530K(174784K), 0.0306244 secs] 238100K->183967K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0307317 secs] [Times: user=0.03 sys=0.00, real=0.03 secs]
2020-10-28T17:23:25.874-0800: [Full GC (Allocation Failure) 2020-10-28T17:23:25.874-0800: [Tenured: 174530K->174767K(174784K), 0.0251847 secs] 253080K->196290K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0252295 secs] [Times: user=0.03 sys=0.00, real=0.02 secs]
2020-10-28T17:23:25.908-0800: [Full GC (Allocation Failure) 2020-10-28T17:23:25.908-0800: [Tenured: 174767K->174498K(174784K), 0.0305577 secs] 253361K->198951K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0306070 secs] [Times: user=0.03 sys=0.00, real=0.03 secs]
2020-10-28T17:23:25.949-0800: [Full GC (Allocation Failure) 2020-10-28T17:23:25.949-0800: [Tenured: 174498K->174563K(174784K), 0.0094760 secs] 253033K->215564K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0095213 secs] [Times: user=0.01 sys=0.00, real=0.01 secs]
2020-10-28T17:23:25.966-0800: [Full GC (Allocation Failure) 2020-10-28T17:23:25.966-0800: [Tenured: 174563K->174701K(174784K), 0.0210364 secs] 252970K->222675K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0210834 secs] [Times: user=0.02 sys=0.00, real=0.02 secs]
2020-10-28T17:23:25.992-0800: [Full GC (Allocation Failure) 2020-10-28T17:23:25.992-0800: [Tenured: 174701K->174608K(174784K), 0.0272270 secs] 253097K->227058K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0272695 secs] [Times: user=0.02 sys=0.00, real=0.02 secs]
2020-10-28T17:23:26.024-0800: [Full GC (Allocation Failure) 2020-10-28T17:23:26.024-0800: [Tenured: 174717K->174750K(174784K), 0.0362003 secs] 253339K->221893K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0362428 secs] [Times: user=0.03 sys=0.00, real=0.04 secs]
2020-10-28T17:23:26.065-0800: [Full GC (Allocation Failure) 2020-10-28T17:23:26.065-0800: [Tenured: 174750K->174750K(174784K), 0.0085905 secs] 253378K->234123K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0086385 secs] [Times: user=0.01 sys=0.00, real=0.01 secs]
2020-10-28T17:23:26.078-0800: [Full GC (Allocation Failure) 2020-10-28T17:23:26.078-0800: [Tenured: 174750K->174750K(174784K), 0.0080415 secs] 253289K->239222K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0080859 secs] [Times: user=0.01 sys=0.00, real=0.01 secs]
2020-10-28T17:23:26.089-0800: [Full GC (Allocation Failure) 2020-10-28T17:23:26.089-0800: [Tenured: 174750K->174508K(174784K), 0.0156915 secs] 253403K->240569K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0157350 secs] [Times: user=0.02 sys=0.00, real=0.02 secs]
2020-10-28T17:23:26.107-0800: [Full GC (Allocation Failure) 2020-10-28T17:23:26.107-0800: [Tenured: 174758K->174394K(174784K), 0.0372432 secs] 253413K->233744K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0372812 secs] [Times: user=0.04 sys=0.00, real=0.04 secs]
2020-10-28T17:23:26.148-0800: [Full GC (Allocation Failure) 2020-10-28T17:23:26.148-0800: [Tenured: 174394K->174394K(174784K), 0.0086780 secs] 252814K->237987K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0087272 secs] [Times: user=0.01 sys=0.00, real=0.01 secs]
2020-10-28T17:23:26.159-0800: [Full GC (Allocation Failure) 2020-10-28T17:23:26.159-0800: [Tenured: 174394K->174394K(174784K), 0.0104733 secs] 252771K->242142K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0105157 secs] [Times: user=0.01 sys=0.00, real=0.02 secs]
2020-10-28T17:23:26.172-0800: [Full GC (Allocation Failure) 2020-10-28T17:23:26.172-0800: [Tenured: 174394K->174394K(174784K), 0.0116103 secs] 252959K->245297K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0116519 secs] [Times: user=0.01 sys=0.00, real=0.01 secs]
2020-10-28T17:23:26.186-0800: [Full GC (Allocation Failure) 2020-10-28T17:23:26.186-0800: [Tenured: 174394K->174579K(174784K), 0.0336228 secs] 252963K->239988K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0336684 secs] [Times: user=0.03 sys=0.00, real=0.04 secs]
2020-10-28T17:23:26.223-0800: [Full GC (Allocation Failure) 2020-10-28T17:23:26.223-0800: [Tenured: 174714K->174714K(174784K), 0.0060487 secs] 253364K->242274K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0060925 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
2020-10-28T17:23:26.232-0800: [Full GC (Allocation Failure) 2020-10-28T17:23:26.232-0800: [Tenured: 174750K->174750K(174784K), 0.0062851 secs] 253385K->244672K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0063319 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
2020-10-28T17:23:26.240-0800: [Full GC (Allocation Failure) 2020-10-28T17:23:26.240-0800: [Tenured: 174750K->174750K(174784K), 0.0094016 secs] 252672K->247678K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0094782 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
2020-10-28T17:23:26.250-0800: [Full GC (Allocation Failure) 2020-10-28T17:23:26.250-0800: [Tenured: 174750K->174677K(174784K), 0.0347806 secs] 253392K->245049K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0348206 secs] [Times: user=0.03 sys=0.00, real=0.03 secs]
2020-10-28T17:23:26.287-0800: [Full GC (Allocation Failure) 2020-10-28T17:23:26.287-0800: [Tenured: 174677K->174677K(174784K), 0.0088048 secs] 253324K->247154K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0088507 secs] [Times: user=0.01 sys=0.00, real=0.01 secs]
2020-10-28T17:23:26.298-0800: [Full GC (Allocation Failure) 2020-10-28T17:23:26.298-0800: [Tenured: 174677K->174677K(174784K), 0.0081933 secs] 253016K->248756K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0082435 secs] [Times: user=0.01 sys=0.00, real=0.01 secs]
2020-10-28T17:23:26.307-0800: [Full GC (Allocation Failure) 2020-10-28T17:23:26.307-0800: [Tenured: 174763K->174763K(174784K), 0.0043532 secs] 253391K->248905K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0043912 secs] [Times: user=0.01 sys=0.00, real=0.01 secs]
2020-10-28T17:23:26.312-0800: [Full GC (Allocation Failure) 2020-10-28T17:23:26.312-0800: [Tenured: 174763K->174755K(174784K), 0.0354132 secs] 253397K->246739K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0354533 secs] [Times: user=0.03 sys=0.00, real=0.03 secs]
2020-10-28T17:23:26.349-0800: [Full GC (Allocation Failure) 2020-10-28T17:23:26.349-0800: [Tenured: 174755K->174755K(174784K), 0.0032508 secs] 253409K->247637K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0032885 secs] [Times: user=0.01 sys=0.00, real=0.01 secs]
2020-10-28T17:23:26.353-0800: [Full GC (Allocation Failure) 2020-10-28T17:23:26.353-0800: [Tenured: 174755K->174755K(174784K), 0.0100977 secs] 253036K->248152K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0101773 secs] [Times: user=0.01 sys=0.01, real=0.01 secs]
2020-10-28T17:23:26.364-0800: [Full GC (Allocation Failure) 2020-10-28T17:23:26.365-0800: [Tenured: 174755K->174755K(174784K), 0.0068505 secs] 253089K->249480K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0068959 secs] [Times: user=0.00 sys=0.00, real=0.01 secs]
2020-10-28T17:23:26.372-0800: [Full GC (Allocation Failure) 2020-10-28T17:23:26.372-0800: [Tenured: 174755K->174752K(174784K), 0.0332135 secs] 253266K->249623K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0332541 secs] [Times: user=0.03 sys=0.00, real=0.03 secs]
2020-10-28T17:23:26.407-0800: [Full GC (Allocation Failure) 2020-10-28T17:23:26.407-0800: [Tenured: 174752K->174752K(174784K), 0.0095642 secs] 253321K->250144K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0096067 secs] [Times: user=0.01 sys=0.00, real=0.01 secs]
2020-10-28T17:23:26.417-0800: [Full GC (Allocation Failure) 2020-10-28T17:23:26.417-0800: [Tenured: 174752K->174752K(174784K), 0.0020461 secs] 253344K->251368K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0020819 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:23:26.420-0800: [Full GC (Allocation Failure) 2020-10-28T17:23:26.420-0800: [Tenured: 174752K->174752K(174784K), 0.0082506 secs] 253160K->252208K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0082881 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
2020-10-28T17:23:26.428-0800: [Full GC (Allocation Failure) 2020-10-28T17:23:26.428-0800: [Tenured: 174752K->174209K(174784K), 0.0305218 secs] 253401K->250911K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0305963 secs] [Times: user=0.03 sys=0.00, real=0.03 secs]
2020-10-28T17:23:26.460-0800: [Full GC (Allocation Failure) 2020-10-28T17:23:26.460-0800: [Tenured: 174209K->174209K(174784K), 0.0065341 secs] 252322K->251072K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0065778 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
2020-10-28T17:23:26.467-0800: [Full GC (Allocation Failure) 2020-10-28T17:23:26.467-0800: [Tenured: 174551K->174551K(174784K), 0.0086944 secs] 253180K->251058K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0087373 secs] [Times: user=0.01 sys=0.00, real=0.01 secs]
2020-10-28T17:23:26.476-0800: [Full GC (Allocation Failure) 2020-10-28T17:23:26.476-0800: [Tenured: 174739K->174739K(174784K), 0.0069792 secs] 253210K->251905K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0071129 secs] [Times: user=0.00 sys=0.00, real=0.01 secs]
2020-10-28T17:23:26.484-0800: [Full GC (Allocation Failure) 2020-10-28T17:23:26.484-0800: [Tenured: 174739K->174750K(174784K), 0.0332736 secs] 253248K->251339K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0333169 secs] [Times: user=0.04 sys=0.00, real=0.03 secs]
2020-10-28T17:23:26.517-0800: [Full GC (Allocation Failure) 2020-10-28T17:23:26.517-0800: [Tenured: 174750K->174750K(174784K), 0.0018706 secs] 253011K->251339K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0019012 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:23:26.520-0800: [Full GC (Allocation Failure) 2020-10-28T17:23:26.520-0800: [Tenured: 174750K->174750K(174784K), 0.0034016 secs] 253189K->252164K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0034382 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:23:26.524-0800: [Full GC (Allocation Failure) 2020-10-28T17:23:26.524-0800: [Tenured: 174750K->174750K(174784K), 0.0113330 secs] 253384K->251859K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0113780 secs] [Times: user=0.01 sys=0.00, real=0.01 secs]
2020-10-28T17:23:26.536-0800: [Full GC (Allocation Failure) 2020-10-28T17:23:26.536-0800: [Tenured: 174750K->174716K(174784K), 0.0110047 secs] 252841K->251625K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0110484 secs] [Times: user=0.01 sys=0.00, real=0.01 secs]
执行结束！ 共生成对象次数：4334
Heap
 def new generation   total 78656K, used 77967K [0x00000007b0000000, 0x00000007b5550000, 0x00000007b5550000)
  eden space 69952K, 100% used [0x00000007b0000000, 0x00000007b4450000, 0x00000007b4450000)
  from space 8704K,  92% used [0x00000007b4cd0000, 0x00000007b54a3e80, 0x00000007b5550000)
  to   space 8704K,   0% used [0x00000007b4450000, 0x00000007b4450000, 0x00000007b4cd0000)
 tenured generation   total 174784K, used 174716K [0x00000007b5550000, 0x00000007c0000000, 0x00000007c0000000)
   the space 174784K,  99% used [0x00000007b5550000, 0x00000007bffef0e8, 0x00000007bffef200, 0x00000007c0000000)
 Metaspace       used 2684K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 294K, capacity 386K, committed 512K, reserved 1048576K
```

### 512M堆内存 串行GC日志：

```
Learning_notes (main) ✗ java -XX:+UseSerialGC -Xms512m -Xmx512m -XX:+PrintGCDetails -XX:+PrintGCDateStamps  GCLogAnalysis
正在执行
2020-10-28T17:18:41.322-0800: [GC (Allocation Failure) 2020-10-28T17:18:41.322-0800: [DefNew: 139776K->17472K(157248K), 0.0422957 secs] 139776K->43897K(506816K), 0.0423483 secs] [Times: user=0.02 sys=0.02, real=0.04 secs]
2020-10-28T17:18:41.399-0800: [GC (Allocation Failure) 2020-10-28T17:18:41.399-0800: [DefNew: 156517K->17471K(157248K), 0.0429541 secs] 182943K->90471K(506816K), 0.0429893 secs] [Times: user=0.02 sys=0.02, real=0.05 secs]
2020-10-28T17:18:41.462-0800: [GC (Allocation Failure) 2020-10-28T17:18:41.462-0800: [DefNew: 157247K->17471K(157248K), 0.0373394 secs] 230247K->140384K(506816K), 0.0373790 secs] [Times: user=0.02 sys=0.02, real=0.04 secs]
2020-10-28T17:18:41.522-0800: [GC (Allocation Failure) 2020-10-28T17:18:41.522-0800: [DefNew: 157247K->17471K(157248K), 0.0312535 secs] 280160K->183647K(506816K), 0.0312891 secs] [Times: user=0.02 sys=0.01, real=0.03 secs]
2020-10-28T17:18:41.576-0800: [GC (Allocation Failure) 2020-10-28T17:18:41.576-0800: [DefNew: 157247K->17470K(157248K), 0.0405926 secs] 323423K->231345K(506816K), 0.0406287 secs] [Times: user=0.02 sys=0.02, real=0.04 secs]
2020-10-28T17:18:41.640-0800: [GC (Allocation Failure) 2020-10-28T17:18:41.640-0800: [DefNew: 157246K->17471K(157248K), 0.0458337 secs] 371121K->276867K(506816K), 0.0458776 secs] [Times: user=0.02 sys=0.01, real=0.04 secs]
2020-10-28T17:18:41.710-0800: [GC (Allocation Failure) 2020-10-28T17:18:41.710-0800: [DefNew: 157247K->17470K(157248K), 0.0998372 secs] 416643K->325342K(506816K), 0.0998813 secs] [Times: user=0.02 sys=0.02, real=0.10 secs]
2020-10-28T17:18:41.834-0800: [GC (Allocation Failure) 2020-10-28T17:18:41.834-0800: [DefNew: 157246K->157246K(157248K), 0.0000170 secs]2020-10-28T17:18:41.834-0800: [Tenured: 307872K->275697K(349568K), 0.0626824 secs] 465118K->275697K(506816K), [Metaspace: 2677K->2677K(1056768K)], 0.0627505 secs] [Times: user=0.06 sys=0.00, real=0.06 secs]
2020-10-28T17:18:41.921-0800: [GC (Allocation Failure) 2020-10-28T17:18:41.921-0800: [DefNew: 139776K->17471K(157248K), 0.0080001 secs] 415473K->324926K(506816K), 0.0080394 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
2020-10-28T17:18:41.954-0800: [GC (Allocation Failure) 2020-10-28T17:18:41.954-0800: [DefNew: 157247K->157247K(157248K), 0.0000171 secs]2020-10-28T17:18:41.954-0800: [Tenured: 307454K->309447K(349568K), 0.0425341 secs] 464702K->309447K(506816K), [Metaspace: 2678K->2678K(1056768K)], 0.0426032 secs] [Times: user=0.04 sys=0.00, real=0.04 secs]
2020-10-28T17:18:42.017-0800: [GC (Allocation Failure) 2020-10-28T17:18:42.017-0800: [DefNew: 139776K->139776K(157248K), 0.0000167 secs]2020-10-28T17:18:42.017-0800: [Tenured: 309447K->316408K(349568K), 0.0435089 secs] 449223K->316408K(506816K), [Metaspace: 2678K->2678K(1056768K)], 0.0435750 secs] [Times: user=0.04 sys=0.01, real=0.05 secs]
2020-10-28T17:18:42.081-0800: [GC (Allocation Failure) 2020-10-28T17:18:42.081-0800: [DefNew: 139776K->139776K(157248K), 0.0000211 secs]2020-10-28T17:18:42.081-0800: [Tenured: 316408K->313655K(349568K), 0.0467549 secs] 456184K->313655K(506816K), [Metaspace: 2678K->2678K(1056768K)], 0.0468273 secs] [Times: user=0.05 sys=0.00, real=0.04 secs]
2020-10-28T17:18:42.147-0800: [GC (Allocation Failure) 2020-10-28T17:18:42.147-0800: [DefNew: 139776K->139776K(157248K), 0.0000262 secs]2020-10-28T17:18:42.147-0800: [Tenured: 313655K->343179K(349568K), 0.0445420 secs] 453431K->343179K(506816K), [Metaspace: 2678K->2678K(1056768K)], 0.0446227 secs] [Times: user=0.03 sys=0.00, real=0.05 secs]
2020-10-28T17:18:42.212-0800: [GC (Allocation Failure) 2020-10-28T17:18:42.212-0800: [DefNew: 139776K->139776K(157248K), 0.0000257 secs]2020-10-28T17:18:42.212-0800: [Tenured: 343179K->347831K(349568K), 0.0434947 secs] 482955K->347831K(506816K), [Metaspace: 2678K->2678K(1056768K)], 0.0435918 secs] [Times: user=0.04 sys=0.01, real=0.04 secs]
执行结束！ 共生成对象次数：7336
Heap
 def new generation   total 157248K, used 6214K [0x00000007a0000000, 0x00000007aaaa0000, 0x00000007aaaa0000)
  eden space 139776K,   4% used [0x00000007a0000000, 0x00000007a06119a0, 0x00000007a8880000)
  from space 17472K,   0% used [0x00000007a8880000, 0x00000007a8880000, 0x00000007a9990000)
  to   space 17472K,   0% used [0x00000007a9990000, 0x00000007a9990000, 0x00000007aaaa0000)
 tenured generation   total 349568K, used 347831K [0x00000007aaaa0000, 0x00000007c0000000, 0x00000007c0000000)
   the space 349568K,  99% used [0x00000007aaaa0000, 0x00000007bfe4de98, 0x00000007bfe4e000, 0x00000007c0000000)
 Metaspace       used 2684K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 294K, capacity 386K, committed 512K, reserved 1048576K
```

### 1G堆内存 串行GC日志：

```
Learning_notes (main) ✗ java -XX:+UseSerialGC -Xms1g -Xmx1g -XX:+PrintGCDetails -XX:+PrintGCDateStamps  GCLogAnalysis
正在执行
2020-10-28T17:25:19.595-0800: [GC (Allocation Failure) 2020-10-28T17:25:19.595-0800: [DefNew: 279616K->34943K(314560K), 0.0517447 secs] 279616K->88102K(1013632K), 0.0517847 secs] [Times: user=0.03 sys=0.02, real=0.05 secs]
2020-10-28T17:25:19.700-0800: [GC (Allocation Failure) 2020-10-28T17:25:19.700-0800: [DefNew: 314559K->34943K(314560K), 0.0656548 secs] 367718K->155962K(1013632K), 0.0656895 secs] [Times: user=0.04 sys=0.03, real=0.06 secs]
2020-10-28T17:25:19.806-0800: [GC (Allocation Failure) 2020-10-28T17:25:19.806-0800: [DefNew: 314559K->34943K(314560K), 0.0543186 secs] 435578K->231219K(1013632K), 0.0543569 secs] [Times: user=0.03 sys=0.02, real=0.06 secs]
2020-10-28T17:25:19.897-0800: [GC (Allocation Failure) 2020-10-28T17:25:19.897-0800: [DefNew: 314559K->34943K(314560K), 0.0563115 secs] 510835K->311554K(1013632K), 0.0563485 secs] [Times: user=0.04 sys=0.02, real=0.06 secs]
2020-10-28T17:25:19.992-0800: [GC (Allocation Failure) 2020-10-28T17:25:19.992-0800: [DefNew: 314559K->34943K(314560K), 0.0555159 secs] 591170K->390769K(1013632K), 0.0555535 secs] [Times: user=0.03 sys=0.03, real=0.05 secs]
2020-10-28T17:25:20.091-0800: [GC (Allocation Failure) 2020-10-28T17:25:20.091-0800: [DefNew: 314559K->34941K(314560K), 0.0488204 secs] 670385K->459125K(1013632K), 0.0488580 secs] [Times: user=0.03 sys=0.02, real=0.04 secs]
2020-10-28T17:25:20.180-0800: [GC (Allocation Failure) 2020-10-28T17:25:20.180-0800: [DefNew: 314557K->34943K(314560K), 0.0628975 secs] 738741K->543647K(1013632K), 0.0629393 secs] [Times: user=0.03 sys=0.02, real=0.06 secs]
2020-10-28T17:25:20.283-0800: [GC (Allocation Failure) 2020-10-28T17:25:20.283-0800: [DefNew: 314559K->34943K(314560K), 0.0878184 secs] 823263K->620581K(1013632K), 0.0878558 secs] [Times: user=0.03 sys=0.03, real=0.09 secs]
2020-10-28T17:25:20.413-0800: [GC (Allocation Failure) 2020-10-28T17:25:20.413-0800: [DefNew: 314559K->34942K(314560K), 0.0728601 secs] 900197K->702923K(1013632K), 0.0729030 secs] [Times: user=0.04 sys=0.03, real=0.07 secs]
执行结束！ 共生成对象次数：9403
Heap
 def new generation   total 314560K, used 46381K [0x0000000780000000, 0x0000000795550000, 0x0000000795550000)
  eden space 279616K,   4% used [0x0000000780000000, 0x0000000780b2be10, 0x0000000791110000)
  from space 34944K,  99% used [0x0000000793330000, 0x000000079554f9a8, 0x0000000795550000)
  to   space 34944K,   0% used [0x0000000791110000, 0x0000000791110000, 0x0000000793330000)
 tenured generation   total 699072K, used 667981K [0x0000000795550000, 0x00000007c0000000, 0x00000007c0000000)
   the space 699072K,  95% used [0x0000000795550000, 0x00000007be1a3438, 0x00000007be1a3600, 0x00000007c0000000)
 Metaspace       used 2684K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 294K, capacity 386K, committed 512K, reserved 1048576K
```

### 2G堆内存 串行GC日志：

```
java -XX:+UseSerialGC -Xms2g -Xmx2g -XX:+PrintGCDetails -XX:+PrintGCDateStamps  GCLogAnalysis
正在执行
2020-10-28T17:26:04.434-0800: [GC (Allocation Failure) 2020-10-28T17:26:04.434-0800: [DefNew: 559232K->69888K(629120K), 0.0917799 secs] 559232K->159859K(2027264K), 0.0918248 secs] [Times: user=0.05 sys=0.04, real=0.09 secs]
2020-10-28T17:26:04.606-0800: [GC (Allocation Failure) 2020-10-28T17:26:04.606-0800: [DefNew: 629120K->69887K(629120K), 0.1087106 secs] 719091K->283430K(2027264K), 0.1087462 secs] [Times: user=0.06 sys=0.05, real=0.11 secs]
2020-10-28T17:26:04.792-0800: [GC (Allocation Failure) 2020-10-28T17:26:04.792-0800: [DefNew: 629119K->69887K(629120K), 0.0792271 secs] 842662K->404714K(2027264K), 0.0792611 secs] [Times: user=0.05 sys=0.03, real=0.08 secs]
2020-10-28T17:26:04.937-0800: [GC (Allocation Failure) 2020-10-28T17:26:04.937-0800: [DefNew: 629119K->69887K(629120K), 0.0780086 secs] 963946K->525843K(2027264K), 0.0780429 secs] [Times: user=0.05 sys=0.03, real=0.08 secs]
2020-10-28T17:26:05.086-0800: [GC (Allocation Failure) 2020-10-28T17:26:05.086-0800: [DefNew: 629119K->69887K(629120K), 0.0800751 secs] 1085075K->645858K(2027264K), 0.0801086 secs] [Times: user=0.05 sys=0.03, real=0.08 secs]
执行结束！ 共生成对象次数：10520
Heap
 def new generation   total 629120K, used 92373K [0x0000000740000000, 0x000000076aaa0000, 0x000000076aaa0000)
  eden space 559232K,   4% used [0x0000000740000000, 0x00000007415f54e8, 0x0000000762220000)
  from space 69888K,  99% used [0x0000000766660000, 0x000000076aa9fff0, 0x000000076aaa0000)
  to   space 69888K,   0% used [0x0000000762220000, 0x0000000762220000, 0x0000000766660000)
 tenured generation   total 1398144K, used 575970K [0x000000076aaa0000, 0x00000007c0000000, 0x00000007c0000000)
   the space 1398144K,  41% used [0x000000076aaa0000, 0x000000078dd18858, 0x000000078dd18a00, 0x00000007c0000000)
 Metaspace       used 2684K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 294K, capacity 386K, committed 512K, reserved 1048576K
```

### 4G堆内存 串行GC日志：

```
 Learning_notes (main) ✗ java -XX:+UseSerialGC -Xms4g -Xmx4g -XX:+PrintGCDetails -XX:+PrintGCDateStamps  GCLogAnalysis
正在执行
2020-10-28T17:26:59.644-0800: [GC (Allocation Failure) 2020-10-28T17:26:59.645-0800: [DefNew: 1118528K->139775K(1258304K), 0.1813294 secs] 1118528K->262480K(4054528K), 0.1813705 secs] [Times: user=0.10 sys=0.07, real=0.18 secs]
2020-10-28T17:27:00.014-0800: [GC (Allocation Failure) 2020-10-28T17:27:00.014-0800: [DefNew: 1258303K->139775K(1258304K), 0.2447145 secs] 1381008K->417255K(4054528K), 0.2447534 secs] [Times: user=0.10 sys=0.09, real=0.24 secs]
执行结束！ 共生成对象次数：8428
Heap
 def new generation   total 1258304K, used 184670K [0x00000006c0000000, 0x0000000715550000, 0x0000000715550000)
  eden space 1118528K,   4% used [0x00000006c0000000, 0x00000006c2bd7a38, 0x0000000704450000)
  from space 139776K,  99% used [0x0000000704450000, 0x000000070cccfff0, 0x000000070ccd0000)
  to   space 139776K,   0% used [0x000000070ccd0000, 0x000000070ccd0000, 0x0000000715550000)
 tenured generation   total 2796224K, used 277479K [0x0000000715550000, 0x00000007c0000000, 0x00000007c0000000)
   the space 2796224K,   9% used [0x0000000715550000, 0x0000000726449f70, 0x000000072644a000, 0x00000007c0000000)
 Metaspace       used 2684K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 294K, capacity 386K, committed 512K, reserved 1048576K
```

8G堆内存 串行GC日志：

```
Learning_notes (main) ✗ java -XX:+UseSerialGC -Xms8g -Xmx8g -XX:+PrintGCDetails -XX:+PrintGCDateStamps  GCLogAnalysis
正在执行
执行结束！ 共生成对象次数：6357
Heap
 def new generation   total 2516544K, used 1799273K [0x00000005c0000000, 0x000000066aaa0000, 0x000000066aaa0000)
  eden space 2236928K,  80% used [0x00000005c0000000, 0x000000062dd1a580, 0x0000000648880000)
  from space 279616K,   0% used [0x0000000648880000, 0x0000000648880000, 0x0000000659990000)
  to   space 279616K,   0% used [0x0000000659990000, 0x0000000659990000, 0x000000066aaa0000)
 tenured generation   total 5592448K, used 0K [0x000000066aaa0000, 0x00000007c0000000, 0x00000007c0000000)
   the space 5592448K,   0% used [0x000000066aaa0000, 0x000000066aaa0000, 0x000000066aaa0200, 0x00000007c0000000)
 Metaspace       used 2684K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 294K, capacity 386K, committed 512K, reserved 1048576K
```

## 并行GC日志

### 256M内存 并行GC日志

```
 Learning_notes (main) ✗ java -XX:+UseParallelGC -Xms256m -Xmx256m -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis
正在执行
2020-10-28T17:30:03.057-0800: [GC (Allocation Failure) [PSYoungGen: 65414K->10748K(76288K)] 65414K->25537K(251392K), 0.0100472 secs] [Times: user=0.02 sys=0.04, real=0.01 secs]
2020-10-28T17:30:03.082-0800: [GC (Allocation Failure) [PSYoungGen: 75583K->10751K(76288K)] 90372K->43297K(251392K), 0.0117256 secs] [Times: user=0.02 sys=0.05, real=0.01 secs]
2020-10-28T17:30:03.108-0800: [GC (Allocation Failure) [PSYoungGen: 76287K->10750K(76288K)] 108833K->61618K(251392K), 0.0092458 secs] [Times: user=0.02 sys=0.03, real=0.01 secs]
2020-10-28T17:30:03.130-0800: [GC (Allocation Failure) [PSYoungGen: 76206K->10741K(76288K)] 127075K->84681K(251392K), 0.0119042 secs] [Times: user=0.03 sys=0.05, real=0.01 secs]
2020-10-28T17:30:03.156-0800: [GC (Allocation Failure) [PSYoungGen: 76277K->10750K(76288K)] 150217K->106783K(251392K), 0.0109708 secs] [Times: user=0.02 sys=0.04, real=0.01 secs]
2020-10-28T17:30:03.177-0800: [GC (Allocation Failure) [PSYoungGen: 76076K->10746K(40448K)] 172109K->129982K(215552K), 0.0142001 secs] [Times: user=0.02 sys=0.04, real=0.02 secs]
2020-10-28T17:30:03.197-0800: [GC (Allocation Failure) [PSYoungGen: 40391K->14504K(58368K)] 159627K->136922K(233472K), 0.0034318 secs] [Times: user=0.02 sys=0.01, real=0.01 secs]
2020-10-28T17:30:03.205-0800: [GC (Allocation Failure) [PSYoungGen: 44200K->20415K(58368K)] 166618K->145766K(233472K), 0.0039650 secs] [Times: user=0.03 sys=0.00, real=0.00 secs]
2020-10-28T17:30:03.214-0800: [GC (Allocation Failure) [PSYoungGen: 50111K->28361K(58368K)] 175462K->157431K(233472K), 0.0049450 secs] [Times: user=0.03 sys=0.01, real=0.00 secs]
2020-10-28T17:30:03.225-0800: [GC (Allocation Failure) [PSYoungGen: 57639K->23063K(58368K)] 186709K->167969K(233472K), 0.0084908 secs] [Times: user=0.02 sys=0.03, real=0.01 secs]
2020-10-28T17:30:03.234-0800: [Full GC (Ergonomics) [PSYoungGen: 23063K->0K(58368K)] [ParOldGen: 144905K->140127K(175104K)] 167969K->140127K(233472K), [Metaspace: 2678K->2678K(1056768K)], 0.0204331 secs] [Times: user=0.12 sys=0.01, real=0.02 secs]
2020-10-28T17:30:03.260-0800: [GC (Allocation Failure) [PSYoungGen: 29696K->9938K(58368K)] 169823K->150065K(233472K), 0.0023550 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
2020-10-28T17:30:03.269-0800: [GC (Allocation Failure) [PSYoungGen: 38963K->11626K(58368K)] 179090K->161247K(233472K), 0.0043978 secs] [Times: user=0.02 sys=0.01, real=0.01 secs]
2020-10-28T17:30:03.274-0800: [Full GC (Ergonomics) [PSYoungGen: 11626K->0K(58368K)] [ParOldGen: 149621K->153345K(175104K)] 161247K->153345K(233472K), [Metaspace: 2678K->2678K(1056768K)], 0.0197275 secs] [Times: user=0.11 sys=0.01, real=0.02 secs]
2020-10-28T17:30:03.301-0800: [Full GC (Ergonomics) [PSYoungGen: 29696K->0K(58368K)] [ParOldGen: 153345K->158572K(175104K)] 183041K->158572K(233472K), [Metaspace: 2678K->2678K(1056768K)], 0.0217337 secs] [Times: user=0.10 sys=0.01, real=0.02 secs]
2020-10-28T17:30:03.329-0800: [Full GC (Ergonomics) [PSYoungGen: 29661K->0K(58368K)] [ParOldGen: 158572K->162250K(175104K)] 188233K->162250K(233472K), [Metaspace: 2678K->2678K(1056768K)], 0.0230794 secs] [Times: user=0.10 sys=0.01, real=0.03 secs]
2020-10-28T17:30:03.359-0800: [Full GC (Ergonomics) [PSYoungGen: 29696K->0K(58368K)] [ParOldGen: 162250K->173263K(175104K)] 191946K->173263K(233472K), [Metaspace: 2678K->2678K(1056768K)], 0.0242874 secs] [Times: user=0.12 sys=0.02, real=0.03 secs]
2020-10-28T17:30:03.389-0800: [Full GC (Ergonomics) [PSYoungGen: 29256K->3761K(58368K)] [ParOldGen: 173263K->174686K(175104K)] 202519K->178447K(233472K), [Metaspace: 2678K->2678K(1056768K)], 0.0250308 secs] [Times: user=0.13 sys=0.00, real=0.03 secs]
2020-10-28T17:30:03.420-0800: [Full GC (Ergonomics) [PSYoungGen: 29408K->7140K(58368K)] [ParOldGen: 174686K->175001K(175104K)] 204094K->182142K(233472K), [Metaspace: 2678K->2678K(1056768K)], 0.0234358 secs] [Times: user=0.14 sys=0.01, real=0.02 secs]
2020-10-28T17:30:03.450-0800: [Full GC (Ergonomics) [PSYoungGen: 29696K->10727K(58368K)] [ParOldGen: 175001K->174522K(175104K)] 204697K->185249K(233472K), [Metaspace: 2678K->2678K(1056768K)], 0.0249157 secs] [Times: user=0.11 sys=0.00, real=0.02 secs]
2020-10-28T17:30:03.480-0800: [Full GC (Ergonomics) [PSYoungGen: 29563K->15015K(58368K)] [ParOldGen: 174522K->174350K(175104K)] 204085K->189366K(233472K), [Metaspace: 2678K->2678K(1056768K)], 0.0261625 secs] [Times: user=0.13 sys=0.00, real=0.02 secs]
2020-10-28T17:30:03.509-0800: [Full GC (Ergonomics) [PSYoungGen: 29667K->15890K(58368K)] [ParOldGen: 174350K->174545K(175104K)] 204017K->190436K(233472K), [Metaspace: 2678K->2678K(1056768K)], 0.0252998 secs] [Times: user=0.15 sys=0.00, real=0.03 secs]
2020-10-28T17:30:03.537-0800: [Full GC (Ergonomics) [PSYoungGen: 29696K->18025K(58368K)] [ParOldGen: 174545K->174810K(175104K)] 204241K->192835K(233472K), [Metaspace: 2678K->2678K(1056768K)], 0.0250460 secs] [Times: user=0.17 sys=0.00, real=0.03 secs]
2020-10-28T17:30:03.564-0800: [Full GC (Ergonomics) [PSYoungGen: 29637K->19580K(58368K)] [ParOldGen: 174810K->174892K(175104K)] 204447K->194472K(233472K), [Metaspace: 2678K->2678K(1056768K)], 0.0217244 secs] [Times: user=0.14 sys=0.01, real=0.02 secs]
2020-10-28T17:30:03.588-0800: [Full GC (Ergonomics) [PSYoungGen: 29619K->23802K(58368K)] [ParOldGen: 174892K->174888K(175104K)] 204512K->198690K(233472K), [Metaspace: 2678K->2678K(1056768K)], 0.0242850 secs] [Times: user=0.16 sys=0.00, real=0.03 secs]
2020-10-28T17:30:03.613-0800: [Full GC (Ergonomics) [PSYoungGen: 29654K->23868K(58368K)] [ParOldGen: 174888K->175000K(175104K)] 204542K->198868K(233472K), [Metaspace: 2678K->2678K(1056768K)], 0.0202639 secs] [Times: user=0.12 sys=0.00, real=0.02 secs]
2020-10-28T17:30:03.636-0800: [Full GC (Ergonomics) [PSYoungGen: 29635K->24344K(58368K)] [ParOldGen: 175000K->174631K(175104K)] 204635K->198976K(233472K), [Metaspace: 2678K->2678K(1056768K)], 0.0206868 secs] [Times: user=0.12 sys=0.00, real=0.02 secs]
2020-10-28T17:30:03.657-0800: [Full GC (Ergonomics) [PSYoungGen: 29470K->25422K(58368K)] [ParOldGen: 174631K->174532K(175104K)] 204101K->199955K(233472K), [Metaspace: 2678K->2678K(1056768K)], 0.0123513 secs] [Times: user=0.07 sys=0.00, real=0.02 secs]
2020-10-28T17:30:03.670-0800: [Full GC (Ergonomics) [PSYoungGen: 29531K->26139K(58368K)] [ParOldGen: 174532K->174956K(175104K)] 204064K->201096K(233472K), [Metaspace: 2678K->2678K(1056768K)], 0.0257267 secs] [Times: user=0.16 sys=0.00, real=0.02 secs]
2020-10-28T17:30:03.697-0800: [Full GC (Ergonomics) [PSYoungGen: 29693K->27070K(58368K)] [ParOldGen: 174956K->174789K(175104K)] 204650K->201860K(233472K), [Metaspace: 2678K->2678K(1056768K)], 0.0243107 secs] [Times: user=0.16 sys=0.01, real=0.03 secs]
2020-10-28T17:30:03.722-0800: [Full GC (Ergonomics) [PSYoungGen: 29658K->27845K(58368K)] [ParOldGen: 174789K->174379K(175104K)] 204448K->202225K(233472K), [Metaspace: 2678K->2678K(1056768K)], 0.0209275 secs] [Times: user=0.14 sys=0.00, real=0.02 secs]
2020-10-28T17:30:03.743-0800: [Full GC (Ergonomics) [PSYoungGen: 29500K->27410K(58368K)] [ParOldGen: 174379K->175058K(175104K)] 203879K->202468K(233472K), [Metaspace: 2678K->2678K(1056768K)], 0.0195943 secs] [Times: user=0.12 sys=0.00, real=0.02 secs]
2020-10-28T17:30:03.763-0800: [Full GC (Ergonomics) [PSYoungGen: 29354K->28885K(58368K)] [ParOldGen: 175058K->174583K(175104K)] 204413K->203468K(233472K), [Metaspace: 2678K->2678K(1056768K)], 0.0205829 secs] [Times: user=0.14 sys=0.00, real=0.02 secs]
2020-10-28T17:30:03.784-0800: [Full GC (Ergonomics) [PSYoungGen: 29604K->29604K(58368K)] [ParOldGen: 174583K->174583K(175104K)] 204188K->204188K(233472K), [Metaspace: 2678K->2678K(1056768K)], 0.0036260 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
2020-10-28T17:30:03.788-0800: [Full GC (Ergonomics) [PSYoungGen: 29604K->29604K(58368K)] [ParOldGen: 174707K->174583K(175104K)] 204312K->204188K(233472K), [Metaspace: 2678K->2678K(1056768K)], 0.0026627 secs] [Times: user=0.01 sys=0.00, real=0.01 secs]
2020-10-28T17:30:03.790-0800: [Full GC (Allocation Failure) [PSYoungGen: 29604K->29604K(58368K)] [ParOldGen: 174583K->174563K(175104K)] 204188K->204168K(233472K), [Metaspace: 2678K->2678K(1056768K)], 0.0152678 secs] [Times: user=0.10 sys=0.00, real=0.01 secs]
Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
	at GCLogAnalysis.generateGarbage(GCLogAnalysis.java:60)
	at GCLogAnalysis.main(GCLogAnalysis.java:31)
Heap
 PSYoungGen      total 58368K, used 29696K [0x00000007bab00000, 0x00000007c0000000, 0x00000007c0000000)
  eden space 29696K, 100% used [0x00000007bab00000,0x00000007bc800000,0x00000007bc800000)
  from space 28672K, 0% used [0x00000007be400000,0x00000007be400000,0x00000007c0000000)
  to   space 28672K, 0% used [0x00000007bc800000,0x00000007bc800000,0x00000007be400000)
 ParOldGen       total 175104K, used 174563K [0x00000007b0000000, 0x00000007bab00000, 0x00000007bab00000)
  object space 175104K, 99% used [0x00000007b0000000,0x00000007baa78f08,0x00000007bab00000)
 Metaspace       used 2708K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 297K, capacity 386K, committed 512K, reserved 1048576K
```

### 512M内存 并行GC日志

```
Learning_notes (main) ✗ java -XX:+UseParallelGC -Xms512m -Xmx512m -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis
正在执行
2020-10-28T17:30:49.257-0800: [GC (Allocation Failure) [PSYoungGen: 131584K->21493K(153088K)] 131584K->44201K(502784K), 0.0172941 secs] [Times: user=0.02 sys=0.09, real=0.02 secs]
2020-10-28T17:30:49.304-0800: [GC (Allocation Failure) [PSYoungGen: 153077K->21489K(153088K)] 175785K->86159K(502784K), 0.0249816 secs] [Times: user=0.04 sys=0.11, real=0.02 secs]
2020-10-28T17:30:49.347-0800: [GC (Allocation Failure) [PSYoungGen: 153073K->21499K(153088K)] 217743K->128830K(502784K), 0.0230683 secs] [Times: user=0.04 sys=0.10, real=0.03 secs]
2020-10-28T17:30:49.389-0800: [GC (Allocation Failure) [PSYoungGen: 153077K->21498K(153088K)] 260408K->173282K(502784K), 0.0233284 secs] [Times: user=0.04 sys=0.10, real=0.03 secs]
2020-10-28T17:30:49.432-0800: [GC (Allocation Failure) [PSYoungGen: 153082K->21491K(153088K)] 304866K->216518K(502784K), 0.0218531 secs] [Times: user=0.03 sys=0.09, real=0.02 secs]
2020-10-28T17:30:49.473-0800: [GC (Allocation Failure) [PSYoungGen: 153075K->21498K(80384K)] 348102K->255883K(430080K), 0.0211237 secs] [Times: user=0.04 sys=0.09, real=0.02 secs]
2020-10-28T17:30:49.503-0800: [GC (Allocation Failure) [PSYoungGen: 79796K->38630K(116736K)] 314181K->276178K(466432K), 0.0050862 secs] [Times: user=0.03 sys=0.00, real=0.00 secs]
2020-10-28T17:30:49.519-0800: [GC (Allocation Failure) [PSYoungGen: 97510K->50091K(116736K)] 335058K->292848K(466432K), 0.0082298 secs] [Times: user=0.04 sys=0.01, real=0.01 secs]
2020-10-28T17:30:49.537-0800: [GC (Allocation Failure) [PSYoungGen: 108967K->57721K(116736K)] 351725K->309884K(466432K), 0.0116315 secs] [Times: user=0.05 sys=0.02, real=0.01 secs]
2020-10-28T17:30:49.560-0800: [GC (Allocation Failure) [PSYoungGen: 116564K->38429K(116736K)] 368727K->323327K(466432K), 0.0176745 secs] [Times: user=0.04 sys=0.07, real=0.01 secs]
2020-10-28T17:30:49.588-0800: [GC (Allocation Failure) [PSYoungGen: 97309K->20887K(116736K)] 382207K->340070K(466432K), 0.0178082 secs] [Times: user=0.03 sys=0.07, real=0.02 secs]
2020-10-28T17:30:49.606-0800: [Full GC (Ergonomics) [PSYoungGen: 20887K->0K(116736K)] [ParOldGen: 319183K->238238K(349696K)] 340070K->238238K(466432K), [Metaspace: 2678K->2678K(1056768K)], 0.0366207 secs] [Times: user=0.19 sys=0.01, real=0.04 secs]
2020-10-28T17:30:49.655-0800: [GC (Allocation Failure) [PSYoungGen: 58651K->14484K(116736K)] 296890K->252723K(466432K), 0.0021529 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
2020-10-28T17:30:49.668-0800: [GC (Allocation Failure) [PSYoungGen: 73364K->24025K(116736K)] 311603K->275650K(466432K), 0.0053394 secs] [Times: user=0.04 sys=0.00, real=0.01 secs]
2020-10-28T17:30:49.684-0800: [GC (Allocation Failure) [PSYoungGen: 82483K->19782K(116736K)] 334107K->293826K(466432K), 0.0058295 secs] [Times: user=0.04 sys=0.00, real=0.01 secs]
2020-10-28T17:30:49.702-0800: [GC (Allocation Failure) [PSYoungGen: 78662K->18220K(116736K)] 352706K->311229K(466432K), 0.0057487 secs] [Times: user=0.03 sys=0.00, real=0.00 secs]
2020-10-28T17:30:49.719-0800: [GC (Allocation Failure) [PSYoungGen: 77100K->17599K(116736K)] 370109K->328171K(466432K), 0.0056839 secs] [Times: user=0.03 sys=0.00, real=0.01 secs]
2020-10-28T17:30:49.724-0800: [Full GC (Ergonomics) [PSYoungGen: 17599K->0K(116736K)] [ParOldGen: 310572K->267598K(349696K)] 328171K->267598K(466432K), [Metaspace: 2678K->2678K(1056768K)], 0.0367548 secs] [Times: user=0.19 sys=0.01, real=0.04 secs]
2020-10-28T17:30:49.774-0800: [GC (Allocation Failure) [PSYoungGen: 58609K->20160K(116736K)] 326207K->287758K(466432K), 0.0046939 secs] [Times: user=0.02 sys=0.00, real=0.00 secs]
2020-10-28T17:30:49.789-0800: [GC (Allocation Failure) [PSYoungGen: 79028K->18560K(116736K)] 346626K->305297K(466432K), 0.0056834 secs] [Times: user=0.03 sys=0.00, real=0.01 secs]
2020-10-28T17:30:49.805-0800: [GC (Allocation Failure) [PSYoungGen: 77440K->25791K(116736K)] 364177K->328900K(466432K), 0.0064709 secs] [Times: user=0.04 sys=0.00, real=0.01 secs]
2020-10-28T17:30:49.821-0800: [GC (Allocation Failure) [PSYoungGen: 84134K->25420K(116736K)] 387243K->353075K(466432K), 0.0086230 secs] [Times: user=0.02 sys=0.02, real=0.01 secs]
2020-10-28T17:30:49.830-0800: [Full GC (Ergonomics) [PSYoungGen: 25420K->0K(116736K)] [ParOldGen: 327654K->300444K(349696K)] 353075K->300444K(466432K), [Metaspace: 2678K->2678K(1056768K)], 0.0386200 secs] [Times: user=0.21 sys=0.00, real=0.03 secs]
2020-10-28T17:30:49.884-0800: [GC (Allocation Failure) [PSYoungGen: 58880K->20822K(116736K)] 359324K->321267K(466432K), 0.0039121 secs] [Times: user=0.02 sys=0.00, real=0.00 secs]
2020-10-28T17:30:49.898-0800: [GC (Allocation Failure) [PSYoungGen: 79702K->18555K(116736K)] 380147K->338305K(466432K), 0.0055579 secs] [Times: user=0.04 sys=0.00, real=0.01 secs]
2020-10-28T17:30:49.904-0800: [Full GC (Ergonomics) [PSYoungGen: 18555K->0K(116736K)] [ParOldGen: 319750K->310166K(349696K)] 338305K->310166K(466432K), [Metaspace: 2678K->2678K(1056768K)], 0.0389125 secs] [Times: user=0.21 sys=0.01, real=0.04 secs]
2020-10-28T17:30:49.957-0800: [GC (Allocation Failure) [PSYoungGen: 58841K->22552K(116736K)] 369008K->332719K(466432K), 0.0050450 secs] [Times: user=0.03 sys=0.00, real=0.01 secs]
2020-10-28T17:30:49.973-0800: [GC (Allocation Failure) [PSYoungGen: 81284K->21563K(116736K)] 391450K->353498K(466432K), 0.0078708 secs] [Times: user=0.03 sys=0.01, real=0.01 secs]
2020-10-28T17:30:49.981-0800: [Full GC (Ergonomics) [PSYoungGen: 21563K->0K(116736K)] [ParOldGen: 331934K->321001K(349696K)] 353498K->321001K(466432K), [Metaspace: 2678K->2678K(1056768K)], 0.0393775 secs] [Times: user=0.24 sys=0.00, real=0.04 secs]
2020-10-28T17:30:50.035-0800: [Full GC (Ergonomics) [PSYoungGen: 58436K->0K(116736K)] [ParOldGen: 321001K->325098K(349696K)] 379437K->325098K(466432K), [Metaspace: 2678K->2678K(1056768K)], 0.0413652 secs] [Times: user=0.24 sys=0.01, real=0.04 secs]
2020-10-28T17:30:50.089-0800: [Full GC (Ergonomics) [PSYoungGen: 58880K->0K(116736K)] [ParOldGen: 325098K->330497K(349696K)] 383978K->330497K(466432K), [Metaspace: 2678K->2678K(1056768K)], 0.0402942 secs] [Times: user=0.24 sys=0.00, real=0.04 secs]
2020-10-28T17:30:50.141-0800: [Full GC (Ergonomics) [PSYoungGen: 58880K->0K(116736K)] [ParOldGen: 330497K->334357K(349696K)] 389377K->334357K(466432K), [Metaspace: 2678K->2678K(1056768K)], 0.0469755 secs] [Times: user=0.23 sys=0.01, real=0.04 secs]
执行结束！ 共生成对象次数：7787
Heap
 PSYoungGen      total 116736K, used 2631K [0x00000007b5580000, 0x00000007c0000000, 0x00000007c0000000)
  eden space 58880K, 4% used [0x00000007b5580000,0x00000007b5811db8,0x00000007b8f00000)
  from space 57856K, 0% used [0x00000007bc780000,0x00000007bc780000,0x00000007c0000000)
  to   space 57856K, 0% used [0x00000007b8f00000,0x00000007b8f00000,0x00000007bc780000)
 ParOldGen       total 349696K, used 334357K [0x00000007a0000000, 0x00000007b5580000, 0x00000007b5580000)
  object space 349696K, 95% used [0x00000007a0000000,0x00000007b46856d8,0x00000007b5580000)
 Metaspace       used 2684K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 294K, capacity 386K, committed 512K, reserved 1048576K
```

### 1G内存 并行GC日志

```
java -XX:+UseParallelGC -Xms1g -Xmx1g -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis
正在执行
2020-10-28T17:31:19.395-0800: [GC (Allocation Failure) [PSYoungGen: 262144K->43502K(305664K)] 262144K->75639K(1005056K), 0.0323196 secs] [Times: user=0.04 sys=0.14, real=0.03 secs]
2020-10-28T17:31:19.465-0800: [GC (Allocation Failure) [PSYoungGen: 305646K->43519K(305664K)] 337783K->149795K(1005056K), 0.0454068 secs] [Times: user=0.06 sys=0.17, real=0.05 secs]
2020-10-28T17:31:19.549-0800: [GC (Allocation Failure) [PSYoungGen: 305663K->43518K(305664K)] 411939K->216217K(1005056K), 0.0350760 secs] [Times: user=0.07 sys=0.09, real=0.04 secs]
2020-10-28T17:31:19.620-0800: [GC (Allocation Failure) [PSYoungGen: 305662K->43511K(305664K)] 478361K->289311K(1005056K), 0.0391987 secs] [Times: user=0.07 sys=0.15, real=0.03 secs]
2020-10-28T17:31:19.703-0800: [GC (Allocation Failure) [PSYoungGen: 305655K->43507K(305664K)] 551455K->360777K(1005056K), 0.0364331 secs] [Times: user=0.06 sys=0.15, real=0.04 secs]
2020-10-28T17:31:19.779-0800: [GC (Allocation Failure) [PSYoungGen: 305651K->43511K(160256K)] 622921K->441436K(859648K), 0.0396001 secs] [Times: user=0.06 sys=0.17, real=0.04 secs]
2020-10-28T17:31:19.835-0800: [GC (Allocation Failure) [PSYoungGen: 160247K->77226K(232960K)] 558172K->478051K(932352K), 0.0109938 secs] [Times: user=0.06 sys=0.01, real=0.01 secs]
2020-10-28T17:31:19.866-0800: [GC (Allocation Failure) [PSYoungGen: 193652K->99827K(232960K)] 594478K->509750K(932352K), 0.0155469 secs] [Times: user=0.07 sys=0.02, real=0.02 secs]
2020-10-28T17:31:19.901-0800: [GC (Allocation Failure) [PSYoungGen: 216452K->112438K(232960K)] 626375K->542651K(932352K), 0.0208013 secs] [Times: user=0.07 sys=0.03, real=0.02 secs]
2020-10-28T17:31:19.940-0800: [GC (Allocation Failure) [PSYoungGen: 229174K->77506K(232960K)] 659387K->567901K(932352K), 0.0333276 secs] [Times: user=0.06 sys=0.14, real=0.03 secs]
2020-10-28T17:31:19.993-0800: [GC (Allocation Failure) [PSYoungGen: 194242K->41360K(232960K)] 684637K->598685K(932352K), 0.0313548 secs] [Times: user=0.04 sys=0.14, real=0.03 secs]
2020-10-28T17:31:20.043-0800: [GC (Allocation Failure) [PSYoungGen: 158096K->36679K(232960K)] 715421K->630158K(932352K), 0.0177119 secs] [Times: user=0.03 sys=0.07, real=0.02 secs]
2020-10-28T17:31:20.082-0800: [GC (Allocation Failure) [PSYoungGen: 153415K->31290K(232960K)] 746894K->658640K(932352K), 0.0158651 secs] [Times: user=0.04 sys=0.07, real=0.01 secs]
2020-10-28T17:31:20.098-0800: [Full GC (Ergonomics) [PSYoungGen: 31290K->0K(232960K)] [ParOldGen: 627350K->330974K(699392K)] 658640K->330974K(932352K), [Metaspace: 2678K->2678K(1056768K)], 0.0542396 secs] [Times: user=0.26 sys=0.01, real=0.06 secs]
2020-10-28T17:31:20.175-0800: [GC (Allocation Failure) [PSYoungGen: 116736K->36282K(232960K)] 447710K->367257K(932352K), 0.0122410 secs] [Times: user=0.02 sys=0.01, real=0.01 secs]
2020-10-28T17:31:20.214-0800: [GC (Allocation Failure) [PSYoungGen: 153018K->34312K(232960K)] 483993K->397488K(932352K), 0.0108175 secs] [Times: user=0.05 sys=0.00, real=0.01 secs]
执行结束！ 共生成对象次数：10106
Heap
 PSYoungGen      total 232960K, used 81814K [0x00000007aab00000, 0x00000007c0000000, 0x00000007c0000000)
  eden space 116736K, 40% used [0x00000007aab00000,0x00000007ad9639b0,0x00000007b1d00000)
  from space 116224K, 29% used [0x00000007b1d00000,0x00000007b3e82160,0x00000007b8e80000)
  to   space 116224K, 0% used [0x00000007b8e80000,0x00000007b8e80000,0x00000007c0000000)
 ParOldGen       total 699392K, used 363176K [0x0000000780000000, 0x00000007aab00000, 0x00000007aab00000)
  object space 699392K, 51% used [0x0000000780000000,0x00000007962aa048,0x00000007aab00000)
 Metaspace       used 2684K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 294K, capacity 386K, committed 512K, reserved 1048576K
```

### 2G内存 并行GC日志

```
java -XX:+UseParallelGC -Xms2g -Xmx2g -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis
正在执行
2020-10-28T17:31:42.073-0800: [GC (Allocation Failure) [PSYoungGen: 524800K->87030K(611840K)] 524800K->143377K(2010112K), 0.0580173 secs] [Times: user=0.07 sys=0.29, real=0.06 secs]
2020-10-28T17:31:42.212-0800: [GC (Allocation Failure) [PSYoungGen: 611830K->87036K(611840K)] 668177K->254243K(2010112K), 0.0790885 secs] [Times: user=0.09 sys=0.39, real=0.08 secs]
2020-10-28T17:31:42.364-0800: [GC (Allocation Failure) [PSYoungGen: 611836K->87026K(611840K)] 779043K->371293K(2010112K), 0.0635106 secs] [Times: user=0.11 sys=0.25, real=0.06 secs]
2020-10-28T17:31:42.493-0800: [GC (Allocation Failure) [PSYoungGen: 611826K->87039K(611840K)] 896093K->477458K(2010112K), 0.0629194 secs] [Times: user=0.11 sys=0.26, real=0.06 secs]
2020-10-28T17:31:42.627-0800: [GC (Allocation Failure) [PSYoungGen: 611695K->87033K(611840K)] 1002114K->586009K(2010112K), 0.0627900 secs] [Times: user=0.11 sys=0.24, real=0.07 secs]
执行结束！ 共生成对象次数：11867
Heap
 PSYoungGen      total 611840K, used 611833K [0x0000000795580000, 0x00000007c0000000, 0x00000007c0000000)
  eden space 524800K, 100% used [0x0000000795580000,0x00000007b5600000,0x00000007b5600000)
  from space 87040K, 99% used [0x00000007b5600000,0x00000007baafe538,0x00000007bab00000)
  to   space 87040K, 0% used [0x00000007bab00000,0x00000007bab00000,0x00000007c0000000)
 ParOldGen       total 1398272K, used 498976K [0x0000000740000000, 0x0000000795580000, 0x0000000795580000)
  object space 1398272K, 35% used [0x0000000740000000,0x000000075e748218,0x0000000795580000)
 Metaspace       used 2684K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 294K, capacity 386K, committed 512K, reserved 1048576K
```

### 4G内存 并行GC日志

```
java -XX:+UseParallelGC -Xms4g -Xmx4g -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis
正在执行
2020-10-28T17:32:22.581-0800: [GC (Allocation Failure) [PSYoungGen: 1048576K->174580K(1223168K)] 1048576K->238872K(4019712K), 0.0996263 secs] [Times: user=0.10 sys=0.44, real=0.10 secs]
2020-10-28T17:32:22.837-0800: [GC (Allocation Failure) [PSYoungGen: 1223156K->174590K(1223168K)] 1287448K->372940K(4019712K), 0.1257143 secs] [Times: user=0.14 sys=0.60, real=0.13 secs]
执行结束！ 共生成对象次数：8587
Heap
 PSYoungGen      total 1223168K, used 407020K [0x000000076ab00000, 0x00000007c0000000, 0x00000007c0000000)
  eden space 1048576K, 22% used [0x000000076ab00000,0x0000000778dfbae8,0x00000007aab00000)
  from space 174592K, 99% used [0x00000007b5580000,0x00000007bffff828,0x00000007c0000000)
  to   space 174592K, 0% used [0x00000007aab00000,0x00000007aab00000,0x00000007b5580000)
 ParOldGen       total 2796544K, used 198350K [0x00000006c0000000, 0x000000076ab00000, 0x000000076ab00000)
  object space 2796544K, 7% used [0x00000006c0000000,0x00000006cc1b38a8,0x000000076ab00000)
 Metaspace       used 2684K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 294K, capacity 386K, committed 512K, reserved 1048576K
```

### 8G内存 并行GC日志

```
 Learning_notes (main) ✗ java -XX:+UseParallelGC -Xms8g -Xmx8g -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis
正在执行
执行结束！ 共生成对象次数：6434
Heap
 PSYoungGen      total 2446848K, used 1814029K [0x0000000715580000, 0x00000007c0000000, 0x00000007c0000000)
  eden space 2097664K, 86% used [0x0000000715580000,0x0000000784103408,0x0000000795600000)
  from space 349184K, 0% used [0x00000007aab00000,0x00000007aab00000,0x00000007c0000000)
  to   space 349184K, 0% used [0x0000000795600000,0x0000000795600000,0x00000007aab00000)
 ParOldGen       total 5592576K, used 0K [0x00000005c0000000, 0x0000000715580000, 0x0000000715580000)
  object space 5592576K, 0% used [0x00000005c0000000,0x00000005c0000000,0x0000000715580000)
 Metaspace       used 2684K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 294K, capacity 386K, committed 512K, reserved 1048576K
```

## CMS GC日志

### 256M堆内存 CMS GC日志

```
 Learning_notes (main) ✗ java -XX:+UseConcMarkSweepGC -Xms256m -Xmx256m -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis
正在执行
2020-10-28T17:33:44.934-0800: [GC (Allocation Failure) 2020-10-28T17:33:44.934-0800: [ParNew: 69952K->8699K(78656K), 0.0088419 secs] 69952K->20426K(253440K), 0.0089039 secs] [Times: user=0.02 sys=0.03, real=0.01 secs]
2020-10-28T17:33:44.961-0800: [GC (Allocation Failure) 2020-10-28T17:33:44.961-0800: [ParNew: 78412K->8693K(78656K), 0.0108120 secs] 90139K->39796K(253440K), 0.0108515 secs] [Times: user=0.03 sys=0.04, real=0.01 secs]
2020-10-28T17:33:44.986-0800: [GC (Allocation Failure) 2020-10-28T17:33:44.986-0800: [ParNew: 78645K->8704K(78656K), 0.0172434 secs] 109748K->63761K(253440K), 0.0172923 secs] [Times: user=0.09 sys=0.01, real=0.02 secs]
2020-10-28T17:33:45.018-0800: [GC (Allocation Failure) 2020-10-28T17:33:45.018-0800: [ParNew: 78656K->8699K(78656K), 0.0144355 secs] 133713K->85818K(253440K), 0.0144731 secs] [Times: user=0.09 sys=0.01, real=0.02 secs]
2020-10-28T17:33:45.044-0800: [GC (Allocation Failure) 2020-10-28T17:33:45.044-0800: [ParNew: 78651K->8700K(78656K), 0.0172643 secs] 155770K->112741K(253440K), 0.0173123 secs] [Times: user=0.12 sys=0.01, real=0.02 secs]
2020-10-28T17:33:45.061-0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 104040K(174784K)] 112817K(253440K), 0.0001874 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.061-0800: [CMS-concurrent-mark-start]
2020-10-28T17:33:45.063-0800: [CMS-concurrent-mark: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.063-0800: [CMS-concurrent-preclean-start]
2020-10-28T17:33:45.063-0800: [CMS-concurrent-preclean: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.063-0800: [CMS-concurrent-abortable-preclean-start]
2020-10-28T17:33:45.071-0800: [GC (Allocation Failure) 2020-10-28T17:33:45.071-0800: [ParNew: 78621K->8698K(78656K), 0.0170563 secs] 182662K->137990K(253440K), 0.0170948 secs] [Times: user=0.11 sys=0.01, real=0.01 secs]
2020-10-28T17:33:45.099-0800: [GC (Allocation Failure) 2020-10-28T17:33:45.099-0800: [ParNew: 78313K->8702K(78656K), 0.0157949 secs] 207606K->161029K(253440K), 0.0158329 secs] [Times: user=0.10 sys=0.01, real=0.02 secs]
2020-10-28T17:33:45.126-0800: [GC (Allocation Failure) 2020-10-28T17:33:45.126-0800: [ParNew: 78137K->78137K(78656K), 0.0000201 secs]2020-10-28T17:33:45.126-0800: [CMS2020-10-28T17:33:45.126-0800: [CMS-concurrent-abortable-preclean: 0.002/0.063 secs] [Times: user=0.24 sys=0.02, real=0.06 secs]
 (concurrent mode failure): 152326K->154594K(174784K), 0.0280426 secs] 230464K->154594K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0281172 secs] [Times: user=0.03 sys=0.00, real=0.03 secs]
2020-10-28T17:33:45.166-0800: [GC (Allocation Failure) 2020-10-28T17:33:45.166-0800: [ParNew: 69952K->8701K(78656K), 0.0096386 secs] 224546K->177188K(253440K), 0.0096775 secs] [Times: user=0.06 sys=0.01, real=0.01 secs]
2020-10-28T17:33:45.176-0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 168486K(174784K)] 177900K(253440K), 0.0001883 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.176-0800: [CMS-concurrent-mark-start]
2020-10-28T17:33:45.177-0800: [CMS-concurrent-mark: 0.001/0.001 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.177-0800: [CMS-concurrent-preclean-start]
2020-10-28T17:33:45.177-0800: [CMS-concurrent-preclean: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.177-0800: [CMS-concurrent-abortable-preclean-start]
2020-10-28T17:33:45.178-0800: [CMS-concurrent-abortable-preclean: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.178-0800: [GC (CMS Final Remark) [YG occupancy: 17915 K (78656 K)]2020-10-28T17:33:45.178-0800: [Rescan (parallel) , 0.0002093 secs]2020-10-28T17:33:45.178-0800: [weak refs processing, 0.0000098 secs]2020-10-28T17:33:45.178-0800: [class unloading, 0.0001836 secs]2020-10-28T17:33:45.178-0800: [scrub symbol table, 0.0002854 secs]2020-10-28T17:33:45.178-0800: [scrub string table, 0.0001183 secs][1 CMS-remark: 168486K(174784K)] 186402K(253440K), 0.0008475 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.178-0800: [CMS-concurrent-sweep-start]
2020-10-28T17:33:45.179-0800: [CMS-concurrent-sweep: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.179-0800: [CMS-concurrent-reset-start]
2020-10-28T17:33:45.179-0800: [CMS-concurrent-reset: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.189-0800: [GC (Allocation Failure) 2020-10-28T17:33:45.189-0800: [ParNew (promotion failed): 78095K->78096K(78656K), 0.0072044 secs]2020-10-28T17:33:45.196-0800: [CMS: 174309K->174551K(174784K), 0.0240507 secs] 234896K->180928K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0313149 secs] [Times: user=0.06 sys=0.00, real=0.04 secs]
2020-10-28T17:33:45.221-0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 174551K(174784K)] 181602K(253440K), 0.0001581 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.221-0800: [CMS-concurrent-mark-start]
2020-10-28T17:33:45.222-0800: [CMS-concurrent-mark: 0.001/0.001 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.222-0800: [CMS-concurrent-preclean-start]
2020-10-28T17:33:45.222-0800: [CMS-concurrent-preclean: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.222-0800: [CMS-concurrent-abortable-preclean-start]
2020-10-28T17:33:45.222-0800: [CMS-concurrent-abortable-preclean: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.223-0800: [GC (CMS Final Remark) [YG occupancy: 17981 K (78656 K)]2020-10-28T17:33:45.223-0800: [Rescan (parallel) , 0.0002086 secs]2020-10-28T17:33:45.223-0800: [weak refs processing, 0.0000117 secs]2020-10-28T17:33:45.223-0800: [class unloading, 0.0001817 secs]2020-10-28T17:33:45.223-0800: [scrub symbol table, 0.0002889 secs]2020-10-28T17:33:45.223-0800: [scrub string table, 0.0001186 secs][1 CMS-remark: 174551K(174784K)] 192532K(253440K), 0.0008581 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.224-0800: [CMS-concurrent-sweep-start]
2020-10-28T17:33:45.224-0800: [CMS-concurrent-sweep: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.224-0800: [CMS-concurrent-reset-start]
2020-10-28T17:33:45.224-0800: [CMS-concurrent-reset: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.233-0800: [GC (Allocation Failure) 2020-10-28T17:33:45.233-0800: [ParNew: 78607K->78607K(78656K), 0.0000193 secs]2020-10-28T17:33:45.233-0800: [CMS: 174551K->174699K(174784K), 0.0263119 secs] 253158K->190906K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0263813 secs] [Times: user=0.03 sys=0.00, real=0.03 secs]
2020-10-28T17:33:45.260-0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 174699K(174784K)] 191542K(253440K), 0.0001780 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.260-0800: [CMS-concurrent-mark-start]
2020-10-28T17:33:45.261-0800: [CMS-concurrent-mark: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.261-0800: [CMS-concurrent-preclean-start]
2020-10-28T17:33:45.262-0800: [CMS-concurrent-preclean: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.262-0800: [CMS-concurrent-abortable-preclean-start]
2020-10-28T17:33:45.262-0800: [CMS-concurrent-abortable-preclean: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.262-0800: [GC (CMS Final Remark) [YG occupancy: 28326 K (78656 K)]2020-10-28T17:33:45.262-0800: [Rescan (parallel) , 0.0009024 secs]2020-10-28T17:33:45.263-0800: [weak refs processing, 0.0000153 secs]2020-10-28T17:33:45.263-0800: [class unloading, 0.0001910 secs]2020-10-28T17:33:45.263-0800: [scrub symbol table, 0.0002861 secs]2020-10-28T17:33:45.264-0800: [scrub string table, 0.0001184 secs][1 CMS-remark: 174699K(174784K)] 203025K(253440K), 0.0015649 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.264-0800: [CMS-concurrent-sweep-start]
2020-10-28T17:33:45.264-0800: [CMS-concurrent-sweep: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.264-0800: [CMS-concurrent-reset-start]
2020-10-28T17:33:45.264-0800: [CMS-concurrent-reset: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.271-0800: [GC (Allocation Failure) 2020-10-28T17:33:45.271-0800: [ParNew: 78558K->78558K(78656K), 0.0000208 secs]2020-10-28T17:33:45.271-0800: [CMS: 174651K->174082K(174784K), 0.0266043 secs] 253209K->202294K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0266801 secs] [Times: user=0.03 sys=0.00, real=0.02 secs]
2020-10-28T17:33:45.298-0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 174082K(174784K)] 202725K(253440K), 0.0001760 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.298-0800: [CMS-concurrent-mark-start]
2020-10-28T17:33:45.299-0800: [CMS-concurrent-mark: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.299-0800: [CMS-concurrent-preclean-start]
2020-10-28T17:33:45.300-0800: [CMS-concurrent-preclean: 0.001/0.001 secs] [Times: user=0.01 sys=0.00, real=0.01 secs]
2020-10-28T17:33:45.300-0800: [CMS-concurrent-abortable-preclean-start]
2020-10-28T17:33:45.300-0800: [CMS-concurrent-abortable-preclean: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.300-0800: [GC (CMS Final Remark) [YG occupancy: 39383 K (78656 K)]2020-10-28T17:33:45.300-0800: [Rescan (parallel) , 0.0006049 secs]2020-10-28T17:33:45.301-0800: [weak refs processing, 0.0000136 secs]2020-10-28T17:33:45.301-0800: [class unloading, 0.0001833 secs]2020-10-28T17:33:45.301-0800: [scrub symbol table, 0.0002827 secs]2020-10-28T17:33:45.301-0800: [scrub string table, 0.0001187 secs][1 CMS-remark: 174082K(174784K)] 213465K(253440K), 0.0012511 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.301-0800: [CMS-concurrent-sweep-start]
2020-10-28T17:33:45.302-0800: [CMS-concurrent-sweep: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.302-0800: [CMS-concurrent-reset-start]
2020-10-28T17:33:45.302-0800: [CMS-concurrent-reset: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.307-0800: [GC (Allocation Failure) 2020-10-28T17:33:45.307-0800: [ParNew: 78655K->78655K(78656K), 0.0000198 secs]2020-10-28T17:33:45.307-0800: [CMS: 173886K->174600K(174784K), 0.0277043 secs] 252541K->214632K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0277738 secs] [Times: user=0.02 sys=0.00, real=0.03 secs]
2020-10-28T17:33:45.335-0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 174600K(174784K)] 214668K(253440K), 0.0001803 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.335-0800: [CMS-concurrent-mark-start]
2020-10-28T17:33:45.336-0800: [CMS-concurrent-mark: 0.001/0.001 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.336-0800: [CMS-concurrent-preclean-start]
2020-10-28T17:33:45.337-0800: [CMS-concurrent-preclean: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.337-0800: [CMS-concurrent-abortable-preclean-start]
2020-10-28T17:33:45.337-0800: [CMS-concurrent-abortable-preclean: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.337-0800: [GC (CMS Final Remark) [YG occupancy: 51966 K (78656 K)]2020-10-28T17:33:45.337-0800: [Rescan (parallel) , 0.0010210 secs]2020-10-28T17:33:45.338-0800: [weak refs processing, 0.0000221 secs]2020-10-28T17:33:45.338-0800: [class unloading, 0.0002375 secs]2020-10-28T17:33:45.338-0800: [scrub symbol table, 0.0003349 secs]2020-10-28T17:33:45.339-0800: [scrub string table, 0.0001374 secs][1 CMS-remark: 174600K(174784K)] 226567K(253440K), 0.0018363 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.339-0800: [CMS-concurrent-sweep-start]
2020-10-28T17:33:45.339-0800: [CMS-concurrent-sweep: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.339-0800: [CMS-concurrent-reset-start]
2020-10-28T17:33:45.339-0800: [CMS-concurrent-reset: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.343-0800: [GC (Allocation Failure) 2020-10-28T17:33:45.343-0800: [ParNew: 78475K->78475K(78656K), 0.0000194 secs]2020-10-28T17:33:45.343-0800: [CMS: 174600K->174478K(174784K), 0.0287244 secs] 253075K->222659K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0287973 secs] [Times: user=0.03 sys=0.00, real=0.03 secs]
2020-10-28T17:33:45.372-0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 174478K(174784K)] 222908K(253440K), 0.0001814 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.372-0800: [CMS-concurrent-mark-start]
2020-10-28T17:33:45.373-0800: [CMS-concurrent-mark: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.373-0800: [CMS-concurrent-preclean-start]
2020-10-28T17:33:45.374-0800: [CMS-concurrent-preclean: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.374-0800: [CMS-concurrent-abortable-preclean-start]
2020-10-28T17:33:45.374-0800: [CMS-concurrent-abortable-preclean: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.374-0800: [GC (CMS Final Remark) [YG occupancy: 60767 K (78656 K)]2020-10-28T17:33:45.374-0800: [Rescan (parallel) , 0.0002255 secs]2020-10-28T17:33:45.374-0800: [weak refs processing, 0.0000100 secs]2020-10-28T17:33:45.374-0800: [class unloading, 0.0001858 secs]2020-10-28T17:33:45.375-0800: [scrub symbol table, 0.0002858 secs]2020-10-28T17:33:45.375-0800: [scrub string table, 0.0001186 secs][1 CMS-remark: 174478K(174784K)] 235245K(253440K), 0.0008711 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.375-0800: [CMS-concurrent-sweep-start]
2020-10-28T17:33:45.375-0800: [CMS-concurrent-sweep: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.375-0800: [CMS-concurrent-reset-start]
2020-10-28T17:33:45.375-0800: [CMS-concurrent-reset: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.378-0800: [GC (Allocation Failure) 2020-10-28T17:33:45.378-0800: [ParNew: 78641K->78641K(78656K), 0.0000192 secs]2020-10-28T17:33:45.378-0800: [CMS: 174478K->174526K(174784K), 0.0282014 secs] 253119K->226947K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0282688 secs] [Times: user=0.02 sys=0.00, real=0.03 secs]
2020-10-28T17:33:45.406-0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 174526K(174784K)] 227019K(253440K), 0.0001887 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.407-0800: [CMS-concurrent-mark-start]
2020-10-28T17:33:45.407-0800: [CMS-concurrent-mark: 0.001/0.001 secs] [Times: user=0.01 sys=0.01, real=0.00 secs]
2020-10-28T17:33:45.407-0800: [CMS-concurrent-preclean-start]
2020-10-28T17:33:45.408-0800: [CMS-concurrent-preclean: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.408-0800: [CMS-concurrent-abortable-preclean-start]
2020-10-28T17:33:45.408-0800: [CMS-concurrent-abortable-preclean: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.408-0800: [GC (CMS Final Remark) [YG occupancy: 64816 K (78656 K)]2020-10-28T17:33:45.408-0800: [Rescan (parallel) , 0.0002069 secs]2020-10-28T17:33:45.409-0800: [weak refs processing, 0.0000097 secs]2020-10-28T17:33:45.409-0800: [class unloading, 0.0001761 secs]2020-10-28T17:33:45.409-0800: [scrub symbol table, 0.0003004 secs]2020-10-28T17:33:45.409-0800: [scrub string table, 0.0001360 secs][1 CMS-remark: 174526K(174784K)] 239342K(253440K), 0.0008789 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.409-0800: [CMS-concurrent-sweep-start]
2020-10-28T17:33:45.410-0800: [CMS-concurrent-sweep: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.01 secs]
2020-10-28T17:33:45.410-0800: [CMS-concurrent-reset-start]
2020-10-28T17:33:45.410-0800: [CMS-concurrent-reset: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.412-0800: [GC (Allocation Failure) 2020-10-28T17:33:45.412-0800: [ParNew: 78464K->78464K(78656K), 0.0000302 secs]2020-10-28T17:33:45.412-0800: [CMS: 174526K->174700K(174784K), 0.0303129 secs] 252990K->232110K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0304547 secs] [Times: user=0.03 sys=0.00, real=0.03 secs]
2020-10-28T17:33:45.443-0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 174700K(174784K)] 232398K(253440K), 0.0001999 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.443-0800: [CMS-concurrent-mark-start]
2020-10-28T17:33:45.444-0800: [CMS-concurrent-mark: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.444-0800: [CMS-concurrent-preclean-start]
2020-10-28T17:33:45.445-0800: [CMS-concurrent-preclean: 0.001/0.001 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.445-0800: [CMS-concurrent-abortable-preclean-start]
2020-10-28T17:33:45.445-0800: [CMS-concurrent-abortable-preclean: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.445-0800: [GC (CMS Final Remark) [YG occupancy: 67254 K (78656 K)]2020-10-28T17:33:45.445-0800: [Rescan (parallel) , 0.0002315 secs]2020-10-28T17:33:45.445-0800: [weak refs processing, 0.0000121 secs]2020-10-28T17:33:45.445-0800: [class unloading, 0.0001807 secs]2020-10-28T17:33:45.445-0800: [scrub symbol table, 0.0002874 secs]2020-10-28T17:33:45.446-0800: [scrub string table, 0.0001192 secs][1 CMS-remark: 174700K(174784K)] 241955K(253440K), 0.0008760 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.446-0800: [CMS-concurrent-sweep-start]
2020-10-28T17:33:45.446-0800: [CMS-concurrent-sweep: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.446-0800: [CMS-concurrent-reset-start]
2020-10-28T17:33:45.446-0800: [CMS-concurrent-reset: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.448-0800: [GC (Allocation Failure) 2020-10-28T17:33:45.448-0800: [ParNew: 78550K->78550K(78656K), 0.0000168 secs]2020-10-28T17:33:45.448-0800: [CMS: 174513K->174733K(174784K), 0.0274961 secs] 253063K->234208K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0275528 secs] [Times: user=0.03 sys=0.00, real=0.03 secs]
2020-10-28T17:33:45.476-0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 174733K(174784K)] 234352K(253440K), 0.0003442 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.476-0800: [CMS-concurrent-mark-start]
2020-10-28T17:33:45.477-0800: [CMS-concurrent-mark: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.477-0800: [CMS-concurrent-preclean-start]
2020-10-28T17:33:45.478-0800: [CMS-concurrent-preclean: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.478-0800: [CMS-concurrent-abortable-preclean-start]
2020-10-28T17:33:45.478-0800: [CMS-concurrent-abortable-preclean: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.478-0800: [GC (CMS Final Remark) [YG occupancy: 70018 K (78656 K)]2020-10-28T17:33:45.478-0800: [Rescan (parallel) , 0.0002879 secs]2020-10-28T17:33:45.478-0800: [weak refs processing, 0.0000124 secs]2020-10-28T17:33:45.478-0800: [class unloading, 0.0001802 secs]2020-10-28T17:33:45.478-0800: [scrub symbol table, 0.0002851 secs]2020-10-28T17:33:45.479-0800: [scrub string table, 0.0001183 secs][1 CMS-remark: 174733K(174784K)] 244752K(253440K), 0.0009289 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.479-0800: [CMS-concurrent-sweep-start]
2020-10-28T17:33:45.479-0800: [CMS-concurrent-sweep: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.479-0800: [CMS-concurrent-reset-start]
2020-10-28T17:33:45.479-0800: [CMS-concurrent-reset: 0.000/0.000 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.480-0800: [GC (Allocation Failure) 2020-10-28T17:33:45.480-0800: [ParNew: 78644K->78644K(78656K), 0.0000158 secs]2020-10-28T17:33:45.480-0800: [CMS: 174733K->174291K(174784K), 0.0287811 secs] 253377K->234981K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0288357 secs] [Times: user=0.02 sys=0.00, real=0.02 secs]
2020-10-28T17:33:45.509-0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 174291K(174784K)] 235105K(253440K), 0.0002453 secs] [Times: user=0.00 sys=0.00, real=0.01 secs]
2020-10-28T17:33:45.510-0800: [CMS-concurrent-mark-start]
2020-10-28T17:33:45.511-0800: [CMS-concurrent-mark: 0.001/0.001 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.511-0800: [CMS-concurrent-preclean-start]
2020-10-28T17:33:45.512-0800: [CMS-concurrent-preclean: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.512-0800: [CMS-concurrent-abortable-preclean-start]
2020-10-28T17:33:45.512-0800: [CMS-concurrent-abortable-preclean: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.512-0800: [GC (CMS Final Remark) [YG occupancy: 71131 K (78656 K)]2020-10-28T17:33:45.512-0800: [Rescan (parallel) , 0.0009001 secs]2020-10-28T17:33:45.513-0800: [weak refs processing, 0.0000123 secs]2020-10-28T17:33:45.513-0800: [class unloading, 0.0002222 secs]2020-10-28T17:33:45.513-0800: [scrub symbol table, 0.0003162 secs]2020-10-28T17:33:45.513-0800: [scrub string table, 0.0001197 secs][1 CMS-remark: 174291K(174784K)] 245422K(253440K), 0.0016286 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.513-0800: [CMS-concurrent-sweep-start]
2020-10-28T17:33:45.514-0800: [CMS-concurrent-sweep: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.514-0800: [CMS-concurrent-reset-start]
2020-10-28T17:33:45.514-0800: [CMS-concurrent-reset: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.515-0800: [GC (Allocation Failure) 2020-10-28T17:33:45.515-0800: [ParNew: 78404K->78404K(78656K), 0.0000159 secs]2020-10-28T17:33:45.515-0800: [CMS: 174291K->174610K(174784K), 0.0273834 secs] 252695K->238833K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0274364 secs] [Times: user=0.02 sys=0.00, real=0.03 secs]
2020-10-28T17:33:45.542-0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 174610K(174784K)] 239633K(253440K), 0.0002133 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.543-0800: [CMS-concurrent-mark-start]
2020-10-28T17:33:45.543-0800: [CMS-concurrent-mark: 0.001/0.001 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.543-0800: [CMS-concurrent-preclean-start]
2020-10-28T17:33:45.544-0800: [CMS-concurrent-preclean: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.544-0800: [CMS-concurrent-abortable-preclean-start]
2020-10-28T17:33:45.544-0800: [CMS-concurrent-abortable-preclean: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.544-0800: [GC (CMS Final Remark) [YG occupancy: 73941 K (78656 K)]2020-10-28T17:33:45.544-0800: [Rescan (parallel) , 0.0002855 secs]2020-10-28T17:33:45.545-0800: [weak refs processing, 0.0000124 secs]2020-10-28T17:33:45.545-0800: [class unloading, 0.0001815 secs]2020-10-28T17:33:45.545-0800: [scrub symbol table, 0.0002852 secs]2020-10-28T17:33:45.545-0800: [scrub string table, 0.0001184 secs][1 CMS-remark: 174610K(174784K)] 248551K(253440K), 0.0009285 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.545-0800: [CMS-concurrent-sweep-start]
2020-10-28T17:33:45.546-0800: [CMS-concurrent-sweep: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.546-0800: [CMS-concurrent-reset-start]
2020-10-28T17:33:45.546-0800: [CMS-concurrent-reset: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.546-0800: [GC (Allocation Failure) 2020-10-28T17:33:45.546-0800: [ParNew: 78646K->78646K(78656K), 0.0000158 secs]2020-10-28T17:33:45.546-0800: [CMS: 174610K->174406K(174784K), 0.0266180 secs] 253257K->239979K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0266723 secs] [Times: user=0.03 sys=0.00, real=0.03 secs]
2020-10-28T17:33:45.573-0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 174406K(174784K)] 239997K(253440K), 0.0002971 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.574-0800: [CMS-concurrent-mark-start]
2020-10-28T17:33:45.574-0800: [CMS-concurrent-mark: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.574-0800: [CMS-concurrent-preclean-start]
2020-10-28T17:33:45.575-0800: [CMS-concurrent-preclean: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.575-0800: [CMS-concurrent-abortable-preclean-start]
2020-10-28T17:33:45.575-0800: [CMS-concurrent-abortable-preclean: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.575-0800: [GC (CMS Final Remark) [YG occupancy: 75041 K (78656 K)]2020-10-28T17:33:45.575-0800: [Rescan (parallel) , 0.0001852 secs]2020-10-28T17:33:45.576-0800: [weak refs processing, 0.0000106 secs]2020-10-28T17:33:45.576-0800: [class unloading, 0.0001988 secs]2020-10-28T17:33:45.576-0800: [scrub symbol table, 0.0002976 secs]2020-10-28T17:33:45.576-0800: [scrub string table, 0.0001208 secs][1 CMS-remark: 174406K(174784K)] 249447K(253440K), 0.0008575 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.576-0800: [CMS-concurrent-sweep-start]
2020-10-28T17:33:45.577-0800: [CMS-concurrent-sweep: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.577-0800: [CMS-concurrent-reset-start]
2020-10-28T17:33:45.577-0800: [CMS-concurrent-reset: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.577-0800: [GC (Allocation Failure) 2020-10-28T17:33:45.577-0800: [ParNew: 78494K->78494K(78656K), 0.0000190 secs]2020-10-28T17:33:45.577-0800: [CMS: 174406K->174521K(174784K), 0.0302419 secs] 252901K->241570K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0303011 secs] [Times: user=0.03 sys=0.00, real=0.03 secs]
2020-10-28T17:33:45.608-0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 174521K(174784K)] 241755K(253440K), 0.0002363 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.608-0800: [CMS-concurrent-mark-start]
2020-10-28T17:33:45.609-0800: [CMS-concurrent-mark: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.609-0800: [CMS-concurrent-preclean-start]
2020-10-28T17:33:45.610-0800: [CMS-concurrent-preclean: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.01 secs]
2020-10-28T17:33:45.610-0800: [CMS-concurrent-abortable-preclean-start]
2020-10-28T17:33:45.610-0800: [CMS-concurrent-abortable-preclean: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.610-0800: [GC (CMS Final Remark) [YG occupancy: 75939 K (78656 K)]2020-10-28T17:33:45.610-0800: [Rescan (parallel) , 0.0009214 secs]2020-10-28T17:33:45.611-0800: [weak refs processing, 0.0000155 secs]2020-10-28T17:33:45.611-0800: [class unloading, 0.0002091 secs]2020-10-28T17:33:45.611-0800: [scrub symbol table, 0.0002963 secs]2020-10-28T17:33:45.611-0800: [scrub string table, 0.0001188 secs][1 CMS-remark: 174521K(174784K)] 250461K(253440K), 0.0016151 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.612-0800: [CMS-concurrent-sweep-start]
2020-10-28T17:33:45.612-0800: [CMS-concurrent-sweep: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.612-0800: [CMS-concurrent-reset-start]
2020-10-28T17:33:45.612-0800: [CMS-concurrent-reset: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.612-0800: [GC (Allocation Failure) 2020-10-28T17:33:45.612-0800: [ParNew: 78558K->78558K(78656K), 0.0000164 secs]2020-10-28T17:33:45.612-0800: [CMS: 174047K->174753K(174784K), 0.0285343 secs] 252605K->242596K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0285882 secs] [Times: user=0.02 sys=0.00, real=0.03 secs]
2020-10-28T17:33:45.641-0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 174753K(174784K)] 242975K(253440K), 0.0002041 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.641-0800: [CMS-concurrent-mark-start]
2020-10-28T17:33:45.642-0800: [CMS-concurrent-mark: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.642-0800: [CMS-concurrent-preclean-start]
2020-10-28T17:33:45.643-0800: [Full GC (Allocation Failure) 2020-10-28T17:33:45.643-0800: [CMS2020-10-28T17:33:45.643-0800: [CMS-concurrent-preclean: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
 (concurrent mode failure): 174753K->174313K(174784K), 0.0288081 secs] 253346K->244587K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0288456 secs] [Times: user=0.03 sys=0.00, real=0.03 secs]
2020-10-28T17:33:45.673-0800: [Full GC (Allocation Failure) 2020-10-28T17:33:45.673-0800: [CMS: 174736K->174728K(174784K), 0.0275819 secs] 253392K->244147K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0276223 secs] [Times: user=0.03 sys=0.00, real=0.03 secs]
2020-10-28T17:33:45.701-0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 174728K(174784K)] 244291K(253440K), 0.0002704 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.701-0800: [CMS-concurrent-mark-start]
2020-10-28T17:33:45.702-0800: [CMS-concurrent-mark: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.702-0800: [CMS-concurrent-preclean-start]
2020-10-28T17:33:45.702-0800: [Full GC (Allocation Failure) 2020-10-28T17:33:45.702-0800: [CMS2020-10-28T17:33:45.703-0800: [CMS-concurrent-preclean: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
 (concurrent mode failure): 174728K->174515K(174784K), 0.0282740 secs] 253282K->243814K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0283096 secs] [Times: user=0.03 sys=0.00, real=0.03 secs]
2020-10-28T17:33:45.733-0800: [Full GC (Allocation Failure) 2020-10-28T17:33:45.733-0800: [CMS: 174515K->174671K(174784K), 0.0241133 secs] 252965K->245003K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0241569 secs] [Times: user=0.02 sys=0.00, real=0.02 secs]
2020-10-28T17:33:45.757-0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 174671K(174784K)] 245291K(253440K), 0.0002338 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.757-0800: [CMS-concurrent-mark-start]
2020-10-28T17:33:45.758-0800: [CMS-concurrent-mark: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.758-0800: [CMS-concurrent-preclean-start]
2020-10-28T17:33:45.759-0800: [Full GC (Allocation Failure) 2020-10-28T17:33:45.759-0800: [CMS2020-10-28T17:33:45.759-0800: [CMS-concurrent-preclean: 0.001/0.001 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
 (concurrent mode failure): 174671K->174740K(174784K), 0.0154233 secs] 253246K->246730K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0154596 secs] [Times: user=0.01 sys=0.00, real=0.02 secs]
2020-10-28T17:33:45.776-0800: [Full GC (Allocation Failure) 2020-10-28T17:33:45.776-0800: [CMS: 174740K->174509K(174784K), 0.0273112 secs] 253271K->248338K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0273535 secs] [Times: user=0.03 sys=0.00, real=0.03 secs]
2020-10-28T17:33:45.803-0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 174509K(174784K)] 248482K(253440K), 0.0002266 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.803-0800: [CMS-concurrent-mark-start]
2020-10-28T17:33:45.804-0800: [Full GC (Allocation Failure) 2020-10-28T17:33:45.804-0800: [CMS2020-10-28T17:33:45.806-0800: [CMS-concurrent-mark: 0.001/0.002 secs] [Times: user=0.00 sys=0.01, real=0.00 secs]
 (concurrent mode failure): 174653K->174603K(174784K), 0.0328721 secs] 253235K->248619K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0329069 secs] [Times: user=0.03 sys=0.01, real=0.03 secs]
2020-10-28T17:33:45.838-0800: [Full GC (Allocation Failure) 2020-10-28T17:33:45.838-0800: [CMS: 174603K->174273K(174784K), 0.0315543 secs] 253256K->249009K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0315931 secs] [Times: user=0.03 sys=0.00, real=0.04 secs]
2020-10-28T17:33:45.870-0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 174273K(174784K)] 249138K(253440K), 0.0001815 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:33:45.870-0800: [CMS-concurrent-mark-start]
2020-10-28T17:33:45.870-0800: [Full GC (Allocation Failure) 2020-10-28T17:33:45.870-0800: [CMS2020-10-28T17:33:45.872-0800: [CMS-concurrent-mark: 0.001/0.002 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
 (concurrent mode failure): 174273K->174642K(174784K), 0.0275340 secs] 252631K->249023K(253440K), [Metaspace: 2678K->2678K(1056768K)], 0.0275720 secs] [Times: user=0.03 sys=0.00, real=0.02 secs]
执行结束！ 共生成对象次数：4219
Heap
 par new generation   total 78656K, used 75354K [0x00000007b0000000, 0x00000007b5550000, 0x00000007b5550000)
  eden space 69952K, 100% used [0x00000007b0000000, 0x00000007b4450000, 0x00000007b4450000)
  from space 8704K,  62% used [0x00000007b4cd0000, 0x00000007b5216b80, 0x00000007b5550000)
  to   space 8704K,   0% used [0x00000007b4450000, 0x00000007b4450000, 0x00000007b4cd0000)
 concurrent mark-sweep generation total 174784K, used 174642K [0x00000007b5550000, 0x00000007c0000000, 0x00000007c0000000)
 Metaspace       used 2684K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 294K, capacity 386K, committed 512K, reserved 1048576K
```

### 512M堆内存 CMS GC日志

```
 Learning_notes (main) ✗ java -XX:+UseConcMarkSweepGC -Xms512m -Xmx512m -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis
正在执行
2020-10-28T17:34:37.004-0800: [GC (Allocation Failure) 2020-10-28T17:34:37.004-0800: [ParNew: 139776K->17472K(157248K), 0.0180339 secs] 139776K->42898K(506816K), 0.0180890 secs] [Times: user=0.05 sys=0.07, real=0.02 secs]
2020-10-28T17:34:37.051-0800: [GC (Allocation Failure) 2020-10-28T17:34:37.051-0800: [ParNew: 157201K->17471K(157248K), 0.0221618 secs] 182627K->89010K(506816K), 0.0222015 secs] [Times: user=0.04 sys=0.10, real=0.02 secs]
2020-10-28T17:34:37.090-0800: [GC (Allocation Failure) 2020-10-28T17:34:37.090-0800: [ParNew: 157247K->17470K(157248K), 0.0353817 secs] 228786K->144028K(506816K), 0.0354200 secs] [Times: user=0.24 sys=0.02, real=0.03 secs]
2020-10-28T17:34:37.144-0800: [GC (Allocation Failure) 2020-10-28T17:34:37.144-0800: [ParNew: 157246K->17470K(157248K), 0.0282565 secs] 283804K->190336K(506816K), 0.0282990 secs] [Times: user=0.19 sys=0.02, real=0.03 secs]
2020-10-28T17:34:37.193-0800: [GC (Allocation Failure) 2020-10-28T17:34:37.193-0800: [ParNew: 157246K->17470K(157248K), 0.0273488 secs] 330112K->233625K(506816K), 0.0273869 secs] [Times: user=0.18 sys=0.01, real=0.03 secs]
2020-10-28T17:34:37.220-0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 216154K(349568K)] 234094K(506816K), 0.0002374 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:34:37.221-0800: [CMS-concurrent-mark-start]
2020-10-28T17:34:37.223-0800: [CMS-concurrent-mark: 0.002/0.002 secs] [Times: user=0.01 sys=0.01, real=0.00 secs]
2020-10-28T17:34:37.223-0800: [CMS-concurrent-preclean-start]
2020-10-28T17:34:37.224-0800: [CMS-concurrent-preclean: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:34:37.224-0800: [CMS-concurrent-abortable-preclean-start]
2020-10-28T17:34:37.239-0800: [GC (Allocation Failure) 2020-10-28T17:34:37.239-0800: [ParNew: 157246K->17469K(157248K), 0.0260492 secs] 373401K->274769K(506816K), 0.0260885 secs] [Times: user=0.17 sys=0.01, real=0.03 secs]
2020-10-28T17:34:37.287-0800: [GC (Allocation Failure) 2020-10-28T17:34:37.287-0800: [ParNew: 157245K->17470K(157248K), 0.0288901 secs] 414545K->319415K(506816K), 0.0289264 secs] [Times: user=0.17 sys=0.02, real=0.03 secs]
2020-10-28T17:34:37.335-0800: [GC (Allocation Failure) 2020-10-28T17:34:37.335-0800: [ParNew: 157246K->157246K(157248K), 0.0000193 secs]2020-10-28T17:34:37.335-0800: [CMS2020-10-28T17:34:37.335-0800: [CMS-concurrent-abortable-preclean: 0.005/0.111 secs] [Times: user=0.40 sys=0.03, real=0.11 secs]
 (concurrent mode failure): 301944K->246242K(349568K), 0.0462978 secs] 459191K->246242K(506816K), [Metaspace: 2678K->2678K(1056768K)], 0.0463705 secs] [Times: user=0.05 sys=0.00, real=0.05 secs]
2020-10-28T17:34:37.403-0800: [GC (Allocation Failure) 2020-10-28T17:34:37.403-0800: [ParNew: 139776K->17469K(157248K), 0.0072489 secs] 386018K->295018K(506816K), 0.0072919 secs] [Times: user=0.05 sys=0.00, real=0.01 secs]
2020-10-28T17:34:37.410-0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 277548K(349568K)] 295090K(506816K), 0.0001786 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:34:37.411-0800: [CMS-concurrent-mark-start]
2020-10-28T17:34:37.412-0800: [CMS-concurrent-mark: 0.002/0.002 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:34:37.412-0800: [CMS-concurrent-preclean-start]
2020-10-28T17:34:37.414-0800: [CMS-concurrent-preclean: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:34:37.414-0800: [CMS-concurrent-abortable-preclean-start]
2020-10-28T17:34:37.430-0800: [GC (Allocation Failure) 2020-10-28T17:34:37.430-0800: [ParNew: 157245K->17470K(157248K), 0.0193818 secs] 434794K->339379K(506816K), 0.0195074 secs] [Times: user=0.11 sys=0.01, real=0.02 secs]
2020-10-28T17:34:37.451-0800: [CMS-concurrent-abortable-preclean: 0.002/0.038 secs] [Times: user=0.13 sys=0.01, real=0.04 secs]
2020-10-28T17:34:37.451-0800: [GC (CMS Final Remark) [YG occupancy: 27865 K (157248 K)]2020-10-28T17:34:37.451-0800: [Rescan (parallel) , 0.0006080 secs]2020-10-28T17:34:37.452-0800: [weak refs processing, 0.0000139 secs]2020-10-28T17:34:37.452-0800: [class unloading, 0.0002118 secs]2020-10-28T17:34:37.452-0800: [scrub symbol table, 0.0003040 secs]2020-10-28T17:34:37.453-0800: [scrub string table, 0.0001291 secs][1 CMS-remark: 321908K(349568K)] 349773K(506816K), 0.0013157 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
2020-10-28T17:34:37.453-0800: [CMS-concurrent-sweep-start]
2020-10-28T17:34:37.453-0800: [CMS-concurrent-sweep: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:34:37.453-0800: [CMS-concurrent-reset-start]
2020-10-28T17:34:37.454-0800: [CMS-concurrent-reset: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:34:37.469-0800: [GC (Allocation Failure) 2020-10-28T17:34:37.469-0800: [ParNew: 157246K->17471K(157248K), 0.0150700 secs] 445005K->351096K(506816K), 0.0151108 secs] [Times: user=0.10 sys=0.01, real=0.02 secs]
2020-10-28T17:34:37.484-0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 333624K(349568K)] 354043K(506816K), 0.0001307 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:34:37.484-0800: [CMS-concurrent-mark-start]
2020-10-28T17:34:37.486-0800: [CMS-concurrent-mark: 0.001/0.001 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
2020-10-28T17:34:37.486-0800: [CMS-concurrent-preclean-start]
2020-10-28T17:34:37.487-0800: [CMS-concurrent-preclean: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:34:37.487-0800: [CMS-concurrent-abortable-preclean-start]
2020-10-28T17:34:37.487-0800: [CMS-concurrent-abortable-preclean: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:34:37.487-0800: [GC (CMS Final Remark) [YG occupancy: 39439 K (157248 K)]2020-10-28T17:34:37.487-0800: [Rescan (parallel) , 0.0003806 secs]2020-10-28T17:34:37.487-0800: [weak refs processing, 0.0000131 secs]2020-10-28T17:34:37.487-0800: [class unloading, 0.0001978 secs]2020-10-28T17:34:37.488-0800: [scrub symbol table, 0.0002877 secs]2020-10-28T17:34:37.488-0800: [scrub string table, 0.0001189 secs][1 CMS-remark: 333624K(349568K)] 373064K(506816K), 0.0010454 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:34:37.488-0800: [CMS-concurrent-sweep-start]
2020-10-28T17:34:37.489-0800: [CMS-concurrent-sweep: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:34:37.489-0800: [CMS-concurrent-reset-start]
2020-10-28T17:34:37.489-0800: [CMS-concurrent-reset: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:34:37.505-0800: [GC (Allocation Failure) 2020-10-28T17:34:37.505-0800: [ParNew: 157233K->17467K(157248K), 0.0083362 secs] 425993K->326928K(506816K), 0.0083851 secs] [Times: user=0.06 sys=0.00, real=0.01 secs]
2020-10-28T17:34:37.513-0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 309460K(349568K)] 327228K(506816K), 0.0002177 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:34:37.514-0800: [CMS-concurrent-mark-start]
2020-10-28T17:34:37.515-0800: [CMS-concurrent-mark: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:34:37.515-0800: [CMS-concurrent-preclean-start]
2020-10-28T17:34:37.516-0800: [CMS-concurrent-preclean: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:34:37.516-0800: [CMS-concurrent-abortable-preclean-start]
2020-10-28T17:34:37.532-0800: [GC (Allocation Failure) 2020-10-28T17:34:37.532-0800: [ParNew: 157243K->157243K(157248K), 0.0000199 secs]2020-10-28T17:34:37.532-0800: [CMS2020-10-28T17:34:37.532-0800: [CMS-concurrent-abortable-preclean: 0.001/0.016 secs] [Times: user=0.02 sys=0.00, real=0.02 secs]
 (concurrent mode failure): 309460K->311995K(349568K), 0.0478763 secs] 466704K->311995K(506816K), [Metaspace: 2678K->2678K(1056768K)], 0.0479520 secs] [Times: user=0.05 sys=0.00, real=0.05 secs]
2020-10-28T17:34:37.601-0800: [GC (Allocation Failure) 2020-10-28T17:34:37.601-0800: [ParNew: 139776K->17468K(157248K), 0.0100901 secs] 451771K->357906K(506816K), 0.0101320 secs] [Times: user=0.07 sys=0.00, real=0.01 secs]
2020-10-28T17:34:37.611-0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 340437K(349568K)] 358050K(506816K), 0.0001868 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:34:37.612-0800: [CMS-concurrent-mark-start]
2020-10-28T17:34:37.613-0800: [CMS-concurrent-mark: 0.002/0.002 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:34:37.613-0800: [CMS-concurrent-preclean-start]
2020-10-28T17:34:37.614-0800: [CMS-concurrent-preclean: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:34:37.614-0800: [CMS-concurrent-abortable-preclean-start]
2020-10-28T17:34:37.614-0800: [CMS-concurrent-abortable-preclean: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:34:37.614-0800: [GC (CMS Final Remark) [YG occupancy: 34304 K (157248 K)]2020-10-28T17:34:37.614-0800: [Rescan (parallel) , 0.0006964 secs]2020-10-28T17:34:37.615-0800: [weak refs processing, 0.0000184 secs]2020-10-28T17:34:37.615-0800: [class unloading, 0.0002460 secs]2020-10-28T17:34:37.615-0800: [scrub symbol table, 0.0003219 secs]2020-10-28T17:34:37.616-0800: [scrub string table, 0.0001331 secs][1 CMS-remark: 340437K(349568K)] 374742K(506816K), 0.0014780 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
2020-10-28T17:34:37.616-0800: [CMS-concurrent-sweep-start]
2020-10-28T17:34:37.617-0800: [CMS-concurrent-sweep: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:34:37.617-0800: [CMS-concurrent-reset-start]
2020-10-28T17:34:37.617-0800: [CMS-concurrent-reset: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:34:37.634-0800: [GC (Allocation Failure) 2020-10-28T17:34:37.634-0800: [ParNew: 156990K->17471K(157248K), 0.0106351 secs] 458469K->359947K(506816K), 0.0106848 secs] [Times: user=0.07 sys=0.00, real=0.01 secs]
2020-10-28T17:34:37.645-0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 342475K(349568K)] 360667K(506816K), 0.0002456 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:34:37.645-0800: [CMS-concurrent-mark-start]
2020-10-28T17:34:37.647-0800: [CMS-concurrent-mark: 0.001/0.001 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
2020-10-28T17:34:37.647-0800: [CMS-concurrent-preclean-start]
2020-10-28T17:34:37.648-0800: [CMS-concurrent-preclean: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:34:37.648-0800: [CMS-concurrent-abortable-preclean-start]
2020-10-28T17:34:37.648-0800: [CMS-concurrent-abortable-preclean: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:34:37.648-0800: [GC (CMS Final Remark) [YG occupancy: 36313 K (157248 K)]2020-10-28T17:34:37.648-0800: [Rescan (parallel) , 0.0010329 secs]2020-10-28T17:34:37.649-0800: [weak refs processing, 0.0000131 secs]2020-10-28T17:34:37.649-0800: [class unloading, 0.0001989 secs]2020-10-28T17:34:37.649-0800: [scrub symbol table, 0.0003559 secs]2020-10-28T17:34:37.650-0800: [scrub string table, 0.0001291 secs][1 CMS-remark: 342475K(349568K)] 378789K(506816K), 0.0017936 secs] [Times: user=0.01 sys=0.01, real=0.01 secs]
2020-10-28T17:34:37.650-0800: [CMS-concurrent-sweep-start]
2020-10-28T17:34:37.651-0800: [CMS-concurrent-sweep: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:34:37.651-0800: [CMS-concurrent-reset-start]
2020-10-28T17:34:37.651-0800: [CMS-concurrent-reset: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:34:37.665-0800: [GC (Allocation Failure) 2020-10-28T17:34:37.665-0800: [ParNew: 157247K->157247K(157248K), 0.0000210 secs]2020-10-28T17:34:37.666-0800: [CMS: 302863K->325511K(349568K), 0.0475952 secs] 460111K->325511K(506816K), [Metaspace: 2678K->2678K(1056768K)], 0.0476769 secs] [Times: user=0.05 sys=0.00, real=0.05 secs]
2020-10-28T17:34:37.713-0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 325511K(349568K)] 326171K(506816K), 0.0002271 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:34:37.714-0800: [CMS-concurrent-mark-start]
2020-10-28T17:34:37.715-0800: [CMS-concurrent-mark: 0.002/0.002 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:34:37.715-0800: [CMS-concurrent-preclean-start]
2020-10-28T17:34:37.716-0800: [CMS-concurrent-preclean: 0.001/0.001 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
2020-10-28T17:34:37.716-0800: [CMS-concurrent-abortable-preclean-start]
2020-10-28T17:34:37.736-0800: [GC (Allocation Failure) 2020-10-28T17:34:37.736-0800: [ParNew: 139776K->139776K(157248K), 0.0000258 secs]2020-10-28T17:34:37.736-0800: [CMS2020-10-28T17:34:37.736-0800: [CMS-concurrent-abortable-preclean: 0.001/0.019 secs] [Times: user=0.02 sys=0.00, real=0.02 secs]
 (concurrent mode failure): 325511K->326846K(349568K), 0.0503374 secs] 465287K->326846K(506816K), [Metaspace: 2678K->2678K(1056768K)], 0.0504517 secs] [Times: user=0.05 sys=0.00, real=0.05 secs]
2020-10-28T17:34:37.811-0800: [GC (Allocation Failure) 2020-10-28T17:34:37.811-0800: [ParNew: 139776K->139776K(157248K), 0.0000221 secs]2020-10-28T17:34:37.811-0800: [CMS: 326846K->333566K(349568K), 0.0517201 secs] 466622K->333566K(506816K), [Metaspace: 2678K->2678K(1056768K)], 0.0517984 secs] [Times: user=0.05 sys=0.00, real=0.05 secs]
2020-10-28T17:34:37.862-0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 333566K(349568K)] 333633K(506816K), 0.0002379 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:34:37.863-0800: [CMS-concurrent-mark-start]
2020-10-28T17:34:37.864-0800: [CMS-concurrent-mark: 0.002/0.002 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
2020-10-28T17:34:37.864-0800: [CMS-concurrent-preclean-start]
2020-10-28T17:34:37.865-0800: [CMS-concurrent-preclean: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:34:37.865-0800: [CMS-concurrent-abortable-preclean-start]
2020-10-28T17:34:37.865-0800: [CMS-concurrent-abortable-preclean: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:34:37.866-0800: [GC (CMS Final Remark) [YG occupancy: 16032 K (157248 K)]2020-10-28T17:34:37.866-0800: [Rescan (parallel) , 0.0015116 secs]2020-10-28T17:34:37.867-0800: [weak refs processing, 0.0000157 secs]2020-10-28T17:34:37.867-0800: [class unloading, 0.0002300 secs]2020-10-28T17:34:37.867-0800: [scrub symbol table, 0.0003221 secs]2020-10-28T17:34:37.868-0800: [scrub string table, 0.0001433 secs][1 CMS-remark: 333566K(349568K)] 349599K(506816K), 0.0022784 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:34:37.868-0800: [CMS-concurrent-sweep-start]
2020-10-28T17:34:37.869-0800: [CMS-concurrent-sweep: 0.001/0.001 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
2020-10-28T17:34:37.869-0800: [CMS-concurrent-reset-start]
2020-10-28T17:34:37.869-0800: [CMS-concurrent-reset: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:34:37.888-0800: [GC (Allocation Failure) 2020-10-28T17:34:37.888-0800: [ParNew: 139776K->139776K(157248K), 0.0000197 secs]2020-10-28T17:34:37.888-0800: [CMS: 333234K->330188K(349568K), 0.0478397 secs] 473010K->330188K(506816K), [Metaspace: 2678K->2678K(1056768K)], 0.0479126 secs] [Times: user=0.04 sys=0.00, real=0.05 secs]
2020-10-28T17:34:37.936-0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 330188K(349568K)] 330332K(506816K), 0.0002126 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:34:37.936-0800: [CMS-concurrent-mark-start]
执行结束！ 共生成对象次数：10046
Heap
 par new generation   total 157248K, used 5716K [0x00000007a0000000, 0x00000007aaaa0000, 0x00000007aaaa0000)
  eden space 139776K,   4% used [0x00000007a0000000, 0x00000007a0595118, 0x00000007a8880000)
  from space 17472K,   0% used [0x00000007a9990000, 0x00000007a9990000, 0x00000007aaaa0000)
  to   space 17472K,   0% used [0x00000007a8880000, 0x00000007a8880000, 0x00000007a9990000)
 concurrent mark-sweep generation total 349568K, used 330188K [0x00000007aaaa0000, 0x00000007c0000000, 0x00000007c0000000)
 Metaspace       used 2684K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 294K, capacity 386K, committed 512K, reserved 1048576K
```

### 1G堆内存 CMS GC日志

```
java -XX:+UseConcMarkSweepGC -Xms1g -Xmx1g -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis
正在执行
2020-10-28T17:35:15.474-0800: [GC (Allocation Failure) 2020-10-28T17:35:15.474-0800: [ParNew: 279616K->34944K(314560K), 0.0333906 secs] 279616K->82192K(1013632K), 0.0334438 secs] [Times: user=0.06 sys=0.15, real=0.03 secs]
2020-10-28T17:35:15.552-0800: [GC (Allocation Failure) 2020-10-28T17:35:15.552-0800: [ParNew: 314560K->34943K(314560K), 0.0341288 secs] 361808K->154368K(1013632K), 0.0341690 secs] [Times: user=0.07 sys=0.15, real=0.03 secs]
2020-10-28T17:35:15.626-0800: [GC (Allocation Failure) 2020-10-28T17:35:15.626-0800: [ParNew: 314559K->34942K(314560K), 0.0461409 secs] 433984K->229119K(1013632K), 0.0461815 secs] [Times: user=0.31 sys=0.03, real=0.05 secs]
2020-10-28T17:35:15.707-0800: [GC (Allocation Failure) 2020-10-28T17:35:15.707-0800: [ParNew: 314558K->34942K(314560K), 0.0432460 secs] 508735K->297408K(1013632K), 0.0432852 secs] [Times: user=0.27 sys=0.03, real=0.05 secs]
2020-10-28T17:35:15.786-0800: [GC (Allocation Failure) 2020-10-28T17:35:15.786-0800: [ParNew: 314558K->34942K(314560K), 0.0577662 secs] 577024K->389203K(1013632K), 0.0578405 secs] [Times: user=0.34 sys=0.04, real=0.06 secs]
2020-10-28T17:35:15.844-0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 354260K(699072K)] 389714K(1013632K), 0.0003405 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:35:15.844-0800: [CMS-concurrent-mark-start]
2020-10-28T17:35:15.848-0800: [CMS-concurrent-mark: 0.004/0.004 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
2020-10-28T17:35:15.848-0800: [CMS-concurrent-preclean-start]
2020-10-28T17:35:15.849-0800: [CMS-concurrent-preclean: 0.002/0.002 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:35:15.849-0800: [CMS-concurrent-abortable-preclean-start]
2020-10-28T17:35:15.886-0800: [GC (Allocation Failure) 2020-10-28T17:35:15.886-0800: [ParNew: 314558K->34942K(314560K), 0.0515069 secs] 668819K->474648K(1013632K), 0.0515445 secs] [Times: user=0.36 sys=0.03, real=0.05 secs]
2020-10-28T17:35:15.976-0800: [GC (Allocation Failure) 2020-10-28T17:35:15.976-0800: [ParNew: 314558K->34943K(314560K), 0.0471178 secs] 754264K->551653K(1013632K), 0.0471580 secs] [Times: user=0.31 sys=0.03, real=0.05 secs]
2020-10-28T17:35:16.059-0800: [GC (Allocation Failure) 2020-10-28T17:35:16.059-0800: [ParNew: 314559K->34943K(314560K), 0.0500496 secs] 831269K->631245K(1013632K), 0.0500870 secs] [Times: user=0.33 sys=0.03, real=0.05 secs]
2020-10-28T17:35:16.146-0800: [GC (Allocation Failure) 2020-10-28T17:35:16.146-0800: [ParNew: 314559K->34942K(314560K), 0.0508076 secs] 910861K->711752K(1013632K), 0.0508448 secs] [Times: user=0.31 sys=0.03, real=0.05 secs]
2020-10-28T17:35:16.196-0800: [CMS-concurrent-abortable-preclean: 0.013/0.347 secs] [Times: user=1.47 sys=0.12, real=0.35 secs]
2020-10-28T17:35:16.197-0800: [GC (CMS Final Remark) [YG occupancy: 40814 K (314560 K)]2020-10-28T17:35:16.197-0800: [Rescan (parallel) , 0.0006218 secs]2020-10-28T17:35:16.197-0800: [weak refs processing, 0.0000178 secs]2020-10-28T17:35:16.197-0800: [class unloading, 0.0002649 secs]2020-10-28T17:35:16.198-0800: [scrub symbol table, 0.0002867 secs]2020-10-28T17:35:16.198-0800: [scrub string table, 0.0001163 secs][1 CMS-remark: 676810K(699072K)] 717625K(1013632K), 0.0013753 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:35:16.198-0800: [CMS-concurrent-sweep-start]
2020-10-28T17:35:16.199-0800: [CMS-concurrent-sweep: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.01 secs]
2020-10-28T17:35:16.200-0800: [CMS-concurrent-reset-start]
2020-10-28T17:35:16.201-0800: [CMS-concurrent-reset: 0.002/0.002 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
2020-10-28T17:35:16.234-0800: [GC (Allocation Failure) 2020-10-28T17:35:16.234-0800: [ParNew: 314558K->34942K(314560K), 0.0189829 secs] 868675K->672514K(1013632K), 0.0190210 secs] [Times: user=0.13 sys=0.01, real=0.02 secs]
2020-10-28T17:35:16.253-0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 637571K(699072K)] 678248K(1013632K), 0.0001667 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:35:16.254-0800: [CMS-concurrent-mark-start]
2020-10-28T17:35:16.256-0800: [CMS-concurrent-mark: 0.002/0.002 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T17:35:16.256-0800: [CMS-concurrent-preclean-start]
2020-10-28T17:35:16.258-0800: [CMS-concurrent-preclean: 0.002/0.002 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
2020-10-28T17:35:16.258-0800: [CMS-concurrent-abortable-preclean-start]
2020-10-28T17:35:16.297-0800: [GC (Allocation Failure) 2020-10-28T17:35:16.297-0800: [ParNew: 314558K->314558K(314560K), 0.0000197 secs]2020-10-28T17:35:16.297-0800: [CMS2020-10-28T17:35:16.297-0800: [CMS-concurrent-abortable-preclean: 0.002/0.039 secs] [Times: user=0.04 sys=0.00, real=0.04 secs]
 (concurrent mode failure): 637571K->352133K(699072K), 0.0650754 secs] 952130K->352133K(1013632K), [Metaspace: 2678K->2678K(1056768K)], 0.0651509 secs] [Times: user=0.06 sys=0.00, real=0.07 secs]
执行结束！ 共生成对象次数：11698
Heap
 par new generation   total 314560K, used 11474K [0x0000000780000000, 0x0000000795550000, 0x0000000795550000)
  eden space 279616K,   4% used [0x0000000780000000, 0x0000000780b34a50, 0x0000000791110000)
  from space 34944K,   0% used [0x0000000791110000, 0x0000000791110000, 0x0000000793330000)
  to   space 34944K,   0% used [0x0000000793330000, 0x0000000793330000, 0x0000000795550000)
 concurrent mark-sweep generation total 699072K, used 352133K [0x0000000795550000, 0x00000007c0000000, 0x00000007c0000000)
 Metaspace       used 2684K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 294K, capacity 386K, committed 512K, reserved 1048576K
```

### 2G堆内存 CMS GC日志

```
ava -XX:+UseConcMarkSweepGC -Xms2g -Xmx2g -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis
正在执行
2020-10-28T17:35:39.476-0800: [GC (Allocation Failure) 2020-10-28T17:35:39.477-0800: [ParNew: 545344K->68096K(613440K), 0.0640574 secs] 545344K->154929K(2029056K), 0.0641104 secs] [Times: user=0.10 sys=0.22, real=0.07 secs]
2020-10-28T17:35:39.627-0800: [GC (Allocation Failure) 2020-10-28T17:35:39.627-0800: [ParNew: 613440K->68096K(613440K), 0.0747146 secs] 700273K->284573K(2029056K), 0.0747553 secs] [Times: user=0.12 sys=0.25, real=0.08 secs]
2020-10-28T17:35:39.778-0800: [GC (Allocation Failure) 2020-10-28T17:35:39.778-0800: [ParNew: 613440K->68096K(613440K), 0.0916658 secs] 829917K->408641K(2029056K), 0.0917110 secs] [Times: user=0.43 sys=0.04, real=0.10 secs]
2020-10-28T17:35:39.942-0800: [GC (Allocation Failure) 2020-10-28T17:35:39.942-0800: [ParNew: 613440K->68095K(613440K), 0.0988522 secs] 953985K->539393K(2029056K), 0.0988923 secs] [Times: user=0.45 sys=0.05, real=0.10 secs]
2020-10-28T17:35:40.115-0800: [GC (Allocation Failure) 2020-10-28T17:35:40.115-0800: [ParNew: 613439K->68095K(613440K), 0.0856929 secs] 1084737K->666780K(2029056K), 0.0857357 secs] [Times: user=0.56 sys=0.04, real=0.09 secs]
执行结束！ 共生成对象次数：10173
Heap
 par new generation   total 613440K, used 90041K [0x0000000740000000, 0x0000000769990000, 0x0000000769990000)
  eden space 545344K,   4% used [0x0000000740000000, 0x000000074156e9f8, 0x0000000761490000)
  from space 68096K,  99% used [0x0000000765710000, 0x000000076998fc98, 0x0000000769990000)
  to   space 68096K,   0% used [0x0000000761490000, 0x0000000761490000, 0x0000000765710000)
 concurrent mark-sweep generation total 1415616K, used 598685K [0x0000000769990000, 0x00000007c0000000, 0x00000007c0000000)
 Metaspace       used 2684K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 294K, capacity 386K, committed 512K, reserved 1048576K
```

### 4G堆内存 CMS GC日志

```
Learning_notes (main) ✗ java -XX:+UseConcMarkSweepGC -Xms4g -Xmx4g -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis
正在执行
2020-10-28T17:36:09.754-0800: [GC (Allocation Failure) 2020-10-28T17:36:09.754-0800: [ParNew: 545344K->68096K(613440K), 0.0659369 secs] 545344K->155326K(4126208K), 0.0659897 secs] [Times: user=0.13 sys=0.29, real=0.07 secs]
2020-10-28T17:36:09.896-0800: [GC (Allocation Failure) 2020-10-28T17:36:09.896-0800: [ParNew: 613440K->68096K(613440K), 0.0668243 secs] 700670K->275230K(4126208K), 0.0668700 secs] [Times: user=0.13 sys=0.28, real=0.07 secs]
2020-10-28T17:36:10.044-0800: [GC (Allocation Failure) 2020-10-28T17:36:10.044-0800: [ParNew: 613440K->68096K(613440K), 0.0886651 secs] 820574K->401453K(4126208K), 0.0887019 secs] [Times: user=0.56 sys=0.05, real=0.09 secs]
2020-10-28T17:36:10.209-0800: [GC (Allocation Failure) 2020-10-28T17:36:10.209-0800: [ParNew: 613440K->68095K(613440K), 0.0785825 secs] 946797K->515421K(4126208K), 0.0786190 secs] [Times: user=0.51 sys=0.05, real=0.08 secs]
2020-10-28T17:36:10.365-0800: [GC (Allocation Failure) 2020-10-28T17:36:10.365-0800: [ParNew: 613000K->68095K(613440K), 0.0803197 secs] 1060326K->643368K(4126208K), 0.0803556 secs] [Times: user=0.52 sys=0.05, real=0.08 secs]
执行结束！ 共生成对象次数：10215
Heap
 par new generation   total 613440K, used 90448K [0x00000006c0000000, 0x00000006e9990000, 0x00000006e9990000)
  eden space 545344K,   4% used [0x00000006c0000000, 0x00000006c15d4570, 0x00000006e1490000)
  from space 68096K,  99% used [0x00000006e5710000, 0x00000006e998fe18, 0x00000006e9990000)
  to   space 68096K,   0% used [0x00000006e1490000, 0x00000006e1490000, 0x00000006e5710000)
 concurrent mark-sweep generation total 3512768K, used 575273K [0x00000006e9990000, 0x00000007c0000000, 0x00000007c0000000)
 Metaspace       used 2684K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 294K, capacity 386K, committed 512K, reserved 1048576K
```

### 8G堆内存 CMS GC日志

```
 Learning_notes (main) ✗ java -XX:+UseConcMarkSweepGC -Xms8g -Xmx8g -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis
正在执行
2020-10-28T17:36:50.125-0800: [GC (Allocation Failure) 2020-10-28T17:36:50.125-0800: [ParNew: 545344K->68095K(613440K), 0.0788707 secs] 545344K->142091K(8320512K), 0.0789218 secs] [Times: user=0.19 sys=0.26, real=0.08 secs]
2020-10-28T17:36:50.286-0800: [GC (Allocation Failure) 2020-10-28T17:36:50.286-0800: [ParNew: 613439K->68092K(613440K), 0.0959894 secs] 687435K->282958K(8320512K), 0.0960330 secs] [Times: user=0.24 sys=0.34, real=0.10 secs]
2020-10-28T17:36:50.458-0800: [GC (Allocation Failure) 2020-10-28T17:36:50.458-0800: [ParNew: 613436K->68095K(613440K), 0.0957797 secs] 828302K->401465K(8320512K), 0.0958477 secs] [Times: user=0.63 sys=0.04, real=0.10 secs]
2020-10-28T17:36:50.633-0800: [GC (Allocation Failure) 2020-10-28T17:36:50.633-0800: [ParNew: 613439K->68095K(613440K), 0.0910626 secs] 946809K->517784K(8320512K), 0.0911013 secs] [Times: user=0.60 sys=0.05, real=0.09 secs]
2020-10-28T17:36:50.800-0800: [GC (Allocation Failure) 2020-10-28T17:36:50.800-0800: [ParNew: 613439K->68095K(613440K), 0.0978195 secs] 1063128K->649032K(8320512K), 0.0978563 secs] [Times: user=0.63 sys=0.05, real=0.09 secs]
执行结束！ 共生成对象次数：10318
Heap
 par new generation   total 613440K, used 90119K [0x00000005c0000000, 0x00000005e9990000, 0x00000005e9990000)
  eden space 545344K,   4% used [0x00000005c0000000, 0x00000005c1581f30, 0x00000005e1490000)
  from space 68096K,  99% used [0x00000005e5710000, 0x00000005e998fd88, 0x00000005e9990000)
  to   space 68096K,   0% used [0x00000005e1490000, 0x00000005e1490000, 0x00000005e5710000)
 concurrent mark-sweep generation total 7707072K, used 580937K [0x00000005e9990000, 0x00000007c0000000, 0x00000007c0000000)
 Metaspace       used 2684K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 294K, capacity 386K, committed 512K, reserved 1048576K
```

## G1 GC日志

### 256M堆内存 G1 GC日志

```
 Learning_notes (main) ✗ java -XX:+UseG1GC -Xms256m -Xmx256m -XX:+PrintGC -XX:+PrintGCDateStamps GCLogAnalysis
正在执行
2020-10-28T17:37:22.423-0800: [GC pause (G1 Evacuation Pause) (young) 34M->13M(256M), 0.0048383 secs]
2020-10-28T17:37:22.439-0800: [GC pause (G1 Evacuation Pause) (young) 45M->23M(256M), 0.0044306 secs]
2020-10-28T17:37:22.456-0800: [GC pause (G1 Evacuation Pause) (young) 66M->37M(256M), 0.0059509 secs]
2020-10-28T17:37:22.492-0800: [GC pause (G1 Evacuation Pause) (young) 114M->61M(256M), 0.0076887 secs]
2020-10-28T17:37:22.538-0800: [GC pause (G1 Evacuation Pause) (young) 176M->99M(256M), 0.0132035 secs]
2020-10-28T17:37:22.556-0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark) 130M->111M(256M), 0.0034839 secs]
2020-10-28T17:37:22.559-0800: [GC concurrent-root-region-scan-start]
2020-10-28T17:37:22.560-0800: [GC concurrent-root-region-scan-end, 0.0001663 secs]
2020-10-28T17:37:22.560-0800: [GC concurrent-mark-start]
2020-10-28T17:37:22.561-0800: [GC concurrent-mark-end, 0.0011127 secs]
2020-10-28T17:37:22.561-0800: [GC remark, 0.0009107 secs]
2020-10-28T17:37:22.562-0800: [GC cleanup 121M->121M(256M), 0.0004876 secs]
2020-10-28T17:37:22.581-0800: [GC pause (G1 Evacuation Pause) (young) 198M->136M(256M), 0.0039608 secs]
2020-10-28T17:37:22.586-0800: [GC pause (G1 Evacuation Pause) (mixed) 141M->136M(256M), 0.0016775 secs]
2020-10-28T17:37:22.588-0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark) 139M->137M(256M), 0.0014148 secs]
2020-10-28T17:37:22.589-0800: [GC concurrent-root-region-scan-start]
2020-10-28T17:37:22.589-0800: [GC concurrent-root-region-scan-end, 0.0001143 secs]
2020-10-28T17:37:22.589-0800: [GC concurrent-mark-start]
2020-10-28T17:37:22.590-0800: [GC concurrent-mark-end, 0.0006579 secs]
2020-10-28T17:37:22.590-0800: [GC remark, 0.0010572 secs]
2020-10-28T17:37:22.591-0800: [GC cleanup 144M->144M(256M), 0.0004859 secs]
2020-10-28T17:37:22.603-0800: [GC pause (G1 Evacuation Pause) (young)-- 213M->189M(256M), 0.0021860 secs]
2020-10-28T17:37:22.606-0800: [GC pause (G1 Evacuation Pause) (mixed) 194M->184M(256M), 0.0024652 secs]
2020-10-28T17:37:22.609-0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark) 187M->184M(256M), 0.0011838 secs]
2020-10-28T17:37:22.610-0800: [GC concurrent-root-region-scan-start]
2020-10-28T17:37:22.610-0800: [GC concurrent-root-region-scan-end, 0.0001059 secs]
2020-10-28T17:37:22.610-0800: [GC concurrent-mark-start]
2020-10-28T17:37:22.611-0800: [GC concurrent-mark-end, 0.0007120 secs]
2020-10-28T17:37:22.611-0800: [GC remark, 0.0009987 secs]
2020-10-28T17:37:22.612-0800: [GC cleanup 190M->190M(256M), 0.0004167 secs]
2020-10-28T17:37:22.614-0800: [GC pause (G1 Evacuation Pause) (young) 202M->190M(256M), 0.0013174 secs]
2020-10-28T17:37:22.618-0800: [GC pause (G1 Evacuation Pause) (mixed) 202M->172M(256M), 0.0017245 secs]
2020-10-28T17:37:22.621-0800: [GC pause (G1 Evacuation Pause) (mixed) 183M->166M(256M), 0.0028578 secs]
2020-10-28T17:37:22.625-0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark) 170M->167M(256M), 0.0010604 secs]
2020-10-28T17:37:22.626-0800: [GC concurrent-root-region-scan-start]
2020-10-28T17:37:22.627-0800: [GC concurrent-root-region-scan-end, 0.0000770 secs]
2020-10-28T17:37:22.627-0800: [GC concurrent-mark-start]
2020-10-28T17:37:22.627-0800: [GC concurrent-mark-end, 0.0006284 secs]
2020-10-28T17:37:22.627-0800: [GC remark, 0.0010385 secs]
2020-10-28T17:37:22.629-0800: [GC cleanup 172M->172M(256M), 0.0005355 secs]
2020-10-28T17:37:22.633-0800: [GC pause (G1 Evacuation Pause) (young) 199M->178M(256M), 0.0027134 secs]
2020-10-28T17:37:22.638-0800: [GC pause (G1 Evacuation Pause) (mixed) 190M->176M(256M), 0.0030229 secs]
2020-10-28T17:37:22.642-0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark) 177M->177M(256M), 0.0015703 secs]
2020-10-28T17:37:22.643-0800: [GC concurrent-root-region-scan-start]
2020-10-28T17:37:22.643-0800: [GC concurrent-root-region-scan-end, 0.0000494 secs]
2020-10-28T17:37:22.643-0800: [GC concurrent-mark-start]
2020-10-28T17:37:22.644-0800: [GC concurrent-mark-end, 0.0006969 secs]
2020-10-28T17:37:22.644-0800: [GC remark, 0.0010297 secs]
2020-10-28T17:37:22.645-0800: [GC cleanup 182M->182M(256M), 0.0005346 secs]
2020-10-28T17:37:22.649-0800: [GC pause (G1 Evacuation Pause) (young) 200M->187M(256M), 0.0013357 secs]
2020-10-28T17:37:22.652-0800: [GC pause (G1 Evacuation Pause) (mixed) 201M->189M(256M), 0.0029987 secs]
2020-10-28T17:37:22.655-0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark) 190M->187M(256M), 0.0015820 secs]
2020-10-28T17:37:22.657-0800: [GC concurrent-root-region-scan-start]
2020-10-28T17:37:22.657-0800: [GC concurrent-root-region-scan-end, 0.0001104 secs]
2020-10-28T17:37:22.657-0800: [GC concurrent-mark-start]
2020-10-28T17:37:22.658-0800: [GC concurrent-mark-end, 0.0007464 secs]
2020-10-28T17:37:22.658-0800: [GC remark, 0.0009473 secs]
2020-10-28T17:37:22.659-0800: [GC cleanup 191M->191M(256M), 0.0004492 secs]
2020-10-28T17:37:22.661-0800: [GC pause (G1 Evacuation Pause) (young) 201M->191M(256M), 0.0016871 secs]
2020-10-28T17:37:22.665-0800: [GC pause (G1 Evacuation Pause) (mixed) 204M->192M(256M), 0.0019996 secs]
2020-10-28T17:37:22.668-0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark) 193M->192M(256M), 0.0009262 secs]
2020-10-28T17:37:22.669-0800: [GC concurrent-root-region-scan-start]
2020-10-28T17:37:22.669-0800: [GC concurrent-root-region-scan-end, 0.0001438 secs]
2020-10-28T17:37:22.669-0800: [GC concurrent-mark-start]
2020-10-28T17:37:22.669-0800: [GC concurrent-mark-end, 0.0007325 secs]
2020-10-28T17:37:22.670-0800: [GC remark, 0.0009095 secs]
2020-10-28T17:37:22.671-0800: [GC cleanup 198M->198M(256M), 0.0004869 secs]
2020-10-28T17:37:22.672-0800: [GC pause (G1 Evacuation Pause) (young) 205M->196M(256M), 0.0012129 secs]
2020-10-28T17:37:22.676-0800: [GC pause (G1 Evacuation Pause) (mixed) 208M->196M(256M), 0.0016569 secs]
2020-10-28T17:37:22.677-0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark) 197M->196M(256M), 0.0009397 secs]
2020-10-28T17:37:22.678-0800: [GC concurrent-root-region-scan-start]
2020-10-28T17:37:22.679-0800: [GC concurrent-root-region-scan-end, 0.0001129 secs]
2020-10-28T17:37:22.679-0800: [GC concurrent-mark-start]
2020-10-28T17:37:22.679-0800: [GC concurrent-mark-end, 0.0007756 secs]
2020-10-28T17:37:22.679-0800: [GC remark, 0.0009564 secs]
2020-10-28T17:37:22.680-0800: [GC cleanup 203M->203M(256M), 0.0004095 secs]
2020-10-28T17:37:22.682-0800: [GC pause (G1 Evacuation Pause) (young) 209M->200M(256M), 0.0012112 secs]
2020-10-28T17:37:22.685-0800: [GC pause (G1 Evacuation Pause) (mixed)-- 211M->212M(256M), 0.0024716 secs]
2020-10-28T17:37:22.688-0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark) 213M->212M(256M), 0.0008451 secs]
2020-10-28T17:37:22.689-0800: [GC concurrent-root-region-scan-start]
2020-10-28T17:37:22.689-0800: [GC concurrent-root-region-scan-end, 0.0000989 secs]
2020-10-28T17:37:22.689-0800: [GC concurrent-mark-start]
2020-10-28T17:37:22.690-0800: [GC concurrent-mark-end, 0.0007401 secs]
2020-10-28T17:37:22.690-0800: [GC remark, 0.0009561 secs]
2020-10-28T17:37:22.691-0800: [GC cleanup 219M->219M(256M), 0.0004786 secs]
2020-10-28T17:37:22.692-0800: [GC pause (G1 Evacuation Pause) (young)-- 221M->218M(256M), 0.0012382 secs]
2020-10-28T17:37:22.694-0800: [GC pause (G1 Evacuation Pause) (mixed)-- 221M->221M(256M), 0.0009174 secs]
2020-10-28T17:37:22.695-0800: [Full GC (Allocation Failure)  221M->181M(256M), 0.0206145 secs]
2020-10-28T17:37:22.716-0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark) 185M->183M(256M), 0.0007964 secs]
2020-10-28T17:37:22.717-0800: [GC concurrent-root-region-scan-start]
2020-10-28T17:37:22.717-0800: [GC concurrent-root-region-scan-end, 0.0001004 secs]
2020-10-28T17:37:22.717-0800: [GC concurrent-mark-start]
2020-10-28T17:37:22.718-0800: [GC concurrent-mark-end, 0.0006509 secs]
2020-10-28T17:37:22.718-0800: [GC remark, 0.0009775 secs]
2020-10-28T17:37:22.719-0800: [GC cleanup 186M->186M(256M), 0.0004909 secs]
2020-10-28T17:37:22.721-0800: [GC pause (G1 Evacuation Pause) (young) 196M->185M(256M), 0.0019422 secs]
2020-10-28T17:37:22.723-0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark) 185M->184M(256M), 0.0008542 secs]
2020-10-28T17:37:22.724-0800: [GC concurrent-root-region-scan-start]
2020-10-28T17:37:22.724-0800: [GC concurrent-root-region-scan-end, 0.0000457 secs]
2020-10-28T17:37:22.724-0800: [GC concurrent-mark-start]
2020-10-28T17:37:22.725-0800: [GC concurrent-mark-end, 0.0007427 secs]
2020-10-28T17:37:22.725-0800: [GC remark, 0.0010470 secs]
2020-10-28T17:37:22.726-0800: [GC cleanup 190M->190M(256M), 0.0004069 secs]
2020-10-28T17:37:22.729-0800: [GC pause (G1 Evacuation Pause) (young) 199M->189M(256M), 0.0016952 secs]
2020-10-28T17:37:22.732-0800: [GC pause (G1 Evacuation Pause) (mixed)-- 200M->197M(256M), 0.0021431 secs]
2020-10-28T17:37:22.735-0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark)-- 204M->202M(256M), 0.0011266 secs]
2020-10-28T17:37:22.737-0800: [GC concurrent-root-region-scan-start]
2020-10-28T17:37:22.737-0800: [GC concurrent-root-region-scan-end, 0.0001001 secs]
2020-10-28T17:37:22.737-0800: [GC concurrent-mark-start]
2020-10-28T17:37:22.737-0800: [GC pause (G1 Evacuation Pause) (young)-- 207M->205M(256M), 0.0008216 secs]
2020-10-28T17:37:22.738-0800: [GC pause (G1 Evacuation Pause) (young) 207M->206M(256M), 0.0006422 secs]
2020-10-28T17:37:22.739-0800: [GC concurrent-mark-end, 0.0023798 secs]
2020-10-28T17:37:22.739-0800: [GC remark, 0.0009216 secs]
2020-10-28T17:37:22.740-0800: [GC cleanup 207M->207M(256M), 0.0003809 secs]
2020-10-28T17:37:22.741-0800: [GC pause (G1 Humongous Allocation) (young)-- 207M->207M(256M), 0.0005999 secs]
2020-10-28T17:37:22.741-0800: [Full GC (Allocation Failure)  207M->189M(256M), 0.0174892 secs]
2020-10-28T17:37:22.759-0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark) 190M->190M(256M), 0.0007944 secs]
2020-10-28T17:37:22.760-0800: [GC concurrent-root-region-scan-start]
2020-10-28T17:37:22.760-0800: [GC concurrent-root-region-scan-end, 0.0000139 secs]
2020-10-28T17:37:22.760-0800: [GC concurrent-mark-start]
2020-10-28T17:37:22.761-0800: [GC concurrent-mark-end, 0.0011157 secs]
2020-10-28T17:37:22.761-0800: [GC remark, 0.0012788 secs]
2020-10-28T17:37:22.762-0800: [GC cleanup 197M->197M(256M), 0.0005430 secs]
2020-10-28T17:37:22.764-0800: [GC pause (G1 Evacuation Pause) (young)-- 202M->197M(256M), 0.0015609 secs]
2020-10-28T17:37:22.766-0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark) 197M->197M(256M), 0.0008603 secs]
2020-10-28T17:37:22.767-0800: [GC concurrent-root-region-scan-start]
2020-10-28T17:37:22.767-0800: [GC concurrent-root-region-scan-end, 0.0000166 secs]
2020-10-28T17:37:22.767-0800: [GC concurrent-mark-start]
2020-10-28T17:37:22.768-0800: [GC concurrent-mark-end, 0.0008705 secs]
2020-10-28T17:37:22.768-0800: [GC remark, 0.0011550 secs]
2020-10-28T17:37:22.769-0800: [GC cleanup 201M->201M(256M), 0.0005837 secs]
2020-10-28T17:37:22.770-0800: [GC pause (G1 Evacuation Pause) (young)-- 203M->203M(256M), 0.0010054 secs]
2020-10-28T17:37:22.772-0800: [GC pause (G1 Evacuation Pause) (mixed)-- 204M->204M(256M), 0.0007069 secs]
2020-10-28T17:37:22.772-0800: [Full GC (Allocation Failure)  204M->193M(256M), 0.0130119 secs]
2020-10-28T17:37:22.786-0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark) 194M->193M(256M), 0.0009890 secs]
2020-10-28T17:37:22.787-0800: [GC concurrent-root-region-scan-start]
2020-10-28T17:37:22.787-0800: [GC concurrent-root-region-scan-end, 0.0000857 secs]
2020-10-28T17:37:22.787-0800: [GC concurrent-mark-start]
2020-10-28T17:37:22.788-0800: [GC concurrent-mark-end, 0.0007937 secs]
2020-10-28T17:37:22.788-0800: [GC remark, 0.0011833 secs]
2020-10-28T17:37:22.789-0800: [GC cleanup 199M->199M(256M), 0.0006437 secs]
2020-10-28T17:37:22.790-0800: [GC pause (G1 Evacuation Pause) (young)-- 202M->199M(256M), 0.0011010 secs]
2020-10-28T17:37:22.792-0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark) 200M->199M(256M), 0.0006747 secs]
2020-10-28T17:37:22.793-0800: [GC concurrent-root-region-scan-start]
2020-10-28T17:37:22.793-0800: [GC concurrent-root-region-scan-end, 0.0000809 secs]
2020-10-28T17:37:22.793-0800: [GC concurrent-mark-start]
2020-10-28T17:37:22.793-0800: [GC pause (G1 Humongous Allocation) (young)-- 202M->202M(256M), 0.0011871 secs]
2020-10-28T17:37:22.794-0800: [Full GC (Allocation Failure)  202M->194M(256M), 0.0067809 secs]
2020-10-28T17:37:22.801-0800: [GC concurrent-mark-abort]
2020-10-28T17:37:22.801-0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark) 195M->194M(256M), 0.0009116 secs]
2020-10-28T17:37:22.802-0800: [GC concurrent-root-region-scan-start]
2020-10-28T17:37:22.802-0800: [GC concurrent-root-region-scan-end, 0.0000547 secs]
2020-10-28T17:37:22.802-0800: [GC concurrent-mark-start]
2020-10-28T17:37:22.803-0800: [GC concurrent-mark-end, 0.0009542 secs]
2020-10-28T17:37:22.804-0800: [GC remark, 0.0011597 secs]
2020-10-28T17:37:22.805-0800: [GC cleanup 199M->199M(256M), 0.0005163 secs]
2020-10-28T17:37:22.806-0800: [GC pause (G1 Evacuation Pause) (young)-- 200M->198M(256M), 0.0010232 secs]
2020-10-28T17:37:22.807-0800: [GC pause (G1 Evacuation Pause) (mixed)-- 201M->200M(256M), 0.0008563 secs]
2020-10-28T17:37:22.808-0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark)-- 201M->201M(256M), 0.0010041 secs]
2020-10-28T17:37:22.809-0800: [GC concurrent-root-region-scan-start]
2020-10-28T17:37:22.809-0800: [GC concurrent-root-region-scan-end, 0.0000180 secs]
2020-10-28T17:37:22.809-0800: [GC concurrent-mark-start]
2020-10-28T17:37:22.809-0800: [GC pause (G1 Humongous Allocation) (young) 201M->201M(256M), 0.0014053 secs]
2020-10-28T17:37:22.811-0800: [Full GC (Allocation Failure)  201M->193M(256M), 0.0168918 secs]
2020-10-28T17:37:22.828-0800: [GC concurrent-mark-abort]
2020-10-28T17:37:22.828-0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark) 194M->194M(256M), 0.0017985 secs]
2020-10-28T17:37:22.830-0800: [GC concurrent-root-region-scan-start]
2020-10-28T17:37:22.830-0800: [GC concurrent-root-region-scan-end, 0.0000888 secs]
2020-10-28T17:37:22.830-0800: [GC concurrent-mark-start]
2020-10-28T17:37:22.831-0800: [GC concurrent-mark-end, 0.0008460 secs]
2020-10-28T17:37:22.831-0800: [GC remark, 0.0013321 secs]
2020-10-28T17:37:22.833-0800: [GC cleanup 199M->199M(256M), 0.0006151 secs]
2020-10-28T17:37:22.834-0800: [GC pause (G1 Evacuation Pause) (young)-- 202M->201M(256M), 0.0011052 secs]
2020-10-28T17:37:22.835-0800: [GC pause (G1 Evacuation Pause) (young)-- 202M->202M(256M), 0.0009265 secs]
2020-10-28T17:37:22.836-0800: [Full GC (Allocation Failure)  202M->195M(256M), 0.0093894 secs]
2020-10-28T17:37:22.847-0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark) 200M->197M(256M), 0.0009975 secs]
2020-10-28T17:37:22.848-0800: [GC concurrent-root-region-scan-start]
2020-10-28T17:37:22.848-0800: [GC concurrent-root-region-scan-end, 0.0001283 secs]
2020-10-28T17:37:22.848-0800: [GC concurrent-mark-start]
2020-10-28T17:37:22.849-0800: [GC pause (G1 Evacuation Pause) (young)-- 201M->201M(256M), 0.0024390 secs]
2020-10-28T17:37:22.852-0800: [Full GC (Allocation Failure)  201M->197M(256M), 0.0138701 secs]
2020-10-28T17:37:22.866-0800: [GC concurrent-mark-abort]
2020-10-28T17:37:22.868-0800: [GC pause (G1 Evacuation Pause) (young)-- 202M->202M(256M), 0.0011686 secs]
2020-10-28T17:37:22.869-0800: [Full GC (Allocation Failure)  202M->197M(256M), 0.0116869 secs]
2020-10-28T17:37:22.881-0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark) 198M->197M(256M), 0.0010773 secs]
2020-10-28T17:37:22.882-0800: [GC concurrent-root-region-scan-start]
2020-10-28T17:37:22.882-0800: [GC concurrent-root-region-scan-end, 0.0000829 secs]
2020-10-28T17:37:22.882-0800: [GC concurrent-mark-start]
2020-10-28T17:37:22.883-0800: [GC pause (G1 Evacuation Pause) (young)-- 201M->199M(256M), 0.0010314 secs]
2020-10-28T17:37:22.884-0800: [GC concurrent-mark-end, 0.0020061 secs]
2020-10-28T17:37:22.884-0800: [GC pause (G1 Humongous Allocation) (young)-- 201M->200M(256M), 0.0008527 secs]
2020-10-28T17:37:22.885-0800: [GC remark, 0.0010484 secs]
2020-10-28T17:37:22.886-0800: [GC pause (G1 Humongous Allocation) (young) 201M->201M(256M), 0.0012749 secs]
2020-10-28T17:37:22.888-0800: [Full GC (Allocation Failure)  201M->199M(256M), 0.0230398 secs]
2020-10-28T17:37:22.911-0800: [GC cleanup, 0.0000207 secs]
2020-10-28T17:37:22.911-0800: [GC concurrent-mark-abort]
2020-10-28T17:37:22.911-0800: [GC pause (G1 Evacuation Pause) (young)-- 201M->200M(256M), 0.0010847 secs]
2020-10-28T17:37:22.913-0800: [GC pause (G1 Evacuation Pause) (young) (initial-mark)-- 201M->201M(256M), 0.0011905 secs]
2020-10-28T17:37:22.914-0800: [GC concurrent-root-region-scan-start]
2020-10-28T17:37:22.914-0800: [GC concurrent-root-region-scan-end, 0.0000156 secs]
2020-10-28T17:37:22.914-0800: [GC concurrent-mark-start]
2020-10-28T17:37:22.914-0800: [Full GC (Allocation Failure)  201M->200M(256M), 0.0108333 secs]
2020-10-28T17:37:22.925-0800: [GC concurrent-mark-abort]
2020-10-28T17:37:22.925-0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark) 200M->200M(256M), 0.0009512 secs]
2020-10-28T17:37:22.926-0800: [GC concurrent-root-region-scan-start]
2020-10-28T17:37:22.926-0800: [GC concurrent-root-region-scan-end, 0.0000893 secs]
2020-10-28T17:37:22.926-0800: [GC concurrent-mark-start]
2020-10-28T17:37:22.927-0800: [GC pause (G1 Evacuation Pause) (young)-- 201M->201M(256M), 0.0015830 secs]
2020-10-28T17:37:22.929-0800: [GC concurrent-mark-end, 0.0024820 secs]
2020-10-28T17:37:22.929-0800: [GC pause (G1 Humongous Allocation) (young)-- 201M->201M(256M), 0.0015715 secs]
2020-10-28T17:37:22.930-0800: [GC remark, 0.0020999 secs]
2020-10-28T17:37:22.933-0800: [Full GC (Allocation Failure)  201M->200M(256M), 0.0029352 secs]
2020-10-28T17:37:22.936-0800: [GC cleanup, 0.0000125 secs]
2020-10-28T17:37:22.936-0800: [GC concurrent-mark-abort]
2020-10-28T17:37:22.936-0800: [GC pause (G1 Evacuation Pause) (young)-- 201M->201M(256M), 0.0016318 secs]
2020-10-28T17:37:22.938-0800: [Full GC (Allocation Failure)  201M->200M(256M), 0.0034248 secs]
2020-10-28T17:37:22.941-0800: [GC pause (G1 Evacuation Pause) (young)-- 201M->201M(256M), 0.0019533 secs]
2020-10-28T17:37:22.943-0800: [Full GC (Allocation Failure)  201M->200M(256M), 0.0121218 secs]
2020-10-28T17:37:22.956-0800: [GC pause (G1 Evacuation Pause) (young)-- 201M->201M(256M), 0.0008732 secs]
2020-10-28T17:37:22.957-0800: [Full GC (Allocation Failure)  201M->201M(256M), 0.0037057 secs]
2020-10-28T17:37:22.961-0800: [GC pause (G1 Evacuation Pause) (young)-- 202M->201M(256M), 0.0015933 secs]
2020-10-28T17:37:22.963-0800: [GC pause (G1 Evacuation Pause) (young) (initial-mark)-- 202M->202M(256M), 0.0017500 secs]
2020-10-28T17:37:22.965-0800: [GC concurrent-root-region-scan-start]
2020-10-28T17:37:22.965-0800: [GC concurrent-root-region-scan-end, 0.0000166 secs]
2020-10-28T17:37:22.965-0800: [GC concurrent-mark-start]
2020-10-28T17:37:22.965-0800: [Full GC (Allocation Failure)  202M->200M(256M), 0.0050062 secs]
2020-10-28T17:37:22.970-0800: [Full GC (Allocation Failure)  200M->200M(256M), 0.0028928 secs]
2020-10-28T17:37:22.973-0800: [GC concurrent-mark-abort]
2020-10-28T17:37:22.973-0800: [GC pause (G1 Evacuation Pause) (young) 200M->200M(256M), 0.0009329 secs]
2020-10-28T17:37:22.974-0800: [GC pause (G1 Evacuation Pause) (young) (initial-mark) 200M->200M(256M), 0.0008712 secs]
2020-10-28T17:37:22.975-0800: [GC concurrent-root-region-scan-start]
2020-10-28T17:37:22.975-0800: [GC concurrent-root-region-scan-end, 0.0000142 secs]
2020-10-28T17:37:22.975-0800: [GC concurrent-mark-start]
2020-10-28T17:37:22.975-0800: [Full GC (Allocation Failure)  200M->268K(256M), 0.0025541 secs]
2020-10-28T17:37:22.977-0800: [GC concurrent-mark-abort]
Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
	at GCLogAnalysis.generateGarbage(GCLogAnalysis.java:54)
	at GCLogAnalysis.main(GCLogAnalysis.java:31)
```

### 512M堆内存 G1 GC日志

```
 Learning_notes (main) ✗  java -XX:+UseG1GC -Xms512m -Xmx512m -XX:+PrintGC -XX:+PrintGCDateStamps GCLogAnalysis
正在执行
2020-10-28T17:37:55.464-0800: [GC pause (G1 Evacuation Pause) (young) 30M->11M(512M), 0.0048784 secs]
2020-10-28T17:37:55.475-0800: [GC pause (G1 Evacuation Pause) (young) 37M->19M(512M), 0.0032333 secs]
2020-10-28T17:37:55.503-0800: [GC pause (G1 Evacuation Pause) (young) 75M->37M(512M), 0.0070461 secs]
2020-10-28T17:37:55.700-0800: [GC pause (G1 Evacuation Pause) (young)-- 419M->302M(512M), 0.0168497 secs]
2020-10-28T17:37:55.718-0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark) 304M->304M(512M), 0.0069151 secs]
2020-10-28T17:37:55.725-0800: [GC concurrent-root-region-scan-start]
2020-10-28T17:37:55.725-0800: [GC concurrent-root-region-scan-end, 0.0001642 secs]
2020-10-28T17:37:55.725-0800: [GC concurrent-mark-start]
2020-10-28T17:37:55.727-0800: [GC concurrent-mark-end, 0.0022999 secs]
2020-10-28T17:37:55.727-0800: [GC remark, 0.0010529 secs]
2020-10-28T17:37:55.729-0800: [GC cleanup 318M->318M(512M), 0.0005533 secs]
2020-10-28T17:37:55.756-0800: [GC pause (G1 Evacuation Pause) (young) 437M->346M(512M), 0.0038102 secs]
2020-10-28T17:37:55.763-0800: [GC pause (G1 Evacuation Pause) (mixed) 363M->301M(512M), 0.0029818 secs]
2020-10-28T17:37:55.770-0800: [GC pause (G1 Evacuation Pause) (mixed) 327M->261M(512M), 0.0032122 secs]
2020-10-28T17:37:55.778-0800: [GC pause (G1 Evacuation Pause) (mixed) 285M->229M(512M), 0.0040238 secs]
2020-10-28T17:37:55.786-0800: [GC pause (G1 Evacuation Pause) (mixed) 255M->225M(512M), 0.0026559 secs]
2020-10-28T17:37:55.789-0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark) 226M->225M(512M), 0.0015351 secs]
2020-10-28T17:37:55.790-0800: [GC concurrent-root-region-scan-start]
2020-10-28T17:37:55.791-0800: [GC concurrent-root-region-scan-end, 0.0001215 secs]
2020-10-28T17:37:55.791-0800: [GC concurrent-mark-start]
2020-10-28T17:37:55.791-0800: [GC concurrent-mark-end, 0.0008353 secs]
2020-10-28T17:37:55.791-0800: [GC remark, 0.0013424 secs]
2020-10-28T17:37:55.793-0800: [GC cleanup 230M->229M(512M), 0.0007584 secs]
2020-10-28T17:37:55.794-0800: [GC concurrent-cleanup-start]
2020-10-28T17:37:55.794-0800: [GC concurrent-cleanup-end, 0.0000182 secs]
2020-10-28T17:37:55.829-0800: [GC pause (G1 Evacuation Pause) (young)-- 412M->290M(512M), 0.0064016 secs]
2020-10-28T17:37:55.837-0800: [GC pause (G1 Evacuation Pause) (mixed) 297M->269M(512M), 0.0063431 secs]
2020-10-28T17:37:55.844-0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark) 271M->269M(512M), 0.0022768 secs]
2020-10-28T17:37:55.846-0800: [GC concurrent-root-region-scan-start]
2020-10-28T17:37:55.846-0800: [GC concurrent-root-region-scan-end, 0.0001239 secs]
2020-10-28T17:37:55.846-0800: [GC concurrent-mark-start]
2020-10-28T17:37:55.847-0800: [GC concurrent-mark-end, 0.0011189 secs]
2020-10-28T17:37:55.847-0800: [GC remark, 0.0012422 secs]
2020-10-28T17:37:55.849-0800: [GC cleanup 276M->275M(512M), 0.0007496 secs]
2020-10-28T17:37:55.849-0800: [GC concurrent-cleanup-start]
2020-10-28T17:37:55.849-0800: [GC concurrent-cleanup-end, 0.0000179 secs]
2020-10-28T17:37:55.874-0800: [GC pause (G1 Evacuation Pause) (young) 412M->307M(512M), 0.0047505 secs]
2020-10-28T17:37:55.881-0800: [GC pause (G1 Evacuation Pause) (mixed) 319M->274M(512M), 0.0049773 secs]
2020-10-28T17:37:55.892-0800: [GC pause (G1 Evacuation Pause) (mixed) 298M->282M(512M), 0.0036821 secs]
2020-10-28T17:37:55.896-0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark) 282M->282M(512M), 0.0014887 secs]
2020-10-28T17:37:55.897-0800: [GC concurrent-root-region-scan-start]
2020-10-28T17:37:55.898-0800: [GC concurrent-root-region-scan-end, 0.0001739 secs]
2020-10-28T17:37:55.898-0800: [GC concurrent-mark-start]
2020-10-28T17:37:55.899-0800: [GC concurrent-mark-end, 0.0011575 secs]
2020-10-28T17:37:55.899-0800: [GC remark, 0.0012867 secs]
2020-10-28T17:37:55.900-0800: [GC cleanup 288M->288M(512M), 0.0005778 secs]
2020-10-28T17:37:55.925-0800: [GC pause (G1 Evacuation Pause) (young)-- 420M->330M(512M), 0.0052085 secs]
2020-10-28T17:37:55.932-0800: [GC pause (G1 Evacuation Pause) (mixed) 342M->311M(512M), 0.0057396 secs]
2020-10-28T17:37:55.939-0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark) 316M->312M(512M), 0.0013254 secs]
2020-10-28T17:37:55.941-0800: [GC concurrent-root-region-scan-start]
2020-10-28T17:37:55.941-0800: [GC concurrent-root-region-scan-end, 0.0001429 secs]
2020-10-28T17:37:55.941-0800: [GC concurrent-mark-start]
2020-10-28T17:37:55.942-0800: [GC concurrent-mark-end, 0.0012232 secs]
2020-10-28T17:37:55.942-0800: [GC remark, 0.0016931 secs]
2020-10-28T17:37:55.944-0800: [GC cleanup 322M->322M(512M), 0.0006263 secs]
2020-10-28T17:37:55.961-0800: [GC pause (G1 Evacuation Pause) (young) 422M->344M(512M), 0.0055068 secs]
2020-10-28T17:37:55.970-0800: [GC pause (G1 Evacuation Pause) (mixed) 360M->314M(512M), 0.0053395 secs]
2020-10-28T17:37:55.980-0800: [GC pause (G1 Evacuation Pause) (mixed) 341M->313M(512M), 0.0030762 secs]
2020-10-28T17:37:55.984-0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark) 314M->313M(512M), 0.0021556 secs]
2020-10-28T17:37:55.986-0800: [GC concurrent-root-region-scan-start]
2020-10-28T17:37:55.986-0800: [GC concurrent-root-region-scan-end, 0.0001018 secs]
2020-10-28T17:37:55.986-0800: [GC concurrent-mark-start]
2020-10-28T17:37:55.987-0800: [GC concurrent-mark-end, 0.0010955 secs]
2020-10-28T17:37:55.987-0800: [GC remark, 0.0013116 secs]
2020-10-28T17:37:55.989-0800: [GC cleanup 320M->319M(512M), 0.0006829 secs]
2020-10-28T17:37:55.989-0800: [GC concurrent-cleanup-start]
2020-10-28T17:37:55.989-0800: [GC concurrent-cleanup-end, 0.0000176 secs]
2020-10-28T17:37:56.006-0800: [GC pause (G1 Evacuation Pause) (young) 420M->348M(512M), 0.0049978 secs]
2020-10-28T17:37:56.015-0800: [GC pause (G1 Evacuation Pause) (mixed) 364M->327M(512M), 0.0056048 secs]
2020-10-28T17:37:56.020-0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark) 328M->328M(512M), 0.0013462 secs]
2020-10-28T17:37:56.022-0800: [GC concurrent-root-region-scan-start]
2020-10-28T17:37:56.022-0800: [GC concurrent-root-region-scan-end, 0.0001007 secs]
2020-10-28T17:37:56.022-0800: [GC concurrent-mark-start]
2020-10-28T17:37:56.023-0800: [GC concurrent-mark-end, 0.0010813 secs]
2020-10-28T17:37:56.023-0800: [GC remark, 0.0014307 secs]
2020-10-28T17:37:56.025-0800: [GC cleanup 334M->334M(512M), 0.0007524 secs]
2020-10-28T17:37:56.040-0800: [GC pause (G1 Evacuation Pause) (young) 415M->353M(512M), 0.0044198 secs]
2020-10-28T17:37:56.048-0800: [GC pause (G1 Evacuation Pause) (mixed) 371M->330M(512M), 0.0050846 secs]
2020-10-28T17:37:56.058-0800: [GC pause (G1 Evacuation Pause) (mixed) 361M->339M(512M), 0.0038992 secs]
2020-10-28T17:37:56.062-0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark) 341M->340M(512M), 0.0020400 secs]
2020-10-28T17:37:56.064-0800: [GC concurrent-root-region-scan-start]
2020-10-28T17:37:56.064-0800: [GC concurrent-root-region-scan-end, 0.0001572 secs]
2020-10-28T17:37:56.065-0800: [GC concurrent-mark-start]
2020-10-28T17:37:56.066-0800: [GC concurrent-mark-end, 0.0015517 secs]
2020-10-28T17:37:56.066-0800: [GC remark, 0.0015832 secs]
2020-10-28T17:37:56.068-0800: [GC cleanup 349M->349M(512M), 0.0007675 secs]
2020-10-28T17:37:56.069-0800: [GC concurrent-cleanup-start]
2020-10-28T17:37:56.069-0800: [GC concurrent-cleanup-end, 0.0000178 secs]
2020-10-28T17:37:56.079-0800: [GC pause (G1 Evacuation Pause) (young) 402M->359M(512M), 0.0037371 secs]
2020-10-28T17:37:56.086-0800: [GC pause (G1 Evacuation Pause) (mixed) 380M->343M(512M), 0.0063242 secs]
2020-10-28T17:37:56.099-0800: [GC pause (G1 Evacuation Pause) (mixed) 374M->353M(512M), 0.0030269 secs]
2020-10-28T17:37:56.103-0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark) 356M->354M(512M), 0.0014587 secs]
2020-10-28T17:37:56.104-0800: [GC concurrent-root-region-scan-start]
2020-10-28T17:37:56.105-0800: [GC concurrent-root-region-scan-end, 0.0001460 secs]
2020-10-28T17:37:56.105-0800: [GC concurrent-mark-start]
2020-10-28T17:37:56.106-0800: [GC concurrent-mark-end, 0.0012640 secs]
2020-10-28T17:37:56.106-0800: [GC remark, 0.0015092 secs]
2020-10-28T17:37:56.108-0800: [GC cleanup 364M->364M(512M), 0.0006737 secs]
2020-10-28T17:37:56.115-0800: [GC pause (G1 Evacuation Pause) (young) 398M->360M(512M), 0.0034289 secs]
2020-10-28T17:37:56.123-0800: [GC pause (G1 Evacuation Pause) (mixed) 381M->344M(512M), 0.0070435 secs]
2020-10-28T17:37:56.136-0800: [GC pause (G1 Evacuation Pause) (mixed) 369M->348M(512M), 0.0023993 secs]
2020-10-28T17:37:56.139-0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark) 350M->347M(512M), 0.0013035 secs]
2020-10-28T17:37:56.140-0800: [GC concurrent-root-region-scan-start]
2020-10-28T17:37:56.140-0800: [GC concurrent-root-region-scan-end, 0.0001535 secs]
2020-10-28T17:37:56.140-0800: [GC concurrent-mark-start]
2020-10-28T17:37:56.142-0800: [GC concurrent-mark-end, 0.0012380 secs]
2020-10-28T17:37:56.142-0800: [GC remark, 0.0014225 secs]
2020-10-28T17:37:56.143-0800: [GC cleanup 354M->354M(512M), 0.0006083 secs]
2020-10-28T17:37:56.154-0800: [GC pause (G1 Evacuation Pause) (young) 409M->364M(512M), 0.0030872 secs]
2020-10-28T17:37:56.162-0800: [GC pause (G1 Evacuation Pause) (mixed) 389M->356M(512M), 0.0054986 secs]
2020-10-28T17:37:56.168-0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark) 359M->356M(512M), 0.0016577 secs]
2020-10-28T17:37:56.170-0800: [GC concurrent-root-region-scan-start]
2020-10-28T17:37:56.170-0800: [GC concurrent-root-region-scan-end, 0.0001229 secs]
2020-10-28T17:37:56.170-0800: [GC concurrent-mark-start]
2020-10-28T17:37:56.171-0800: [GC concurrent-mark-end, 0.0012061 secs]
2020-10-28T17:37:56.171-0800: [GC remark, 0.0015987 secs]
2020-10-28T17:37:56.173-0800: [GC cleanup 365M->365M(512M), 0.0006106 secs]
2020-10-28T17:37:56.181-0800: [GC pause (G1 Evacuation Pause) (young) 402M->366M(512M), 0.0053228 secs]
2020-10-28T17:37:56.191-0800: [GC pause (G1 Evacuation Pause) (mixed) 393M->357M(512M), 0.0059182 secs]
2020-10-28T17:37:56.197-0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark) 360M->357M(512M), 0.0012683 secs]
2020-10-28T17:37:56.198-0800: [GC concurrent-root-region-scan-start]
2020-10-28T17:37:56.199-0800: [GC concurrent-root-region-scan-end, 0.0001541 secs]
2020-10-28T17:37:56.199-0800: [GC concurrent-mark-start]
2020-10-28T17:37:56.200-0800: [GC concurrent-mark-end, 0.0013706 secs]
2020-10-28T17:37:56.200-0800: [GC remark, 0.0014036 secs]
2020-10-28T17:37:56.202-0800: [GC cleanup 368M->368M(512M), 0.0006670 secs]
2020-10-28T17:37:56.209-0800: [GC pause (G1 Evacuation Pause) (young) 401M->371M(512M), 0.0033649 secs]
2020-10-28T17:37:56.217-0800: [GC pause (G1 Evacuation Pause) (mixed) 396M->357M(512M), 0.0047377 secs]
2020-10-28T17:37:56.223-0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark) 359M->356M(512M), 0.0012739 secs]
2020-10-28T17:37:56.224-0800: [GC concurrent-root-region-scan-start]
2020-10-28T17:37:56.224-0800: [GC concurrent-root-region-scan-end, 0.0000104 secs]
2020-10-28T17:37:56.224-0800: [GC concurrent-mark-start]
2020-10-28T17:37:56.225-0800: [GC concurrent-mark-end, 0.0012439 secs]
2020-10-28T17:37:56.225-0800: [GC remark, 0.0024532 secs]
2020-10-28T17:37:56.228-0800: [GC cleanup 365M->365M(512M), 0.0007465 secs]
2020-10-28T17:37:56.236-0800: [GC pause (G1 Evacuation Pause) (young) 406M->373M(512M), 0.0029663 secs]
2020-10-28T17:37:56.244-0800: [GC pause (G1 Evacuation Pause) (mixed) 398M->365M(512M), 0.0046893 secs]
2020-10-28T17:37:56.249-0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark) 366M->366M(512M), 0.0027454 secs]
2020-10-28T17:37:56.251-0800: [GC concurrent-root-region-scan-start]
2020-10-28T17:37:56.251-0800: [GC concurrent-root-region-scan-end, 0.0000994 secs]
2020-10-28T17:37:56.252-0800: [GC concurrent-mark-start]
2020-10-28T17:37:56.253-0800: [GC concurrent-mark-end, 0.0015343 secs]
2020-10-28T17:37:56.253-0800: [GC remark, 0.0014862 secs]
2020-10-28T17:37:56.255-0800: [GC cleanup 373M->372M(512M), 0.0006620 secs]
2020-10-28T17:37:56.255-0800: [GC concurrent-cleanup-start]
2020-10-28T17:37:56.256-0800: [GC concurrent-cleanup-end, 0.0000176 secs]
2020-10-28T17:37:56.260-0800: [GC pause (G1 Evacuation Pause) (young) 394M->370M(512M), 0.0022404 secs]
2020-10-28T17:37:56.267-0800: [GC pause (G1 Evacuation Pause) (mixed) 397M->362M(512M), 0.0051909 secs]
2020-10-28T17:37:56.273-0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark) 365M->362M(512M), 0.0023778 secs]
2020-10-28T17:37:56.275-0800: [GC concurrent-root-region-scan-start]
2020-10-28T17:37:56.275-0800: [GC concurrent-root-region-scan-end, 0.0001322 secs]
2020-10-28T17:37:56.276-0800: [GC concurrent-mark-start]
2020-10-28T17:37:56.277-0800: [GC concurrent-mark-end, 0.0011837 secs]
2020-10-28T17:37:56.277-0800: [GC remark, 0.0014813 secs]
2020-10-28T17:37:56.278-0800: [GC cleanup 372M->372M(512M), 0.0006447 secs]
2020-10-28T17:37:56.285-0800: [GC pause (G1 Evacuation Pause) (young) 398M->372M(512M), 0.0034519 secs]
2020-10-28T17:37:56.294-0800: [GC pause (G1 Evacuation Pause) (mixed) 401M->365M(512M), 0.0060335 secs]
2020-10-28T17:37:56.300-0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark) 367M->365M(512M), 0.0013181 secs]
2020-10-28T17:37:56.302-0800: [GC concurrent-root-region-scan-start]
2020-10-28T17:37:56.302-0800: [GC concurrent-root-region-scan-end, 0.0001561 secs]
2020-10-28T17:37:56.302-0800: [GC concurrent-mark-start]
2020-10-28T17:37:56.303-0800: [GC concurrent-mark-end, 0.0011560 secs]
2020-10-28T17:37:56.303-0800: [GC remark, 0.0015121 secs]
2020-10-28T17:37:56.305-0800: [GC cleanup 372M->372M(512M), 0.0006388 secs]
2020-10-28T17:37:56.309-0800: [GC pause (G1 Evacuation Pause) (young) 393M->372M(512M), 0.0023350 secs]
2020-10-28T17:37:56.316-0800: [GC pause (G1 Evacuation Pause) (mixed) 394M->360M(512M), 0.0062292 secs]
2020-10-28T17:37:56.322-0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark) 361M->361M(512M), 0.0013116 secs]
2020-10-28T17:37:56.324-0800: [GC concurrent-root-region-scan-start]
2020-10-28T17:37:56.324-0800: [GC concurrent-root-region-scan-end, 0.0000619 secs]
2020-10-28T17:37:56.324-0800: [GC concurrent-mark-start]
2020-10-28T17:37:56.325-0800: [GC concurrent-mark-end, 0.0012226 secs]
2020-10-28T17:37:56.325-0800: [GC remark, 0.0016248 secs]
2020-10-28T17:37:56.327-0800: [GC cleanup 368M->368M(512M), 0.0007924 secs]
2020-10-28T17:37:56.333-0800: [GC pause (G1 Evacuation Pause) (young) 399M->370M(512M), 0.0024200 secs]
2020-10-28T17:37:56.340-0800: [GC pause (G1 Evacuation Pause) (mixed) 394M->362M(512M), 0.0064275 secs]
2020-10-28T17:37:56.347-0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark) 364M->362M(512M), 0.0018207 secs]
2020-10-28T17:37:56.349-0800: [GC concurrent-root-region-scan-start]
2020-10-28T17:37:56.349-0800: [GC concurrent-root-region-scan-end, 0.0001371 secs]
2020-10-28T17:37:56.349-0800: [GC concurrent-mark-start]
2020-10-28T17:37:56.351-0800: [GC concurrent-mark-end, 0.0014950 secs]
2020-10-28T17:37:56.351-0800: [GC remark, 0.0016386 secs]
2020-10-28T17:37:56.353-0800: [GC cleanup 368M->368M(512M), 0.0006985 secs]
2020-10-28T17:37:56.359-0800: [GC pause (G1 Evacuation Pause) (young) 399M->373M(512M), 0.0032093 secs]
2020-10-28T17:37:56.368-0800: [GC pause (G1 Evacuation Pause) (mixed) 397M->365M(512M), 0.0041457 secs]
2020-10-28T17:37:56.373-0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark) 368M->365M(512M), 0.0028732 secs]
2020-10-28T17:37:56.376-0800: [GC concurrent-root-region-scan-start]
2020-10-28T17:37:56.376-0800: [GC concurrent-root-region-scan-end, 0.0001174 secs]
2020-10-28T17:37:56.376-0800: [GC concurrent-mark-start]
2020-10-28T17:37:56.377-0800: [GC concurrent-mark-end, 0.0012427 secs]
2020-10-28T17:37:56.377-0800: [GC remark, 0.0015740 secs]
2020-10-28T17:37:56.379-0800: [GC cleanup 374M->374M(512M), 0.0006373 secs]
2020-10-28T17:37:56.385-0800: [GC pause (G1 Evacuation Pause) (young) 404M->379M(512M), 0.0050623 secs]
2020-10-28T17:37:56.395-0800: [GC pause (G1 Evacuation Pause) (mixed) 405M->369M(512M), 0.0052086 secs]
2020-10-28T17:37:56.401-0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark) 370M->369M(512M), 0.0023739 secs]
2020-10-28T17:37:56.403-0800: [GC concurrent-root-region-scan-start]
2020-10-28T17:37:56.404-0800: [GC concurrent-root-region-scan-end, 0.0001880 secs]
2020-10-28T17:37:56.404-0800: [GC concurrent-mark-start]
2020-10-28T17:37:56.405-0800: [GC concurrent-mark-end, 0.0012204 secs]
2020-10-28T17:37:56.405-0800: [GC remark, 0.0014976 secs]
2020-10-28T17:37:56.407-0800: [GC cleanup 379M->379M(512M), 0.0006870 secs]
2020-10-28T17:37:56.411-0800: [GC pause (G1 Evacuation Pause) (young) 402M->379M(512M), 0.0024589 secs]
2020-10-28T17:37:56.419-0800: [GC pause (G1 Evacuation Pause) (mixed) 408M->375M(512M), 0.0051110 secs]
2020-10-28T17:37:56.424-0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark) 375M->375M(512M), 0.0029213 secs]
2020-10-28T17:37:56.427-0800: [GC concurrent-root-region-scan-start]
2020-10-28T17:37:56.427-0800: [GC concurrent-root-region-scan-end, 0.0000125 secs]
2020-10-28T17:37:56.427-0800: [GC concurrent-mark-start]
2020-10-28T17:37:56.428-0800: [GC concurrent-mark-end, 0.0012633 secs]
2020-10-28T17:37:56.428-0800: [GC remark, 0.0015612 secs]
2020-10-28T17:37:56.430-0800: [GC cleanup 383M->383M(512M), 0.0006570 secs]
2020-10-28T17:37:56.436-0800: [GC pause (G1 Evacuation Pause) (young) 409M->385M(512M), 0.0034377 secs]
执行结束！ 共生成对象次数：9593
```

### 1G堆内存 G1 GC日志

```
Learning_notes (main) ✗ java -XX:+UseG1GC -Xms1g -Xmx1g -XX:+PrintGC -XX:+PrintGCDateStamps GCLogAnalysis
正在执行
2020-10-28T17:38:51.001-0800: [GC pause (G1 Evacuation Pause) (young) 72M->22M(1024M), 0.0062039 secs]
2020-10-28T17:38:51.018-0800: [GC pause (G1 Evacuation Pause) (young) 76M->38M(1024M), 0.0058841 secs]
2020-10-28T17:38:51.036-0800: [GC pause (G1 Evacuation Pause) (young) 91M->54M(1024M), 0.0063578 secs]
2020-10-28T17:38:51.063-0800: [GC pause (G1 Evacuation Pause) (young) 125M->83M(1024M), 0.0124005 secs]
2020-10-28T17:38:51.098-0800: [GC pause (G1 Evacuation Pause) (young) 164M->105M(1024M), 0.0092569 secs]
2020-10-28T17:38:51.131-0800: [GC pause (G1 Evacuation Pause) (young) 202M->136M(1024M), 0.0120010 secs]
2020-10-28T17:38:51.223-0800: [GC pause (G1 Evacuation Pause) (young) 340M->184M(1024M), 0.0228177 secs]
2020-10-28T17:38:51.281-0800: [GC pause (G1 Evacuation Pause) (young) 359M->232M(1024M), 0.0207753 secs]
2020-10-28T17:38:51.341-0800: [GC pause (G1 Evacuation Pause) (young) 438M->286M(1024M), 0.0181692 secs]
2020-10-28T17:38:51.543-0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark) 770M->388M(1024M), 0.0437378 secs]
2020-10-28T17:38:51.587-0800: [GC concurrent-root-region-scan-start]
2020-10-28T17:38:51.587-0800: [GC concurrent-root-region-scan-end, 0.0002056 secs]
2020-10-28T17:38:51.587-0800: [GC concurrent-mark-start]
2020-10-28T17:38:51.590-0800: [GC concurrent-mark-end, 0.0025257 secs]
2020-10-28T17:38:51.590-0800: [GC remark, 0.0012450 secs]
2020-10-28T17:38:51.591-0800: [GC cleanup 407M->400M(1024M), 0.0008244 secs]
2020-10-28T17:38:51.592-0800: [GC concurrent-cleanup-start]
2020-10-28T17:38:51.592-0800: [GC concurrent-cleanup-end, 0.0000206 secs]
2020-10-28T17:38:51.606-0800: [GC pause (G1 Evacuation Pause) (young) 484M->406M(1024M), 0.0116056 secs]
2020-10-28T17:38:51.625-0800: [GC pause (G1 Evacuation Pause) (mixed) 447M->350M(1024M), 0.0080566 secs]
2020-10-28T17:38:51.668-0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark) 563M->406M(1024M), 0.0064617 secs]
2020-10-28T17:38:51.674-0800: [GC concurrent-root-region-scan-start]
2020-10-28T17:38:51.674-0800: [GC concurrent-root-region-scan-end, 0.0002256 secs]
2020-10-28T17:38:51.674-0800: [GC concurrent-mark-start]
2020-10-28T17:38:51.676-0800: [GC concurrent-mark-end, 0.0011113 secs]
2020-10-28T17:38:51.676-0800: [GC remark, 0.0014484 secs]
2020-10-28T17:38:51.677-0800: [GC cleanup 415M->410M(1024M), 0.0009806 secs]
2020-10-28T17:38:51.678-0800: [GC concurrent-cleanup-start]
2020-10-28T17:38:51.678-0800: [GC concurrent-cleanup-end, 0.0000200 secs]
2020-10-28T17:38:51.764-0800: [GC pause (G1 Evacuation Pause) (young)-- 839M->633M(1024M), 0.0093063 secs]
2020-10-28T17:38:51.775-0800: [GC pause (G1 Evacuation Pause) (mixed) 639M->577M(1024M), 0.0097171 secs]
2020-10-28T17:38:51.785-0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark) 581M->579M(1024M), 0.0017935 secs]
2020-10-28T17:38:51.787-0800: [GC concurrent-root-region-scan-start]
2020-10-28T17:38:51.787-0800: [GC concurrent-root-region-scan-end, 0.0001397 secs]
2020-10-28T17:38:51.787-0800: [GC concurrent-mark-start]
2020-10-28T17:38:51.789-0800: [GC concurrent-mark-end, 0.0013635 secs]
2020-10-28T17:38:51.789-0800: [GC remark, 0.0015240 secs]
2020-10-28T17:38:51.791-0800: [GC cleanup 589M->582M(1024M), 0.0009617 secs]
2020-10-28T17:38:51.792-0800: [GC concurrent-cleanup-start]
2020-10-28T17:38:51.792-0800: [GC concurrent-cleanup-end, 0.0000164 secs]
2020-10-28T17:38:51.837-0800: [GC pause (G1 Evacuation Pause) (young) 845M->629M(1024M), 0.0096547 secs]
2020-10-28T17:38:51.852-0800: [GC pause (G1 Evacuation Pause) (mixed) 657M->537M(1024M), 0.0054220 secs]
2020-10-28T17:38:51.867-0800: [GC pause (G1 Evacuation Pause) (mixed) 593M->471M(1024M), 0.0055333 secs]
2020-10-28T17:38:51.885-0800: [GC pause (G1 Evacuation Pause) (mixed) 533M->448M(1024M), 0.0048173 secs]
2020-10-28T17:38:51.893-0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark) 462M->452M(1024M), 0.0021663 secs]
2020-10-28T17:38:51.895-0800: [GC concurrent-root-region-scan-start]
2020-10-28T17:38:51.895-0800: [GC concurrent-root-region-scan-end, 0.0001342 secs]
2020-10-28T17:38:51.895-0800: [GC concurrent-mark-start]
2020-10-28T17:38:51.897-0800: [GC concurrent-mark-end, 0.0012777 secs]
2020-10-28T17:38:51.897-0800: [GC remark, 0.0023995 secs]
2020-10-28T17:38:51.899-0800: [GC cleanup 459M->453M(1024M), 0.0009393 secs]
2020-10-28T17:38:51.900-0800: [GC concurrent-cleanup-start]
2020-10-28T17:38:51.900-0800: [GC concurrent-cleanup-end, 0.0000270 secs]
执行结束！ 共生成对象次数：11061
```

### 2G堆内存 G1 GC日志

```
Learning_notes (main) ✗ java -XX:+UseG1GC -Xms2g -Xmx2g -XX:+PrintGC -XX:+PrintGCDateStamps GCLogAnalysis
正在执行
2020-10-28T17:39:15.189-0800: [GC pause (G1 Evacuation Pause) (young) 121M->36M(2048M), 0.0122631 secs]
2020-10-28T17:39:15.228-0800: [GC pause (G1 Evacuation Pause) (young) 142M->68M(2048M), 0.0113469 secs]
2020-10-28T17:39:15.265-0800: [GC pause (G1 Evacuation Pause) (young) 173M->105M(2048M), 0.0119550 secs]
2020-10-28T17:39:15.308-0800: [GC pause (G1 Evacuation Pause) (young) 220M->140M(2048M), 0.0116792 secs]
2020-10-28T17:39:15.343-0800: [GC pause (G1 Evacuation Pause) (young) 258M->174M(2048M), 0.0124379 secs]
2020-10-28T17:39:15.406-0800: [GC pause (G1 Evacuation Pause) (young) 349M->229M(2048M), 0.0184841 secs]
2020-10-28T17:39:15.710-0800: [GC pause (G1 Evacuation Pause) (young) 875M->370M(2048M), 0.0528112 secs]
2020-10-28T17:39:15.770-0800: [GC pause (G1 Evacuation Pause) (young) 414M->378M(2048M), 0.0089638 secs]
2020-10-28T17:39:15.843-0800: [GC pause (G1 Evacuation Pause) (young) 718M->453M(2048M), 0.0213807 secs]
2020-10-28T17:39:15.923-0800: [GC pause (G1 Evacuation Pause) (young) 779M->518M(2048M), 0.0302269 secs]
2020-10-28T17:39:16.023-0800: [GC pause (G1 Evacuation Pause) (young) 893M->599M(2048M), 0.0295562 secs]
执行结束！ 共生成对象次数：10186
```

### 4G堆内存 G1 GC日志

```
 Learning_notes (main) ✗ java -XX:+UseG1GC -Xms4g -Xmx4g -XX:+PrintGC -XX:+PrintGCDateStamps GCLogAnalysis
正在执行
2020-10-28T17:39:44.171-0800: [GC pause (G1 Evacuation Pause) (young) 204M->61M(4096M), 0.0268331 secs]
2020-10-28T17:39:44.235-0800: [GC pause (G1 Evacuation Pause) (young) 239M->110M(4096M), 0.0230679 secs]
2020-10-28T17:39:44.289-0800: [GC pause (G1 Evacuation Pause) (young) 288M->163M(4096M), 0.0252689 secs]
2020-10-28T17:39:44.344-0800: [GC pause (G1 Evacuation Pause) (young) 341M->213M(4096M), 0.0237294 secs]
2020-10-28T17:39:44.401-0800: [GC pause (G1 Evacuation Pause) (young) 391M->269M(4096M), 0.0285358 secs]
2020-10-28T17:39:44.460-0800: [GC pause (G1 Evacuation Pause) (young) 447M->326M(4096M), 0.0247653 secs]
2020-10-28T17:39:44.510-0800: [GC pause (G1 Evacuation Pause) (young) 504M->386M(4096M), 0.0255301 secs]
2020-10-28T17:39:44.565-0800: [GC pause (G1 Evacuation Pause) (young) 574M->449M(4096M), 0.0292658 secs]
2020-10-28T17:39:44.632-0800: [GC pause (G1 Evacuation Pause) (young) 671M->514M(4096M), 0.0312543 secs]
2020-10-28T17:39:44.726-0800: [GC pause (G1 Evacuation Pause) (young) 796M->593M(4096M), 0.0403226 secs]
执行结束！ 共生成对象次数：9752
```



### 8G堆内存 G1 GC日志

```
➜  Learning_notes (main) ✗ java -XX:+UseG1GC -Xms8g -Xmx8g -XX:+PrintGC -XX:+PrintGCDateStamps GCLogAnalysis
正在执行
2020-10-28T17:41:19.752-0800: [GC pause (G1 Evacuation Pause) (young) 408M->121M(8192M), 0.0474560 secs]
2020-10-28T17:41:19.855-0800: [GC pause (G1 Evacuation Pause) (young) 477M->215M(8192M), 0.0389705 secs]
2020-10-28T17:41:19.948-0800: [GC pause (G1 Evacuation Pause) (young) 571M->301M(8192M), 0.0373245 secs]
2020-10-28T17:41:20.036-0800: [GC pause (G1 Evacuation Pause) (young) 657M->400M(8192M), 0.0423555 secs]
2020-10-28T17:41:20.127-0800: [GC pause (G1 Evacuation Pause) (young) 756M->493M(8192M), 0.0405221 secs]
2020-10-28T17:41:20.220-0800: [GC pause (G1 Evacuation Pause) (young) 849M->593M(8192M), 0.0429381 secs]
2020-10-28T17:41:20.313-0800: [GC pause (G1 Evacuation Pause) (young) 949M->699M(8192M), 0.0472177 secs]
2020-10-28T17:41:20.415-0800: [GC pause (G1 Evacuation Pause) (young) 1055M->798M(8192M), 0.0505110 secs]
执行结束！ 共生成对象次数：12143
```

### 

我的测试机子

MacBook Pro (15-inch, Late 2016)

cpu : 2.7 GHz 四核Intel Core i7

内存:16g÷

##统计结果

| 内存     | 串行GC                       | 并行GC                       | CMS GC                       | G1 GC                        |
| -------- | ---------------------------- | ---------------------------- | ---------------------------- | ---------------------------- |
| **256m** | OOM                          | OOM                          | OOM                          | OOM                          |
| 512m     | 17次YGC 8次FULL GC /生成对象 | 17次YGC 8次FULL GC /生成对象 | 17次YGC 8次FULL GC /生成对象 | 17次YGC 8次FULL GC /生成对象 |
| 1g       | 17次YGC 8次FULL GC /生成对象 | 17次YGC 8次FULL GC /生成对象 | 17次YGC 8次FULL GC /生成对象 | 17次YGC 8次FULL GC /生成对象 |
| 2g       | 17次YGC 8次FULL GC /生成对象 | 17次YGC 8次FULL GC /生成对象 | 17次YGC 8次FULL GC /生成对象 | 17次YGC 8次FULL GC /生成对象 |
| 4g       | 17次YGC 8次FULL GC /生成对象 | 17次YGC 8次FULL GC /生成对象 | 17次YGC 8次FULL GC /生成对象 | 17次YGC 8次FULL GC /生成对象 |
| 8g       | 17次YGC 8次FULL GC /生成对象 | 17次YGC 8次FULL GC /生成对象 | 17次YGC 8次FULL GC /生成对象 | 17次YGC 8次FULL GC /生成对象 |

### 结果分析

**四种GC算法的共同点：**

在内存为256M时候，内存不够使用，不论怎么回收，老年代都会堆满活动对象无法回收，四种GC算法都无法处理这样的场景，最后都会导致OOM，在内存足够用时候，内存8g以上，老年代不会出现内存不够用的情况，四种GC算法都不需要进行full gc, 对于同一种GC而言，堆内存空间越大，触发GC的次数就越少，相对地业务线程的执行时间会变多，吞吐量也就会提升  

**四种GC算法的不同点：**

在堆内存比较小时候，四种GC的频次都比较高，在这样场景下每次GC中存活的对象都是很少的，多线程并行处理并不能发挥出优势来，反而会有多线程竞争，造成时间浪费，此时串行GC下的吞吐率反而高于并行GC的吞吐率，但是堆内存比较充足的情况下，gc次数减少，每次GC的存活对象相对多一些时候，并行多线程就会发挥出优势，从统计结果中可以看出内存量比较充足时候，并行GC下的吞吐量是最大的，串行GC的吞吐量是最低的，CMS GC 的实现中GC和业务线程是并发执行的，目的是减少延迟，回收效率相比并行GC要低一些，吞吐量相对并行GC而言同样会低一些，同样的，G1 GC并不是每一次GC都把所有垃圾对象清理掉，回收效率相对并行GC而言低一些，总的吞吐率也相对低一些，并且由于是启发式的GC算法，所以造成吞吐率浮动比较大，表现出来的垃圾回收效率没有其他几种GC稳定



## 作业五

    写一段代码，使用 HttpClient 或 OkHttp 访问[http://localhost:8801，代码提交到](http://localhost:8801，代码提交到/) github

    这里使用OKHttp

    需要添加下面两个Maven依赖：

```shell
        <dependency>
            <groupId>com.squareup.okhttp3</groupId>
            <artifactId>okhttp</artifactId>
            <version>4.9.0</version>
        </dependency>
```

    代码如下：

```java
package cn.qj.week2;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/* Copyright @ 2019 Citycloud Co. Ltd.
 * All right reserved.
 * OkHttpDemo
 *
 * @author edd1225
 * @Description: OkHttpDemo
 * @create 2020/10/28 12:23
 **/
public class OkHttpDemo {
    public static void main(String[] args) throws IOException {
        OkHttpClient client = new OkHttpClient();
        try {
            Request request = new Request.Builder().url("http://127.0.0.1:8801/test").build();
            Response response = client.newCall(request).execute();
            System.out.println(response.body().string());
        } finally {
            client.clone();
        }
    }
}
```

    返回结果：

