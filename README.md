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
