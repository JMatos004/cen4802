# cen4802

**Author:** Jeffrey Matos

## Tennis Score Tracker

A small Java web application that runs locally and allows a user to enter two tennis players and their current game scores. The application uses Java's built-in HTTP server and contains logic to determine whether the set is tied, which player is leading, or whether a player has won the set.

## Requirements

- Java 17 or newer
- Apache Maven 3.9 or newer
- Git

## Project Structure

```text
cen4802/
├── pom.xml
├── README.md
├── .gitignore
└── src/
    └── main/
        └── java/
            └── TennisScoreApp.java



## Unit Testing

Automated unit testing is implemented using JUnit 5. The tests verify multiple input scenarios in the application's score-processing logic, including normal scores, negative scores, scores above the allowed maximum, and invalid input.

Test source files are located in:

```text
src/test/java/TennisScoreAppTest.java

```

Run the unit tests with:

```text
./apache-maven-3.9.16/bin/mvn test
```

Run the complete automated build, including the unit tests, with:

```text
./apache-maven-3.9.16/bin/mvn clean package
```

## Application Logging

The Tennis Score Tracker uses Java's built-in java.util.logging framework.

### Configuration
Logging settings are stored in src/main/resources/logging.properties.

Logs are written to the console using ConsoleHandler and SimpleFormatter.
Each entry includes a timestamp, severity level, source, and message.

### Logging Levels
- INFO: Page visits, score submissions, and completed sets.
- WARNING: Invalid scores and unsupported HTTP methods.
- SEVERE: Unexpected request-processing errors.
- FINE: HTTP requests and form-parsing diagnostics.

### Running the Application
Build and test using Maven:

    ./apache-maven-3.9.16/bin/mvn clean test package

Run with logging enabled:

    java -Djava.util.logging.config.file=src/main/resources/logging.properties -jar target/tennis-score-tracker-1.0.jar

To save logs to a file:

    java -Djava.util.logging.config.file=src/main/resources/logging.properties -jar target/tennis-score-tracker-1.0.jar > application.log 2>&1

### Examining Logs
Count INFO entries:

    grep -c ' INFO ' application.log

Filter completed sets:

    grep 'Set completed' application.log

Filter warnings:

    grep ' WARNING ' application.log

### Logging Configuration Changes
The minimum logging level can be changed in logging.properties.

INFO records normal application activity and more severe events.
FINE additionally records detailed diagnostic information.

Restart the application after changing the configuration.

### Logging Demonstration
Testing generated 309 INFO entries through application activity.
Filtering identified completed-set events and repeated scoring patterns.

Invalid score input generated a WARNING with a NumberFormatException
stack trace while allowing the application to continue running.

Logs can be viewed in Terminal or redirected to a file.
File logs remain available until deleted, overwritten, or rotated.
Container logs can also be accessed using Docker or Kubernetes tools.

The accompanying assignment document contains screenshots and analysis.
