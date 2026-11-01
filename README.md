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

## Systems Monitoring

Docker Desktop provides monitoring for the Tennis Score Tracker container.
`docker stats` reports container CPU utilization, memory consumption,
network I/O, block I/O, and process/thread counts.

### Build and Run

Build the application and run its unit tests:

```bash
mvn clean package
```

Build and start the monitoring container:

```bash
docker build -t tennis-score-tracker:monitoring .
docker run -d --name tennis-monitoring -p 8083:8080 tennis-score-tracker:monitoring
```

If the container already exists, start it with:

```bash
docker start tennis-monitoring
```

Access the application at http://localhost:8083.

### Monitor the Application

Identify the running container and monitor its resource usage:

```bash
docker ps --filter name=tennis-monitoring
docker stats tennis-monitoring
```

Observe idle readings for approximately 30 seconds before generating workload.

### Generate Workload

In another Terminal tab, repeatedly request the application for 120 seconds:

```bash
bash -c 'end=$((SECONDS + 120)); count=0; echo "Sending requests for 120 seconds..."; while (( SECONDS < end )); do curl -fsS --max-time 5 http://localhost:8083/ -o /dev/null || break; count=$((count + 1)); done; echo "Completed $count successful requests."'
```

Observe Docker statistics while requests are running. After the workload
finishes, wait approximately 30 seconds and observe recovery.

### Observed Results

| Condition | CPU utilization | Memory consumption |
|---|---:|---:|
| Initial idle baseline | 0.17% | 69.68 MiB |
| During workload | 6.14% | 155.4 MiB |
| After workload | 0.17% | 153.8 MiB |

These are individual observed readings, not averages. Three workload runs
completed 14,569, 15,178, and 13,476 successful requests. The workload
measurements shown above were collected during the third run, after
the earlier runs had already exercised the application.

CPU utilization increased during requests and returned to baseline afterward.
Memory remained above the initial baseline. JVM memory retention is a possible
explanation, but these container measurements alone cannot establish its cause
or demonstrate a memory leak.

### Operational Investigation

Because memory remained elevated, inspect container status for evidence
of restarts or an out-of-memory termination:

```bash
docker inspect tennis-monitoring --format 'Status={{.State.Status}} Running={{.State.Running}} OOMKilled={{.State.OOMKilled}} Restarts={{.RestartCount}}'
```

The result was:

```text
Status=running Running=true OOMKilled=false Restarts=0
```

The container remained running, with no reported out-of-memory termination
and zero restarts. This supports continued container operation but does not
prove that all aspects of application health are normal.

Screenshots and further analysis are included in the accompanying report.
