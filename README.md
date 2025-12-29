# Task Management Mini System

Mini backend project to manage **users** and **tasks** using **Java**, **Spring Boot**, and **JWT**.

This project is a training exercise to build REST APIs with authentication, authorization, and a clean layered architecture (**Controller / Service / Repository**).

✅ **Reports (JasperReports):** Generate **PDF / XLSX / RTF** reports from the backend using `POST /api/reports`.

---

## 1) Technologies Used

- Java 21 (can be changed to Java 8 if needed)
- Spring Boot
- Spring Web (REST APIs)
- Spring Data JPA (Hibernate)
- Spring Security
- JWT (JSON Web Token)
- Oracle Database (local)
- Maven
- JasperReports (report generation)

---

## 2) Project Structure (Layers)

The project is organized into the following layers:

- `controller`
  - `RegistrationController` – handles user registration and login
  - `TaskController` – handles CRUD operations for tasks

- `service`
  - `RegistrationService` – business logic for registration and login
  - `TaskService` – business logic for task operations
  - `MyUserDetailsService` – loads user details for Spring Security

- `repo`
  - `UserRepo` – repository for the `User` entity
  - `TaskRepo` – repository for the `Tasks` entity

- `model` / `entity`
  - `User` – represents the users table
  - `Tasks` – represents the tasks table
  - `TaskStatus` – enum for task status (`NEW`, `IN_PROGRESS`, `DONE`)
  - DTOs such as `LoginRequest`, `RegisterRequest`, `TaskDto`

- `security` / `config`
  - `SecurityConfig` – Spring Security configuration
  - `JwtService` – creates and validates JWT tokens
  - `JwtFilter` – reads JWT from the request and sets authentication
  - `UserPrincipal` – implementation of `UserDetails` for Spring Security

- `report` (JasperReports)
  - `report/controller/ReportController` – generates and returns report files
  - `report/service/impl/JasperReportServiceImpl` – compiles templates, loads tasks, exports PDF/XLSX/RTF
  - `report/enums/ReportType` and `report/enums/ReportOutputFormat`
  - `report/dto/*` – request/response DTOs and report row mapping

---

## 3) Database Configuration

Default configuration in `application.properties`:

```properties
spring.datasource.url=jdbc:oracle:thin:@//localhost:1521/XEPDB1
spring.datasource.username=database
spring.datasource.password=1234

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

Update the following values if your local database is different:

- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`

---

## 4) Report Configuration (JasperReports)

### 4.1 Template location

The backend reads report templates from the path configured in `application.properties`:

```properties
app.report.path=Reports/
```

You can use:

- **Relative path** (recommended for training): `Reports/` (a folder at the project root)
- **Absolute path** (recommended for servers): `C:/TASK_REPORTS/` (Windows) or `/opt/task-reports/` (Linux)

### 4.2 Templates

Place your templates here:

- `Reports/*.jrxml`

The service will compile `*.jrxml` into `*.jasper` automatically on the first report request.

Included sample templates:

- `Reports/TasksListReport.jrxml`
- `Reports/TasksDashboardReport.jrxml`

> If your report uses images (logo/background), put them in the same `Reports/` folder and reference them using a parameter (example below).

### 4.3 Arabic text (optional)

If you generate Arabic PDF reports, make sure your template uses a font that supports Arabic and set:

- `pdfEncoding="Identity-H"`

You can also bundle a `.ttf` font and reference it from the template.

---

## 5) How to Run the Project

### Prerequisites

- JDK 21 (or JDK 8 if you change the project settings)
- Maven
- Oracle Database running locally (or adjust the connection string)

### Run from IDE (Eclipse / IntelliJ)

1. Import the project as a Maven project.
2. Wait for Maven to download all dependencies.
3. Run the main Spring Boot application class as **Spring Boot App**.
4. By default, the application runs on:

   `http://localhost:8080`

### Run from Command Line

From the project root:

```bash
mvn spring-boot:run
```

---

## 6) Authentication & Security

### JWT Authentication

1. The user calls the login endpoint.
2. The server returns a JWT token.
3. All protected endpoints require the token in the header:

```http
Authorization: Bearer <token>
```

### BCrypt Password Encoding

User passwords are encoded with `BCryptPasswordEncoder` before being stored in the database.

### Role-Based Authorization

