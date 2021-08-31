FROM adoptopenjdk:11-jre-hotspot
VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
#debug enabled
ENTRYPOINT ["java","-Xdebug", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar","/app.jar"]
