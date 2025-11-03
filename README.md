## 一个短链生成系统
展示网址 http://miniuri.com

### 概述
包含短链生成与跳转

### 核心功能
- ✅ 短链生成与跳转
- ✅ 访问统计（轻量级，CPU 开销 < 1ms）
- ✅ PV/UV 统计（Redis + MySQL）
- ✅ 定时任务自动分析日志

### 环境
- jdk 21
- springboot
- redis
- mysql
- thymeleaf

### 访问统计方案
针对资源受限环境（0.2 核 CPU），采用**日志 + 定时解析**方案：
- 主请求只写日志文件（< 1ms，不阻塞页面）
- 定时任务解析日志并统计（每 5 分钟）
- 数据存储：Redis（实时） → MySQL（每天同步）

详见：[docs/visitor-stats-solutions.md](docs/visitor-stats-solutions.md)

可选监控方案（需额外资源）：
- Grafana Loki（日志可视化）
- Uptime Kuma（服务监控）

详见：[monitoring/README.md](monitoring/README.md)

### docker
- 自己配置 redis 和 mysql
- docker run 时，可以直接挂载卷映射替换application.yml

  `docker run -di -v /home/miniuri/application.yml:/miniuri-server/application.yml -p 8888:8888 miniuri`

### 声明
本在线网站只用于项目展示，随时可能关闭，并不保证绝对的可用性，切勿用于商业用途或非法传播，因此产生的任何纠纷与本人无关。
