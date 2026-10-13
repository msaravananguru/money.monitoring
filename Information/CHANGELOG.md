# Monitoring Project – Change Log

Project: `money.monitoring`
Stack: Spring Boot 4.1.1 · Java 21 · Spring Data JPA · MySQL 8 · Maven

Every new feature or change is added here (newest first).

---

## 2026-10-13 – Log files saved by date

Logs are written to the Eclipse Console and also to a file inside the project's `log` folder. A new file is created for each date.

| Item | Value |
|---|---|
| Folder | `log` (inside the project folder, created automatically) |
| File name | `monitoring-yyyy-MM-dd.log`, for example `monitoring-2026-10-13.log` |
| Same day, app restarted | New lines are added to the same day's file |
| New day | A new file starts automatically at midnight |
| Line format | `2026-10-13 23:51:07.123 INFO  [http-nio-8080-exec-1] m.m.c.CategoryManagerController - STEP 1 ...` |

Files added/changed:
- Added `src/main/resources/logback-spring.xml` (console + daily file settings).
- Added `log/` to `.gitignore` so log files are not pushed to GitHub.

Notes:
- The folder is relative, so it works on any laptop. Start the app from the project folder (Eclipse does this by default).
- Old log files are not deleted automatically.

---

## 2026-10-13 – Step-by-step logging for Category Manager flow

Every step of the add-category flow now writes a log line to the Eclipse Console, so the flow can be followed while testing in Postman.

| Step | Layer | What is logged |
|---|---|---|
| STEP 1 | Controller | Request received, with userId, category, reason |
| STEP 2 | Controller | Validation passed, calling service |
| STEP 3 | Service | `addCategory` started, trimming input |
| STEP 4 | Service | Checking duplicate in database |
| STEP 5 | Service | No duplicate found (or duplicate found and flow stops) |
| STEP 6 | Service | Saving to table `category_manager` |
| STEP 7 | Service | Saved, with generated `S_NO` |
| STEP 8 | Service | Building response, returning to controller |
| STEP 9 | Controller | Sending `201 CREATED` response |
| ERROR | ExceptionHandler | Validation failure (400), invalid JSON (400), duplicate (409), DB constraint (409) |

Example console output for a successful request:
```
STEP 1 [Controller] Request received: POST /api/category-manager/add | userId=101, category=Food, reason=Monthly groceries
STEP 2 [Controller] Request validation passed. Calling service
STEP 3 [Service] addCategory started. Trimming input values
STEP 4 [Service] Checking duplicate in database | userId=101, reason=Monthly groceries
STEP 5 [Service] No duplicate found. Safe to save
STEP 6 [Service] Saving to table category_manager | userId=101, category=Food, reason=Monthly groceries
STEP 7 [Service] Saved in database with S_NO=1
STEP 8 [Service] Building response and returning to controller
STEP 9 [Controller] Service returned serialNo=1. Sending 201 CREATED response
```

Files changed: `CategoryManagerController.java`, `CategoryManagerServiceImpl.java`, `GlobalExceptionHandler.java` (SLF4J logger, no new dependency).

For every new API, keep the same pattern: number the steps and log them in each layer.

---

## 2026-10-13 – Category Manager: Add New Category API

### Functionality
Add a new category for a user. Each record stores `S_NO`, `USER_ID`, `CATEGORY`, `REASON`.

**Business rule:** One category can have many reasons, but the same reason cannot be saved more than once for the same user (comparison is case-insensitive).

### API

| Item | Value |
|---|---|
| Method / URL | `POST http://localhost:8080/api/category-manager/add` |
| Content-Type | `application/json` |

Request body:
```json
{
  "userId": 101,
  "category": "Food",
  "reason": "Monthly groceries"
}
```

| Status | When | Response |
|---|---|---|
| `201 Created` | Saved successfully | `{ "success": true, "message": "Category added successfully", "data": { "serialNo": 1, "userId": 101, "category": "Food", "reason": "Monthly groceries" } }` |
| `409 Conflict` | Same reason already exists for that user | `{ "success": false, "message": "Reason 'Monthly groceries' already exists for userId 101", "data": null }` |
| `400 Bad Request` | `userId`, `category` or `reason` is missing/blank, or the JSON is invalid | `{ "success": false, "message": "Validation failed", "data": { "<field>": "<error>" } }` |

### Database
- Table: `category_manager` (created automatically by Hibernate on app start).
- Columns: `S_NO` (PK, auto increment), `USER_ID`, `CATEGORY`, `REASON` – all NOT NULL.
- Unique key: `UK_USER_ID_REASON` on (`USER_ID`, `REASON`).
- SQL reference is in `database_scripts.sql`.

### Files added
Base package: `src/main/java/money/monitor`

| Layer | File |
|---|---|
| Controller | `controller/CategoryManagerController.java` |
| Service | `service/CategoryManagerService.java` |
| Service impl | `service/impl/CategoryManagerServiceImpl.java` |
| Repository | `repository/CategoryManagerRepository.java` |
| Entity | `entity/CategoryManager.java` |
| DTO | `dto/CategoryManagerRequest.java` |
| DTO | `dto/CategoryManagerResponse.java` |
| DTO | `dto/ApiResponse.java` (common response wrapper) |
| Exception | `exception/DuplicateReasonException.java` |
| Exception | `exception/GlobalExceptionHandler.java` |

### Files changed
`src/main/resources/application.properties` – added:
```properties
spring.jpa.hibernate.ddl-auto=update
spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
```
- `ddl-auto=update` lets Hibernate create `category_manager` on startup.
- The naming strategy keeps column names exactly as written (`USER_ID`, `S_NO`, …).

### Notes
- Classes are written in plain Java (no Lombok) because Lombok was not enabled in Eclipse and caused compile errors. `pom.xml` was not changed.
- `ddl-auto=update` is fine for development. For production, create tables with SQL scripts and change it to `validate` or `none`.

---

## 2026-10-13 – Project setup: MySQL database and connection

- Created database `monitoring_db` and user `monitor_user` in MySQL (see `database_scripts.sql`).
- `application.properties` datasource:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/monitoring_db
spring.datasource.username=monitor_user
spring.datasource.password=monitoring_db
```
- `pom.xml` includes `mysql-connector-j` (runtime), Spring Data JPA, validation and web MVC.