Roles such as `USER` and `ADMIN` are supported.

- `GET /api/tasks/all` is restricted to users with the `ADMIN` role.
- Reports that start with `ALL_...` are restricted to `ADMIN`.

### Important note about registration

Even if you send `role` during registration, the backend currently **sets all new users to `USER` by default**.

To make a user admin, update the database:

```sql
UPDATE USERS
SET role = 'ADMIN'
WHERE username = 'user1';

COMMIT;
```

---

## 7) REST API Endpoints

### 7.1 Auth APIs

#### 1) Register User

URL: `POST /api/auth/register`

Body (JSON):

```json
{
  "username": "user1",
  "email": "user1@example.com",
  "password": "1234",
  "role": "USER"
}
```

Response:

- `200 OK` – `"User registered successfully"`

#### 2) Login

URL: `POST /api/auth/login`

Body (JSON):

```json
{
  "username": "user1",
  "password": "1234"
}
```

Response:

- `200 OK` – JWT token as plain text string.

Use this token in the `Authorization` header for all protected endpoints.

---

### 7.2 Task APIs (Protected)

All endpoints below require a valid JWT token:

```http
Authorization: Bearer <token>
```

#### 1) Create Task

URL: `POST /api/tasks`

Body (JSON):

```json
{
  "title": "Finish mini project",
  "description": "Complete Task Management Mini System",
  "status": "NEW"
}
```

Description: Creates a new task linked to the currently authenticated user.

#### 2) Get My Tasks

URL: `GET /api/tasks/my`

Description: Returns all tasks that belong to the currently authenticated user.

#### 3) Get All Tasks (Admin Only)

URL: `GET /api/tasks/all`

Authorization: Requires `ADMIN` role.

Description: Returns all tasks in the system.

#### 4) Update Task

URL: `PUT /api/tasks/{id}`

Body (JSON):

```json
{
  "title": "Updated title",
  "description": "Updated description",
  "status": "IN_PROGRESS"
}
```

Description: Updates an existing task by ID.

#### 5) Delete Task

URL: `DELETE /api/tasks/{id}`

Description: Deletes a task by ID.

---

### 7.3 Report APIs (Protected)

Generate a report file (PDF / XLSX / RTF).

URL: `POST /api/reports`

Headers:

```http
Authorization: Bearer <token>
Content-Type: application/json
```

Body (JSON):

```json
{
  "type": "MY_TASKS_LIST",
  "format": "PDF"
}
```

Supported `format`:

- `PDF`
- `XLSX`
- `RTF`

Supported `type`:

- `MY_TASKS_LIST` (tasks for current user)
- `MY_TASKS_DASHBOARD` (status counters for current user)
- `ALL_TASKS_LIST` (**admin only**)
- `ALL_TASKS_DASHBOARD` (**admin only**)

#### Postman tip

Because the response is a file (binary), use:

- **Send and Download** (in Postman) to save the report to your machine.

---

## 8) Adding a New Report Template

1. Design your template in **iReport 5.6.0** (or Jaspersoft Studio).
2. Export/save the template as `YourReport.jrxml`.
3. Copy it to the configured folder (default: `Reports/`).
4. Add a new value in `ReportType` pointing to your `.jrxml`.
5. If your template needs different data/parameters, update `JasperReportServiceImpl`.

### Field mapping (important)

For list reports, the service uses a list of `TaskReportRowDTO`.
Your `.jrxml` fields should match these names:

- `id`
- `title`
- `description`
- `status`
- `createdAt`
- `username`

---

## 9) Postman Collection

A Postman collection can be used to test all APIs.

Recommended path inside the project:

- `postman/TaskManagement.postman_collection.json`

Suggested order:

1. Register
2. Login (copy token)
3. Task APIs
4. Report APIs

---

<div dir="rtl">

## ملخص سريع بالعربي (Quick Start)

1) شغّل المشروع.

2) سجّل دخول وخذ Token.

3) عشان الأدمن يطلع كل التاسكات:
- لازم قيمة `role` في جدول `USERS` تكون `ADMIN`.

4) توليد تقرير:
- Endpoint: `POST /api/reports`
- Body مثال:

```json
{ "type": "ALL_TASKS_LIST", "format": "PDF" }
```

5) في Postman استخدم **Send and Download** عشان ينزل الملف.

</div>
