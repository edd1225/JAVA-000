## 简介

    Java kafka 简单代码示例



**1. 安装kafka**

$  brew install kafka

(1)  安装过程将依赖安装 zookeeper

(2)  软件位置

```
/usr/local/Cellar/zookeeper

/usr/local/Cellar/kafka
```

(3)  配置文件位置

```
/usr/local/etc/kafka/zookeeper.properties

/usr/local/etc/kafka/server.properties
```

备注：后续操作均需进入 /usr/local/Cellar/kafka/2.0.0/bin 目录下。

**2. 启动zookeeper**

```
zookeeper-server-start /usr/local/etc/kafka/zookeeper.properties 
```

**3. 启动kafka服务**
```
kafka-server-start /usr/local/etc/kafka/server.properties 
```
**4. 创建topic**
```
kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test1
```
**删除topic**
```
kafka-topics --delete --zookeeper localhost:2181 --topic test1
```
**5. 查看创建的topic**
```
kafka-topics --list --zookeeper localhost:2181 
```
**6. 生产数据**
``````
kafka-console-producer --broker-list localhost:9092 --topic test1
```
**7. 消费数据**
```
kafka-console-consumer --bootstrap-server 127.0.0.1:9092 --topic test1 --from-beginning
```
备注：--from-beginning  将从第一个消息还是接收

### maven依赖配置

```xml
<!-- kafka -->
<dependency>
    <groupId>org.apache.kafka</groupId>
    <artifactId>kafka-clients</artifactId>
    <version>0.11.0.0</version>
</dependency>
```

### kakfa生产和消费者生成

```java
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.*;

/**
 * @author edd1225
 */
public class KafkaUtils {

    public static KafkaConsumer<String, String> createConsumer(String servers, String topic) {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", servers);
        properties.put("group.id", "group-1");
        properties.put("enable.auto.commit", "false");
        properties.put("auto.commit.interval.ms", "1000");
        properties.put("auto.offset.reset", "earliest");
        properties.put("session.timeout.ms", "30000");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<String, String>(properties);
        kafkaConsumer.subscribe(Arrays.asList(topic));
        return kafkaConsumer;
    }

    public static void readMessage(KafkaConsumer<String, String> kafkaConsumer, int timeout) {
        while (true) {
            ConsumerRecords<String, String> records = kafkaConsumer.poll(timeout);
            for (ConsumerRecord<String, String> record : records) {
                String value = record.value();
                kafkaConsumer.commitAsync();
                System.out.println(value);
            }
        }
    }

    public static KafkaProducer<String, String> createProducer(String servers) {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", servers);
        properties.put("acks", "all");
        properties.put("retries", 0);
        properties.put("batch.size", 16384);
        properties.put("linger.ms", 1);
        properties.put("buffer.memory", 33554432);
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return new KafkaProducer<String, String>(properties);
    }

    public static void send(KafkaProducer<String, String> producer, String topic, String message) {
        producer.send(new ProducerRecord<String, String>(topic, message));
    }
}
```

### 运行

```java
public class Main {

    public static void main(String[] args) {
        String servers = "localhost:9092,localhost:9093,localhost:9094";
        String topic = "TestTopic";
        String message = "test";

        KafkaProducer<String, String> producer = KafkaUtil.createProducer(servers);
        KafkaUtil.send(producer, topic, message);

        KafkaConsumer<String, String> consumer = KafkaUtil.createConsumer(servers, topic);
        KafkaUtil.readMessage(consumer, 100);
    }
}
```



具体实例： 



##【总结笔记】kafka工作常见问题汇总

### 没有禁用"自动创建topic"的功能

> 已经增加参数auto.create.topics.enable=false，否则任何人只要发数据给kafka，没有对应的topic都会自动创建，一旦生产者多了会乱套。

### 数据保留周期过短

> 已经修改为3天，log.retention.hours=72。默认是3小时，阿里云的破环境有时候网络出问题就得三小时，来不及消费的消息就没了。

### 节点有点少(一主一备，最小集群得3个，暂时2个也能玩)

> 咱们kafka broker就2个节点，创建topic一般会配置副本因为为1，副本和主本一般应该在不同的机器上，否则从数据冗余角度来说没啥意义。

### data目录跟mariadb的数据目录混在一起了

> 我重启kafka发现起不来，原因是数据目录下放了mariadb的数据，启动时kafka认为是一个topic的分区，结果检查数据发现不对就直接退出了。 由log.dirs=/data改为了log.dirs=/data/kafka

### 消费者无法消费的问题

> 这个问题有点隐蔽，打开host.name配置就好，在每个broker分配打开配置：host.name=10.45.41.173(各节点用自己的IP)，原因如下：
>
> 我们知道配置consumer时只需要提供zookeeper信息即可，consumer会从zk获取broker、topic、partition信息的。 而zk里存的这些信息是broker启动起来之后写到zk里的，[broker有个配置项advertised.host.name](http://xn--brokeradvertised-f69yp883ag4qejy8cwq5a.host.name/)，broker就是把它的值写到了zk。
>
> advertised.host.name如果不配置，他会等于host.name的值，前提是host.name配置打开了，否则host.name的值就是java.net.InetAddress.getCanonicalHostName 的值，在mac上、windows上这个值返回IP，但在linux上这个值就是 hostname 的值。
>
> 一旦zk里存的是broker的hostname，而producers和consumers的/etc/hosts文件又没改，那他没法完成从hostname到IP的映射，因此就不能生产和消费。

### 已有topic的partition有点少

> partition都只有2个，创建topic时可以多一点，比如16个，消费的时候一个topic对应一个线程比较合适
> 

# 集群 ：

# Docker化 kafka集群 环境搭建

配合上一节的 Zookeeper 环境,计划搭建一个 3 节点的集群。宿主机 IP 为 `192.168.1.5`

**docker-compose-kafka-cluster.yml**

```yaml
version: '3.7'

