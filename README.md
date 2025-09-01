# Full REST API with Docker

## Prerequisites
- Docker
- Docker Compose

## How to run

1. Clone the repository
2. Run the application:
```bash docker-compose up --build``` 

3. Access the API at: http://localhost:8080

4. Access Swagger UI at: http://localhost:8080/docs

## API Endpoints
1. Authors: /authors
2. Books: /books

##  Database
- running on port 5432
- Auto-created schema on startup

text

## 6. Important Notes

1. **Database Connection**: In your Spring Boot application, the database URL now uses `db` (the service name from Docker Compose) instead of `localhost`.
2. **Dependency Order**: The `depends_on` ensures PostgreSQL starts before your Spring Boot app.
3. **Network**: Both services are on the same Docker network, allowing them to communicate.
4. **Data Persistence**: The `volumes` section ensures PostgreSQL data persists between container restarts.

## 7. Running the Application

Users can now simply:
```bash
git clone <your-repo>
cd <project-directory>
docker-compose up --build
```
The application will:
- Build the Spring Boot app
- Start PostgreSQL
- Run the application with proper database connection
- Expose ports 8080 (app) and 5432 (database)