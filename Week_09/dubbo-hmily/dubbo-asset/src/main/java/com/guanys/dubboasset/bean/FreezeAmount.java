package com.guanys.dubboasset.bean;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @PROJECT_NAME: dubbo-asset
 * @PACKAGE_NAME: com.guanys.dubboasset.bean
 * @NAME: FreezeAmount
 * @Author: guanys
 * @DATE: 12/18/20:10:28 AM
 * @Description: TODO
 * @Version: 1.0
 **/
@Data
public class FreezeAmount {
    private Long id;

    private String userId;

    private Long accountId;

    private BigDecimal freezeAmount;

    private Date updateTime;

}