networks:
  docker_net:
    external: true

services:

  kafka1:
    image: wurstmeister/kafka
    restart: unless-stopped
    container_name: kafka1
    ports:
      - "9093:9092"
    external_links:
      - zoo1
      - zoo2
      - zoo3
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ADVERTISED_HOST_NAME: 192.168.124.5                   ## 修改:宿主机IP
      KAFKA_ADVERTISED_PORT: 9093                                 ## 修改:宿主机映射port
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://192.168.124.5:9093    ## 绑定发布订阅的端口。修改:宿主机IP
      KAFKA_ZOOKEEPER_CONNECT: "zoo1:2181,zoo2:2181,zoo3:2181"
    volumes:
      - "./kafka/kafka1/docker.sock:/var/run/docker.sock"
      - "./kafka/kafka1/data/:/kafka"
    networks:
      - docker_net


  kafka2:
    image: wurstmeister/kafka
    restart: unless-stopped
    container_name: kafka2
    ports:
      - "9094:9092"
    external_links:
      - zoo1
      - zoo2
      - zoo3
    environment:
      KAFKA_BROKER_ID: 2
      KAFKA_ADVERTISED_HOST_NAME: 192.168.124.5                 ## 修改:宿主机IP
      KAFKA_ADVERTISED_PORT: 9094                               ## 修改:宿主机映射port
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://192.168.124.5:9094   ## 修改:宿主机IP
      KAFKA_ZOOKEEPER_CONNECT: "zoo1:2181,zoo2:2181,zoo3:2181"
    volumes:
      - "./kafka/kafka2/docker.sock:/var/run/docker.sock"
      - "./kafka/kafka2/data/:/kafka"
    networks:
      - docker_net

  kafka3:
    image: wurstmeister/kafka
    restart: unless-stopped
    container_name: kafka3
    ports:
      - "9095:9092"
    external_links:
      - zoo1
      - zoo2
      - zoo3
    environment:
      KAFKA_BROKER_ID: 3
      KAFKA_ADVERTISED_HOST_NAME: 192.168.124.5                 ## 修改:宿主机IP
      KAFKA_ADVERTISED_PORT: 9095                              ## 修改:宿主机映射port
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://192.168.124.5:9095   ## 修改:宿主机IP
      KAFKA_ZOOKEEPER_CONNECT: "zoo1:2181,zoo2:2181,zoo3:2181"
    volumes:
      - "./kafka/kafka3/docker.sock:/var/run/docker.sock"
      - "./kafka/kafka3/data/:/kafka"
    networks:
      - docker_net

  kafka-manager:
    image: sheepkiller/kafka-manager:latest
    restart: unless-stopped
    container_name: kafka-manager
    hostname: kafka-manager
    ports:
      - "9000:9000"
    links:            # 连接本compose文件创建的container
      - kafka1
      - kafka2
      - kafka3
    external_links:   # 连接本compose文件以外的container
      - zoo1
      - zoo2
      - zoo3
    environment:
      ZK_HOSTS: zoo1:2181,zoo2:2181,zoo3:2181                 ## 修改:宿主机IP
      TZ: CST-8
    networks:
      - docker_net
