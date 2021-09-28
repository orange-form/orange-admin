<template>
  <div>
    <div class="title">
      <p>浏览完每个功能过后，自然会发现些与众不同。然而这一切都是<span> 橙单 </span>生成的。</p>
    </div>
    <el-row type="flex">
      <div style="width: 100%;">
        <el-card class="box-card" shadow="never" :body-style="{padding: '0px 20px'}">
          <el-collapse v-model="currentItem">
            <el-collapse-item title="选择我们的 5 个理由" name="1">
              <ul class="item-list">
                <li>是真正的生成器，而非脚手架。</li>
                <li>技术选型、包名、作者署名都由您钦定。</li>
                <li>手把手教的操作指南和教学视频。</li>
                <li>详尽、完善、免费、护眼的开发文档。</li>
                <li>全网超合理、超低价商业授权，就是撸顿串的价格。</li>
              </ul>
            </el-collapse-item>
            <el-collapse-item title="我们的技术选型" name="2">
              <ul class="item-list">
                <li>Element (Vue) / Ant Design (React) / ECharts / AntV / Axios / Webpack</li>
                <li>Spring Boot / Spring Cloud / Spring Cloud Alibaba + Mybatis + Jwt</li>
                <li>Hutool + Guava + Caffeine + Lombok + MapStruct + 通用 Mapper</li>
                <li>Redis + Zookeeper + Nacos + Consul + Apollo + XXL-Job + Kafka + Seata</li>
                <li>ELK + PinPoint / SkyWalking + Grafana + Prometheus</li>
              </ul>
            </el-collapse-item>
            <el-collapse-item title="可快速上手的高质量代码" name="3">
              <ul class="item-list">
                <li>无任何二次封装，只生成您最懂的代码。</li>
                <li>遵循阿里巴巴标准的代码规范。</li>
                <li>主流技术栈恣意组合，对面试大有裨益。</li>
                <li>产品级代码强度，层次清晰、滴水不漏。</li>
                <li>15年架构师优化的每一处细节。</li>
              </ul>
            </el-collapse-item>
            <el-collapse-item title="设计理念和未来目标" name="4">
              <ul class="item-list">
                <li>全面、实用的日志跟踪和服务监控体系。</li>
                <li>前沿的单表组合式设计，使系统拆分SO EASY。</li>
                <li>先代码，后SQL的原则，让服务扩充更具弹性。</li>
                <li>统一的数据组装接口，更高效的满足微服务的拆分与再合并。</li>
                <li>拥抱云原生架构，更多可落地的实用功能正在开发之中。</li>
              </ul>
            </el-collapse-item>
          </el-collapse>
        </el-card>
      </div>
      <el-card class="box-card" style="min-width: 450px; margin-left: 20px;" shadow="never" :body-style="{padding: '0px 20px'}">
        <div class="item">
          <span style="width: 120px;">产品名称</span>
          <el-tag effect="dark">橙单代码生成器</el-tag>
        </div>
        <el-divider></el-divider>
        <div class="item">
          <span style="width: 120px;">网站首页</span>
          <a href="http://www.orangeforms.com/" target="_blank">http://www.orangeforms.com/</a>
        </div>
        <el-divider></el-divider>
        <div class="item">
          <span style="width: 120px;">教学视频</span>
          <a href="https://www.bilibili.com/video/BV1Wg4y1i7vP" target="_blank">https://www.bilibili.com/video/BV1Wg4y1i7vP</a>
        </div>
        <el-divider></el-divider>
        <div class="item">
          <span style="width: 120px;">操作指南</span>
          <a href="http://www.orangeforms.com/orange-doc/" target="_blank">http://www.orangeforms.com/orange-doc/</a>
        </div>
        <el-divider></el-divider>
        <div class="item">
          <span style="width: 120px;">开发文档</span>
          <a href="http://www.orangeforms.com/development-doc/" target="_blank">http://www.orangeforms.com/development-doc/</a>
        </div>
        <el-divider></el-divider>
        <div class="item">
          <span style="width: 120px;">开源代码</span>
          <a href="https://gitee.com/orangeform/orange-admin" target="_blank">https://gitee.com/orangeform/orange-admin</a>
        </div>
        <el-divider></el-divider>
        <div class="item">
          <span style="width: 120px;">联系方式</span>
          <span>
            QQ群
            <a target="_blank" style="margin-left: 5px;"
            href="//shang.qq.com/wpa/qunwpa?idkey=590857a1b4c587e2be3d66b9a7e2015109772e777c6451c897dee393489b1661">
            788581363
            </a>
          </span>
        </div>
        <el-divider></el-divider>
        <div style="width: 100%; text-align: center; padding: 10px;">
          <img src="../../assets/img/orange-group1.png" />
        </div>
      </el-card>
    </el-row>
  </div>
</template>

<script>
export default {
  data () {
    return {
      currentItem: ['1', '2', '3', '4']
    }
  }
}
</script>

<style scoped>
  >>> .el-collapse {
    border: none;
  }

  >>> .el-divider--horizontal {
    margin: 0px;
  }
  >>> .el-tag {
    padding: 0px 30px;
  }
</style>

<style lang="scss" scoped>
  @import '@/assets/style/element-variables.scss';
  .title {
    border: 1px solid #EDEDED;
    border-left-width: 5px;
    border-left-color: $--color-primary;
    margin-bottom: 20px;
  }

  .title p {
    height: 40px;
    line-height: 40px;
    font-size: 16px;
    margin: 0px;
    padding-left: 20px;
  }

  .title p span {
    font-size: 20px;
    color: $--color-primary;
  }

  .item-list {
    margin: 0px;
  }
  .item-list li {
    margin: 10px 0px;
  }

  .item {
    height: 48px;
    display: flex;
    align-items: center;
  }
</style>
