package com.qj.week9.dubboapi.service;

import com.qj.week9.dubboapi.bean.Order;

public interface OrderService {

    Order findOrderById(int id);
}
