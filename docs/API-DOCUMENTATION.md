# Migme Backend API Documentation

## Base URL
`https://api.migxchat.net/api`

## Authentication
All authenticated endpoints require a Bearer token in the Authorization header.

## Endpoints

### Health
- `GET /health` - Server health check

### Authentication
- `POST /auth/login` - Login with username/password
- `POST /auth/register` - Register new account
- `POST /auth/logout` - Logout

### Users
- `GET /users/{username}` - Get user profile
- `GET /users/me` - Get current user profile

### Chat
- `GET /chat/history/{destination}` - Get message history

### Upload
- `POST /upload/image` - Upload image file
