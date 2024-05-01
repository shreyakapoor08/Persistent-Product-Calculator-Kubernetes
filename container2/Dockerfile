FROM maven:3.8.3-openjdk-17 AS build
COPY . /home/app
WORKDIR /home/app
RUN mvn -f /home/app/pom.xml clean install

FROM openjdk:17
COPY --from=build /home/app/target/container-2.jar /app/container-2.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/container-2.jar"]