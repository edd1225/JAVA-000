-- # 插入数据到订单表中，关闭自动提交，批量插入
DROP PROCEDURE IF EXISTS orders_initData;
DELIMITER $
CREATE PROCEDURE orders_initData()
BEGIN
    DECLARE i INT DEFAULT 1;
    set autocommit=0;
    WHILE i<=1000000 DO
            insert into test.orders (user_id, commodities, status, deliver_status, total_price, create_time, update_time)
            VALUES (CEILING(rand()*100), '{"key": "value"}', 0, '{"key":"value"}', 1, unix_timestamp(now()) , unix_timestamp(now()));
        SET i = i+1;
    END WHILE;
    commit;
END $
CALL orders_initData();

-- # 插入数据到订单表中，自动提交，一条一条的插入
DROP PROCEDURE IF EXISTS orders_initData_one;
DELIMITER $
CREATE PROCEDURE orders_initData_one()
BEGIN
    DECLARE i INT DEFAULT 1;
    WHILE i<=1000000 DO
            insert into test.orders (user_id, commodities, status, deliver_status, total_price, create_time, update_time)
            VALUES (CEILING(rand()*100), '{"key": "value"}', 0, '{"key":"value"}', 1, unix_timestamp(now()) , unix_timestamp(now()));
        SET i = i+1;
    END WHILE;
END $
CALL orders_initData_one();

