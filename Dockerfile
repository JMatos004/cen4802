FROM eclipse-temurin:17-jre

WORKDIR /app

COPY target/tennis-score-tracker-1.0.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]