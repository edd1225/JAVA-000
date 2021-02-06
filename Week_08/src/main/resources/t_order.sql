CREATE TABLE IF NOT EXISTS `t_order` (
  `order_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `user_id` bigint(20) unsigned NOT NULL COMMENT '用户ID',
  `goods_id` bigint(20) unsigned NOT NULL COMMENT '商品ID',
  `total_amount` bigint(20) unsigned DEFAULT NULL COMMENT '订单金额',
  `status` tinyint(1) DEFAULT NULL COMMENT '订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单',
  `create_time` bigint(20) DEFAULT NULL COMMENT '下单时间',
  `update_time` bigint(20) DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='订单表';