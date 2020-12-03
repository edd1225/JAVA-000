package com.qj.week7.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qj.week7.entity.User;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @ClassName UserMapper
 * @Author edd1225 
 * @Version V1.0
 **/

public interface UserMapper extends BaseMapper<User> {

    /**
     * 查询所有
     *
     * @return
     */
    @Select("SELECT * FROM user")
    List<User> userList();

    /**
     * 分页查询
     *
     * @return
     */
    @Select("SELECT * FROM user limit 0,2")
    List<User> userListPage();

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
    List<User> userOrderBy();

    /**
     * get by id
     *
     * @return
     */
    @Select("SELECT * FROM user where id =#{id} ")
    List<User> getByUserId(Long id);
}
