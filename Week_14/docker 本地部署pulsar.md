## mac 下 docker 本地部署pulsar



**目录：**

**1.docker本地部署pulsar单节点集群**

**2.docker本地部署pulsar仪表盘**

**3.测试pulsar集群**

4.**docker本地部署pulsar-manager**

**5.pulsar-metrics&prometheus&grafana**

**6.特别说明**

**7.参考资源**

## 1.docker本地部署pulsar单节点集群

```
docker pull apachepulsar/pulsar:latest

docker run -d -it \

  -p 6650:6650 \

  -p 8088:8080 \

  -v pulsardata:/Users/hepengyuan/Documents/docker-local/docker-local-pulsar/data \

  -v pulsarconf:/Users/hepengyuan/Documents/docker-local/docker-local-pulsar/conf \

  --name pulsar-standalone \

  apachepulsar/pulsar:latest \

  bin/pulsar standalone
```

> -v pulsardata：配置pulsar消息存储目录
>
> -v pulsarconf：存放pulsar配置

```shell
docker run --name pulsar -dit -p 8088:8080 -p 6650:6650 apachepulsar/pulsar-all bin/pulsar standalone
```

## 2.docker本地部署pulsar仪表盘

```shell
docker pull apachepulsar/pulsar-dashboard:latest

docker run --name pulsar-dashboard -dit -p 8089:80 -e SERVICE_URL=http://pulsar-standalone:8080 --link pulsar-standalone apachepulsar/pulsar-dashboard
```

浏览器访问: http://127.0.0.1:8089 

## 3.测试pulsar集群



```shell
docker exec -it pulsar-standalone bash bin/pulsar-client produce my-topic --messages "hello-pulsar"
```

如果消息已成功发布到主题，您应该在pulsar-client日志中看到如下确认

> [main] INFO org.apache.pulsar.client.cli.PulsarClientTool - 1 messages successfully produced

## 4.docker本地部署pulsar-manager

```
docker pull apachepulsar/pulsar-manager
```



需要开启bkvm，这样才能看到“Bookkeeper Visual Manager”的web：

https://github.com/apache/pulsar-manager/blob/master/src/main/resources/bkvm.conf

```shell 
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# Change this to true in order to start BKVM
#这里需要改成true
bkvm.enabled=true
# BookKeeper Connection
# Default value zk+null://127.0.0.1:2181/ledgers works for Pulsar Standalone
metadataServiceUri=zk+null://127.0.0.1:2181/ledgers
# Refresh BK metadata at boot.
# BK metadata are not scanned automatically in BKVM, you have to request it from the UI
metdata.refreshAtBoot=true
# HerdDB database connection, not to be changed if you are running embedded HerdDB in Pulsar Manager
# If you are using PostGRE SQL you will have to change this configuration
# We want to use the HerdDB database started by PulsarManager itself, by default BKVM wants to start its one database
jdbc.url=jdbc:herddb:localhost:7000?server.mode=standalone&server.start=false
jdbc.startDatabase=false
server.mode=standalone
server.start=false
```

```shell
docker run -it -d\
  -p 9527:9527 -p 7750:7750 \
  -e SPRING_CONFIGURATION_FILE=/pulsar-manager/pulsar-manager/application.properties \
  -v /Users/hepengyuan/Documents/docker-local/docker-local-pulsar/conf/bkvm.conf:/pulsar-manager/pulsar-manager/bkvm.conf \
  --link pulsar-standalone \
  apachepulsar/pulsar-manager
```

没查到默认用户名和密码，需要修改：

```
CSRF_TOKEN=$(curl http://127.0.0.1:7750/pulsar-manager/csrf-token)
curl \
  -H "X-XSRF-TOKEN: $CSRF_TOKEN" \
  -H "Cookie: XSRF-TOKEN=$CSRF_TOKEN;" \
  -H 'Content-Type: application/json' \
  -X PUT http://127.0.0.1:7750/pulsar-manager/users/superuser \
  -d '{"name": "admin", "password": "apachepulsar", "description": "test", "email": "username@test.org"}'
```

ledgers-web-ui：

http://127.0.0.1:7750/bkvm/#/ledgers

用户名密码都是：admin



pulsar-manager后台：

http://127.0.0.1:9527/#/environments

## 5.pulsar-metrics&prometheus&grafana



```
git clone https://github.com/streamnative/apache-pulsar-grafana-dashboard.git

cd apache-pulsar-grafana-dashboard
```

执行脚本生成可用的dashboard：

```
./scripts/generate_dashboards.sh
```

我的：

prometheus-url：http://192.168.111.64:9090

clustername：Prometheus

执行命令生成dashboard：

```
./scripts/generate_dashboards.sh http://192.168.111.64:9090 Prometheus
```

可能会报错：j2 command not found

root用户执行：
```
pip3 -q install --user --upgrade j2cli
```
mac机器执行：
```
brew install jinja2-cli
```
需要做软链接：
```
ln -s /usr/local/bin/jinja2 /usr/local/bin/j2
```

## 6.特别说明

本文是单节点集群，不可用于生产环境。

主要用于本地开发、调试、探索。



## 7.参考资源
1.https://github.com/apache/pulsar-manager

2.https://pulsar.apache.org/docs/en/deploy-monitoring/

3.Pulsar Metrics

https://pulsar.apache.org/docs/en/reference-metrics/

4.pulsar metric dashboard

https://github.com/streamnative/apache-pulsar-grafana-dashboard

5.修改pulsar-manager密码

https://github.com/apache/pulsar-manager#access-pulsar-manager