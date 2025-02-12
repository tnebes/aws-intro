FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S app && adduser -S app -G app && apk add --no-cache curl
USER app:app
COPY --from=builder /app/target/*.jar application.jar
COPY --from=builder /app/target/lib /app/lib
EXPOSE 8082
ENTRYPOINT ["java", \
            "-XX:+UseContainerSupport", \
            "-XX:MaxRAMPercentage=75.0", \
            "-Djava.security.egd=file:/dev/./urandom", \
            "-cp", "application.jar:lib/*", \
            "com.tnebes.orderprocessing.Main"] 