# CoreDesk Backend

> Spring Boot REST API for the **CoreDesk** Ticket Management System.

The CoreDesk Backend powers the server-side logic for [CoreDesk Frontend](https://github.com/dimasutamaa/CoreDeskFrontend), handling authentication, user management, and the full ticket/incident lifecycle through a secure, RESTful API.

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4 |
| Security | Spring Security + JWT |
| Persistence | Spring Data JPA + PostgreSQL |
| Validation | Spring Boot Validation |
| Boilerplate | Lombok |
| Build Tool | Maven |

## API Overview

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/auth/register` | Register a new user |
| `POST` | `/api/auth/login` | Login and receive JWT token |

### Tickets / Incidents
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/tickets` | Get all tickets |
| `GET` | `/api/tickets/{id}` | Get ticket by ID |
| `POST` | `/api/tickets` | Create a new ticket |
| `PUT` | `/api/tickets/{id}` | Update a ticket |

### Comments
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/comments/{ticketId}` | Get all comments for a ticket |
| `POST` | `/api/comments/{ticketId}` | Add a comment to a ticket |
| `DELETE` | `/api/comments/{commentId}` | Delete a comment |

### Users
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/users?role={role}` | Get users filtered by role |
| `GET` | `/api/users/recap` | Get data recap for the authenticated user |

## Related Repository

- 🖥️ **Frontend:** [CoreDeskFrontend](https://github.com/dimasutamaa/CoreDeskFrontend)
