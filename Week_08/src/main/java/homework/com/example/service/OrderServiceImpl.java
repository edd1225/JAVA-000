/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package homework.com.example.service;

import homework.com.example.dao.domain.Order;
import homework.com.example.dao.mapper.OrderMapper;
import org.apache.shardingsphere.transaction.annotation.ShardingTransactionType;
import org.apache.shardingsphere.transaction.core.TransactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.concurrent.ThreadLocalRandom;

/**
 * T
 */
@Component
public class OrderServiceImpl implements OrderService {
    @Autowired
    OrderMapper orderMapper;

    @Transactional(rollbackFor = SQLException.class)
    @ShardingTransactionType(TransactionType.XA)
    public void insert() throws SQLException {
        Long uid= ThreadLocalRandom.current().nextLong(100);
        System.out.println("*******************");
        System.out.println(System.currentTimeMillis());
        System.out.println("uid="+uid);
        System.out.println("uid%2="+uid%2);
        System.out.println("*******************");

        Order order=new Order();
        order.setUserId(uid);
        order.setGoodsId(100L);
        orderMapper.insert(order);
        throw new SQLException("hehe");
    }

}
