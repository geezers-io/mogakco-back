FROM openjdk:11

RUN mkdir /app

COPY build/libs/*.jar /app/app.jar

CMD ["java", "-jar", "/app/app.jar"]