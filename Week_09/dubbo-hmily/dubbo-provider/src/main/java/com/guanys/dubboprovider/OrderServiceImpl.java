package com.guanys.dubboprovider;

import com.guanys.dubboapi.bean.Order;
import com.guanys.dubboapi.service.OrderService;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @PROJECT_NAME: dubbo-provider
 * @PACKAGE_NAME: com.guanys.dubboprovider
 * @NAME: OrderServiceImpl
 * @Author: guanys
 * @DATE: 12/18/20:10:58 AM
 * @Description: TODO
 * @Version: 1.0
 **/
@DubboService(version = "1.0.0")
public class OrderServiceImpl implements OrderService {

    @Override
    public Order findOrderById(int id) {
        return new Order(id, "Cuijing" + System.currentTimeMillis(), 9.9f);
    }
}
