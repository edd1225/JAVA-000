#  docker部署pulsar多节点集群

**目录：**

**(1).docker部署bpulsar-zookeeper节点**

**(2).initial pulsar集群**

**(3).docker部署bookie节点**

**(4).docker部署broker节点并附带启用pulsar-connectors**

**(5).docker部署pulsar-manager**

**5.1.部署pulsar-manager**

**5.2.增加超级管理员用户**

**5.3.增加普通用户**

**5.4.使用pulsar-manager创建测试用环境

**(6).使用pulsar-data-generator-connector生成测试数据测试集群**

**(7).pulsar-connector初探**

**(8).docker部署查询引擎pulsar-presto**

**(9).使用pulsar-presto**

**(10).docker部署sqlpad与简单使用**

**(11). 其他**

**(12). 参考文章**





### 集群部署顺序：

```
zookeeper>bookkeeper>broker
docker pull apachepulsar/pulsar-all:2.7.0
```

==本文意图:==

>  通过深入剖析安装过程中遇到的各种问题,来进一步理解 pulsar 集群的原理与运行机制 。 使用 docker 一方面可以节省大量工作量 ， 另一方面可以为后续的kubernetes 化做准备 。 也为了能更好的使用 / 使用对 pulsar。 


## (1).docker部署pulsar-zookeeper节点  

```
mkdir -p /root/docker-local/docker-local-pulsar-zookeeper/conf

mkdir -p /root/docker-local/docker-local-pulsar-zookeeper/data/zookeeper
```



需要下载相关配置文件：放到上边新建的对应目录中

```cp https://github.com/apache/pulsar/blob/v2.7.0/conf/zookeeper.conf /root/docker-local/docker-local-pulsar-zookeeper/conf

```



```
docker run -it -d -p 2181:2181 -p 9990:9990 -p 18000:8000 -v /root/docker-local/docker-local-pulsar-zookeeper/conf/zookeeper.conf:/pulsar/conf/zookeeper.conf -v /root/docker-local/docker-local-pulsar-zookeeper/data/zookeeper:/pulsar/data/zookeeper --name zookeeper apachepulsar/pulsar-all:2.7.0 /pulsar/bin/pulsar zookeeper
```



8000端口：http metrics

07:31:17.870 [main] INFO org.apache.pulsar.zookeeper.ZooKeeperStarter - Starting ZK stats HTTP server at port 8000

zookeeper的metrics在zk启动命令中开启4字命令



2181端口：

07:31:18.137 [main] INFO org.apache.zookeeper.server.quorum.QuorumPeerConfig - clientPortAddress is 0.0.0.0:2181



9990端口：

07:31:18.340 [main] INFO org.eclipse.jetty.server.AbstractConnector - Started ServerConnector@2c95ac9e{HTTP/1.1, (http/1.1)}{0.0.0.0:9990}

##  (2).initial pulsar集群



```
docker run -it --link zookeeper apachepulsar/pulsar-all:2.7.0 /pulsar/bin/pulsar initialize-cluster-metadata --cluster pulsarClusterInDocker --zookeeper zookeeper:2181 --configuration-store zookeeper:2181 --web-service-url http://broker:8080 --broker-service-url pulsar://broker:6650
```

> --cluster pulsarClusterInDocker
>
> 指定要初始化的pulsar集群名称，后边启动broker时要在broker的配置文件中写这个名称。
>

> --zookeeper zookeeper:2181
>
> 指定broker的注册中心
>

> --configuration-store zookeeper:2181
>
> 指定store(bookie)层的注册中心
>

> --web-service-url http://broker:8080
>
> 指定broker的通信端口，后边启动broker的端口要和这里保持一致

 ## (3).docker部署bookie节点



```
mkdir -p /root/docker-local/docker-local-pulsar-bookkeeper/conf

mkdir -p /root/docker-local/docker-local-pulsar-bookkeeper/data
```

需要下载相关配置文件：放到上边新建的对应目录中

```
cp https://github.com/apache/pulsar/blob/v2.7.0/conf/bookkeeper.conf /root/docker-local/docker-local-pulsar-bookkeeper/conf
```

> 修改zookeeper地址：
>
> zkServers=zookeeper:2181
>

```
docker run -it -d -p 3181:3181 -p 8000:8000 -v /root/docker-local/docker-local-pulsar-bookkeeper/conf/bookkeeper.conf:/pulsar/conf/bookkeeper.conf -v /root/docker-local/docker-local-pulsar-bookkeeper/data:/pulsar/data --name bookkeeper --link zookeeper apachepulsar/pulsar-all:2.7.0 /pulsar/bin/pulsar bookie
```

