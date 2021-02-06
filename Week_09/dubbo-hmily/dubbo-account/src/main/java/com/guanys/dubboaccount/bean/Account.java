package com.guanys.dubboaccount.bean;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @PROJECT_NAME: dubbo-account
 * @PACKAGE_NAME: com.guanys.dubboaccount.bean
 * @NAME: Account
 * @Author: guanys
 * @DATE: 12/17/20:5:15 PM
 * @Description: TODO
 * @Version: 1.0
 **/
@Data
public class Account {

    private Long id;

    private String userId;

    private BigDecimal balance;

    private Integer ready;

    private Date createTime;

    private Date updateTime;
}
