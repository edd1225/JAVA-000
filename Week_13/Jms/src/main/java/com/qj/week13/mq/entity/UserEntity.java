package com.qj.week13.mq.entity;

/**
 * Copyright @ 2019 Citycloud Co. Ltd.
 * All right reserved.
 * 主页
 *
 * @author edd1225
 * @Description: java类作用描述
 * @create 2021/2/6 20:30
 **/
public class UserEntity {
    public UserEntity(long l, String s, int i) {
    }

    long id;
    String nam;
    int age;

    public long getId() {
        return id;
    }

    public String getNam() {
        return nam;
    }

    public int getAge() {
        return age;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setNam(String nam) {
        this.nam = nam;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
