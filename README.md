# EarringOfTheDay (EOTD)

[![Build Check](https://github.com/kbuffardi/earringoftheday/actions/workflows/build.yml/badge.svg)](https://github.com/kbuffardi/earringoftheday/actions/workflows/build.yml)

A monorepo web application for your daily earring inspiration.

## Tech Stack

| Layer     | Technology                        |
|-----------|-----------------------------------|
| Frontend  | React.js + Tailwind CSS (Vite)    |
| Backend   | Spring MVC (Spring Boot 3, Java 17) |
| Database  | PostgreSQL 16                     |
| Container | Docker + Docker Compose           |

## Project Structure

```
earringoftheday/
├── backend/          # Spring MVC REST API
├── frontend/         # React.js + Tailwind CSS SPA
├── docker-compose.yml
└── .github/
    └── workflows/
        └── build.yml # PR build check workflow
```

## Getting Started

### Prerequisites

- Docker & Docker Compose
- Java 17+ (for local backend development)
- Node.js 20+ (for local frontend development)

### Local Development

Run each service separately for the fastest development feedback loop.

**Backend** (with Swagger UI enabled at http://localhost:8080/swagger-ui.html):
```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

**Frontend:**
```bash
cd frontend
npm install
npm run dev
```

- Frontend dev server: http://localhost:5173
- Backend API: http://localhost:8080/api
- Swagger UI: http://localhost:8080/swagger-ui.html *(local profile only)*

> **Note:** Swagger UI is only available when the backend is started with the `local` Spring profile. It is automatically disabled in all other environments.

### Production Mode

Build and run all services together using Docker Compose:

```bash
docker compose up --build
```

- Frontend: http://localhost
- Backend API: http://localhost:8080/api

Swagger UI is **not** available in this mode.

## CI/CD

A GitHub Actions workflow runs on every pull request targeting `main`. It checks that:
- The Spring Boot backend builds successfully (including tests)
- The React frontend builds successfully
- Docker images build successfully
