package cn.qj.week4;

import java.util.concurrent.Semaphore;

/**
 * 本周作业：（必做）思考有多少种方式，在main函数启动一个新线程或线程池，
 * 异步运行一个方法，拿到这个方法的返回值后，退出主线程？
 * 写出你的方法，越多越好，提交到github。
 *
 * Semaphore方式
 */
public class TestSemaphoreMethod {
    public static void main(String[] args) throws InterruptedException {

        long start=System.currentTimeMillis();
        // 在这里创建一个线程或线程池，
        // 异步执行 下面方法

        final TestSemaphoreMethod method = new TestSemaphoreMethod();
        Thread thread = new Thread(() -> {
            try {
                method.sum(45);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.start();

        int result = method.getValue(); //这是得到的返回值

        // 确保  拿到result 并输出
        System.out.println("异步计算结果为："+result);

        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");

        // 然后退出main线程
    }
    private volatile Integer value = null;
    final Semaphore semaphore = new Semaphore(1);

    public void sum(int num) throws InterruptedException {
        semaphore.acquire();
        value = fibo(num);
        semaphore.release();
    }

    private int fibo(int a) {
        if ( a < 2) {
            return 1;
        }
        return fibo(a-1) + fibo(a-2);
    }

    public int getValue() throws InterruptedException {
        int result;
        semaphore.acquire();
        result = this.value;
        semaphore.release();
        return result;
    }



}
