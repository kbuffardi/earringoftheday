# EarringOfTheDay (EOTD)

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

### Run with Docker Compose

```bash
docker compose up --build
```

- Frontend: http://localhost
- Backend API: http://localhost:8080/api

### Local Development

**Backend:**
```bash
cd backend
mvn spring-boot:run
```

**Frontend:**
```bash
cd frontend
npm install
npm run dev
```

## CI/CD

A GitHub Actions workflow runs on every pull request targeting `main`. It checks that:
- The Spring Boot backend builds successfully (including tests)
- The React frontend builds successfully
- Docker images build successfully