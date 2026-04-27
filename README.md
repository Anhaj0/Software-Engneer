# Full-Stack Customer Management Starter

This repository contains:
- **backend/**: Maven + Spring Boot 2.7.x (Java 8)
- **frontend/**: React 18 + Tailwind CSS

## Project Layout

```text
.
├── backend
│   ├── pom.xml
│   └── src
│       ├── main
│       │   ├── java/com/example/backend/BackendApplication.java
│       │   └── resources/application.properties
│       └── test/java/com/example/backend/BackendApplicationTests.java
├── frontend
│   ├── package.json
│   ├── tailwind.config.js
│   ├── postcss.config.js
│   ├── public/index.html
│   └── src
│       ├── index.js
│       ├── index.css
│       ├── App.js
│       ├── api.js
│       └── components
│           ├── CustomerForm.jsx
│           ├── CustomerList.jsx
│           └── BulkUpload.jsx
└── README.md
```

## Backend Setup (Spring Boot)

### Prerequisites
- Java 8
- Maven 3.6+
- MariaDB

### MariaDB Configuration
1. Create database:
   ```sql
   CREATE DATABASE customer_db;
   ```
2. Update credentials in `backend/src/main/resources/application.properties`:
   - `spring.datasource.username`
   - `spring.datasource.password`

Default JDBC URL is:
```properties
spring.datasource.url=jdbc:mariadb://localhost:3306/customer_db
```

### Run backend
```bash
cd backend
mvn spring-boot:run
```

Backend starts at `http://localhost:8080`.

## Frontend Setup (React)

### Prerequisites
- Node.js 18+
- npm 9+

### Install and run
# Software Engineer Project Setup & API Guide

## 1) Prerequisites
Install the following before running the application:

- **Java 8 (JDK 1.8)**
- **Apache Maven 3.6+**
- **Node.js 16+** (npm included)
- **MariaDB 10.5+**

Quick version checks:

```bash
java -version
mvn -version
node -v
npm -v
mariadb --version
```

---

## 2) MariaDB Setup

### 2.1 Start MariaDB
Start MariaDB service locally (command varies by OS):

```bash
# Linux (systemd)
sudo systemctl start mariadb
```

### 2.2 Create database and credentials
Login as an admin user and run:

```sql
CREATE DATABASE file_processing_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE USER 'app_user'@'localhost' IDENTIFIED BY 'app_password';
GRANT ALL PRIVILEGES ON file_processing_db.* TO 'app_user'@'localhost';
FLUSH PRIVILEGES;
```

### 2.3 Load schema
Use one of the following patterns based on how your backend is configured:

#### Option A: SQL schema file (recommended in shared environments)
If your repository contains a schema file such as `src/main/resources/schema.sql`:

```bash
mariadb -u app_user -p file_processing_db < src/main/resources/schema.sql
```

If there is seed data (for example `data.sql`):

```bash
mariadb -u app_user -p file_processing_db < src/main/resources/data.sql
```

#### Option B: Hibernate/JPA auto-DDL (development only)
If `spring.jpa.hibernate.ddl-auto=update` (or `create`) is enabled, schema is generated on backend start.

---

## 3) Backend configuration (`application.properties`)
Configure datasource and JPA in `src/main/resources/application.properties`.

Example:

```properties
# Server
server.port=8080

# Datasource
spring.datasource.url=jdbc:mariadb://localhost:3306/file_processing_db
spring.datasource.username=app_user
spring.datasource.password=app_password
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.database-platform=org.hibernate.dialect.MariaDBDialect

# Multipart upload
spring.servlet.multipart.max-file-size=200MB
spring.servlet.multipart.max-request-size=200MB

# Async executor tuning (example)
app.async.core-pool-size=4
app.async.max-pool-size=8
app.async.queue-capacity=100
```

> For production, use environment variables/secrets instead of hardcoded credentials.

---

## 4) Run and Test Commands

### Backend
From project root:

```bash
# Build backend
mvn clean package

# Run tests
mvn test

# Start backend (Spring Boot)
mvn spring-boot:run
```

If the packaged JAR is generated:

```bash
java -jar target/*.jar
```

### Frontend
From the frontend directory (example: `frontend/`):

```bash
cd frontend
npm install
npm start
```

Frontend starts at `http://localhost:3000`.

By default, API calls target `http://localhost:8080/api`. To change this, set:

```bash
REACT_APP_API_BASE_URL=http://localhost:8080/api
```

## Notes
- Bulk upload component is wired for Excel files (`.xlsx`, `.xls`) and expects a backend endpoint at:
  - `POST /api/customers/bulk-upload`
- Customer list and create actions expect:
  - `GET /api/customers`
  - `POST /api/customers`
Optional production build:

```bash
npm run build
```

---

## 5) API Summary and Examples

Base URL:

```text
http://localhost:8080/api
```

### 5.1 Create resource
- **POST** `/records`

Request:

```json
{
  "name": "Quarterly Report",
  "description": "Finance Q1 data"
}
```

Response (`201 Created`):

```json
{
  "id": 101,
  "name": "Quarterly Report",
  "description": "Finance Q1 data",
  "status": "CREATED",
  "createdAt": "2026-04-27T07:30:00Z"
}
```

### 5.2 Read all resources
- **GET** `/records`

Response (`200 OK`):

```json
[
  {
    "id": 101,
    "name": "Quarterly Report",
    "description": "Finance Q1 data",
    "status": "COMPLETED"
  }
]
```

### 5.3 Read one resource
- **GET** `/records/{id}`

Example:

```bash
curl http://localhost:8080/api/records/101
```

Response (`200 OK`):

```json
{
  "id": 101,
  "name": "Quarterly Report",
  "description": "Finance Q1 data",
  "status": "COMPLETED"
}
```

### 5.4 Update resource
- **PUT** `/records/{id}`

Request:

```json
{
  "name": "Quarterly Report (Updated)",
  "description": "Finance Q1 data - revised"
}
```

Response (`200 OK`):

```json
{
  "id": 101,
  "name": "Quarterly Report (Updated)",
  "description": "Finance Q1 data - revised",
  "status": "COMPLETED"
}
```

### 5.5 Delete resource
- **DELETE** `/records/{id}`

Response (`204 No Content`)

### 5.6 Upload file
- **POST** `/records/{id}/upload`
- `Content-Type: multipart/form-data`
- Form field name: `file`

Example:

```bash
curl -X POST "http://localhost:8080/api/records/101/upload" \
  -H "Content-Type: multipart/form-data" \
  -F "file=@/path/to/large-input.csv"
```

Response (`202 Accepted`):

```json
{
  "recordId": 101,
  "jobId": "job_8f3c8a31",
  "status": "QUEUED",
  "message": "File received and queued for processing"
}
```

### 5.7 Check async job status
- **GET** `/jobs/{jobId}/status`

Response while running (`200 OK`):

```json
{
  "jobId": "job_8f3c8a31",
  "recordId": 101,
  "status": "PROCESSING",
  "progress": 65,
  "startedAt": "2026-04-27T07:35:12Z"
}
```

Response when complete (`200 OK`):

```json
{
  "jobId": "job_8f3c8a31",
  "recordId": 101,
  "status": "COMPLETED",
  "progress": 100,
  "finishedAt": "2026-04-27T07:36:02Z",
  "result": {
    "rowsProcessed": 124300,
    "rowsFailed": 0
  }
}
```

---

## 6) Troubleshooting

### Large file upload issues
1. **HTTP 413 (Payload Too Large)**
   - Increase limits:
     - `spring.servlet.multipart.max-file-size`
     - `spring.servlet.multipart.max-request-size`
   - Confirm reverse proxy limits (for example NGINX `client_max_body_size`).

2. **Upload times out**
   - Increase server/proxy timeout values.
   - Prefer asynchronous processing with immediate `202 Accepted` responses.

3. **OutOfMemoryError during processing**
   - Avoid loading entire files into memory.
   - Stream file parsing in chunks/batches.
   - Increase JVM heap only after optimizing processing logic.

### Async processing behavior
1. **Job remains in `QUEUED`**
   - Verify async executor settings and thread pool size.
   - Check if workers are blocked by long-running tasks.

2. **Job status never updates**
   - Confirm job status persistence/update logic is called after each stage.
   - Verify transaction boundaries around job state updates.

3. **Duplicate processing**
   - Enforce idempotency (for example, hash check, unique upload token, or lock by record ID + checksum).

4. **Backend restart during processing**
   - In-memory queues lose state on restart.
   - Use persistent queue/job storage if reliability is required.

---

## Suggested Local Workflow

1. Start MariaDB and create DB/user.
2. Configure `application.properties`.
3. Start backend.
4. Start frontend.
5. Create a record, upload file, poll job status endpoint until complete.
