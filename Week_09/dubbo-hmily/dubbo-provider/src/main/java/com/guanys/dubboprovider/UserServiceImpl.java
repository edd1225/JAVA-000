package com.guanys.dubboprovider;

import com.guanys.dubboapi.bean.User;
import com.guanys.dubboapi.service.UserService;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @PROJECT_NAME: dubbo-provider
 * @PACKAGE_NAME: com.guanys.dubboprovider
 * @NAME: UserServiceImpl
 * @Author: guanys
 * @DATE: 12/18/20:11:04 AM
 * @Description: TODO
 * @Version: 1.0
 **/
@DubboService(version = "1.0.0")
public class UserServiceImpl implements UserService {

    @Override
    public User findById(int id) {
        return new User(id, "cuicui" + System.currentTimeMillis());
    }
}
