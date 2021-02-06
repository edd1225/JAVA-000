package cn.qj.week15.cache.dao.entry;

import java.io.Serializable;

/**************************************************
 *
 * @title
 * @desc ling
 *
 **************************************************/
public class Goods implements Serializable {
	private int goodsId;
	private String goodsName;
	private int price;

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	private int category;

	public Goods() {
	}

	public Goods(int goodsId, String goodsName, int price, int category) {

		this.goodsId = goodsId;
		this.goodsName = goodsName;
		this.price = price;
		this.category = category;
	}

	public int getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(int goodsId) {
		this.goodsId = goodsId;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return "Goods{" +
				"goodsId=" + goodsId +
				", goodsName='" + goodsName + '\'' +
				", price=" + price +
				", category=" + category +
				'}';
	}
}
