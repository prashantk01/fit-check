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

### Step 1.2.5: Code Structure Added core layers
- `model/user/User.java` → User entity
- `model/profile/Profile.java` → Profile entity
- `repository` → Spring Data MongoDB repositories (bridge between DB and service layer)
- `service` → Business logic layer for user and profile
- `model` → schema model for user and profile


### Step 1.2.8 Added DTO, Mapper, Controller, Service Layer logic for User and Profile
1. DTO Structure
- Added User DTOs and Profile DTOs for request and response handling:
- UserCreate – Used for user registration, includes validation rules.
- UserResponse – Returned in API responses for user data.
- ProfileCreate – Used for profile creation/update, includes validation for BMI calculation fields.
- ProfileResponse – Returned in API responses for profile data.
2. Validation
- DTOs now leverage jakarta.validation annotations for request validation:
- @NotBlank, @Size, @Email for strings
- @Positive for numeric fields
- @NotNull for required fields
3. Mappers
- Added UserMapper and ProfileMapper:
- Convert DTOs to Entities and Entities to Response DTOs.
- Encapsulates logic for mapping and ensures clean controller-service separation.

### Step 1.2.9 Added Profile Update Mapper, Service layer logic
- Enabling only non-null fields to get updated while updating user profile

### Step 1.3.3 AuthController Implemented, Custom error handling, BcryptPasswordEncoder
- Added /register /login Authentication end point (username/email/password)
- Badcredentials error handling
- Password is encoded with BcryptPasswordEncoder

### 🧩 Step 1.3.8 — JWT Authentication

- **JWT Generation**
  - A JWT is generated on successful **login** or **register**.
  - It includes:
    - `username, roles, expiry, createdAt`
  - The token is signed using the app’s secret key via `Jwts.builder()`.

- **Client Request**
  - Every client request must include the token in the header:
    ```http
    Authorization: Bearer <JWT_Header.Payload.Signature>
    ```

- **JwtAuthFilter**
  - All incoming requests pass through `JwtAuthFilter`.
  - It **skips** public endpoints:
    ```
    /api/auth/**
    /api/v1/health
    /swagger-ui/**
    ```
  - For all other routes, it **validates**:
    - Token signature  
    - Token expiry  

- **Unauthorized Handling**
  - Returns `401 Unauthorized` if the token is **missing** or **invalid**.

- **Security Configuration**
  - `SecurityConfig` permits only the public routes listed above.
  - All remaining endpoints require a **valid JWT** for access.

---

### Step 1.3.9 - Role Based Access control (RBAC) 
- Added ADMIN and USER roles with JWT-based role claims.
- Implemented SecurityUtil to fetch current user info and roles from context.
- Enforced access: ADMIN can manage all users, USER can only access their own data.
- Added global handler for AccessDeniedException returning clean 403 Forbidden responses.