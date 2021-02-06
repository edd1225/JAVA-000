package com.guanys.dubboapi.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * @PROJECT_NAME: dubboapi
 * @PACKAGE_NAME: com.guanys.dubboapi.bean
 * @NAME: User
 * @Author: guanys
 * @DATE: 12/17/20:5:05 PM
 * @Description: TODO
 * @Version: 1.0
 **/
@Data
public class User implements Serializable {

    private static final long serialVersionUID = 8470370800765879248L;

    private int id;

    private String name;

    public User(){}

    public User(int id,String name){
        this.id = id;
        this.name = name;
    }
}
