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

## OAuth2 Authentication Setup

The application supports login via **Google**, **Microsoft**, **Apple**, and **Facebook** using OAuth2.  
To enable one or more providers, you must register an OAuth2 application with each provider and supply the credentials as environment variables **before** starting the backend.

> These credentials are secrets and **must never be committed to the repository**. Set them as environment variables (e.g., in a `.env` file excluded by `.gitignore`, as Docker Compose secrets, or as CI/CD variables).

### Required environment variables

| Variable | Description |
|---|---|
| `GOOGLE_CLIENT_ID` | OAuth2 Client ID from Google Cloud Console |
| `GOOGLE_CLIENT_SECRET` | OAuth2 Client Secret from Google Cloud Console |
| `MICROSOFT_CLIENT_ID` | OAuth2 Client ID from Azure AD App Registration |
| `MICROSOFT_CLIENT_SECRET` | OAuth2 Client Secret from Azure AD App Registration |
| `APPLE_CLIENT_ID` | Services ID from Apple Developer Portal |
| `APPLE_CLIENT_SECRET` | Generated JWT from Apple Developer Portal |
| `FACEBOOK_CLIENT_ID` | App ID from Facebook Developer Portal |
| `FACEBOOK_CLIENT_SECRET` | App Secret from Facebook Developer Portal |

Only providers with **both** `CLIENT_ID` and `CLIENT_SECRET` set will be available as login options. You may configure any subset of providers.

### Provider setup guides

#### Google
1. Open [Google Cloud Console](https://console.cloud.google.com/) → **APIs & Services** → **Credentials**.
2. Create an **OAuth 2.0 Client ID** (Web application).
3. Add `http://localhost:8080/login/oauth2/code/google` to **Authorized redirect URIs** (for development).  
   For production, add your production backend URL (e.g. `https://your-domain.com/login/oauth2/code/google`).
4. Copy the **Client ID** and **Client Secret** into `GOOGLE_CLIENT_ID` and `GOOGLE_CLIENT_SECRET`.

#### Microsoft (Azure AD)
1. Open [Azure Portal](https://portal.azure.com/) → **Azure Active Directory** → **App registrations** → **New registration**.
2. Add `http://localhost:8080/login/oauth2/code/microsoft` as a **Redirect URI** (Web).
3. Under **Certificates & secrets**, create a new **Client secret**.
4. Copy the **Application (client) ID** into `MICROSOFT_CLIENT_ID` and the secret value into `MICROSOFT_CLIENT_SECRET`.

#### Apple
1. Open [Apple Developer Portal](https://developer.apple.com/) → **Certificates, IDs & Profiles** → **Identifiers** → register a **Services ID**.
2. Enable **Sign In with Apple**, click **Configure**, and add `http://localhost:8080/login/oauth2/code/apple` as a **Return URL**.
3. Generate a **private key** with Sign In with Apple enabled and download the `.p8` file.
4. Create a JWT (client secret) signed with the private key per [Apple's documentation](https://developer.apple.com/documentation/sign_in_with_apple/generate_and_validate_tokens).
5. Copy the **Services ID** into `APPLE_CLIENT_ID` and the generated JWT into `APPLE_CLIENT_SECRET`.

#### Facebook
1. Open [Facebook Developers](https://developers.facebook.com/) → **My Apps** → **Create App** (Consumer type).
2. Add the **Facebook Login** product and configure **Valid OAuth Redirect URIs** to include `http://localhost:8080/login/oauth2/code/facebook`.
3. Copy the **App ID** into `FACEBOOK_CLIENT_ID` and the **App Secret** into `FACEBOOK_CLIENT_SECRET`.

### Local development example

Create a file `backend/.env.local` (already in `.gitignore`) and export the variables before running the backend:

```bash
export GOOGLE_CLIENT_ID=your-google-client-id
export GOOGLE_CLIENT_SECRET=your-google-client-secret
# Add other providers as needed

cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

## CI/CD

A GitHub Actions workflow runs on every pull request targeting `main`. It checks that:
- The Spring Boot backend builds successfully (including tests)
- The React frontend builds successfully
- Docker images build successfully
