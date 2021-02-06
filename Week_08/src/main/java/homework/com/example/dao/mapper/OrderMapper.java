package homework.com.example.dao.mapper;

import homework.com.example.dao.domain.Order;
import org.apache.ibatis.annotations.*;

/**
 * T
 */
public interface OrderMapper {
    @Select("SELECT * FROM t_order WHERE user_id = #{userId} and order_id=#{orderId}")
    @Results({
            @Result(property = "orderId",  column = "order_id"),
            @Result(property = "userId",  column = "user_id"),
            @Result(property = "goodsId", column = "goods_id")
    })
    Order getOne(Order order);

    @Insert("INSERT INTO t_order(user_id,goods_id) VALUES(#{userId}, #{goodsId})")
    void insert(Order order);

    @Delete("delete from t_order where user_id = #{userId} and order_id=#{orderId}")
    int delete(Long userId,Long orderId);

    @Update("update t_order set status = 1 where user_id = #{userId} and order_id=#{orderId}")
    int update(Long userId,Long orderId);
}
