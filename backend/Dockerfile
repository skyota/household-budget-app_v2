# ---- Build stage ----
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

ARG GRADLE_VERSION=8.11.1
RUN apt-get update -qq && \
    apt-get install -y --no-install-recommends wget unzip && \
    wget -q "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" -O /tmp/gradle.zip && \
    unzip -q /tmp/gradle.zip -d /opt && \
    ln -s /opt/gradle-${GRADLE_VERSION}/bin/gradle /usr/local/bin/gradle && \
    apt-get clean && rm -rf /var/lib/apt/lists/* /tmp/gradle.zip

ENV GRADLE_USER_HOME=/root/.gradle

# 依存解決を先にキャッシュ
COPY build.gradle settings.gradle ./
RUN gradle dependencies --no-daemon -q || true

COPY src ./src
RUN gradle bootJar --no-daemon -x test

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
