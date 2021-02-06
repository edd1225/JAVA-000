package homework.com.example;

import homework.com.example.dao.domain.Order;
import homework.com.example.dao.mapper.OrderMapper;
import homework.com.example.service.OrderService;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ThreadLocalRandom;

@SpringBootTest
class TestSsJdbcApplicationTests {

    @Autowired
    OrderMapper orderMapper;

    @Resource(name = "shardingDataSource")
    DataSource shardingDataSource;

    @Autowired
    OrderService orderService;

    @Test
    void contextLoads() throws SQLException {
        testInit();
        Long uid=ThreadLocalRandom.current().nextLong(100);
        Order order=new Order();
        order.setOrderId(1L);
        order.setUserId(uid);
        order.setGoodsId(25L);
        orderMapper.insert(order);
        Order result=orderMapper.getOne(order);
        orderMapper.update(uid,result.getOrderId());
        orderMapper.delete(uid,result.getOrderId());
        Assert.assertEquals(25L,result.getGoodsId().longValue());
    }

    @Test
    void xaTest() {
        try {
            orderService.insert();
        } catch (SQLException e) {
            Assert.assertNotNull(e);
        }
    }

    void testInit() throws SQLException{
        String sql = "CREATE TABLE IF NOT EXISTS t_order (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id BIGINT NOT NULL, goods_id BIGINT NOT NULL, status tinyint(1), PRIMARY KEY (order_id))";
        try (Connection connection = shardingDataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        }
    }

}
