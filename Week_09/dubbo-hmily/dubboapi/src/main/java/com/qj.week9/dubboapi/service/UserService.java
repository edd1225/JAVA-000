package com.qj.week9.dubboapi.service;

import com.qj.week9.dubboapi.bean.User;

public interface UserService {

    User findById(int id);
}
