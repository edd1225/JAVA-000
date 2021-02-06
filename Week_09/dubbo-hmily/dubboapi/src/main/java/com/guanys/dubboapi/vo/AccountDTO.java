package com.guanys.dubboapi.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

/**
 * @PROJECT_NAME: dubboapi
 * @PACKAGE_NAME: com.guanys.dubboapi.vo
 * @NAME: AccountDTO
 * @Author: guanys
 * @DATE: 12/17/20:4:28 PM
 * @Description: TODO
 * @Version: 1.0
 **/
@Data
public class AccountDTO implements Serializable {


    private static final long serialVersionUID = -3609583274142686455L;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 扣款金额
     */
    private BigDecimal amount;

    /**
     * 单位
     */
    private String unit;

}
