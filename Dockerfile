FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn -B clean package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/target/my-blog-backend-0.0.1-SNAPSHOT.war app.war
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.war"]
