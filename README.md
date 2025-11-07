# 📚 Content Manager API

A **Spring Boot REST API** for managing content with **JWT authentication**, **role-based access control**, and **clean layered architecture**.  
Built to demonstrate production-grade design patterns including validation, error handling, and service abstraction.

---

## ✨ Features

- CRUD operations for **Content** (title, description, status)
- **Role-based access control**
    - **Users** can create, edit, and delete their own content.
    - **Admins** can view and delete any content.
- **Dynamic service injection** — controller delegates to user/admin services based on JWT role.
- **Content visibility rules:**
    - Only published content is public.
    - Draft and archived content visible only to its author.
- **JWT Authentication** (register → login → access protected endpoints)
- **Global exception handling** with consistent JSON responses
- **Programmatic admin seeding** on startup
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

### 1. Clone & run

```bash
git clone https://github.com/iamvusumzi/content-manager.git
cd content-manager
mvn spring-boot:run
```

API available at:  
👉 `http://localhost:8080/api`

---

## 🔐 Authentication Flow

### Register

```http
POST /api/auth/register
{
  "username": "vusumzi",
  "password": "secret123"
}
```

> All registered users default to `ROLE_USER`.  
> Only existing admins can register new admins.

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

Include JWT in request headers:
```
Authorization: Bearer <token>
```

---

## 📚 API Endpoints

| Method | Endpoint | Description | Access |
|--------|-----------|-------------|---------|
| POST | `/api/auth/register` | Register new user | Public |
| POST | `/api/auth/login` | Login, get JWT | Public |
| GET | `/api/contents` | List all published content | Public |
| GET | `/api/contents/my` | Get content by logged-in user | User |
| GET | `/api/contents/{id}` | View content by ID (restricted by visibility) | Authenticated |
| POST | `/api/contents` | Create new content | User/Admin |
| PUT | `/api/contents/{id}` | Update existing content | Author only |
| DELETE | `/api/contents/{id}` | Delete content | Author/Admin |

---

## 🧑‍💻 Roles & Permissions

| Role | Can Create | Can Edit | Can Delete    | Can View                |
|------|-------------|-----------|---------------|-------------------------|
| USER | ✅ Own content | ✅ Own content | ✅ Own content | ✅ Published/Own Content |
| ADMIN | ✅ Own content | ✅ Own content | ✅ Published/Own Content | ✅ Published/Own Content |

---

## 🧪 Testing

```bash
mvn test
```

Or using IntelliJ HTTP Client:
- File: `content-api.http`
- Environment: `http-client.env.json`

---

## 🧱 Developer Notes

- Include field for admin registration:  
  **adminSecret:** `appadminsecret123`
- Role & username extracted dynamically from `SecurityContext`.
- Controller auto-selects service implementation (User/Admin) per request.

---

## 🧭 Next Steps

### 1️⃣ Testing Roadmap
| Branch                 | Focus | Description |
|------------------------|--------|-------------|
| `test/auth`            | Security | Validate JWT parsing and filter behavior |
| `test/integration-tests` | End-to-End | Real JWTs, H2 database, and API interaction tests |

---

### 2️⃣ Logging & Monitoring (Pre-Deployment)
Before deployment, add structured logging **and monitoring** for observability and health insights.

- Implement **SLF4J + Logback** for file & console logging
- Add **Spring Boot Actuator** for real-time monitoring
    - Enable endpoints: `/actuator/health`, `/metrics`, `/info`, `/loggers`
- Prepare for future integrations:
    - **Prometheus/Grafana** for metrics visualization
    - **ELK stack** for centralized log analysis

---

### 3️⃣ Integration Tests
Use real JWTs via `JwtUtil`, run against full Spring context with an in-memory H2 database.  
Validate:
- Authenticated users → manage own content
- Admins → manage own content, delete any published content
- Guests → view *published* content

---

### 4️⃣ Future Enhancements
- **Notification Service** for admin deletions
- **Event Publisher/Listener** for async handling
- **Audit Trail** for user activity tracking

---

### 5️⃣ CI/CD Automation
Add **GitHub Actions** to:
- Run all test suites
- Build and containerize app
- Deploy automatically to **Heroku** or **AWS**
- Include static analysis and test coverage reporting

---

**Next up:**  
Start `test/auth`, then implement `feature/logging-monitoring` (Logback + Actuator) before moving to integration and deployment.


📌 Built by [**Vusumzi**](https://github.com/iamvusumzi) — evolving toward production-grade Spring Boot mastery 🚀
