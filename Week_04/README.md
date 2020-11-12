# 作业与总结

---

## 作业一

### 作业要求

**2.（必做）思考有多少种方式，在 main 函数启动一个新线程，运行一个方法，拿到这个方法的返回值后，退出主线程？写出你的方法，越多越好，提交到 Github。**

做题思路

一共 10 种，大致如下：

- 不使用多线程并发工具：
  - TestNoLockMethod.java(使用循环不断判断)
  - TestThreadJoinMethod.java(使用Thread Join)
- 使用多线程并发工具
  - 不使用 Future（使用类似等待-通知机制）
    - TestSynchronizedMethod.java
    - TestSemaphoreMethod.java
    - TestLockConditionMethod.java
    - TestCyclicBarrierMethod.java
    - TestCountDownLatchMethod.java
    
  - 使用 Future（使用线程池的 submit）
    - TestFutureMethod.java
    - TestFutureTaskMethod.java
    - TestCompletableFutureMethod.java
    
    
    
    
    
    
  
  

## 作业二

### 作业要求

4.（必做）把多线程和并发相关知识带你梳理一遍，画一个脑图，截图上传到 Github 上。
可选工具：xmind，百度脑图，wps，MindManage 或其他。

### 总结

这个知识梳理基本就是下面的： Java 并发概览
脑图也放到里面了

&ensp;&ensp;&ensp;&ensp;并发相关知识如下：

 

---

# Java 并发概览

---



- 《Java 并发编程实战》：代码的例子很多，偏实战，很好
- 《Java 并发编程的艺术》：有原理级别，有实战级别的，能读完收获不小，很好
- 《Java多线程编程核心技术》： 第2，3版 
- 《Java高并发编程详解：多线程与架构设计》 汪文君 他的三个视频系列也不错
- 《Java 并发编程实战》 极客时间 王宝令 真五星级专栏





## 并发相关工具、API说明

ThreadPoolExecutors 线程池，集中管理，复用线程资源，避免过多创建线程造成的一系列开销

Semaphore 创建时初始化给定数量资源，当资源用尽，后来的操作将被阻塞，类似于令牌。

CountDownLatch 类似跑步比赛的起跑线，当主线程在某处await时，子线程运行可以countdown，计数为0时，主线程继续执行，一旦创建运行，则不可再用。

CyclicBarrier 与CountDownLatch类似，但是其可以复用，类似于在一个圈中设置一个节点，当圈中任务跑到节点时停下，当所有任务都到节点时，发出一个通知，即调用回调函数。 以上三种在需要线程间协作的场景都可以使用

Future 异步的执行一个任务

ForkJoin 类似Future, 但是其特别之处在于，ForkJoinPool中的两个任务队列，当其中一个任务队列的为空时，对应的线程会从另一个队列拿取任务执行

ReentrantLock Java层面的锁，对应的synchronized为JVM层面的锁，其比sync关键字，更为灵活，拥有尝试机制，可中断。

ReentrantReadWriteLock 类似数据库读写锁，读时共享，写时独占，读多写少场景下很适合用的锁。

LockSupport 类似wait/notify机制，但其不会发生死锁情况，中断线程不会抛出异常，需要特别处理。

ConcurrentHashMap 线程安全的HashMap,适合在多线程情况下使用

CopyOnWriteArrayList 在对该对象内容进行更改时，会在复制原值得到的副本上进行修改，不影响老数据，读多写少的并发场景适合使用，但是因为并发问题，其size方法得到的值，不一定是最新值。



- 
