package cn.qj.week15.cache.dao.service;

import cn.qj.week15.cache.dao.entry.Goods;

import java.util.List;

public interface GoodsService {

	public Goods getGoodsById(int goodsId);

	public List<Integer> selectGoodsIds();
}
