### 项目简介
专为推广"***橙单平台生成器***"而开源的代码演示项目，该项目的前后端代码完全由生成器配置后动态生成。相信在您耐心阅读五分钟后，一定会发现更多与众不同。
>友情提示，一定要放大看每一张截图，直到最后。如果图片不能正常显示，可以考虑科学上网，或者移步到[码云这里](https://gitee.com/orangeform/orange-admin)
<table>
<tr>
  <td><img src="https://images.gitee.com/uploads/images/2020/0511/215755_28f1804f_7431510.png"/></td>
</tr>
</table>

### 橙单生成器
> 如果基础框架是技术轮子，那么我们提供的就是一条生产线，不仅可以造轮子，还能生产您的更多业务所需。与此同时，也希望我们前沿的系统设计理念，能为您的企业中台化改造提供些许的技术思路。

#### 橙单网站
- 网站首页。[http://101.200.178.51](http://101.200.178.51)
- 生成器操作指南。[http://101.200.178.51/orange-doc/](http://101.200.178.51/orange-doc/)
- 后端代码文档。[http://101.200.178.51/development-doc/](http://101.200.178.51/development-doc/)
- 前端代码文档。[http://101.200.178.51/development-vue/](http://101.200.178.51/development-vue/)
- 基于在线教育后台业务生成的，更完整的演示项目。[http://101.200.178.51:8000](http://101.200.178.51:8000)

#### 竞争优势
- 真正的代码生成器，而非脚手架。
- 主流框架任意组合，可生成相对复杂的平台业务代码。
- 图文并茂，手把手教您的配置操作指南。
- 专业、详尽、完善、完全免费、以及暗黑护眼的开发文档。
- 合理、超低价的商业授权。

#### 主要功能
- 提供前后端主流技术栈组件，可按需选择生成您的工程基础架构。
- 可创建多数据库、多应用服务，并导入业务数据表，配置服务内或跨服务的复杂表关系。
- 前端支持基于 Fragment 和 Block 的嵌套布局，配置后即可生成多样化的表单页面，并可预览。
- 操作权限和数据权限，灵活可配、运行时性能高度优化。
- 有别于其他产品，我们不仅提供了Job框架的集成，更能生成任务处理器的业务逻辑代码。
- 微服务和单体服务的接口命名规则和参数定义完全一致，便于您今后的平滑升级。

#### 关于我们
- 刘老师，20年开发经验，后台全栈架构师，代码强迫症、性能洁癖、能看出一像素偏差。精通C++/Java/Scala/Web高并发/大数据/C++服务器/视频直播网络技术，略懂容器。
- 田老师，15年开发经验，前端全栈架构师，思维极缜密、号边界王。精通C++/Java/Javascript/Vue & React/C++客户端/Android原生/小程序/视频直播编解码技术。
- 欢迎加入我们的技术交流QQ群 788581363。

### 生成后工程
>可无限制的用于学习、培训、接私活、公司自用和开发商业项目等场景，其中基础框架代码将永久免费，并持续更新。

#### 开箱即用
项目信息如您所愿，工程名称、目录结构、基础包名、common模块、代码注释中的@author信息等，在创建工程时即已配置，不会留有橙单的任何信息。因此无需二次修改，前后端直接编译运行即可。如编译期和运行时出现问题，那一定是我们的bug，在得到您反馈后，我们将及时修复。

#### 技术选型
- 前端: Element (Vue) / Ant Design (React) + ECharts / AntV + Axios + Webpack。
- 后端: Spring Boot / Spring Cloud / Spring Cloud Alibaba + Mybatis + Jwt。
- 工具库: Hutool + Guava + Caffeine + Lombok + MapStruct + 通用Mapper。
- 服务组件: Redis + Zookeeper + Apollo + XXL-Job + Kafka + Nacos + Sentinel + Seata。
- 日志监控: ELK + PinPoint / SkyWalking + Grafana + Prometheus。

#### 基础功能
- 前端框架：单页面、多标签、多栏目和子路由，多种模式可供选择。
- 前端能力：上传下载、数据导出、自定义打印模板、富文本、分组统计图表、明细数据下钻等。
- 页面布局：支持基于 Fragment 和 Block 的灵活布局方式，通过配置即可生成多样化的表单页面。
- 后台架构：分布式锁、分布式 Id 生成器、分布式缓存、分布式事务和分布式限流等，按需集成。
- 用户部门：用户表和部门表的字段数量，以及与其他表的关联关系，均灵活可配。
- 操作权限：前端控制可精确到按钮级的操作和标签级的显示，后台统一拦截验证更加安全。
- 数据权限：基于 Mybatis 拦截器 + JSqlParser 的实现方式，配置更灵活，代码侵入性更低。
- 多数据源：可根据配置动态生成，路由策略灵活可扩展。
- 数据组装：Java 注解方式实现数据组装，可支持**服务内和跨服务**的一对一、一对多、多对多、字典、聚合计算等。
- 定时任务：支持多种类型的Job处理器代码模板，灵活可配、高度优化、二次开发简单。
- 日志监控：基于 Kafka + ELK 的日志跟踪，基于 PinPoint/SkyWalking 的服务链路跟踪，基于 GPE 的服务性能指标监控。

#### 代码质量
- 无任何二次封装，只生成您最懂的代码。
- 遵循阿里巴巴标准的代码规范。
- 主流技术栈组合，对面试大有裨益。
- 产品级代码强度，层次清晰、滴水不漏。
- 近乎于0的代码重复率，35%以上的注释覆盖率。
- 15年架构师优化的每一处细节。

#### 规则扫描
此为在线演示工程的代码审查报告，而非当前开源示例工程。前者为微服务架构，代码量更大，结构更复杂。
1. SonarQube 扫描
- 基于SonarQube8.2缺省最严格的代码扫描规则，其中代码复杂度要求为15。
- 有气味代码共90处，其中85处为DTO、Model、常量字典、RPC接口等定义出现重复名称所致，均与模拟实际业务有关。
- 其余气味代码，是在权衡性能、可读性和易修改性等因素后保留下来的，具体见图4。
<table>
<tr>
  <td><img src="https://images.gitee.com/uploads/images/2020/0504/132431_a28ba412_7431510.png"/></td>
  <td><img src="https://images.gitee.com/uploads/images/2020/0504/133330_6a7564a1_7431510.png"/></td>
</tr>
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
  <td><img src="https://images.gitee.com/uploads/images/2020/0504/133514_32d8faad_7431510.png"/></td>
</tr>
</table>

3. Statistic 代码统计
- 生成代码总量约为73000行，主要包括Java、XML、YAML和SQL初始化脚本等。
- Java代码覆盖率为37%。
<table>
<tr>
  <td><img src="https://images.gitee.com/uploads/images/2020/0504/133554_8df8ff51_7431510.png"/></td>
  <td><img src="https://images.gitee.com/uploads/images/2020/0504/133613_68dd5482_7431510.png"/></td>
</tr>
</table>

#### 设计理念
- 前沿的单表组合式设计，使系统拆分SO EASY。
- 先代码，后SQL的原则，让服务扩充更具弹性。
- 标准化的服务间调用接口，使服务组合更具正交性。
- 前后端基于约定各司其职，默契配合，让系统运行飞起来。
- 未来将支持更多开发的语言，并逐步演化为云原生架构。

#### 系统运行
1. 初始化数据库。
- 本地创建MySQL数据库。数据库名: zz-orange-admin 字符集: utf8mb4。
- 缺省用户名和密码为: root/123456。如果与本地不一致，可修改工程的application.yml文件。
- 打开zz-orange-admin数据库，执行工程目录下的upms-script.sql和areacode.sql。
2. 后台工程。
- 以Maven的形式导入IDE，直接编译运行即可。
- 配置文件为resources/application.yml，配置项在此修改。
- 如遇问题，可参考我们的开发文档[系统启动章节](http://101.200.178.51/development-doc/system-start/#单体服务启动)。
3. 前端工程。
- 将前端工程导入vscode。
- 修改 src -> core -> config 下的配置文件，将baseUrl修改为您服务器地址。
- 安装依赖，执行 npm install。
- 启动前端服务，执行 npm run dev。
- 如遇问题，可参考我们的前端开发文档[工程启动章节](http://101.200.178.51/development-vue/deployment/#工程启动)。

### 主要截图

#### 橙单生成器
<table>
<tr>
  <td><img src="https://images.gitee.com/uploads/images/2020/0411/165025_1a5dab88_7431510.png"/></td>
  <td><img src="https://images.gitee.com/uploads/images/2020/0411/165127_aa86f874_7431510.png"/></td>
  <td><img src="https://images.gitee.com/uploads/images/2020/0411/165235_333782ce_7431510.png"/></td>
  <td><img src="https://images.gitee.com/uploads/images/2020/0411/165330_3b22761a_7431510.png"/></td>
</tr>
<tr>
  <td><img src="https://images.gitee.com/uploads/images/2020/0411/165440_faecc3ab_7431510.png"/></td>
  <td><img src="https://images.gitee.com/uploads/images/2020/0411/165518_e381e2f7_7431510.png"/></td>
  <td><img src="https://images.gitee.com/uploads/images/2020/0411/181606_265790ac_7431510.png"/></td>
  <td><img src="https://images.gitee.com/uploads/images/2020/0411/165837_de1fef48_7431510.png"/></td>
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

#### 演示工程
<table>
<tr>
  <td><img src="https://images.gitee.com/uploads/images/2020/0411/170631_e325d367_7431510.png"/></td>
  <td><img src="https://images.gitee.com/uploads/images/2020/0411/180705_a2c8a441_7431510.png"/></td>
  <td><img src="https://images.gitee.com/uploads/images/2020/0411/163813_f610c413_7431510.png"/></td>
  <td><img src="https://images.gitee.com/uploads/images/2020/0411/163912_4c1b3007_7431510.png"/></td>
</tr>
<tr>
  <td><img src="https://images.gitee.com/uploads/images/2020/0411/162844_d7731374_7431510.png"/></td>
  <td><img src="https://images.gitee.com/uploads/images/2020/0411/163012_78374da1_7431510.png"/></td>
  <td><img src="https://images.gitee.com/uploads/images/2020/0411/163124_92373034_7431510.png"/></td>
  <td><img src="https://images.gitee.com/uploads/images/2020/0411/163222_c95aae03_7431510.png"/></td>
</tr>
</table>

#### 微服务中间件
<table>
<tr>
  <td><img src="https://images.gitee.com/uploads/images/2020/0411/190701_24eed9f4_7431510.png"/></td>
  <td><img src="https://images.gitee.com/uploads/images/2020/0423/204529_06fb1cd3_7431510.png"/></td>
  <td><img src="https://images.gitee.com/uploads/images/2020/0511/214604_b266bcda_7431510.png"/></td>
  <td><img src="https://images.gitee.com/uploads/images/2020/0411/191404_6eff2e07_7431510.png"/></td>
</tr>
<tr>
  <td><img src="https://images.gitee.com/uploads/images/2020/0411/193120_eb24d1c4_7431510.png"/></td>
  <td><img src="https://images.gitee.com/uploads/images/2020/0411/194208_3dd70cd2_7431510.png"/></td>
  <td><img src="https://images.gitee.com/uploads/images/2020/0511/214049_1f035fbb_7431510.png"/></td>
  <td><img src="https://images.gitee.com/uploads/images/2020/0411/192424_04baa0a6_7431510.png"/></td>
</tr>
</table>
