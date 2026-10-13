# API Key Manager API

Spring Boot REST API for Vault. API keys are encrypted with AES-GCM before persistence. Regular API responses contain masked metadata; the reveal endpoint requires the user's password. MySQL stores users, encrypted API keys, and audit activity.

## Requirements

- Java 21+
- Maven 3.9+
- MySQL 8

## Local setup

Copy `.env.example` to `.env` in this directory. Set `DB_PASSWORD` to your MySQL password, then generate separate random values (32 bytes or more) for `JWT_SECRET` and `ENCRYPTION_SECRET`. Keep `.env` out of Git; it is ignored.

With an installed MySQL service, the default connection targets `localhost:3306` and creates/uses `api_key_manager`. Alternatively, run the included MySQL container from this directory with `docker compose up -d mysql`; do not start it if another server already uses port 3306.

Start the API from this directory:

```powershell
mvn spring-boot:run
```

The API listens on `http://localhost:8081`. Frontend CORS defaults to `http://localhost:5173`.

## Build and tests

```powershell
mvn test
mvn package
```

## Endpoints

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/auth/register`, `/api/auth/login` | Register and authenticate |
| GET / PUT | `/api/users/me` | Read or update profile |
| PUT | `/api/users/me/password` | Change password |
| GET / POST | `/api/keys` | List or create keys |
| GET / PUT / DELETE | `/api/keys/{id}` | Read metadata, update, delete |
| POST | `/api/keys/{id}/reveal` | Password-confirmed reveal |
| POST | `/api/keys/{id}/rotate` | Rotate credential |
| POST | `/api/keys/{id}/revoke`, `/copy` | Revoke or audit copy action |
| GET | `/api/activity`, `/api/dashboard/stats` | Audit history and statistics |

All authenticated routes require `Authorization: Bearer <JWT>`. The tables `users`, `api_keys`, and `activity_logs` are managed by JPA for local development. Use versioned migrations and managed secrets for production.
