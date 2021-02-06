package cn.qj.week15.cache.dao.util;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import cn.qj.week15.cache.dao.GoodsMapper;

import java.util.List;

/**************************************************
 *
 * @author edd1225
 *
 **************************************************/
public class BloomFilterUtil {
	private GoodsMapper goodsMapper;

	public void setGoodsMapper(GoodsMapper goodsMapper) {
		this.goodsMapper = goodsMapper;
	}

	private static final int CAPACITY = 1000000;

	private static BloomFilter<Integer> bloomFilter = BloomFilter.create(Funnels.integerFunnel(), CAPACITY);

	public static boolean mayContains(Integer i) {
		return bloomFilter.mightContain(i);
	}

	/**
	 * 对应热点表id全部进布隆过滤器
	 */
	public void init() {
		List<Integer> goodsList = goodsMapper.selectGoodsIds();
		for (Integer i : goodsList) {
			bloomFilter.put(i);
		}
	}
}
