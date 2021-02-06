package com.guanys.dubboapi.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * @PROJECT_NAME: dubboapi
 * @PACKAGE_NAME: com.guanys.dubboapi.bean
 * @NAME: Order
 * @Author: guanys
 * @DATE: 12/17/20:4:58 PM
 * @Description: TODO
 * @Version: 1.0
 **/
@Data
public class Order implements Serializable {

    private static final long serialVersionUID = -367176568518394793L;

    private int id;

    private String name;

    private float amount;

    public Order(){}

    public Order(int id, String name, float amount) {
        this.id = id;
        this.name = name;
        this.amount = amount;
    }
}
