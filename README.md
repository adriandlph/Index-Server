# Index Server

## Technologies

| Technology | Version |
|---|---|
| Java | 25 |
| Spring Boot | 4.1.0 |
| Spring Web | - |
| Spring Data JPA | - |
| Spring Security | - |
| SQLite (Xerial JDBC) | - |
| Hibernate Community Dialects | - |
| Lombok | - |
| SpringDoc OpenAPI | 2.8.5 |
| Maven | 3.9.16 |

## Build & Run

```bash
# Build
./IndexServer.sh  (option 2)

# Package
./IndexServer.sh  (option 3)

# Run
./IndexServer.sh  (option 4)


## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | `/divisions` | List all divisions |
| GET | `/divisions/{id}` | Get division by ID |
| POST | `/divisions` | Create division |
| PUT | `/divisions/{id}` | Update division |
| DELETE | `/divisions/{id}` | Delete division |
| GET | `/departments` | List all departments |
| GET | `/departments/{id}` | Get department by ID |
| POST | `/departments` | Create department |
| PUT | `/departments/{id}` | Update department |
| DELETE | `/departments/{id}` | Delete department |
| GET | `/projects` | List all projects |
| GET | `/projects/{id}` | Get project by ID |
| POST | `/projects` | Create project |
| PUT | `/projects/{id}` | Update project |
| DELETE | `/projects/{id}` | Delete project |
| GET | `/products` | List all products |
| GET | `/products/{id}` | Get product by ID |
| POST | `/products` | Create product |
| PUT | `/products/{id}` | Update product |
| DELETE | `/products/{id}` | Delete product |

## API Documentation (Swagger)

Once running, visit: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## Releases

### v1.0.0

- Division, Department, Project and Product CRUD
- SQLite persistence with JPA + Hibernate
- REST API with ApiResponse wrapper
- Layered architecture: RestApi → Controller → Repository → Entity
- Field validation with nullable constraints and version format (v00.00.000)
- Swagger UI documentation
- Security: permitAll + CSRF disabled
- Logging to file with rolling policy
