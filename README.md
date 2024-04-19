## 一个短链生成系统
展示网址 http://miniuri.com

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


### 声明
本在线网站只用于项目展示，随时可能关闭，并不保证绝对的可用性，切勿用于商业用途或非法传播，因此产生的任何纠纷与本人无关。
