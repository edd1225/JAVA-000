package cn.qj.week15.cache;

import cn.qj.week15.cache.dao.entry.Goods;
import cn.qj.week15.cache.dao.service.GoodsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**************************************************
 *

 * @author edd225
 *
 **************************************************/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:application.xml")
public class GoodsServiceTest {
	@Autowired
	private GoodsService goodsService;

	@Test
	public void getGoodsById() {
		/**
		 * 测试爆款，非爆款，都不存在的
		 */
		Goods goods = goodsService.getGoodsById(10);
		System.out.println("goods=======>" + goods);
	}
}
