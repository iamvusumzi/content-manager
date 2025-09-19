# 📚 Content Manager API  

A **Spring Boot REST API** that manages content with JWT-based authentication.  
Built to demonstrate **clean layered architecture, validation, error handling, testing, and security**.  

---

## ✨ Features
- CRUD operations for **Content** (title, description, status)  
- DTOs with **validation**  
- **Global error handling** with consistent JSON responses  
- **JWT Authentication** (register → login → access protected endpoints)  
- **RESTful status codes** (`201 Created`, `204 No Content`, `404 Not Found`, etc.)  
- Unit & Controller **tests with JUnit 5, Mockito, MockMvc**  
- `.http` test scripts for **IntelliJ HTTP Client**  

---

## 🏗️ Tech Stack
- Java 17+  
- Spring Boot 3.x  
- Spring Security (JWT)  
- JPA (Hibernate)  
- H2 Database (in-memory)  
- JUnit 5, Mockito, MockMvc  

---

## 🚀 Getting Started  

### 1. Clone the repository
```bash
git clone https://github.com/iamvusumzi/content-manager.git
cd content-manager
```

### 2. Build & run
```bash
mvn spring-boot:run
```

API will be available at:  
👉 `http://localhost:8080/api`  

---

## 🔐 Authentication Flow

### Register
```http
POST /api/auth/register
{
  "username": "vusumzi",
  "password": "secret123",
  "role": "ROLE_USER"
}
```

### Login
```http
POST /api/auth/login
{
  "username": "vusumzi",
  "password": "secret123"
}
```

Response:
```json
{ "token": "eyJhbGciOiJIUzI1NiJ9..." }
```

### Use Token
Include JWT in request headers:  
```
Authorization: Bearer <token>
```

---

## 📚 API Endpoints

| Method | Endpoint             | Description                 | Auth Required |
|--------|----------------------|-----------------------------|---------------|
| POST   | `/api/auth/register` | Register new user           | ❌            |
| POST   | `/api/auth/login`    | Login, get JWT              | ❌            |
| GET    | `/api/contents`      | List all contents           | ✅            |
| GET    | `/api/contents/{id}` | Get content by ID           | ✅            |
| POST   | `/api/contents`      | Create new content          | ✅            |
| PUT    | `/api/contents/{id}` | Update existing content     | ✅            |
| DELETE | `/api/contents/{id}` | Delete content              | ✅            |

---

## 🧪 Testing  

### Run all tests
```bash
mvn test
```

### IntelliJ HTTP Client  
- File: `content-api.http`  
- Supports variables & token reuse via `http-client.env.json`.  
- Test full flow: register → login → CRUD with JWT.  

---

## 🛠️ Next Steps
- [ ] Add **role-based access** (`ROLE_ADMIN` for delete/update)  
- [ ] Add **integration tests with real JWT validation**  
- [ ] Add Dockerfile for containerized setup  
- [ ] Deploy to cloud (Heroku, Render, AWS)  

---

📌 Built by [**Vusumzi**](https://github.com/your-username) — practicing Spring Boot for SDE1 readiness 🚀  
