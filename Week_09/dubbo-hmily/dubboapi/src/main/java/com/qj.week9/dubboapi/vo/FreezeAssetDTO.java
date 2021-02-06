package com.qj.week9.dubboapi.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;


@Data
public class FreezeAssetDTO implements Serializable {

    private static final long serialVersionUID = -8824473170775874165L;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 账户ID
     */
    private Long accountId;

    /**
     * 金额
     */
    private BigDecimal amount;
}
