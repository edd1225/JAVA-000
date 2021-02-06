# #docker 快速搭建 rocketmq 环境

![rocket](https://img2018.cnblogs.com/blog/746311/201909/746311-20190918222323896-1658673482.png)

### 1. 安装 Namesrv

#### 拉取镜像

```
docker pull rocketmqinc/rocketmq:4.4.0`
```

#### 启动容器

```shell
docker run -d -p 9876:9876 -v {RmHome}/data/namesrv/logs:/root/logs -v {RmHome}/data/namesrv/store:/root/store --name rmqnamesrv -e "MAX_POSSIBLE_HEAP=100000000" rocketmqinc/rocketmq:4.4.0 sh mqnamesrv
```

#### 注意事项

>  **{RmHome}** 要替换成你的宿主机想保存 MQ 的日志与数据的地方，通过 docker 的 -v 参数使用 volume 功能，把你本地的目录映射到容器内的目录上。否则所有数据都默认保存在容器运行时的内存中，重启之后就又回到最初的起点。

### 2. 安装 broker 服务器

#### 拉取镜像

与上步是同一个镜像，如果上步完成，此步无需拉取

#### 创建 broker.conf 文件

1. 在 {RmHome}/conf 目录下创建 broker.conf 文件
2. 在 broker.conf 中写入如下内容

```shell
brokerClusterName = DefaultCluster
brokerName = broker-a
brokerId = 0
deleteWhen = 04
fileReservedTime = 48
brokerRole = ASYNC_MASTER
flushDiskType = ASYNC_FLUSH
brokerIP1 = {本地外网 IP}
```

**brokerIP1 要修改成你自己宿主机的 IP**

#### 启动容器

```shell
docker run -d -p 10911:10911 -p 10909:10909 -v  {RmHome}/data/broker/logs:/root/logs -v  {RmHome}/rocketmq/data/broker/store:/root/store -v  {RmHome}/conf/broker.conf:/opt/rocketmq-4.4.0/conf/broker.conf --name rmqbroker --link rmqnamesrv:namesrv -e "NAMESRV_ADDR=namesrv:9876" -e "MAX_POSSIBLE_HEAP=200000000" rocketmqinc/rocketmq:4.4.0 sh mqbroker -c /opt/rocketmq-4.4.0/conf/broker.conf
```

#### 注意事项

注意: **{RmHome}** 同上步一样，不再缀述。broker.conf 的文件中的 brokerIP1 是你的 broker 注册到 Namesrv 中的 ip。如果不指定他会默认取容器中的内网 IP。除非你的应用也同时部署在网络相通的容器中，本地或容器外就无法连接 broker 服务了，进而导致类似 RemotingTooMuchRequestException 等各种异常。

### 3. 安装 rocketmq 控制台

#### 拉取镜像

```
docker pull pangliang/rocketmq-console-ng
```

#### 启动容器

```
docker run -e "JAVA_OPTS=-Drocketmq.namesrv.addr={本地外网 IP}:9876 -Dcom.rocketmq.sendMessageWithVIPChannel=false" -p 8080:8080 -t pangliang/rocketmq-console-ng
```

### 其它

#### 参考文档

1.  主要参考这位博主的文章，写的很好，但是实际安装时遇到几个坑，在此基础上优化一版。加了一 broker.conf 配置，以及换了 rocketmq-console-ng 的镜像源，原文的有点老，有些功能不支持。
2. [rocketmq 连接不上 broker 问题解决](https://blog.csdn.net/huang_550/article/details/90693656)

