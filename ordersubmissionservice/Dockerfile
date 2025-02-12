FROM maven:3.9.9-eclipse-temurin-21 AS builder
COPY pom.xml .
COPY .mvn/ .mvn/
COPY mvnw .
COPY mvnw.cmd .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S spring && adduser -S spring -G spring && apk add --no-cache curl
USER spring:spring
COPY --from=builder /target/*.jar application.jar
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s CMD ["curl", "-f", "http://localhost:8080/actuator/health"]
ENTRYPOINT ["java", \
            "-XX:+UseContainerSupport", \
            "-XX:MaxRAMPercentage=75.0", \
            "-Djava.security.egd=file:/dev/./urandom", \
            "-jar", "application.jar"] 