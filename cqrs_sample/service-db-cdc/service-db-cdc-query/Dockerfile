# Start with a base image containing Java runtime
FROM eclipse-temurin:17-jdk

# Make port 8888 available to the world outside this container
EXPOSE 8888

# The application's jar file
ARG JAR_FILE=target/service-db-cdc-query-0.0.1-SNAPSHOT.jar

# Add the application's jar to the container
ADD ${JAR_FILE} service-db-cdc-query.jar

# Run the jar file 
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/service-db-cdc-query.jar"]