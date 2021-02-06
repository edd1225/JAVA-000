package com.guanys.dubboapi.service;

import com.guanys.dubboapi.bean.User;

public interface UserService {

    User findById(int id);
}
