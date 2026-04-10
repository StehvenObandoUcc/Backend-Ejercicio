FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B
COPY src/ src/
RUN ./mvnw clean package -DskipTests -B

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/decorator-api-0.0.1-SNAPSHOT.jar app.jar
ENV PORT=8080
EXPOSE ${PORT}
ENTRYPOINT ["sh", "-c", "java -Xmx256m -Xms128m -Dserver.port=${PORT} -jar app.jar"]
