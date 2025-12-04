````markdown
# Task Management Mini System

Mini backend project to manage users and tasks using Java, Spring Boot, and JWT.  
The project is a training exercise to build REST APIs with authentication, authorization, and clean layered architecture (Controller / Service / Repository).

---

## 1. Technologies Used

- Java 21 (can be changed to Java 8 if needed)
- Spring Boot
- Spring Web (REST APIs)
- Spring Data JPA (Hibernate)
- Spring Security
- JWT (JSON Web Token)
- Oracle Database (local)
- Maven

---

## 2. Project Structure (Layers)

The project is organized into the following layers:

- `controller`  
  - `RegistrationController` – handles user registration and login  
  - `TaskController` – handles CRUD operations for tasks  

- `service`  
  - `AuthService` – business logic for registration and login  
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

---

## 3. Database Configuration

Default configuration in `application.properties`:

```properties
spring.datasource.url=jdbc:oracle:thin:@//localhost:1521/XEPDB1
spring.datasource.username=hibernate
spring.datasource.password=1234

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
````

Update the following values if your local database is different:

* `spring.datasource.url`
* `spring.datasource.username`
* `spring.datasource.password`

---

## 4. How to Run the Project

### Prerequisites

* JDK 21 (or JDK 8 if you change the project settings)
* Maven
* Oracle Database running locally (or adjust the connection string)

### Run from IDE (Eclipse / IntelliJ)

1. Import the project as a Maven project.
2. Wait for Maven to download all dependencies.
3. Run the main Spring Boot application class as **Spring Boot App**.
4. By default, the application runs on:

   * `http://localhost:8080`

### Run from Command Line

From the project root:

```bash
mvn spring-boot:run
```

---

## 5. Authentication & Security

The project uses:

* **JWT Authentication**

  * The user calls the login endpoint.
  * The server returns a JWT token.
  * All protected endpoints require the token to be sent in the HTTP header:

    ```http
    Authorization: Bearer <token>
    ```

* **BCrypt Password Encoding**

  * User passwords are encoded with `BCryptPasswordEncoder` before being stored in the database.

* **Role-Based Authorization**

  * Roles such as `USER` and `ADMIN` are supported.
  * The endpoint `/api/tasks/all` is restricted to users with the `ADMIN` role.

---

## 6. REST API Endpoints

### 6.1 Auth APIs

#### 1) Register User

* **URL:** `POST /api/auth/register`
* **Body (JSON):**

```json
{
  "username": "user1",
  "email": "user1@example.com",
  "password": "1234",
  "role": "USER"
}
```

* **Response:**

  * `200 OK` – `"User registered successfully"`

---

#### 2) Login

* **URL:** `POST /api/auth/login`
* **Body (JSON):**

```json
{
  "username": "user1",
  "password": "1234"
}
```

* **Response:**

  * `200 OK` – JWT token as plain text string.

Use this token in the `Authorization` header for all protected endpoints:

```http
Authorization: Bearer <token>
```

---

### 6.2 Task APIs (Protected)

All endpoints below require a valid JWT token:

```http
Authorization: Bearer <token>
```

#### 1) Create Task

* **URL:** `POST /api/tasks`
* **Body (JSON):**

```json
{
  "title": "Finish mini project",
  "description": "Complete Task Management Mini System",
  "status": "NEW"
}
```

* **Description:**
  Creates a new task linked to the currently authenticated user.

---

#### 2) Get My Tasks

* **URL:** `GET /api/tasks/my`
* **Description:**
  Returns all tasks that belong to the currently authenticated user.

---

#### 3) Get All Tasks (Admin Only)

* **URL:** `GET /api/tasks/all`
* **Authorization:** Requires `ADMIN` role.
* **Description:**
  Returns all tasks in the system. Only available for admin users.

---

#### 4) Update Task

* **URL:** `PUT /api/tasks/{id}`
* **Body (JSON):**

```json
{
  "title": "Updated title",
  "description": "Updated description",
  "status": "IN_PROGRESS"
}
```

* **Description:**
  Updates an existing task by ID. Typically allowed for the task owner or an admin.

---

#### 5) Delete Task

* **URL:** `DELETE /api/tasks/{id}`
* **Description:**
  Deletes a task by ID. Typically allowed for the task owner or an admin.

---

## 7. Postman Collection

A Postman collection can be used to test all APIs.

* Recommended path inside the project:

  * `postman/TaskManagement.postman_collection.json`

### Usage

1. Open Postman.
2. Click **Import**.
3. Select the `.json` collection file from the project folder.
4. Use the requests in this order:

   * `Register`
   * `Login` (copy or automatically store the token)
   * Task APIs (`Create Task`, `Get My Tasks`, `Get All Tasks`, `Update Task`, `Delete Task`).

```
```
