package com.guanys.dubboapi.service;

import com.guanys.dubboapi.bean.Order;

public interface OrderService {

    Order findOrderById(int id);
}
