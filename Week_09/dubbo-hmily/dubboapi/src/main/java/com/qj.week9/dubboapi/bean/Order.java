package com.qj.week9.dubboapi.bean;

import lombok.Data;

import java.io.Serializable;


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
