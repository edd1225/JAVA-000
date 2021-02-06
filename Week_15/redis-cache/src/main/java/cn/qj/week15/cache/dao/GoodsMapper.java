package cn.qj.week15.cache.dao;

import cn.qj.week15.cache.dao.entry.Goods;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**************************************************
 * @author edd1225
 *
 **************************************************/
@Mapper
public interface GoodsMapper {
	public Goods getGoodsById(int goodsId);

	public List<Integer> selectGoodsIds();
}
