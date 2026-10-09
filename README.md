# AI-Powered Enterprise Knowledge Assistant (RAG)

An enterprise-grade Knowledge Assistant built with **Spring Boot 3.3.5**, **Java 21**, **Spring Security**, and **PostgreSQL (Neon)**. The application enables organizations to securely manage enterprise documents (PDF, DOCX, TXT) with granular Role-Based Access Control (RBAC), multi-tier user registration & approval workflows, and an upcoming Retrieval-Augmented Generation (RAG) AI query engine.

---

## 🏗️ Architecture & Technology Stack

- **Backend Framework**: Spring Boot 3.3.5 / Java 21
- **Security & Auth**: Spring Security 6, Stateless JWT (HMAC-SHA256) with DB-persisted Refresh Tokens & Revocation
- **Database & ORM**: PostgreSQL (Neon Cloud) with Spring Data JPA & Hibernate
- **Vector Engine (Upcoming)**: `pgvector` & Spring AI
- **Document Processing**: Multipart file ingestion supporting PDF, DOCX, TXT with disk storage & metadata tracking
- **Build Tool**: Apache Maven

---

## 👥 Role-Based Access Control (RBAC) & Hierarchy

The system defines four distinct roles and strict hierarchy permissions:

| Role | Access Level | Permissions |
| :--- | :--- | :--- |
| `ROLE_ADMIN` | `ADMIN` | Full system control. Can view all users, approve/reject Managers & Employees, upload documents with any access level, and delete documents. |
| `ROLE_MANAGER` | `MANAGER` | Department manager. Can approve/reject Employee registrations, upload documents with `MANAGER`, `EMPLOYEE`, or `PUBLIC` access levels, and view permitted documents. |
| `ROLE_EMPLOYEE` | `EMPLOYEE` | Standard enterprise staff. Requires Manager or Admin approval to activate. Can access `EMPLOYEE` and `PUBLIC` documents. |
| `ROLE_PUBLIC` | `PUBLIC` | Limited external access. Auto-activated upon registration (`ACTIVE`). Restricted to `PUBLIC` documents only. |

### Account Lifecycle States
- `PENDING_APPROVAL`: Default state for `MANAGER` and `EMPLOYEE` registrations.
- `ACTIVE`: Required to log in and receive JWT tokens.
- `REJECTED`: Accounts explicitly denied by an Admin or Manager.
- `SUSPENDED`: Temporarily deactivated accounts.

---

## 🚀 Getting Started

### Prerequisites
- Java 21 (JDK 21)
- Maven 3.9+
- PostgreSQL database (or Neon cloud database instance)

### Configuration
Update `src/main/resources/application.properties` with your database credentials:
```properties
server.port=8081

spring.datasource.url=jdbc:postgresql://<HOST>/<DATABASE>?sslmode=require
spring.datasource.username=<USERNAME>
spring.datasource.password=<PASSWORD>

# JWT Secret Key
secret=${JWT_SECRET:<YOUR_SECURE_KEY>}

# Upload Directory
app.upload.dir=uploads