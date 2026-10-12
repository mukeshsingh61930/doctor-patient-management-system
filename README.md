# Doctor Patient Management System

Full-stack doctor-patient management application built as a monorepo.

## Structure

```text
doctor-patient-management-system/
  frontend/  Angular application
  backend/   Spring Boot REST API
```

## Frontend

```bash
cd frontend
npm install
npm start
```

Production build:

```bash
cd frontend
npm run build
```

## Backend

```bash
cd backend
./mvnw spring-boot:run
```

On Windows:

```bash
cd backend
mvnw.cmd spring-boot:run
```

## Production Profiles

Run backend in production with:

```bash
SPRING_PROFILES_ACTIVE=prod
```

Required production environment variables:

```text
SPRING_DATASOURCE_URL
SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD
APP_CORS_ALLOWED_ORIGINS
APP_JWT_SECRET
```

Typical deployment:

```text
https://yourdomain.com      -> Angular frontend
https://api.yourdomain.com  -> Spring Boot backend
```
