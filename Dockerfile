FROM openjdk:21-jdk AS build
RUN microdnf install findutils
WORKDIR /app
COPY build.gradle .
COPY settings.gradle .
COPY src src

COPY gradlew .
COPY gradle gradle

RUN chmod +x ./gradlew
RUN ./gradlew build -x test

FROM openjdk:21-jdk
VOLUME /tmp

COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 8080