package com.qj.week9.dubboapi.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;


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