```

执行以下命令启动

```bash
docker-compose -f docker-compose-kafka-cluster.yml up -d
```

可以看到 kafka 集群已经启动成功。

# 生产与消费

##  创建主题

```bash
➜  docker docker exec -it kafka1 /bin/bash   # 进入容器
bash-4.4# cd /opt/kafka/   # 进入安装目录
bash-4.4# ./bin/kafka-topics.sh --list --zookeeper zoo1:2181,zoo2:2181,zoo3:2181   # 查看主题列表
__consumer_offsets
bash-4.4# ./bin/kafka-topics.sh --create --zookeeper zoo1:2181,zoo2:2181,zoo3:2181 --replication-factor 2 --partitions 3 --topic test    # 新建主题
Created topic test.
```

> 说明:
> --replication-factor 副本数;
> --partitions 分区数;
> replication<=broker(一定);
> 有效消费者数<=partitions 分区数(一定);

新建主题后, 再次查看映射目录, 由图可见，partition 在 3 个 broker 上均匀分布。

## 生产消息

```shell
./bin/kafka-console-producer.sh --broker-list kafka1:9092,kafka2:9092,kafka3:9092  --topic test >msg1 >msg2 >msg3 >msg4 >msg5 >msg6
```

##  消费消息
```
./bin/kafka-console-consumer.sh --bootstrap-server kafka1:9092,kafka2:9092,kafka3:9092 --topic test --from-beginning
```
--from-beginning 代表从头开始消费
## 消费详情

```
查看消费者组
 ./bin/kafka-consumer-groups.sh --bootstrap-server kafka1:9092,kafka2:9092,kafka3:9092 --list
KafkaManagerOffsetCache
console-consumer-86137
```

```
消费组偏移量
  ./bin/kafka-consumer-groups.sh --bootstrap-server kafka1:9092,kafka2:9092,kafka3:9092 --describe --group KafkaManagerOffsetCache
```

查看 topic 详情

```
 ./bin/kafka-topics.sh --zookeeper zoo1:2181,zoo2:2181,zoo3:2181 --describe --topic test
Topic: test PartitionCount: 3   ReplicationFactor: 2    Configs:
    Topic: test Partition: 0    Leader: 3   Replicas: 3,1   Isr: 3,1
    Topic: test Partition: 1    Leader: 1   Replicas: 1,2   Isr: 1,2
    Topic: test Partition: 2    Leader: 2   Replicas: 2,3   Isr: 2,3
```

```
查看.log 数据文件
./bin/kafka-run-class.sh kafka.tools.DumpLogSegments --files /kafka/kafka-logs-c4e2e9edc235/test-0/00000000000000000000.log  --print-data-log
Dumping /kafka/kafka-logs-c4e2e9edc235/test-0/00000000000000000000.log
Starting offset: 0
baseOffset: 0 lastOffset: 0 count: 1 baseSequence: -1 lastSequence: -1 producerId: -1 producerEpoch: -1 partitionLeaderEpoch: 0 isTransactional: false isControl: false position: 0 CreateTime: 1583317546421 size: 72 magic: 2 compresscodec: NONE crc: 1454276831 isvalid: true
| offset: 0 CreateTime: 1583317546421 keysize: -1 valuesize: 4 sequence: -1 headerKeys: [] payload: msg2
baseOffset: 1 lastOffset: 1 count: 1 baseSequence: -1 lastSequence: -1 producerId: -1 producerEpoch: -1 partitionLeaderEpoch: 0 isTransactional: false isControl: false position: 72 CreateTime: 1583317550369 size: 72 magic: 2 compresscodec: NONE crc: 3578672322 isvalid: true
| offset: 1 CreateTime: 1583317550369 keysize: -1 valuesize: 4 sequence: -1 headerKeys: [] payload: msg4
baseOffset: 2 lastOffset: 2 count: 1 baseSequence: -1 lastSequence: -1 producerId: -1 producerEpoch: -1 partitionLeaderEpoch: 0 isTransactional: false isControl: false position: 144 CreateTime: 1583317554831 size: 72 magic: 2 compresscodec: NONE crc: 2727139808 isvalid: true
| offset: 2 CreateTime: 1583317554831 keysize: -1 valuesize: 4 sequence: -1 headerKeys: [] payload: msg6
```


这里需要看下自己的文件路径是什么，



```
查看.index 索引文件
 ./bin/kafka-run-class.sh kafka.tools.DumpLogSegments --files /kafka/kafka-logs-c4e2e9edc235/test-0/00000000000000000000.index
Dumping /kafka/kafka-logs-c4e2e9edc235/test-0/00000000000000000000.index
offset: 0 position: 0
```

```
查看.timeindex 索引文件
bash-4.4# ./bin/kafka-run-class.sh kafka.tools.DumpLogSegments --files /kafka/kafka-logs-c4e2e9edc235/test-0/00000000000000000000.timeindex  --verify-index-only
Dumping /kafka/kafka-logs-c4e2e9edc235/test-0/00000000000000000000.timeindex
Found timestamp mismatch in :/kafka/kafka-logs-c4e2e9edc235/test-0/00000000000000000000.timeindex
  Index timestamp: 0, log timestamp: 1583317546421
```

