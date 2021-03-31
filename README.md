### 橙单简介
橙单中台化低代码生成工具由知视科技团队研发，团队经过多年大型企业中台化改造项目的历练，不断总结经验教训，不断努力提升产能，不断积极应对微服务改造过程中出现的分分合合。在历经无数日夜的持续迭代和优化后，终于可以实现生成 70% 的适用于微服务架构的高质量范式化工程级代码。此后，我们就很少加班，热爱工作，并与领导成为了战友，与客户成为了朋友。

### 橙单生成器
如果说基础框架是技术轮子，那么我们就是生产线。不仅可以造出各种尺寸的轮子，通过我们的在线配置工具，还能生成您的更多业务所需。下图所示的微服务组件，在创建项目时可正交化组合。生成后的工程不仅可用于企业中台化改造，同样也适用于开发者进行微服务全技术栈的快速学习。

<table>
<tr>
  <td><img src="https://images.gitee.com/uploads/images/2020/1124/152615_5125dfbb_7431510.png"/></td>
</tr>
</table>

#### 在线资源
- 网站首页。[http://www.orangeforms.com](http://www.orangeforms.com)
- 教学视频。[https://www.bilibili.com/video/bv1Wg4y1i7vP](https://www.bilibili.com/video/bv1Wg4y1i7vP)
- 生成器操作指南。[http://www.orangeforms.com/orange-doc/](http://www.orangeforms.com/orange-doc/)
- 后端代码文档。[http://www.orangeforms.com/development-doc/](http://www.orangeforms.com/development-doc/)
- 前端代码文档。[http://www.orangeforms.com/development-vue/](http://www.orangeforms.com/development-vue/)
- 更完整的演示项目。[http://demo.orangeforms.com](http://demo.orangeforms.com)

#### 主要功能
- 支持前后端全部主流技术栈的自由组合，并可根据所选配置动态生成工程的基础架构。
- 前端支持基于 Fragment 和 Block 的嵌套布局，配置后即可生成多样化的表单页面，支持高交互性的全工程预览。
- 提供极为完善且灵活的用户权限管理和数据权限过滤等基础性功能，运行时性能高度优化。
- 支持多租户版本，租户数据支持逻辑隔离、物理隔离和混合隔离等多种方式，租户权限数据由租户运营管理服务统一管理，实时同步到多个租户运行系统数据库中。不仅非常有利于租户数据的迁移。同时也保证了运行时效率。业务逻辑代码与非多租户系统相比，差异也降至最低。
- 可创建多数据库、多应用服务，并导入业务数据表，配置服务内或服务间的复杂表关联关系。目前已支持**服务内和跨服务**的"一对一关联、一对多关联、多对多关联、字典关联、一对多和多对多分组聚合计算"等范式化关联关系代码的生成。
- 有别于其他产品，我们不仅支持多种主流 Job 框架的集成，更能生成适配 Job 框架的定时任务业务逻辑代码。目前已支持“全量同步、增量同步、增量分组聚合转换同步”等业务代码的生成。
- 项目生成具备较强的裁剪性，既可选择生成不包含权限和表单的适用于 app 的纯后台服务，也可生成支持表单和权限的完整中后台工程。
- 微服务和单体服务的代码风格、接口命名与参数定义规范完全一致，便于您今后的平滑升级。

#### 竞争优势
- 排在首位的就是 15 万字以上的专业、详尽、完善和暗黑护眼的操作指南、开发文档和教学视频。
- 真正的低代码生成工具，而非单纯的敏捷脚手架，可生成 70% 以上相对复杂的中台化业务代码。
- 浅显易懂的极高质量生成后工程代码，经过 SonarQube 和 Alibaba 代码规范严格扫描。
- 合理、透明、全网超低价的教学版和企业版商业授权。(请我们撸一次串的价格)

#### 关于我们
- S.L 老师，20年开发经验，后台全栈架构师，代码强迫症、性能洁癖、能看出一像素偏差。精通 C++/Java/Scala/Web高并发/大数据/C++服务器/视频直播网络技术，略懂容器。
- J.T 老师，15年开发经验，前端全栈架构师，思维极缜密、号边界王。精通 C++/Java/Javascript/Vue & React/C++客户端/Android原生/小程序/视频直播编解码技术。
- 欢迎加入我们的技术交流 QQ 群，几乎每天都有30分钟的技术分享。 [![](https://img.shields.io/badge/加入QQ群-788581363-red.svg)](//shang.qq.com/wpa/qunwpa?idkey=590857a1b4c587e2be3d66b9a7e2015109772e777c6451c897dee393489b1661)
- 由于被恶意举报，如果您通过群号搜索不到，请添加作者的 QQ (3510245832)，我们会将您拉入群中。
![QQ群](https://images.gitee.com/uploads/images/2020/0924/092230_dda4c368_7431510.png "orange-group1.png")

### 生成后工程
>可无限制的用于学习、培训、接私活、公司自用和开发商业项目等场景，其中基础框架代码将永久免费，并持续更新。

#### 开箱即用
项目信息如您所愿，工程名称、目录结构、基础包名、common模块、代码注释中的 @author 信息等，在创建工程时即已配置，不会留有橙单的任何信息。因此无需二次修改，前后端直接编译运行即可。如编译期和运行时出现问题，那一定是我们的 bug，在得到您反馈后，我们将及时修复。

#### 技术选型
- 前端: Element (Vue) / Ant Design (React) + ECharts / AntV + Axios + Webpack。
- 后端: Spring Boot / Spring Cloud / Spring Cloud Alibaba + Spring Security OAuth2 + Mybatis + Jwt。
- 工具库: Hutool + Guava + Caffeine + Lombok + MapStruct + Mybatis Plus + tk mapper + Knife4j + qdox。
- 服务组件: Redis + Zookeeper + Consul + Apollo + XXL-Job + Quartz + Seata + Minio + Canal + RocketMQ + Kafka + Nacos + Sentinel + Nepxion Discovery。
- 系统监控: ELK + PinPoint / SkyWalking + Grafana + Prometheus。

#### 基础功能
- 前端框架：单页面、多标签、多栏目和子路由，多套高颜值样式主题可供选择。
- 前端能力：列表编辑、统计图表、多表联动、明细数据下钻、上传下载、数据导出、自定义打印样式模板、富文本等。
- 页面布局：支持基于 Fragment 和 Block 的灵活布局方式，通过配置即可生成多样化的表单页面，并可预览。
- 接口规范：微服务和单体服务的接口命名和参数定义规范完全一致，便于日后的平滑升级。
- 后台架构：分布式锁、分布式 Id、分布式缓存、分布式事务、分布式限流和灰度发布等，按需集成。
- 用户管理：支持基于 OAuth2 的单点登录。
- 操作权限：前端控制可精确到按钮级的操作和标签级的显示，同时提供了多维度的权限分配路径查询能力。
- 数据权限：基于 Mybatis 拦截器 + JSqlParser 的实现方式，配置更灵活，代码侵入性更低。
- 租户管理：租户权限管理数据、字典等通用数据，均由租户运营管理服务统一管理，并实时同步到多个租户运营库，具有极高的数据库级别容错性。
- 多数据源：可根据配置动态生成，路由策略灵活可扩展。
- 数据组装：Java 注解方式实现数据组装，支持统一接口的**服务内和跨服务**的一对一、一对多、多对多、字典、聚合计算等关系数据组合。
- 定时任务：我们不仅提供了多套 Job 基础框架的集成，更能生成灵活可配、高度优化、便于二次开发的 Job 业务逻辑代码。
- 系统监控：基于 Kafka + ELK 的日志收集，基于 PinPoint/SkyWalking 的服务链路跟踪，基于 GPE 的服务性能指标监控。
- 接口文档：目前已集成 Knife4j，同时支持基于 qdox 逆推 Java 工程代码，实现 0 注解导出 Postman 接口文件和 Markdown 文档。
- 缓存同步：同时集成了 Canal-Admin 和 Canal-Server，以便于用户的 Canal Instance 管理。目前已实现字典变化数据到 Redis 的实时缓存同步。

#### 设计理念
- 前沿的单表组合式设计，使业务服务的拆分与再合并 SO EASY。
- 先代码，后 SQL 的主流中台化设计原则，让服务横向扩充更具弹性。
- 标准化的服务间调用接口，使业务服务组合更具正交性。
- 前后端基于约定各司其职，默契配合，让系统运行飞起来。
- 可平滑的逐步演化为云原生架构。

#### 代码质量
- 无任何二次封装，只生成您最懂的代码。
- 遵循阿里巴巴标准的代码规范。
- SonarQube 基于最严格检测规则的代码扫描。
- 产品级代码质量，层次清晰、滴水不漏。
- 近乎于 0 的代码重复率，35% 以上的注释覆盖率。
- 15 年架构师优化的每一处细节。

#### 规则扫描
此为在线演示工程的代码审查报告，而非当前开源示例工程。前者代码量更大，结构更复杂。
1. SonarQube 扫描
- 基于 SonarQube8.2 缺省最严格的代码扫描规则，其中代码复杂度要求为 15。
- 有气味代码共90处，其中85处为DTO、Model、常量字典、RPC接口等定义出现重复名称所致，均与模拟实际业务有关。
- 其余气味代码，是在权衡性能、可读性和易修改性等因素后保留下来的，具体见图4。
<table>
<tr>
  <td><img src="https://images.gitee.com/uploads/images/2020/0504/132431_a28ba412_7431510.png"/></td>
  <td><img src="https://images.gitee.com/uploads/images/2020/0504/133330_6a7564a1_7431510.png"/></td>
</tr>
</table>
<table>
<tr>
  <td><img src="https://images.gitee.com/uploads/images/2020/0504/141440_c5b8e3c1_7431510.png"/></td>
  <td><img src="https://images.gitee.com/uploads/images/2020/0504/141124_df278683_7431510.png"/></td>
</tr>
</table>

2. Alibaba Code Guide 扫描
- 下载最新版本IDEA插件，同时打开所有审查条件。
- 全部代码扫描通过。
<table>
<tr>
  <td><img src="https://images.gitee.com/uploads/images/2020/0504/134052_c3196376_7431510.png"/></td>
  <td><img src="https://images.gitee.com/uploads/images/2020/0904/092352_3fa8f2e7_7431510.png"/></td>
</tr>
</table>

3. Statistic 代码统计
- 生成代码总量约为86000多行，主要包括Java、XML、YAML和SQL初始化脚本等。
- Java代码覆盖率为37%。
<table>
<tr>
  <td><img src="https://images.gitee.com/uploads/images/2020/0904/092417_b280457b_7431510.png"/></td>
  <td><img src="https://images.gitee.com/uploads/images/2020/0904/092434_f718c982_7431510.png"/></td>
</tr>
</table>

#### 开源工程部署
1. 目录说明
> 根据用户的反馈，我们已经支持生成纯app后台服务。该服务并不包含权限和表单，以便于用户迅速上手开发app后台。
- 微服务app后端工程：orange-demo-multi-service-for-app
- 单体服务app后端工程：orange-demo-single-service-for-app
- 微服务后端工程：orange-demo-multi-service
- 微服务前端工程：orange-demo-multi-web
- 单体服务后端工程：orange-demo-single-service
- 单体服务前端工程：orange-demo-single-web

2. 数据初始化。
- 通过常用的数据库访问工具(如：navicat)，执行工程目录下的zz-resource/db-scripts/zz-orange-demo.sql文件。
- 导入脚本主要负责，数据库创建、数据表创建和测试数据的插入。

3. Api接口文档
- Knife4j文档访问方式为: http://localhost:8082/doc.html#/plus。(以上四个后台服务均支持)
- Postman Api文档和Markdown文档，位于工程根目录的zz-resource/api-docs子目录下，Postman-Api.json可直接导入Postman。(仅app后台服务支持)
<table>
<tr>
  <td><img src="https://images.gitee.com/uploads/images/2020/1029/200000_3698dcbd_7431510.png"/></td>
  <td><img src="https://images.gitee.com/uploads/images/2020/1029/200230_2ffe4390_7431510.png"/></td>
</tr>
<tr>
  <td><img src="https://images.gitee.com/uploads/images/2020/1029/200533_cd13b534_7431510.png"/></td>
  <td><img src="https://images.gitee.com/uploads/images/2020/1029/214437_e40639e5_7431510.png"/></td>
</tr>
</table>

4. 环境准备。    
docker是必选组件。通过docker-compose命令，可快速启停服务所依赖的服务中间件，如nacos、redis、zookeeper、kafka和sentinel dashboard等。中间件的控制台访问方式，可参考工程目录下的README文件。
```shell
# 假定当前目录为工程根目录。
cd zz-resource/docker-files
# 启动和停止带有基础服务中间件的docker-compose.yml文件
docker-compose up -d
# 彻底终止容器
docker-compose down
# 在第一次启动之后，可以考虑每次执行下面的命令启动和停止容器。
docker-compose start
docker-compose stop
```

5. 后台工程导入。    
以Maven的形式导入IDE，直接编译运行即可。具体可参考我们的开发文档[教学版微服务工程导入章节](http://www.orangeforms.com/development-doc/edu-multi/#工程导入)。

6. 后台服务配置。
- 进入工程目录下的 zz-resource/config-data/ 子目录，修改和数据库相关的配置，具体操作详见下图及文字注释。
![](https://images.gitee.com/uploads/images/2020/0812/203758_0f3fd28e_7431510.png)
- 将该目录下的所有配置文件导入nacos。nacos控制台访问地址 localhost:8848/nacos，用户名和密码 nacos/nacos。
- 具体导入方式可参考我们的开发文档[服务配置章节](http://www.orangeforms.com/development-doc/service-config/#微服务-nacos)。

7. 后台服务启动。    
这里仅以upms服务为例。在开发环境中，为每个微服务启动项配置main class，下图为IntelliJ IDEA中的配置截图，配置后点击Debug/Run按钮即可启动。
![](https://images.gitee.com/uploads/images/2020/0812/205946_da5bf311_7431510.png)

8. 前端工程导入和启动。
- 将前端工程导入vscode。
- 修改 src -> core -> config 下的配置文件，将baseUrl修改为您服务器地址。
- 安装依赖，执行 npm install。
- 启动前端服务，执行 npm run dev。
- 具体可参考我们的开发文档[教学版前端工程启动段落](http://www.orangeforms.com/development-doc/edu-multi/#前端工程-2)。

### 主要截图

#### 橙单生成器

<table>
<tr>
  <td><img src="https://images.gitee.com/uploads/images/2020/0411/165025_1a5dab88_7431510.png"/></td>
  <td><img src="https://images.gitee.com/uploads/images/2020/0411/165127_aa86f874_7431510.png"/></td>
</tr>
<tr>
  <td><img src="https://images.gitee.com/uploads/images/2020/0411/165235_333782ce_7431510.png"/></td>
  <td><img src="https://images.gitee.com/uploads/images/2020/0411/165330_3b22761a_7431510.png"/></td>
</tr>
<tr>
  <td><img src="https://images.gitee.com/uploads/images/2020/0411/165440_faecc3ab_7431510.png"/></td>
  <td><img src="https://images.gitee.com/uploads/images/2020/0411/165518_e381e2f7_7431510.png"/></td>
</tr>
<tr>
  <td><img src="https://images.gitee.com/uploads/images/2020/0411/181606_265790ac_7431510.png"/></td>
  <td><img src="https://images.gitee.com/uploads/images/2020/0411/165837_de1fef48_7431510.png"/></td>
</tr>
</table>

#### 演示工程
<table>
<tr>
  <td><img src="https://images.gitee.com/uploads/images/2020/1029/193003_4d88919b_7431510.png"/></td>
  <td><img src="https://images.gitee.com/uploads/images/2020/1101/142627_a55d1ff0_7431510.png"/></td>
</tr>
<tr>
  <td><img src="https://images.gitee.com/uploads/images/2020/1029/193354_a02b1181_7431510.png"/></td>
  <td><img src="https://images.gitee.com/uploads/images/2020/1029/193619_e2673c0b_7431510.png"/></td>
</tr>
<tr>
  <td><img src="https://images.gitee.com/uploads/images/2020/1029/194043_9ea4b42f_7431510.png"/></td>
  <td><img src="https://images.gitee.com/uploads/images/2020/1029/194246_0f29d8ae_7431510.png"/></td>
</tr>
<tr>
  <td><img src="https://images.gitee.com/uploads/images/2020/1029/194503_000901d9_7431510.png"/></td>
  <td><img src="https://images.gitee.com/uploads/images/2020/1029/194620_4c1047ea_7431510.png"/></td>
</tr>
</table>

#### 集成组件
<table>
<tr>
  <td><img src="https://images.gitee.com/uploads/images/2020/0411/190701_24eed9f4_7431510.png"/></td>
  <td><img src="https://images.gitee.com/uploads/images/2020/0423/204529_06fb1cd3_7431510.png"/></td>
</tr>
<tr>
  <td><img src="https://images.gitee.com/uploads/images/2020/0511/214604_b266bcda_7431510.png"/></td>
  <td><img src="https://images.gitee.com/uploads/images/2020/0411/191404_6eff2e07_7431510.png"/></td>
</tr>
<tr>
  <td><img src="https://images.gitee.com/uploads/images/2020/0411/193120_eb24d1c4_7431510.png"/></td>
  <td><img src="https://images.gitee.com/uploads/images/2020/0411/194208_3dd70cd2_7431510.png"/></td>
</tr>
<tr>
  <td><img src="https://images.gitee.com/uploads/images/2020/0511/214049_1f035fbb_7431510.png"/></td>
  <td><img src="https://images.gitee.com/uploads/images/2020/0411/192424_04baa0a6_7431510.png"/></td>
</tr>
</table>

#### 开发文档
<table>
<tr>
  <td><img src="https://images.gitee.com/uploads/images/2020/0423/203644_b838ebc6_7431510.png"/></td>
  <td><img src="https://images.gitee.com/uploads/images/2020/0423/204047_26496989_7431510.png"/></td>
  <td><img src="https://images.gitee.com/uploads/images/2020/0411/162403_9a2500d2_7431510.png"/></td>
  <td><img src="https://images.gitee.com/uploads/images/2020/0411/162502_3bc30a46_7431510.png"/></td>
</tr>
<tr>
  <td><img src="https://images.gitee.com/uploads/images/2020/0411/181337_8e7322e5_7431510.png"/></td>
  <td><img src="https://images.gitee.com/uploads/images/2020/0411/181456_978cbad8_7431510.png"/></td>
  <td><img src="https://images.gitee.com/uploads/images/2020/0423/204233_619b47b9_7431510.png"/></td>
  <td><img src="https://images.gitee.com/uploads/images/2020/0423/204335_51273157_7431510.png"/></td>
</tr>
</table>

#### 价值理念
都看到这里了，如果看的仔细，至少要花 20 分钟，非常感谢您的坚持和耐心，希望能再花 1 秒钟的时间在下面 star 一下。