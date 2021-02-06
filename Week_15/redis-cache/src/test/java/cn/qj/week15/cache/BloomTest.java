package cn.qj.week15.cache;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

/**************************************************
 *
 * @author edd1225
 * 不容过滤器
 *
 **************************************************/
public class BloomTest {
	private static final int CAPACITY = 1000000;

	private static final int KEY = 999998;

	static BloomFilter<Integer> bloomFilter = BloomFilter.create(Funnels.integerFunnel(), CAPACITY);

	static {
		for (int i = 0; i < CAPACITY; i++) {
			bloomFilter.put(i);
		}
	}

	public static void main(String[] args) {

		long start = System.nanoTime();
		if (bloomFilter.mightContain(KEY)) {
			System.out.println("布隆过滤器中找到key：" + KEY);
		}
		long end = System.nanoTime();

		System.out.println("查找耗费时间，end-start=" + (end - start));

		/**
		 * 布隆过滤器不是100%的，他是有一定错误率的
		 */
		int sum = 0;//1000000   1010000 1020000
		for (int i = CAPACITY + 10000; i < CAPACITY + 20000; i++) {
			if (bloomFilter.mightContain(i)) {
				sum++;
			}
		}
		System.out.println(">>>>>>>>>>>>>>>>>sum:"+sum);
	}
}
