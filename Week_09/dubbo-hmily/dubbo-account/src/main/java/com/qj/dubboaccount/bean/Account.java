package com.qj.week9.dubboaccount.bean;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;


@Data
public class Account {

    private Long id;

    private String userId;

    private BigDecimal balance;

    private Integer ready;

    private Date createTime;

    private Date updateTime;
}
