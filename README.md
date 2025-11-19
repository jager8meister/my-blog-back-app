# My Blog Backend

REST API for a simple blogging platform built with Spring Boot 3 / Java 21. The service stores posts, comments and likes in an in-memory H2 database and exposes CRUD endpoints that the frontend consumes. OpenAPI documentation is available via Swagger UI.

## Features
- Create, list, update and delete posts, including uploading/downloading post images.
- Manage comments for every post.
- Increment likes counters.
- Health check endpoint at `/api/health`.

## Tech Stack
- Spring Boot 3 (Web, Data JPA, Data JDBC, Validation).
- H2 in-memory database for persistence.
- MapStruct and Lombok for DTO mapping and boilerplate reduction.
- SpringDoc OpenAPI for API documentation.

## Prerequisites
- JDK 21+
- Maven 3.9+
- (Optional) Docker + Docker Compose if you want to run the backend together with the frontend.

## Build & Test
```bash
# Run unit/integration tests
mvn test

# Build an executable JAR (target/my-blog-backend-0.0.1-SNAPSHOT.jar)
mvn clean package
```

## Run Locally
```bash
# Start the backend from sources
mvn spring-boot:run

# or run the previously built jar
java -jar target/my-blog-backend-0.0.1-SNAPSHOT.jar
```
The application listens on `http://localhost:8080`. Swagger UI lives at `http://localhost:8080/swagger-ui/index.html`.

### Run via Docker Compose
From the repository root (`my-blog-front-app`) start the entire stack:
```bash
docker compose up -d --build
```
Frontend will be available at `http://localhost:8989`, backend at `http://localhost:8080`.

## Using the API
All endpoints are prefixed with `/api`.
- `GET /api/posts` - list posts with pagination/search.
- `POST /api/posts` - create a post (JSON body with `title`, `text`, `tags`).
- `PUT /api/posts/{id}` / `DELETE /api/posts/{id}` - update or delete a post.
- `POST /api/posts/{id}/image` - multipart image upload.
- `GET /api/posts/{id}/image` - download the stored image.
- `GET /api/posts/{id}` - detailed view of a post.
- `GET|POST|PUT|DELETE /api/posts/{postId}/comments` - manage comments.
- `POST /api/posts/{postId}/likes` - increment likes counter.
- `GET /api/health` - service readiness probe.

Refer to Swagger UI for the complete contract and sample payloads.
