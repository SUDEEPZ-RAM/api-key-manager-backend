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

## Deploying the backend to Render

This repository includes a `render.yaml` Blueprint and a Dockerfile configured for Render's `PORT` (default `10000`). Connect this repository to Render as a Blueprint, or create a Docker Web Service using the repository root as the Docker context and `./Dockerfile` as the Dockerfile path. The public health check is `/health`.

The backend requires a reachable MySQL 8 database. A MySQL service running on your computer at `localhost:3306` is not reachable from Render. Create a MySQL private service in the same Render workspace (the [Render MySQL guide](https://render.com/docs/deploy-mysql) describes deploying one with a persistent disk), then set the backend service variables:

- `DB_URL`: `jdbc:mysql://<mysql-private-host>:3306/api_key_manager?useSSL=false&allowPublicKeyRetrieval=true` (use the private hostname shown by the MySQL service)
- `DB_USERNAME` and `DB_PASSWORD`: the database user and password configured for MySQL
- `JWT_SECRET` and `ENCRYPTION_SECRET`: separate random values, each at least 32 characters; keep them stable across deploys so existing tokens and encrypted keys remain usable
- `FRONTEND_ORIGIN`: the deployed frontend's exact `https://...onrender.com` origin, with no trailing slash

Set the variables in Render's Environment page before the service starts. Do not put production secrets in `render.yaml`, Git, or the Docker image. Keep the MySQL service and backend in the same Render region so the backend can use the database's private hostname.

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
