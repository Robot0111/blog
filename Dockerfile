# ===========================
# Stage 1 - Build
# ===========================
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /build

COPY pom.xml .

RUN --mount=type=cache,target=/home/luql/.m2 mvn dependency:go-offline

COPY src src

RUN --mount=type=cache,target=/home/luql/.m2 mvn clean package -DskipTests

# ===========================
# Stage 2 - Runtime
# ===========================
FROM eclipse-temurin:21-jre-jammy

ENV TZ=Asia/ShangHai \
    LANG=C.UTF-8 \
    LC_ALL=C.UTF-8

WORKDIR /app

COPY --from=builder /build/target/*.jar app.jar

# 创建需要映射的目录
RUN mkdir -p \
    /app/config \
    /app/logs \
    /app/upload \
    /app/backup

EXPOSE 8080
ENTRYPOINT ["java","-XX:+UseContainerSupport","-XX:MaxRAMPercentage=75","-Dfile.encoding=UTF-8","-Dspring.config.additional-location=file:/app/config/","-jar","/app/app.jar"]
