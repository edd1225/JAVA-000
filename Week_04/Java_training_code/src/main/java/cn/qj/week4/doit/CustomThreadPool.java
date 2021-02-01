package cn.qj.week4.doit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

/*****
 *
 * 自定义手撕Java线程池 自定义创建一个线程池，以及线程拒绝策略，关闭线程池，线程池
 * 有点想androidnp 的xmpp代码
 *  @author edd1225
 */

public class CustomThreadPool extends Thread {
    //    设置min，active，max等参数以及线程池的扩容和缩容技术
    private int min;
    private int active;
    private int max;
    private int currentSize;
    private int queueSize;//队列数
    //最大任务
    private static final int DEFAULT_QUEUE_SIZE = 30;
    private static final int DEFAULT_SIZE = 4;//默认线程的大小
    //2个任务 任务队列
    private static final LinkedList<Runnable> TASKQUEUE = new LinkedList<>();
    private static final List<WorkerThread> THREADQUEUE = new ArrayList<>();
    private DiscardPolicy discardPolicy;
    //拒绝策略
    private static DiscardPolicy DEFAULT_POLICY = () -> {
        throw new RuntimeException("任务超出预期！");
    };
    //是否已经关闭
    private boolean isDestroy = false;


    public CustomThreadPool() {
        this(4, 8, 12, DEFAULT_QUEUE_SIZE, DEFAULT_POLICY);
    }

    /**
     * 构造函数
     *
     * @param min
     * @param active
     * @param max
     * @param queueSize
     * @param discardPolicy
     */
    public CustomThreadPool(int min, int active, int max, int queueSize, DiscardPolicy discardPolicy) {
        this.min = min;
        this.active = active;
        this.max = max;
        this.currentSize = min;
        this.discardPolicy = discardPolicy;
        this.queueSize = queueSize;
    }
    //init
    public void init() {
        for (int i = 0; i < currentSize; i++) {
            createTask();
        }
    }

    /**
     * create 线程任务
     */
    public void createTask() {
        WorkerThread workerThread = new WorkerThread();
        workerThread.start();
        THREADQUEUE.add(workerThread);
    }

    private enum WorkerThreadState {
        FREE, BLOCK, RUNNING, DEAD
    }

    /**
     * 提交任务
     *
     * @param task
     */
    public void submit(Runnable task) {
        if (isDestroy) {
            throw new RuntimeException("线程池已关闭，不可再提交任务！");
        }
        synchronized (TASKQUEUE) {
            //当前队列中的任务数超出预定的任务就拒绝
            if (TASKQUEUE.size() > queueSize) {
                System.out.println("当前任务队列中的任务数：" + TASKQUEUE.size());
                this.discardPolicy.discard();
            }
            TASKQUEUE.addLast(task);
            TASKQUEUE.notifyAll();
        }
    }

    public void shutdown() throws InterruptedException {
        while (!TASKQUEUE.isEmpty()) {
            Thread.sleep(500);
        }
        synchronized (THREADQUEUE) {
            int threadSize = THREADQUEUE.size();
            while (threadSize > 0) {
                for (WorkerThread workerThread : THREADQUEUE) {
                    //当线程状态为RUNNING的时候不能关闭
                    //线程在等待，夯住了
                    if (workerThread.state == WorkerThreadState.BLOCK) {
                        workerThread.interrupt();
                        workerThread.close();
                        threadSize--;
                    }
                    //线程执行完毕
//					else if(workerThread.state == WorkerThreadState.FREE){
//						workerThread.close();
//						threadSize--;
//					}
                    //线程还在RUNNING,等等吧
                    else {
                        Thread.sleep(500);
                    }
                }
            }
        }

        isDestroy = true;
        System.out.println(">>>>>>>>线程池已关闭！");

    }

    /**
     * 拒绝策略
     */
    public interface DiscardPolicy {
        void discard();
    }

