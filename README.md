## 一个短链生成系统

### 概述
包含短链生成与跳转



### 环境
- jdk 21
- springboot
- redis
- mysql
- thymeleaf


### docker
- 自己配置 redis 和 mysql
- docker run 时，可以直接挂载卷映射替换application.yml

  `docker run -di -v /home/miniuri/application.yml:/miniuri-server/application.yml -p 8888:8888 miniuri`
