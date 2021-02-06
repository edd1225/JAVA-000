package com.qj.week9.dubboasset.bean;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;


@Data
public class FreezeAmount {
    private Long id;

    private String userId;

    private Long accountId;

    private BigDecimal freezeAmount;

    private Date updateTime;

}
