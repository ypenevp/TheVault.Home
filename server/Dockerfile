# Stage 1: build
FROM eclipse-temurin:25-jdk AS builder

WORKDIR /app

COPY gradlew gradlew.bat settings.gradle.kts build.gradle.kts ./
COPY gradle ./gradle

#This command removes \r from every line, converting the Windows line endings to Linux-compatible line endings.
RUN sed -i 's/\r$//' gradlew
# for linux access
RUN chmod +x gradlew
RUN ./gradlew --no-daemon dependencies

COPY src ./src
RUN ./gradlew --no-daemon bootJar -x test

# same like: cp /app/build/libs/server-0.0.1-SNAPSHOT.jar app.jar
RUN find build/libs -name "*.jar" ! -name "*-plain.jar" -exec cp {} app.jar \;

# Stage 2: runtime
FROM eclipse-temurin:25-jre AS runtime

# add group & user for security
RUN groupadd --system spring && useradd --system --gid spring thevault

WORKDIR /app
COPY --from=builder /app/app.jar app.jar

RUN chown thevault:spring app.jar
USER thevault:spring

EXPOSE 7070
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]