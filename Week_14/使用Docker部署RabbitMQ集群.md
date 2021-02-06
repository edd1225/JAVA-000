#使用Docker部署RabbitMQ集群

## 下载RabbitMQ镜像

**1.镜像地址**

RabbitMQ Docker官方认证镜像地址：https://dev.aliyun.com/detail.html?spm=5176.1971733.2.16.6c045aaaDxFoMn&repoId=1256

**2.安装命令**

安装之前，切记把Docker Hub设置为阿里云的加速，方便安装。

> docker pull rabbitmq:3.6.15-management

注意使用后缀为"-management"的镜像版本，是包含网页控制台的。

**3.查看安装**

使用命令：docker images查看下载的镜像，如下图所示：

![img](http://icdn.apigo.cn/blog/rabbitmq-images.png)

## Docker常用命令

容器停止：docker stop 容器名称
       启动容器：docker start 容器名称
		删除容器：docker rm 容器名称
		删除镜像：docker rmi 镜像名称
		查看运行的所有容器：docker ps
		查看所有容器：docker ps -a
		容器复制文件到物理机：docker cp 容器名称:容器目录 物理机目录
		物理机复制文件到容器：docker cp 物理机目录 容器名称:容器目录

## 进入Docker目录

简单的进入Docker容器的方法分为3种：

1. 使用attach
2. 使用SSH
3. 使用exec

### 1.attach

attach有一个缺点，当连接终止的时候，或者使用exit之后，容器就会退出后台运行，所以不适合生产环境使用。既然不好用，我们在这里就不过多的介绍它了。

### 2.SSH

按照之前我们使用liunx的习惯，使用ssh连接服务器似乎是一个很诱人的答案，但这样做并不优雅，也不是最佳的实践方式，详情点击查看：https://blog.csdn.net/bolg_hero/article/details/50267103

### 3.exec

exec无疑是我们现阶段最好的实践方案，一起来看它是怎么使用的。

**进入docker命令：**

> docker exec -it /bin/bash

其中/bin/bash 也可能是/bin/sh.

使用docker exec --help查看更多命令，和命令说明。

**退出docker：**

> exit

## 启动RabbitMQ

> docker run -d --hostname localhost --name myrabbit -p 15672:15672 -p 5672:5672 rabbitmq:3.6.15-management

参数说明：

- -d 后台进程运行
- hostname RabbitMQ主机名称
- name 容器名称
- -p port:port 本地端口:容器端口
- -p 15672:15672 http访问端口
- -p 5672:5672 amqp访问端口

启动完成之后，使用：docker ps 查看程序运行情况。

使用：[http://宿主ip:15672](http://xn--ip-wz2c754c:15672/) 访问，用户名密码使用默认：guest/guest.

## 启动多个RabbitMQ

- docker run -d --hostname localhost --name myrabbit -p 15672:15672 -p 5672:5672 rabbitmq:3.6.15-management
- docker run -d --hostname localhost2 --name myrabbit2 -p 15673:15672 -p 5673:5672 rabbitmq:3.6.15-management

这样我们就可以使用，[http://宿主ip:15672](http://xn--ip-wz2c754c:15672/) 和 [http://宿主ip:15673](http://xn--ip-wz2c754c:15673/) 进行访问了，默认账号密码依旧是guest/guest.

## 搭建RabbitMQ集群

步骤一：安装RabbitMQ；

步骤二：加入RabbitMQ节点到集群；

### 步骤一：安装RabbitMQ 三台机子

> docker run -d --hostname rabbit1 --name myrabbit1 -p 15672:15672 -p 5672:5672 -e RABBITMQ_ERLANG_COOKIE='rabbitcookie' rabbitmq:3.6.15-management

> docker run -d --hostname rabbit2 --name myrabbit2 -p 5673:5672 --link myrabbit1:rabbit1 -e RABBITMQ_ERLANG_COOKIE='rabbitcookie' rabbitmq:3.6.15-management

> docker run -d --hostname rabbit3 --name myrabbit3 -p 5674:5672 --link myrabbit1:rabbit1 --link myrabbit2:rabbit2 -e RABBITMQ_ERLANG_COOKIE='rabbitcookie' rabbitmq:3.6.15-management

具体的参数含义，参见上文“启动RabbitMQ”部分。

**注意点：**

1. 多个容器之间使用“--link”连接，此属性不能少；
2. Erlang Cookie值必须相同，也就是RABBITMQ_ERLANG_COOKIE参数的值必须相同，原因见下文“配置相同Erlang Cookie”部分；

### 步骤二：加入RabbitMQ节点到集群

**设置节点1：**

 ```
 docker exec -it myrabbit1 bash
 rabbitmqctl stop_app 
 rabbitmqctl reset
 rabbitmqctl start_app
 exit
 ```


**设置节点2，加入到集群：**

```
docker exec -it myrabbit2 bash
rabbitmqctl stop_app
rabbitmqctl reset
rabbitmqctl join_cluster --ram rabbit@rabbit1
rabbitmqctl start_app
exit
```

参数“--ram”表示设置为内存节点，忽略次参数默认为磁盘节点。

**设置节点3，加入到集群：**

 ```
docker exec -it myrabbit3 bash
rabbitmqctl stop_app
rabbitmqctl reset
rabbitmqctl join_cluster --ram rabbit@rabbit1
rabbitmqctl start_app
exit
 ```



设置好之后，使用http://物理机ip:15672 进行访问了，默认账号密码是guest/guest，效果如下图：

![img](http://icdn.apigo.cn/blog/rabbitmq-cluster.png)

启动了3个节点，1个磁盘节点和2个内存节点。

### 配置相同Erlang Cookie

有些特殊的情况，比如已经运行了一段时间的几个单个物理机，我们在之前没有设置过相同的Erlang Cookie值，现在我们要把单个的物理机部署成集群，实现我们需要同步Erlang的Cookie值。

> 1.为什么要配置相同的erlang cookie？

因为RabbitMQ是用Erlang实现的，Erlang Cookie相当于不同节点之间相互通讯的秘钥，Erlang节点通过交换Erlang Cookie获得认证。

> 2.Erlang Cookie的位置

要想知道Erlang Cookie位置，首先要取得RabbitMQ启动日志里面的home dir路径，作为根路径。使用：“docker logs 容器名称”查看，如下图：

![img](http://icdn.apigo.cn/blog/rabbitmq-homedir.png)

所以Erlang Cookie的全部路径就是“/var/lib/rabbitmq/.erlang.cookie”。

**注意：每个人的erlang cookie位置可能不同，一定要查看自己的home dir路径。**

> 3.复制Erlang Cookie到其他RabbitMQ节点

获取到第一个RabbitMQ的Erlang Cookie之后，只需要把这个文件复制到其他RabbitMQ节点即可。

物理机和容器之间复制命令如下：

- 容器复制文件到物理机：docker cp 容器名称:容器目录 物理机目录
- 物理机复制文件到容器：docker cp 物理机目录 容器名称:容器目录

设置Erlang Cookie文件权限：“chmod 600 /var/lib/rabbitmq/.erlang.cookie”。

