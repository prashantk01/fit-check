# FitCheck 🏋️‍♂️

A 3-phase project to build a fitness tracker backend + frontend + AI integration.  
The goal is to practice **Spring Boot (Java backend)**, **React frontend**, and finally **AI integration**.  

---

## Project Roadmap (High Level)

### Phase 1 – Core Backend
- Spring Boot backend with MongoDB
- CRUD APIs for BMI tracking
- API documentation with Swagger
- Secure authentication (JWT)
- Dockerized setup

### Phase 2 – Frontend
- React UI for tracking and visualizing progress
- Charts & goals dashboard
- Basic auth integration with backend

### Phase 3 – AI/LLM Integration
- Personalized recommendations using LLM/AI
- Daily insights from BMI & goals
- Smart agentic workflows

---

## Progress (Step-by-Step)

### ✅ Step 1.1: Project Setup
- Initialized Spring Boot 3 project with Maven.
- Added dependencies:  
  - `spring-boot-starter-web`  
  - `spring-boot-starter-data-mongodb`  
  - `spring-boot-starter-security`  
  - `spring-boot-starter-validation`  
  - `springdoc-openapi-starter-webmvc-ui`
- Verified app is running at `http://localhost:8080`.

---

### ✅ Step 1.2: Docker Compose Setup
- Added `docker-compose.yml` with:
  - `fitcheck-mongo` (MongoDB)
  - `fitcheck-mongo-express` (MongoDB Web UI at `http://localhost:8081`)
- Both containers run with a single command:
  ```bash
  docker-compose up -d

### ✅ Step 1.3: Dockerfile for Backend, mongo and all
- Added Dockerfile for backend Spring Boot service.
- Now backend can run inside Docker:
```bash
 docker-compose up --build
```

### ✅ Step for 1.4: env variable setup
- .env file contains:
- Mongo root username & password
- Mongo database name
- docker-compose.yml references .env via env_file:
- application.properties uses ${SPRING_DATA_MONGODB_URI} to read DB connection dynamically


### ✅ Step 1.5: Health Check Endpoint with DTO
- Implemented `/api/v1/health` endpoint.
- Returns structured JSON response using a `HealthResponse` DTO:
  ```json
  {
    "status": "UP",
    "profile": "dev",
    "name": "FitCheck",
    "version": "1.0.0",
    "timestamp": ,
    "message": "FitCheck Application is running..!"
  }
### ✅ Step 1.6: CORS Configuration
- Added global CORS configuration using `CorsFilter`.
- Allows requests from:
  - `http://localhost:3000` (React dev frontend)
  - `https://fitcheck.app` (future production domain)
- Supports HTTP methods: GET, POST, PUT, DELETE, OPTIONS.
- Allows Authorization headers and credentials (JWT ready).
- Ensures frontend can communicate with backend without CORS errors.

### ✅ Step 1.8: Swagger/OpenAPI Setup
- Added Swagger/OpenAPI using `springdoc-openapi-starter-webmvc-ui`.
- Configured `OpenApiConfig` to provide:
  - Custom title: "FitCheck API Documentation"
  - Version: 1.0.0
  - Description: REST APIs for FitCheck backend
  - Contact information and license
  - External documentation link to GitHub repository
- Endpoints are auto-detected from `@RestController` classes.
- Access API documentation and test endpoints at: http://localhost:8080/swagger-ui/index.html