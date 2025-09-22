# 多阶段构建 Dockerfile for mini-uri 项目
# 第一阶段：构建阶段
FROM mcr.microsoft.com/openjdk/jdk:21-ubuntu AS builder

LABEL maintainer="hkz329 190022707@qq.com"

# 安装 Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# 设置工作目录
WORKDIR /build

# 复制整个项目（确保多模块均可用）
COPY . .

# 构建项目
RUN mvn clean package -DskipTests -B

# 第二阶段：运行阶段
FROM mcr.microsoft.com/openjdk/jdk:21-ubuntu AS runtime

LABEL maintainer="hkz329 190022707@qq.com"

# 安装必要的工具（用于时区与健康检查）
RUN apt-get update \
    && apt-get install -y tzdata wget \
    && ln -snf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
    && echo "Asia/Shanghai" > /etc/timezone \
    && rm -rf /var/lib/apt/lists/*

# 创建非root用户
RUN groupadd -g 1000 miniuri && \
    useradd -m -s /bin/sh -u 1000 -g miniuri miniuri

# 创建应用目录（包含日志目录）
RUN mkdir -p /app /usr/app/log && chown -R miniuri:miniuri /app /usr/app

# 切换到非root用户
USER miniuri

# 设置工作目录
WORKDIR /app

# 从构建阶段复制jar文件
COPY --from=builder --chown=miniuri:miniuri /build/mini-uri-run/target/mini-uri-run.jar app.jar

# 设置环境变量
ENV TZ=Asia/Shanghai
ENV JAVA_OPTS="-Xms256m -Xmx512m -Djava.security.egd=file:/dev/./urandom -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
ENV ARGS=""

# 暴露端口
EXPOSE 8888

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8888/actuator/health || exit 1

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar $ARGS"]