> 端口3181：
>
> "bookiePort" : "3181"
>

> 端口8000：
>
> "httpServerPort" : "8000"
>
> "prometheusStatsHttpPort" : "8000"

##(4).docker部署broker节点 并附带启用  pulsar-connectors

```
mkdir -p /root/docker-local/docker-local-pulsar-broker/conf

mkdir -p /root/docker-local/docker-local-pulsar-broker/conf/offloaders
```

需要下载相关配置文件：放到上边新建的对应目录中

```
cp https://github.com/apache/pulsar/blob/v2.7.0/conf/broker.conf /root/docker-local/docker-local-pulsar-broker/conf

cp https://github.com/apache/pulsar/blob/v2.7.0/conf/functions_worker.yml /root/docker-local/docker-local-pulsar-broker/conf
```

### A1.修改broker.conf

修改zk配置：

```shell
Zookeeper quorum connection string

zookeeperServers=zookeeper:2181

# Configuration Store connection string

configurationStoreServers=zookeeper:2181

配置cluster name，否则无法启动broker：

# Name of the cluster to which this broker belongs to

clusterName=pulsarClusterInDocker

# Port to use to server HTTP request

webServicePort=8080

# Broker data port

brokerServicePort=6650

# Enable Functions Worker Service in Broker

#开启pulsar functions, 让functions和broker一起运行

functionsWorkerEnabled=true

```




### A2.修改functions_worker.yml

启用pulsar的connectors：
```shell
workerId: pulsarClusterInDocker
pulsarFunctionsCluster: pulsarClusterInDocker
# Configuration Store connection string
configurationStoreServers: zookeeper:2181
```


```shell
docker run -it -d -p 6650:6650 -p 8080:8080 -v /root/docker-local/docker-local-pulsar-broker/conf/broker.conf:/pulsar/conf/broker.conf -v /root/docker-local/docker-local-pulsar-broker/conf/functions_worker.yml:/pulsar/conf/functions_worker.yml -v /root/docker-local/docker-local-pulsar-broker/conf/offloaders:/pulsar/conf/offloaders --name broker --link zookeeper apachepulsar/pulsar-all:2.7.0 /pulsar/bin/pulsar broker
```

上面有错：

这个问题很坑，参见一个官方issue：

Pulsar broker start failure when set functionsWorkerEnabled=true

https://github.com/apache/pulsar/issues/2328



我的做法是杀掉并rm掉所有pulsar容器，然后清除zk数据重装一遍：

清除zk数据：

