# API Key Manager API

Spring Boot REST API for Vault. API keys are encrypted with AES-GCM before persistence. Regular API responses contain masked metadata; the reveal endpoint requires the user's password. MySQL or PostgreSQL stores users, encrypted API keys, and audit activity.

## Requirements

- Java 21+
- Maven 3.9+
- MySQL 8
- PostgreSQL 15+

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

The backend can use either MySQL or PostgreSQL. A database running on your computer at `localhost` is not reachable from Render. For a no-cost college/demo deployment, Render offers a free PostgreSQL database and a free web service. The free PostgreSQL database expires 30 days after creation, has a 1 GB storage limit, and has no backups; export your data before expiration. The free web service sleeps after 15 minutes without traffic and may take about a minute to wake. See [Render's free instance limits](https://render.com/docs/free).

Set the backend service's variables in Render's Environment page:

- `DB_URL`: `jdbc:postgresql://<internal-host>:5432/<database>?sslmode=require` (use the database's internal connection host and database name)
- `DB_USERNAME` and `DB_PASSWORD`: the database user and password
- `JWT_SECRET` and `ENCRYPTION_SECRET`: separate random values, each at least 32 characters; keep them stable across deploys so existing tokens and encrypted keys remain usable
- `FRONTEND_ORIGIN`: the frontend's exact origin, including `https://` and without a trailing slash

Keep the database and backend in the same Render region so the backend can use the database's internal connection host. Do not put production secrets in `render.yaml`, Git, or the Docker image. Local MySQL remains supported by the default `.env.example` settings.

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