    /**
     * 监控线程池，动态扩容和缩容
     */
    @Override
    public void run() {
        /**
         * * 常识：
         * 	 *
         * 	 * 更少的线程可以处理很多的任务，所以设置三个参数：min，active，max
         * 	 * 1、min<任务数<active,或者任务数<min,线程数只要min就可以了--------------默认
         * 	 * 2、active<任务数<max,线程数要扩展到active
         * 	 * 3、max<任务数，线程数要扩到max
         */
        while (!isDestroy) {
            System.out.println("##当前最小线程数：" + min + ",活动线程数：" + active + ",最大线程数：" + max + ",当前线程数：" + currentSize +
                    "," +
                    "当前任务量："
                    + TASKQUEUE.size());

            synchronized (THREADQUEUE) {
                //扩容
                if (TASKQUEUE.size() > active && currentSize < active) {
                    for (int i = currentSize; i < active; i++) {
                        createTask();
                    }
                    currentSize = active;
                    System.out.println("##线程池已经扩容到acive：" + active);
                } else if (TASKQUEUE.size() > max && currentSize < max) {

                    for (int i = currentSize; i < max; i++) {
                        createTask();
                    }
                    currentSize = max;
                    System.out.println("##线程池扩容到max：" + max);
                } else ;

                //缩容

                if (TASKQUEUE.isEmpty() && currentSize > active) {
                    System.out.println("===========##线程池开始缩容================");
                    int releaseSize = currentSize - active;

                    Iterator<WorkerThread> iterator = THREADQUEUE.iterator();
                    while (iterator.hasNext()) {
                        if (releaseSize <= 0) {
                            break;
                        }

                        WorkerThread workerThread = iterator.next();
                        if (workerThread.state == WorkerThreadState.BLOCK) {
                            workerThread.interrupt();
                            workerThread.close();
                            iterator.remove();
                            releaseSize--;
                            System.out.println("workerThread.state=" + workerThread.state);
                        }
                    }
                    currentSize = active;
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 一、线程池原理
     * <p>
     * 1、定义一个队列，用来存放提交的任务（任务也是线程）
     * 2、内部线程类操作任务队列
     * 3、外部线程池提交任务
     * <p>
     * 二、线程池拒绝策略
     * 任务队列中不可能无限制的增加任务，当任务达到一个阈值的时候，线程池得拒绝请求
     * <p>
     * 三、关闭线程池
     * 线程池在非RUNNING状态的时候，即BLOCK状态的时候才能够被关闭
     * <p>
     * 四、线程池的扩容和缩容
     * 1、扩容
     */
    class WorkerThread extends Thread {
        private volatile WorkerThreadState state = WorkerThreadState.FREE;

        @Override
        public void run() {
            OUTER:
            while (state != WorkerThreadState.DEAD) {
                Runnable task;
                synchronized (TASKQUEUE) {
                    while (TASKQUEUE.isEmpty()) {
                        try {
                            this.state = WorkerThreadState.BLOCK;
                            TASKQUEUE.wait();//线程夯住
//							this.state = WorkerThreadState.BLOCK;
                        } catch (InterruptedException e) {
                            //e.printStackTrace();
                            System.out.println("线程interrupt and will be close");
                            break OUTER;
                        }
                    }
                    task = TASKQUEUE.remove();

                    //不需要同步执行
//					if (task != null) {
//						this.state = WorkerThreadState.RUNNING;
//						task.run();
//						this.state = WorkerThreadState.FREE;
//					}
                }
                //并行执行
                if (task != null) {
                    this.state = WorkerThreadState.RUNNING;
                    task.run();
                    this.state = WorkerThreadState.FREE;
                }
            }
        }
        public void close() {
            this.state = WorkerThreadState.DEAD;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        CustomThreadPool pool = new CustomThreadPool(5, 10, 15, 66, CustomThreadPool.DEFAULT_POLICY);
        pool.init();
        pool.start();
        //提交50个任务
        IntStream.rangeClosed(0, 50).forEach(i -> {
            pool.submit(() -> {
                try {
                    Thread.sleep(1000);
                    System.out.println(">>>>>>>>>>>>>>当前线程：" + Thread.currentThread().getName() + "处理任务:" + i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        });
        Thread.sleep(10000);
        pool.shutdown();
        //simpleThreadPool.submit(() -> System.out.println("再提交一个任务x"));
    }
}