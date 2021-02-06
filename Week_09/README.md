**Week09 作业题目（周四）：**

**1.（选做）**实现简单的 Protocol Buffer/Thrift/gRPC(选任一个) 远程调用 demo。
**2.（选做）**实现简单的 WebService-Axis2/: 远程调用 demo。
**3.（必做）**改造自定义 RPC 的程序，提交到 GitHub：

- 尝试将服务端写死查找接口实现类变成泛型和反射；
- 尝试将客户端动态代理改成 AOP，添加异常处理；
- 尝试使用 Netty+HTTP 作为 client 端传输方式。

**4.（选做☆☆））**升级自定义 RPC 的程序：

- 尝试使用压测并分析优化 RPC 性能；
- 尝试使用 Netty+TCP 作为两端传输方式；
- 尝试自定义二进制序列化；
- 尝试压测改进后的 RPC 并分析优化，有问题欢迎群里讨论；
- 尝试将 fastjson 改成 xstream；
- 尝试使用字节码生成方式代替服务端反射。

**Week09 作业题目（周六）：**
**1.（选做）**按课程第二部分练习各个技术点的应用。
**2.（选做）**按 dubbo-samples 项目的各个 demo 学习具体功能使用。
**3.（必做）**结合 dubbo+hmily，实现一个 TCC 外汇交易处理，代码提交到 GitHub:

- 用户 A 的美元账户和人民币账户都在 A 库，使用 1 美元兑换 7 人民币 ;
- 用户 B 的美元账户和人民币账户都在 B 库，使用 7 人民币兑换 1 美元 ;
- 设计账户表，冻结资产表，实现上述两个本地事务的分布式事务。

**4.（挑战☆☆）**尝试扩展 Dubbo

- 基于上次作业的自定义序列化，实现 Dubbo 的序列化扩展 ;
- 基于上次作业的自定义 RPC，实现 Dubbo 的 RPC 扩展 ;
- 在 Dubbo 的 filter 机制上，实现 REST 权限控制，可参考 dubbox;
- 实现一个自定义 Dubbo 的 Cluster/Loadbalance 扩展，如果一分钟内调用某个服务 / 提供者超过 10 次，则拒绝提供服务直到下一分钟 ;
- 整合 Dubbo+Sentinel，实现限流功能 ;
- 整合 Dubbo 与 Skywalking，实现全链路性能监控。

**作业提交规范**：
\1. 作业不要打包 ；
\2. 同学们写在 md 文件里，而不要发 Word, Excel , PDF 等 ；
\3. 代码类作业需提交完整 Java 代码，不能是片段；
\4. 作业按课时分目录，仅上传作业相关，笔记分开记录；
\5. 画图类作业提交可直接打开的图片或 md，手画的图手机拍照上传后太大，难以查看，推荐画图（推荐 PPT、Keynote）；
\6. 提交记录最好要标明明确的含义（比如第几题作业）。

以上作业，要求 2 道必做题目提交到 GitHub 上面，Week09 作业提交地址：

请务必按照示例格式进行提交，不要复制其他同学的格式，以免格式错误无法抓取作业。
作业提交截止时间 12 月 16 日（下周三）23:59 前。
https://github.com/JAVA-000/JAVA-000/issues/142

**作业参考示例地址，由秦老师和助教共建，每周同步更新：**[ https://github.com/JavaCourse00/JavaCourseCodes](https://github.com/JavaCourse00/JavaCourseCodes)

Github 使用教程：[ https://u.geekbang.org/lesson/51?article=294701](https://u.geekbang.org/lesson/51?article=294701)

学号查询方式：PC 端登录 time.geekbang.org, 点击右上角头像进入我的教室，左侧头像下方 G 开头的为学号