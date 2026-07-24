# Module 08 Lab — Introduction to Docker

## Objective
Run a MySQL database as a Docker container, then write a `Dockerfile` for the Stock Tracker
Spring Boot application, build and run it as a second container, and connect the two together.
Finally, add a simple HTML/JS frontend served from inside the Spring Boot container.

## Prerequisites
- Docker Desktop installed and running (`docker --version` should respond)
- Java 17 and Maven installed locally

## What is provided
The `labs/08-docker/` folder contains a complete Spring Boot application (the stocks REST API
from Module 06). It connects to MySQL on `localhost:3306`. On startup it seeds three stocks.

---

## Steps

### Step 1 — Run MySQL in a container
No MySQL installation needed. Pull and run the official MySQL 8 image:

```bash
docker run -d \
  --name stocksdb \
  -e MYSQL_ROOT_PASSWORD=rootpass \
  -e MYSQL_DATABASE=stocksdb \
  -e MYSQL_USER=appuser \
  -e MYSQL_PASSWORD=apppass \
  -p 3306:3306 \
  mysql:8
```

Wait about 10 seconds then check it is ready:

```bash
docker logs stocksdb
# Look for the line: ready for connections
```

---

### Step 2 — Verify the app runs locally against the containerised MySQL
```bash
cd labs/08-docker
mvn spring-boot:run
```

Visit `http://localhost:8080/api/stocks` — you should see a JSON array of three stocks
being served from MySQL running inside Docker.

Stop the app (`Ctrl+C`) before moving on.

---

### Step 3 — Write the Dockerfile
Create a file called `Dockerfile` (no extension) in the `labs/08-docker/` folder.
Complete each TODO:

```dockerfile
# Stage 1: build the JAR
# TODO 1: Start FROM the maven:3.9-eclipse-temurin-17 image. Name this stage 'build'.
FROM ???

WORKDIR /app

# TODO 2: Copy pom.xml into the working directory
COPY ??? .

# TODO 3: Download all dependencies into the image layer cache
#         Hint: mvn dependency:go-offline -q
RUN ???

# TODO 4: Copy the src directory into the image
COPY ??? ./src

# TODO 5: Package the application, skipping tests
#         Hint: mvn package -DskipTests -q
RUN ???

# Stage 2: run the JAR in a minimal image
# TODO 6: Start FROM eclipse-temurin:17-jre-alpine
FROM ???

WORKDIR /app

# TODO 7: Copy the JAR from the build stage into this image as app.jar
#         Hint: use --from=build and /app/target/*.jar
COPY ??? app.jar

# TODO 8: Tell Docker which port the app listens on
EXPOSE ???

# TODO 9: Set the command to run the JAR
#         Hint: ENTRYPOINT with JSON array syntax
ENTRYPOINT ???
```

---

### Step 4 — Build the image
```bash
docker build -t stock-app:v1 .
```

Watch the output. Run the build a second time and observe the CACHED layers:

```bash
docker build -t stock-app:v1 .
```

---

### Step 5 — Run the app container
The app container needs to reach MySQL. We pass the datasource URL as an environment variable
using `host.docker.internal` so the app container can reach the MySQL container via the host:

```bash
docker run -d \
--network stock-network \
  -p 8081:8080 \
  --name stock-app \
  --add-host=host.docker.internal:host-gateway \
  -e SPRING_DATASOURCE_URL="jdbc:mysql://stocksdb:3306/stocksdb?useSSL=false&allowPublicKeyRetrieval=true" \
  -e SPRING_DATASOURCE_USERNAME=appuser \
  -e SPRING_DATASOURCE_PASSWORD=apppass \
  stock-app:v1
```

Verify it is running and test the API:

```bash
docker ps
docker logs stock-app
curl http://localhost:8080/api/stocks
```

---

### Step 6 — Add a frontend
Create `src/main/resources/static/index.html` with an HTML page that:
1. Has a heading "Stock Tracker"
2. Uses `fetch('/api/stocks')` to load stocks from the API
3. Displays them in a `<table>` with columns: Symbol, Company, Sector, Exchange

Rebuild and re-run:

```bash
docker stop stock-app && docker rm stock-app
docker build -t stock-app:v2 .
docker run -d -p 8080:8080 --name stock-app \
  -e SPRING_DATASOURCE_URL="jdbc:mysql://host.docker.internal:3306/stocksdb?useSSL=false&allowPublicKeyRetrieval=true" \
  -e SPRING_DATASOURCE_USERNAME=appuser \
  -e SPRING_DATASOURCE_PASSWORD=apppass \
  stock-app:v2
```

Open `http://localhost:8080` in a browser — your table should be populated with stocks.

---

### Step 7 — Explore useful Docker commands
```bash
docker ps                          # list running containers
docker ps -a                       # include stopped containers
docker logs stock-app              # view application output
docker exec -it stock-app sh       # shell inside the container
docker stop stock-app              # stop
docker rm stock-app                # remove
docker images                      # list local images
docker rmi stock-app:v1            # remove an image
docker stop stocksdb && docker rm stocksdb
```

---

## Acceptance Criteria
- MySQL runs as a Docker container with no local MySQL installation
- `docker build` completes without errors
- The app container starts and `GET /api/stocks` returns three stocks
- The second build (unchanged `pom.xml`) shows CACHED for the dependency layer
- `http://localhost:8080` serves your HTML page with stocks loaded from the API

## Key Questions
1. Why do we COPY `pom.xml` and run `mvn dependency:go-offline` before copying `src/`?
2. Why does the final image use `eclipse-temurin:17-jre-alpine` instead of the Maven image?
3. What is the difference between `EXPOSE` and `-p` in `docker run`?
4. Why do we pass `SPRING_DATASOURCE_URL` as an environment variable rather than baking it into `application.properties`?
5. What would happen to the data in MySQL if you ran `docker rm stocksdb`?
