FROM openjdk:8-jdk-alpine
EXPOSE 8080
VOLUME /tmp
COPY target/kisalt-*.jar kisalt.jar
ENTRYPOINT ["java","-jar","/kisalt.jar"]
