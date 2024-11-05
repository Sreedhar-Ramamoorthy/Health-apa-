FROM openjdk:14-jdk
ARG DOCKER_SPRING_PROFILES_ACTIVE
ENV SPRING_PROFILES_ACTIVE $DOCKER_SPRING_PROFILES_ACTIVE
COPY target/health-apis-notification*.jar app.jar
EXPOSE 8080
# start application
ENTRYPOINT ["java", "-jar", "app.jar"]
