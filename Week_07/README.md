# 第七周学习笔记
***
## 作业一
### 读写分离 - 动态 切换数据源版本 1.0
&ensp;&ensp;&ensp;&ensp;完成情况说明：

- 配置了三个数据库：1主2从
  - [MySQL docker化配置记录](./MySQLDocker化主从配置.md)
- service中的insert注入主库，query注入从库
- 使用注解实现不同数据库源的注入
- 简单的实现了从库访问的负载均衡

&ensp;&ensp;&ensp;&ensp;作业详情：

- 作业详细代码地址：https://github.com/edd1225/JAVA-000/tree/main/code/src/main/java/com/example/demo/database
- 测试运行代码：https://github.com/edd1225/JAVA-000/tree/main/code/src/test/java/com/example/demo/database/service/OrderServiceImplTest.java

## 作业二
### 读写分离 - 数据库框架版本 2.0
&ensp;&ensp;&ensp;&ensp;简单的直接使用配置文件配置，在测试代码中注入后直接使用SQL进行执行，没有结合ORM
ßßßß
- 代码地址：https://github.com/edd1225/JAVA-000/tree/main/shardingsphere
  - 测试类：ShardingSphere1Test
  - 直接运行测试类中的代码，通过日志可以看到slave和master的切换

### 参考链接
- [USE SPRING BOOT STARTER](https://shardingsphere.apache.org/document/current/en/user-manual/shardingsphere-jdbc/usage/sharding/spring-boot-starter/)
- [READ-WRITE SPLITTING](https://shardingsphere.apache.org/document/legacy/3.x/document/en/manual/sharding-jdbc/usage/read-write-splitting/)
- [ShardingSphere入门实战(1)-Sharding-JDBC使用](https://www.cnblogs.com/wuyongyin/p/13336373.html)

## 作业三
### 按自己设计的表结构，插入 100 万订单模拟数据，测试不同方式的插入效率
&ensp;&ensp;&ensp;&ensp;测试了两种，一个是关闭自动提交，一百万的数据弄好后一起提交；一种是一条一条的提交插入

&ensp;&ensp;&ensp;&ensp;通过测试，批量的插入是非常快的；下面的两个测试都写了存储过程，脚本在下方链接：

- [dataTest.sql](./dataTest.sql)

- 关闭自动提交，一百万的数据弄好后一起提交：completed in 2 m 29 s 195 ms
- 一条一条的提交插入：之前测试过插入10万的，要差一个多小时左右，这里只知道会很久吧，就不测具体数据了