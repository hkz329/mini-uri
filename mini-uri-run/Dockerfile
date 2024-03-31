FROM eclipse-temurin:21-jre

MAINTAINER hkz329 190022707@qq.com
## 创建目录，并使用它作为工作目录
RUN mkdir -p /miniuri-server
WORKDIR /miniuri-server
## 将后端项目的 Jar 文件，复制到镜像中
COPY ./target/mini-uri-run.jar app.jar

## 设置 TZ 时区
ENV TZ=Asia/Shanghai
## 设置 JAVA_OPTS 环境变量，可通过 docker run -e "JAVA_OPTS=" 进行覆盖
ENV JAVA_OPTS="-Xms512m -Xmx512m -Djava.security.egd=file:/dev/./urandom"

## 应用参数
ENV ARGS=""

## 暴露后端项目的 8888 端口
EXPOSE 8888

## 启动后端项目
CMD java ${JAVA_OPTS} -jar app.jar $ARGS
