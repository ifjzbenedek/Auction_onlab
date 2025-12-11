FROM gradle:8.5-jdk21 AS build

WORKDIR /app

# Copy only build files first for dependency caching
COPY build.gradle settings.gradle ./
COPY gradle/ gradle/
COPY gradlew ./

# Download dependencies - this layer will be cached if build files don't change
RUN ./gradlew dependencies --no-daemon

# Copy source code
COPY . .

# Build the application
RUN ./gradlew build -x test --no-daemon

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8081

CMD ["java", "-jar", "app.jar"]