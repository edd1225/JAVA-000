package cn.qj.week15.cache.dao.service.impl;

import cn.qj.week15.cache.dao.GoodsMapper;
import cn.qj.week15.cache.dao.entry.Goods;
import cn.qj.week15.cache.dao.service.GoodsService;
import cn.qj.week15.cache.dao.util.BloomFilterUtil;
import cn.qj.week15.cache.dao.util.RedisDistributedLock;
import cn.qj.week15.cache.dao.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**************************************************
 *
 * @author edd1225
 *
 **************************************************/
@Service
public class GoodsServiceImpl implements GoodsService {

	@Resource
	private GoodsMapper goodsMapper;

	@Autowired
	private RedisUtil redisUtil;

	@Autowired
	private RedisDistributedLock redisDistributedLock;

	//锁前缀
	private static final String LOCK_PREFIX="redis:lock:id:";

	@Override
	public Goods getGoodsById(int goodsId) {
		/**
		 * 1、正常的主体逻辑，没有任何redis失效的防范措施
		 */
//		Goods goods = (Goods)redisUtil.get(goodsId + "");
//		if(goods==null){
//			goods = goodsMapper.getGoodsById(goodsId);
//			if(goods!=null){
//				redisUtil.set(goodsId+"", goods, 100);
//			}
//		}
//		return goods;

		/**
		 * 2、防范redis缓存击穿，缓存穿透，缓存雪崩
		 */
		Goods goods = (Goods)redisUtil.get(goodsId + "");
		//缓存没有
		if(goods==null){

			//查询布隆过滤器。防止穿透，但是会漏一部分
			if(BloomFilterUtil.mayContains(goodsId)) {

				//爆款商品加锁
				if (goodsId == 11) {

					String localKey = LOCK_PREFIX + goodsId;

					String uuid = UUID.randomUUID().toString();
					//方案1
					if (redisDistributedLock.setLock(localKey, uuid, 100 * 1000)) {
						goods = goodsMapper.getGoodsById(goodsId);

						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						if (goods != null) {
							//这里可以不设置超时时间，方案2的设置点
							//redisUtil.set(goodsId+"", goods, 3600);
							redisUtil.set(goodsId + "", goods);
						}

						//释放锁
						redisDistributedLock.releaseLock(localKey, uuid);
					}

					//申请锁失败
					else {
						System.out.println("申请锁失败" + localKey);
						//设置自旋，重新访问
						try {
							Thread.sleep(3000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						return getGoodsById(goodsId);
					}

					System.out.println(">>>>>>>>>>>>>>>>>>>>>爆款from db");
				}

				//非爆款
				else {
					goods = goodsMapper.getGoodsById(goodsId);
					if(goods!=null){
						//此时针对商品是热门还是冷门商品，根据冷热程度设置不同的随机值，防止缓存雪崩

						Random random=new Random();
						int timeOut=0;
						//女装热门，类型1
						if(goods.getCategory()==1){

							timeOut=3600+random.nextInt(3600);
						}

						//其他热门，女鞋
						else if(goods.getCategory()==2){
							timeOut=1800+random.nextInt(1800);
						}

						//冷门
						else {
							timeOut=600+random.nextInt(600);
						}

						redisUtil.set(goodsId+"", goods, timeOut);
						System.out.println(">>>>>>>>>>>>>>>>>>>from db");
					}

					//数据库没有，要防止缓存穿透，2个方案：1-设置空值，2-布隆
					else {
						redisUtil.set(goodsId+"", null, 60);
					}
				}
			}

			//这个地方可以加缓存穿透的设置空值方案
		}

		else{
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>from redis");
		}
		return goods;
	}

	@Override
	public List<Integer> selectGoodsIds() {
		return goodsMapper.selectGoodsIds();
	}
}
