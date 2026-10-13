# Monitoring Project – Architecture

## 1. Overview
A Spring Boot REST backend using a layered architecture. Requests are tested from Postman; there is no frontend yet.

| Item | Value |
|---|---|
| Framework | Spring Boot 4.1.1 |
| Language | Java 21 |
| Build tool | Maven |
| Persistence | Spring Data JPA (Hibernate) |
| Database | MySQL 8 – `monitoring_db` |
| Validation | Jakarta Bean Validation |
| Base package | `money.monitor` |
| Server port | 8080 (default) |

## 2. Layered flow

```
 Postman / Client
        |
        |  HTTP + JSON
        v
+---------------------------+
|        Controller         |  Receives request, validates DTO (@Valid),
|  CategoryManagerController|  returns ResponseEntity
+---------------------------+
        |  CategoryManagerRequest (DTO)
        v
+---------------------------+
|          Service          |  Business rules
|  CategoryManagerService   |  (interface)
|  CategoryManagerServiceImpl|  - trim input
+---------------------------+  - reject duplicate (user + reason)
        |  CategoryManager (Entity)
        v
+---------------------------+
|        Repository         |  Spring Data JPA
| CategoryManagerRepository |  (JpaRepository)
+---------------------------+
        |  SQL via Hibernate
        v
+---------------------------+
|     MySQL: monitoring_db  |
|   table: category_manager |
+---------------------------+

 Errors from any layer  ->  GlobalExceptionHandler  ->  ResponseEntity (400 / 409)
```

## 3. Layer responsibilities

| Layer | Package | Responsibility |
|---|---|---|
| Controller | `controller` | Exposes endpoints, accepts DTOs, returns `ResponseEntity`. No business logic. |
| Service | `service`, `service.impl` | Business rules and transactions. Interface plus implementation. |
| Repository | `repository` | Database access through Spring Data JPA. |
| Entity | `entity` | Maps Java classes to database tables. |
| DTO | `dto` | Request and response objects, so entities are never exposed directly. |
| Exception | `exception` | Custom exceptions and the global error handler. |

## 4. Package structure

```
money.monitor
├── MonitoringApplication.java
├── controller
│   └── CategoryManagerController.java
├── service
│   ├── CategoryManagerService.java
│   └── impl
│       └── CategoryManagerServiceImpl.java
├── repository
│   └── CategoryManagerRepository.java
├── entity
│   └── CategoryManager.java
├── dto
│   ├── CategoryManagerRequest.java
│   ├── CategoryManagerResponse.java
│   └── ApiResponse.java
└── exception
    ├── DuplicateReasonException.java
    └── GlobalExceptionHandler.java
```

## 5. Common response format
All endpoints return `ResponseEntity<ApiResponse<T>>`:

```json
{
  "success": true,
  "message": "Category added successfully",
  "data": { }
}
```

| HTTP status | Meaning |
|---|---|
| 201 | Created |
| 400 | Validation failed or invalid JSON |
| 409 | Duplicate or constraint conflict |

## 6. Data model

**Table: `category_manager`**

| Column | Type | Constraint |
|---|---|---|
| `S_NO` | BIGINT | Primary key, auto increment |
| `USER_ID` | BIGINT | NOT NULL |
| `CATEGORY` | VARCHAR(100) | NOT NULL |
| `REASON` | VARCHAR(255) | NOT NULL |

Unique key `UK_USER_ID_REASON` on (`USER_ID`, `REASON`): a category can have many reasons, but the same reason is saved only once per user.

## 7. Configuration
File: `src/main/resources/application.properties`

| Property | Purpose |
|---|---|
| `spring.datasource.url / username / password` | MySQL connection to `monitoring_db` |
| `spring.jpa.hibernate.ddl-auto=update` | Hibernate creates or updates tables on startup (development) |
| `spring.jpa.hibernate.naming.physical-strategy` | Keeps column names exactly as written in the entity |

File: `src/main/resources/logback-spring.xml`

| Setting | Purpose |
|---|---|
| Console appender | Shows logs in the Eclipse Console |
| File appender | Writes logs to `log/monitoring-yyyy-MM-dd.log`, one file per date |
| Root level | `INFO` |

## 8. Duplicate check (two levels)
1. Service level: `existsByUserIdAndReasonIgnoreCase(userId, reason)` returns `409` with a clear message.
2. Database level: the unique key blocks duplicates if two requests arrive at the same time; the handler also returns `409`.

## 9. Planned (not built yet)
- Other Category Manager operations (list, update, delete).
- Other modules.

*Update this file when a new layer, module or integration is added.*
