FROM amazoncorretto:21-alpine

WORKDIR /app

COPY build/libs/*.jar dekk-api.jar

ENV TZ=Asia/Seoul

ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-jar", "dekk-api.jar"]
