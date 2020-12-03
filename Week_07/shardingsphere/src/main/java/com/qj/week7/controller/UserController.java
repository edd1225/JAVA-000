package com.mayikt.controller;

import com.mayikt.entity.MayiktUser;
import com.mayikt.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ClassName UserController
 * @Author edd1225
 * @Version V1.0
 **/
@RestController
public class UserController {
    @Autowired
    private UserMapper userMapper;


    /**
     * 查询所有
     *
     * @return
     */
    @RequestMapping("/userList")
    public List<User> userList() {
        return userMapper.userList();
    }

    /**
     * 分页查询
     *
     * @return
     */
    @RequestMapping("/userListPage")
    public List<User> userListPage() {
        return userMapper.userListPage();
    }

    /**
     * 排序
     *
     * @return
     */
    @RequestMapping("/userOrderBy")
    public List<User> userOrderBy() {
        return userMapper.userOrderBy();
    }

    @RequestMapping("/getByUserId")
    public List<User> getByUserId(Long id) {
        return userMapper.getByUserId(id);
    }

    @RequestMapping("/insertUser")
    public String insertUser() {
        for (int i = 1; i < 10; i++) {
            User user = new User(i, "mayikt" + i, i);
            try {
               userMapper.insert(user);
            } catch (Exception e) {

            }

        }
        return "success";
    }
}
