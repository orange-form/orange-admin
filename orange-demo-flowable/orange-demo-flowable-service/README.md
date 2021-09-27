### 服务接口文档
---
- Postman
  - 无需启动服务，即可将当前工程的接口导出成Postman格式。在工程的common/common-tools/模块下，找到ExportApiApp文件，并执行main函数。

### 服务启动环境依赖
---

执行docker-compose up -d 命令启动下面依赖的服务。
执行docker-compose down 命令停止下面服务。

- Redis
  - 版本：4
  - 端口: 6379
  - 推荐客户端工具 [AnotherRedisDesktopManager](https://github.com/qishibo/AnotherRedisDesktopManager)
