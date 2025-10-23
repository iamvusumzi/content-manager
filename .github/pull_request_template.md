### 📝 Pull Request Template

**Title:**  
`Implement role-based access model and updated visibility rules`

---

### 🔍 Summary
This PR refactors the application’s access control and content visibility rules.  
It introduces clear separation between **public**, **authenticated**, and **admin** operations across the API.  

---

### ✅ Changes Made
- Updated **Spring Security** configuration:
  - Public access to `GET /api/contents/**` (published content only)
  - Authenticated access for content creation and ownership operations
  - Admin-only access for system-wide content management
- Refactored **ContentService** for ownership and role verification
- Improved authorization logic in update/delete endpoints
- Added **role-based checks** (User vs. Admin)
- Updated **README.md** with:
  - New access model table
  - Simplified endpoints list
  - Layered architecture diagram

---

### 🧪 Testing
- Verified public users can fetch only published content  
- Verified authenticated users can manage their own drafts, archived, and published content  
- Verified admin role can view and manage all content  
- Tested locally via Postman and `mvn test` suite  

---

### 🧭 Next Steps
- Add admin dashboard endpoints  
- Introduce role elevation (promote/demote user)
- Extend integration tests for visibility and permissions  

---

### 🧑‍💻 Author
**Vusumzi Msengana**  
Spring Boot Developer | Portfolio Content Manager  
