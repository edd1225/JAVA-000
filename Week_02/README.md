#极客大学 Java 进阶训练营 JVM 核心技术（三）：调优分析与面试经验  week2 学习笔记 周四



- 第二周作业：[作业](https://github.com/edd1225/JAVA-000/blob/main/Week_02/Learning_notes/HOMEWORK.md)
- [GC 日志解读与分析]
- [NIO相关学习记录]
- [Netty学习总结]

##1. GC 日志解读与分析

**GC** **日志解读与分析**

java -XX:+PrintGCDetails GCLogAnalysis

java -Xloggc:gc.demo.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps 

GCLogAnalysis

1）模拟一下 OOM，java -Xmx128m -XX:+PrintGCDetails GCLogAnalysis

2）分别使用 512m,1024m,2048m,4086m,观察 GC 信息的不同