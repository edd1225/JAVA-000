package cn.qj.ceshi;

import java.util.Map;

/**
 * 一次走1级或2级的总走法问题
 */
public class Main2 {
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        fun(10);
        System.out.println("n=10时运行时间：" + (System.currentTimeMillis() - startTime));
        startTime = System.currentTimeMillis();
        fun(30);
        System.out.println("n=30时运行时间：" + (System.currentTimeMillis() - startTime));
        startTime = System.currentTimeMillis();
        fun(40);
        System.out.println("n=40时运行时间：" + (System.currentTimeMillis() - startTime));

//        climbStairs(44);
        climbStairs2(44);

    }

    /**
     * 递归法（ 复杂度O(N!) ）
     *
     * @param stair
     * @return
     */
    public static int fun(int stair) {
        if (stair == 1) {
            return 1;
        } else if (stair == 2) {
            return 2;
        } else {
            return fun(stair - 1) + fun(stair - 2);
        }
    }

    /***
     * 动态规划法
     * @param n
     * @param map
     * @return
     */
    public static int climbStairs(int n, Map<Integer, Integer> map) {
        if (n <= 0) {
            return 0;
        }
        if (n <= 2) {
            return n;
        }
        if (map.containsKey(n)) {
            return map.get(n);
        } else {
            int temp = climbStairs(n - 1, map) + climbStairs(n - 2, map);
            map.put(n, temp);
            return temp;
        }
    }

    /***
     * 循环法
     * @param n
     * @return
     */
    public static int climbStairs2(int n) {
        if (n == 0) {
            return 0;
        }
        if (n == 1) {
            return 1;
        }
        if (n == 2) {
            return 2;
        }
        int lastF1 = 1, lastF2 = 0, nextF1 = 0, nextF2 = 1;
        int resultF1 = 0, resultF2 = 0;
        for (int i = 3; i <= n; i++) {
            resultF1 = lastF1 + nextF1;
            resultF2 = lastF2 + nextF2;
            lastF1 = nextF1;
            lastF2 = nextF2;
            nextF1 = resultF1;
            nextF2 = resultF2;
        }
        return resultF1 + 2 * resultF2;
    }
}
