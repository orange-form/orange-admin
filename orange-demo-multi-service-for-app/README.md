### 必知要点
---
在该文档中，我们将主要介绍开发和调试阶段，系统所依赖的服务组件的启动与控制台访问方式。

1. 我们目前提供的启动方式包括，docker-compose和本地命令行两种方式。推荐操作更为简便的docker-compose方式。
2. 对于基础服务组件，如注册中心(nacos/consul)、配置中心(nacos/consul)、Redis、Zookeeper和Kafka，均可通过docker-compose的方式启动。
与此同时，我们也为这些服务组件提供了本地启动文档，具体详见开发文档中[环境准备章节](http://www.orangeforms.com/development-doc/environment/)。
3. 出于某些原因，apollo配置中心和skywalking，我们目前尚未提供docker-compose方式，因此只能通过本地命令行的方式启动。
具体详见开发文档中[环境准备章节](http://www.orangeforms.com/development-doc/environment/)。
4. ELK、Prometheus、Grafana和PinPoint，由于不会影响正常的开发和调试，我们目前仅提供了docker-compose的启动方式。

最后，我们真诚的希望能够得到您的反馈，并持续改进我们的产品、文档、服务和操作流程。
### 服务接口文档
---
- Postman
  - 无需启动服务，即可将当前工程的接口导出成Postman格式。在工程的common/common-tools/模块下，找到ExportApiApp文件，并执行main函数。

### 系统依赖服务组件
---
当前工程所有微服务启动前，需将下列服务组件依次启动，可选组件可根据实际需要决定是否启动。
> 如果采用本地启动方式，启动顺序如下。docker-compose方式，脚本文件中已经编排好启动顺序。
- Redis
  - 版本：4
  - 端口: 6379
  - 推荐客户端工具 [AnotherRedisDesktopManager](https://github.com/qishibo/AnotherRedisDesktopManager)
- Zookeeper
  - 版本：3.5.5
  - 端口：2181
  - 推荐客户端工具 [zkui](https://github.com/DeemOpen/zkui)
- Kafka
  - 版本：2.12-2.4.0
  - 端口：9092
  - 推荐客户端工具 [Kafka Tool](http://www.kafkatool.com/download.html)
- Nacos
  - 版本：1.3.1
  - 控制台URL：localhost:8848/nacos
  - 用户名密码：nacos/nacos
- Sentinel-Dashboard (可选)
  - 版本：1.7.2
  - 控制台URL: localhost:8858
  - 用户名密码：sentinel/sentinel
  - 注意：该服务缺省端口为8080，容易冲突，所以改为8858。我们在所有的配置中均使用了8858，而非8080。
- ELK (可选，docker-compose-elk)
  - 版本：7.5.x
  - Kibana控制台URL：localhost:5601
- PinPoint (可选，仅当尝试使用PinPoint进行链路跟踪时使用)
  - 版本：2.0.x
  - 控制台URL：localhost:8079
- admin-monitor服务模块 (可选)
  - 控制台URL：localhost:8769
- 启动upms服务 (保证登录和用户权限服务可用)
- 启动其他业务应用微服务<br>
  推荐在gateway服务之前启动，以便gateway服务启动后可以即刻发现服务。由于gateway是从注册中心定时拉取微服务信息，所以在gateway之后启动的微服务，通常会延迟一小段时间之后才会被发现。
- 启动gateway网关服务<br>
  在所有微服务之后启动，启动后即可发现所有微服务。仅有被gateway发现的微服务，该服务的请求才可以被正常转发。

### 服务组件启动
---
> 下述文件位于工程目录的zz-resource/docker-files子目录内。
- docker-compose.yml (必须)
  - 包含系统所需的必备组件，如注册中心、配置中心、Sentinel-Dashboard、Redis、Kafka和Zookeeper。
  - 第一次启动方式为 docker-compose up -d
  - 停止方式推荐为 docker-compose stop
  - 再次启动方式推荐为 docker-compose start
  - 强行停止方式为 docker-compose down
  - 强行停止后再次启动，可能导致kafka和zookeeper出现数据错误，执行./clear-data.sh可清空部分临时数据。
  - 清空后再次执行docker-compose up -d 即可。
  - docker-compose start和docker-compose stop不会出现该类数据问题。
  - 查看启动日志命令 docker-compose logs
- docker-compose-full.yml (可选)
  - 包含全部服务的启动项，基础服务 + elk + gp。
  - 启动方式为 docker-compose up -f docker-compose-full.yml -d
  - 停止方式和数据错误处理方式，请参考上面docker-compose.yml的说明。
- Pinpoint服务启动 (可选)
  - 进入zz-resource/pinpoint-docker-master目录。
  - 执行docker-compose up启动Pinpoint数据采集和分析服务，docker-compose down停止该服务。

### 本地命令行启动方式
---
1. Nacos、Consul、Sentinel、Apollo、XXL-Job、Redis、Kafka、Zookeeper、Skywalking的本地启动方式，请参考开发文档中[环境准备章节](http://www.orangeforms.com/development-doc/environment/)。
2. ELK、Grafana、Prometheus、Pinpoint目前仅提供docker-compose方式。
3. 再次强调，通过本地命令行启动所有系统服务组件时，启动顺序一定要和上面"系统依赖服务组件"部分列出的服务顺序保持一致，因为服务组件之间是有依赖的。