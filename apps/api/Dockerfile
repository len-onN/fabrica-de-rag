FROM maven:3.9-eclipse-temurin-21
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests -B
CMD ["java", "-jar", "target/fabricarag-0.0.1-SNAPSHOT.jar"]
