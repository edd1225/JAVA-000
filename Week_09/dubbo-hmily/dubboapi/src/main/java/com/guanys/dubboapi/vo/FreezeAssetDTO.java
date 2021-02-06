package com.guanys.dubboapi.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @PROJECT_NAME: dubboapi
 * @PACKAGE_NAME: com.guanys.dubboapi.vo
 * @NAME: FreezeAssetDTO
 * @Author: guanys
 * @DATE: 12/17/20:4:55 PM
 * @Description: TODO
 * @Version: 1.0
 **/
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
