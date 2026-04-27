# Full-Stack Customer Management System

This repository contains a full-stack Customer Management application engineered for high-performance data processing and relationship management.

**Tech Stack**:
- **Backend:** Java 8, Spring Boot 2.7.x, Spring Data JPA, MariaDB, Alibaba EasyExcel
- **Frontend:** React JS 18, Axios, Tailwind CSS

## Architecture Highlights
- **Optimized Relational Schema:** Includes `master_country`, `master_city`, `customer`, `customer_mobile`, `customer_address`, and self-referencing `customer_family`.
- **High-Performance Bulk Operations:** Handles massive `.xlsx` data uploads (tested up to 1M rows) using background execution (`@Async`), EasyExcel's streamed `AnalysisEventListener`, and JDBC batched insertions (`batchUpdate` per 5,000 rows).
- **Graceful Error Resilience:** Prevents N+1 query problems via JPA `@EntityGraph`. Uses `ON DUPLICATE KEY UPDATE` / `IGNORE` natively to deduplicate records quickly based on their Unique NIC Number.
- **Micro UI state-management:** Uses minimal Tailwind structures focusing purely on functionality and multi-card relationship structures.

## Usage Guide

### 1. Database Configuration (MariaDB)
Ensure your MariaDB service is running natively or via docker (`sudo systemctl start mariadb`).
Login and create the target database:
```sql
CREATE DATABASE customer_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Update your `backend/src/main/resources/application.properties` with your localized credentials if they differ from the defaults:
```properties
spring.datasource.url=jdbc:mariadb://localhost:3306/customer_db?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=your_password_here
```

*Note:* Automatic DDL is disabled in favor of using `schema.sql`. The application will automatically execute the provided `src/main/resources/schema.sql` on startup to handle structural integrity.

### 2. Building & Running the Backend
From the `backend` directory, use Maven to build and start the server:
```bash
cd backend
mvn clean compile
mvn spring-boot:run
```
The server will now accept API requests at: `http://localhost:8080/api/customers`.

### 3. Building & Running the Frontend
From the `frontend` directory, perform your generic node start. Let it connect locally to React Scripts:
```bash
cd frontend
npm install
npm start
```
The application SPA will naturally be accessible at: `http://localhost:3000`.