> rm -rf docker-local-pulsar-bookkeeper/data/bookkeeper/*
>
> rm -rf docker-local-pulsar-zookeeper/data/zookeeper/version-2/*

重新来过，broker日志中可以看到public/functions成功创建： 但是broker依然启动失败：bookie数量不足

启动第二个bookie容器：

先建立data目录:

> mkdir -p /root/docker-local/docker-local-pulsar-bookkeeper-2/data

容器化第二个bookie：

```
docker run -it -d -p 23181:3181 -p 28000:8000 -v /root/docker-local/docker-local-pulsar-bookkeeper/conf/bookkeeper.conf:/pulsar/conf/bookkeeper.conf -v /root/docker-local/docker-local-pulsar-bookkeeper-2/data:/pulsar/data --name bookkeeper-2 --link zookeeper apachepulsar/pulsar-all:2.7.0 /pulsar/bin/pulsar bookie
```

再重新启动broker,成功。

需要注意的是，因为只有1个broker，

> 端口6650：
>
> brokerServicePort=6650
>
> 端口8080：
>
> path:/loadbalance/brokers/c8a6f5ec1195:8080

## (5).docker部署pulsar-manager

###  5.1.部署pulsar-manager**

```
mkdir -p /root/docker-local/docker-local-pulsar-manager/conf
```

下载bkvm.conf文件到制定目录：

```
cp https://github.com/apache/pulsar-manager/blob/master/src/main/resources/bkvm.conf /root/docker-local/docker-local-pulsar-manager/conf
```

下载application.properties文件到制定目录：

```
cp https://github.com/apache/pulsar-manager/blob/master/src/main/resources/application.properties /root/docker-local/docker-local-pulsar-manager/conf
```

修改bkvm.conf：

```
Enable bookkeeper visual manager(Optional)：
bkvm.enabled=true
\# BookKeeper Connection
\# Default value zk+null://127.0.0.1:2181/ledgers works for Pulsar Standalone
metadataServiceUri=zk+null://zookeeper:2181/ledgers
```

修改application.properties：

```
support peek message, default false：
pulsar.peek.message=true
```

启动pulsar-manager：

```
docker run -it -d -p 9527:9527 -p 7750:7750 -e SPRING_CONFIGURATION_FILE=/pulsar-manager/pulsar-manager/application.properties -v /root/docker-local/docker-local-pulsar-manager/conf/bkvm.conf:/pulsar-manager/pulsar-manager/bkvm.conf -v /root/docker-local/docker-local-pulsar-manager/conf/application.properties:/pulsar-manager/pulsar-manager/application.properties --link broker --link zookeeper --name pulsar-manager apachepulsar/pulsar-manager
```

登陆pulsar-manager：

http:/ip:9527/

### 5.2.增加超级管理员用户 



增加超级管理员用户和密码：

```
CSRF_TOKEN=$(curl http://127.0.0.1:7750/pulsar-manager/csrf-token)

curl   -H "X-XSRF-TOKEN: $CSRF_TOKEN"   -H "Cookie: XSRF-TOKEN=$CSRF_TOKEN;"   -H 'Content-Type: application/json'   -X PUT http://127.0.0.1:7750/pulsar-manager/users/superuser   -d '{"name": "admin", "password": "apachepulsar", "description": "test", "email": "username@test.org"}'
```



超级管理员新增参考官方文档：特别注意字符问题：

https://github.com/apache/pulsar-manager#access-pulsar-manager

### 5.3.增加普通用户

```
CSRF_TOKEN=$(curl http://127.0.0.1:7750/pulsar-manager/csrf-token)

curl   -H "X-XSRF-TOKEN: $CSRF_TOKEN"   -H "Cookie: XSRF-TOKEN=$CSRF_TOKEN;"   -H 'Content-Type: application/json'   -X PUT http://127.0.0.1:17750/pulsar-manager/users/user   -d '{"name": "hepengyuan", "password": "hepengyuan", "description": "test", "email": "hepengyuan@test.org"}'
```

提示失败，后续有时间的话研究一下 ==备注一下哦==





### 有两个web后台：

pulsar-manager后台：

http://ipx:9527/#/environments

bkvm后台：需要在application.properties配置文件中开启

http://ipy:7750/bkvm

### 5.4.使用pulsar-manager创建测试用环境

访问pulsar-manager：http://ip:19527/#/environments

用户名：admin

密码：apachepulsar

创建环境：test

## (6).使用pulsar-data-generator-connector生成测试数据测试集群

找到broker:

```
docker ps -a | grep -i pulsar | grep  -i broker
```

进入broker：docker exec -it 428a7a88df0c /bin/sh

执行pulsar-data-generator-connector生成测试数据：

```
./bin/pulsar-admin --admin-url http://127.0.0.1:8080/ sources create --name generator --destinationTopicName generator_test --source-type data-generator --tenant public --namespace default
```

可以在pulsar-manager中可以看到：一直在不断的发送测试消息。

列出正在运行的connector：可以看到刚才创建的generator connector在运行列表中：

```
./bin/pulsar-admin sources list
```

这个generator就是之前跑的**pulsar-data-generator-connector。**

也可以使用命令停止此  test generate：

```
./bin/pulsar-admin --admin-url http://127.0.0.1:8080/ sources stop --name generator
```

pulsar-manager中验证stop：In Rate已经变为0。

用list查看还能看到，因为只是暂停，不是删除

```
./bin/pulsar-admin sources list
```

删除connector使用命令：

```
./bin/pulsar-admin --admin-url http://127.0.0.1:8080/ sources delete --name generator
```

## (7).pulsar-connector初探

找到broker:

```
docker ps -a | grep -i pulsar | grep  -i broker
```

进入broker：docker exec -it 428a7a88df0c /bin/sh

常用命令：

列出所有可用的connector:

```
./bin/pulsar-admin sources available-sources
```

可以看到，pulsar对于大数据的支持还是不错的。



还有很多命令，通过如下命令可以查看用法：

```
./bin/pulsar-admin --admin-url http://127.0.0.1:8080/ sources
```

##（8).docker部署查询引擎pulsar-presto

pulsar-presto可以让我们用sql语法去查询数据。

```
mkdir -p /root/docker-local/docker-local-pulsar-presto/conf/presto/catalog
```

```
cp https://github.com/apache/pulsar/blob/master/conf/presto/catalog/pulsar.properties /root/docker-local/docker-local-pulsar-presto/conf/presto/catalog
```

> 修改pulsar.properties：
>
> \#the url of Pulsar broker service
>
> \#如果多个broker，用,分隔
>
> pulsar.broker-service-url=http://broker:8080
>
> \#URI of Zookeeper cluster
>
> \#如果多个zk，用,分隔
>
> pulsar.zookeeper-uri=zookeeper:2181

```
docker run -it -d -p 8081:8081 -v /root/docker-local/docker-local-pulsar-presto/conf/presto/catalog/pulsar.properties:/pulsar/conf/presto/catalog/pulsar.properties --name sql-worker --link broker --link zookeeper apachepulsar/pulsar-all:2.7.0 /pulsar/bin/pulsar sql-worker run


```

启动sql-worker有可能会遇到问题：

```
/usr/bin/docker-current: Error response from daemon: oci runtime error: container_linux.go:235: starting container process caused "container init exited prematurely".
```

解决方式：

```
a1.卸载旧版本 docker
yum remove docker docker-common docker-selinux dockesr-engine -y

a2.升级系统软件
yum upgrade -y

a3.安装必要的一些系统工具
sudo yum install -y yum-utils device-mapper-persistent-data lvm2

a4.添加软件源信息
yum-config-manager --add-repo https://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo

a5.更新并安装 docker-ce
yum makecache fast yum install docker-ce -y

a6.启动服务
systemctl daemon-reload systemctl restart docker
```

## (9).使用pulsar-presto



```
mkdir -p /root/docker-local/docker-local-pulsar-sql/bin
```

```
cp https://github.com/apache/pulsar/blob/v2.7.0/bin/pulsar /root/docker-local/docker-local-pulsar-sql/bin
```

记得要赋权：

```
chmod 755 pulsar
```

修改357行左右：

```
exec $JAVA -cp "${PRESTO_HOME}/lib/*" io.prestosql.cli.Presto --server sql-worker:8081 "${@}"
```
进入命令行：
```
docker run -it --name sql --link sql-worker -v /root/docker-local/docker-local-pulsar-sql/bin/pulsar:/pulsar/bin/pulsar apachepulsar/pulsar-all:2.7.0 /pulsar/bin/pulsar sql
```

查看

```
show catalogs
show schemas in pulsar;

#为了查询消息，我们启动test data generator：

./bin/pulsar-admin --admin-url http://127.0.0.1:8080/ sources create --name generator --destinationTopicName generator_test --source-type data-generator --tenant public --namespace default



show tables in pulsar."public/default";
select * from pulsar."public/default".generator_test;
select * from pulsar."public/default".generator_test limit 1\G
```

## (10).docker部署sqlpad与简单使用

相关的官方地址：

https://hub.docker.com/r/sqlpad/sqlpad

https://github.com/sqlpad/sqlpad

```
docker pull sqlpad/sqlpad:latest

docker run -it -d --name sqlpad -p 3000:3000 sqlpad/sqlpad --admin admin@example.com --passphrase StrongPassphrase
```



> 注册管理员：
>
> http://ip:3000/signup
>
> 输入：user和pwd要和之前docker命令里的user/pwd保持一致，否则注册失败，提示forbidden。
>
> user：admin@example.com
>
> pwd：StrongPassphrase

> 然后登陆：
>
> http://ip:3000/signin



ip要写presto(即sql-worker)所在的ip和port：

简单使用：

```
show catalogs
show schemas in pulsar;
show tables in pulsar."public/default";
select * from pulsar."public/default".generator_test; #这个命令悠着用，要加limit，否则按照10000条来差，容易出毛病。
select * from pulsar."public/default".generator_test limit 1;
```



##  (11).其他



如果要清除数据进行重装，需要执行下述命令清理数据：

```
rm -rf docker-local-pulsar-bookkeeper/data/bookkeeper/*

rm -rf docker-local-pulsar-zookeeper/data/zookeeper/version-2/*
```

## (12).参考文章

1.部署和管理 functions-worker

https://pulsar.apache.org/docs/zh-TW/next/functions-worker/

2.使用 Pulsar SQL 查詢資料

http://pulsar.apache.org/docs/zh-TW/next/sql-getting-started/

3.Pulsar SQL 配置和部署：

http://pulsar.apache.org/docs/zh-TW/next/sql-deployment-configurations/

4.安装SQLPad：用于MySQL/PostgreSQL/SQL Server的基于Web的SQL编辑器

https://ywnz.com/linuxysjk/4213.html

5.pulsar 使用笔记

https://www.jianshu.com/p/dd328bdd2a32

6.pulsar2.2.0安装步骤

http://news.migage.com/articles/pulsar220%E5%AE%89%E8%A3%85%E6%AD%A5%E9%AA%A4_1135775_csdn.html

7.Pulsar Connector 预览篇

https://zhuanlan.zhihu.com/p/74217687