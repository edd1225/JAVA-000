package com.mayikt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mayikt.entity.MayiktUser;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @ClassName UserMapper
 * @Author 蚂蚁课堂余胜军 QQ644064779 www.mayikt.com
 * @Version V1.0
 **/

public interface UserMapper extends BaseMapper<User> {

    /**
     * 查询所有
     *
     * @return
     */
    @Select("SELECT * FROM user")
    List<MayiktUser> userList();

    /**
     * 分页查询
     *
     * @return
     */
    @Select("SELECT * FROM user limit 0,2")
    List<MayiktUser> userListPage();

    /**
     * user_0 2,4 user_1 1,3
     *2 4  1 3 limit 2 24
     */

    /**
     * 排序
     *
     * @return
     */
    @Select("SELECT * FROM user order by id desc ")
    List<MayiktUser> userOrderBy();

    /**
     * get by id
     *
     * @return
     */
    @Select("SELECT * FROM user where id =#{id} ")
    List<MayiktUser> getByUserId(Long id);
}
