FROM java:8
EXPOSE 8080
ADD /target/recomendationservice-0.0.1-SNAPSHOT.jar recomendationservice-0.0.1-SNAPSHOT.jar
ADD key.json key.json
ENTRYPOINT ["java", "-jar", "recomendationservice-0.0.1-SNAPSHOT.jar"]
