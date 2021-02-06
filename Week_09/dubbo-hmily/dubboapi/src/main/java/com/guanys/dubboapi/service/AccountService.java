package com.guanys.dubboapi.service;

import com.guanys.dubboapi.vo.AccountDTO;
import org.dromara.hmily.annotation.Hmily;

public interface AccountService {

    @Hmily
    boolean exchange(AccountDTO accountDTO);
}
