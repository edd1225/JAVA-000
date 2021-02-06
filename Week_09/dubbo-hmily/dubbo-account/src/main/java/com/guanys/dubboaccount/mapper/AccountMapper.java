package com.guanys.dubboaccount.mapper;

import com.guanys.dubboapi.vo.AccountDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

/**
 * @PROJECT_NAME: dubbo-account
 * @PACKAGE_NAME: com.guanys.dubboaccount.mapper
 * @NAME: AccountMapper
 * @Author: guanys
 * @DATE: 12/18/20:9:41 AM
 * @Description: TODO
 * @Version: 1.0
 **/
@Mapper
@Repository
public interface AccountMapper {

    @Update("update account set balance = balance - #{amount}," +
            " update_time = now()" +
            " where user_id =#{userId}  and  balance > 0  ")
    int update(AccountDTO accountDTO);

    @Update("update account set ready=1 where user_id =#{userId}  and ready=0 ")
    int confirm(AccountDTO accountDTO);

    @Update("update account set balance = balance + #{amount} where user_id =#{userId} and ready=0")
    int cancel(AccountDTO accountDTO);
}
