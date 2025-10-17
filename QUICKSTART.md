# Tenga Knowledge Base - Quick Start Guide

## 🚀 Fast Start with Docker Compose

The easiest way to run the entire stack (PostgreSQL + Application with integrated frontend):

```bash
# Clone and navigate
cd /mnt/e/Dev/tenga

# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down
```

**Access Points (everything on port 8080):**
- **Web UI**: http://localhost:8080
- **Backend API**: http://localhost:8080/api/v1
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **MCP Manifest**: http://localhost:8080/mcp/manifest

## 📦 What Gets Started?

1. **PostgreSQL 17** - Database on port 5432
2. **Tenga Application** - Spring Boot with integrated Vue.js frontend on port 8080
   - REST API + MCP Server
   - Web UI served as static resources from Spring Boot

## 🔑 First Time Setup

### 1. Generate an API Key

You need an API key to use the system. Run this inside the backend container:

```bash
# Access the backend container
docker-compose exec tenga-app /bin/sh

# Generate an API key (inside container)
# This would normally be done via admin endpoint or database script
# For now, you can create one manually in PostgreSQL

# Exit container
exit
```

**Temporary workaround** - Connect to PostgreSQL and insert a test key:

```bash
# Connect to PostgreSQL
docker-compose exec postgres psql -U tenga -d tenga

# Insert a test API key (BCrypt hash of "test-key-123")
INSERT INTO api_keys (key_hash, created_at)
VALUES ('$2a$10$ZbyK0iQa5DGdB7CeRzo98.7dB3gc.MVyThSPZA80NgHxX9MxrHeKm', NOW());

# Exit PostgreSQL
\q
```

Now you can use `test-key-123` as your API key.

### 2. Access the Web UI

1. Open http://localhost:3000
2. Enter your API key: `test-key-123`
3. Click "Login"
4. Start creating documents!

## 🛠️ Development Mode

### Run Full Stack Locally

```bash
# Start PostgreSQL
docker-compose up -d postgres

# Build and run (includes frontend build)
mvn spring-boot:run

# Access at http://localhost:8080
```

### Frontend Development with Hot Reload

If you want to develop the frontend with live reload:

```bash
# Terminal 1: Start backend
mvn spring-boot:run

# Terminal 2: Start frontend dev server
cd frontend
npm install
npm run dev

# Access at http://localhost:3000 (proxied to backend)
```

**Note**: In production, frontend is built into the JAR and served by Spring Boot.

## 📚 API Usage Examples

### Create a Document

```bash
curl -X POST http://localhost:8080/api/v1/documents \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer test-key-123" \
  -d '{
    "title": "My First Document",
    "content": "# Hello World\n\nThis is my first document.",
    "tags": ["tutorial", "getting-started"]
  }'
```

### Search Documents

```bash
curl -X GET "http://localhost:8080/api/v1/search?q=hello&page=0&size=10" \
  -H "Authorization: Bearer test-key-123"
```

### List Documents

```bash
curl -X GET "http://localhost:8080/api/v1/documents?page=0&size=20" \
  -H "Authorization: Bearer test-key-123"
```

## 🤖 MCP Integration (Chat Agents)

### Get MCP Manifest

```bash
curl http://localhost:8080/mcp/manifest
```

### Create Note via MCP

```bash
curl -X POST http://localhost:8080/mcp \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer test-key-123" \
  -d '{
    "jsonrpc": "2.0",
    "method": "createNote",
    "params": {
      "content": "# Quick Note\n\nCreated via MCP!",
      "suggestTags": true
    },
    "id": 1
  }'
```

## 🔍 Verify Everything Works

### Check Backend Health

```bash
curl http://localhost:8080/actuator/health
```

Expected response:
```json
{"status":"UP"}
```

### Check Frontend

Open http://localhost:8080 - you should see the login page.

### Check Database

```bash
docker-compose exec postgres psql -U tenga -d tenga -c "SELECT COUNT(*) FROM documents;"
```

## 📊 Features Overview

### ✅ Implemented Features

- **Document Management**: Create, read, update, delete, restore
- **Markdown Support**: Full Markdown rendering and editing
- **Hierarchical Tags**: Parent-child tag relationships (max 5 levels)
- **Version Control**: Automatic versioning, history, compare, restore (10 versions kept)
- **Full-Text Search**: PostgreSQL-powered search with ranking
- **MCP Server**: JSON-RPC 2.0 for chat agent integration
- **Rate Limiting**: 60 requests per minute per API key
- **Web UI**: Vue.js 3 + TypeScript with responsive design
- **Docker Deployment**: Complete containerized stack

### 🎯 Core Capabilities

1. **Document Operations**: CRUD with soft delete
2. **Tag Organization**: Hierarchical with auto-suggestion
3. **Search**: Full-text with snippets and relevance scoring
4. **Versions**: Automatic snapshots on every update
5. **API Security**: Bearer token authentication
6. **Chat Integration**: MCP protocol for AI assistants

## 🐛 Troubleshooting

### Port Already in Use

If ports 5432 or 8080 are already in use:

```bash
# Check what's using the ports
lsof -i :5432
lsof -i :8080

# Stop the services or change ports in docker-compose.yml
```

### Frontend Can't Connect to Backend

Check that backend is running:
```bash
curl http://localhost:8080/actuator/health
```

Check browser console for CORS errors.

### Database Connection Issues

```bash
# Check PostgreSQL logs
docker-compose logs postgres

# Restart PostgreSQL
docker-compose restart postgres
```

### Clear All Data and Restart

```bash
# Stop and remove volumes
docker-compose down -v

# Start fresh
docker-compose up -d
```

## 📖 Next Steps

1. **Read the Full README**: `/mnt/e/Dev/tenga/README.md`
2. **Explore the API**: http://localhost:8080/swagger-ui.html
3. **Check MCP Methods**: http://localhost:8080/mcp/manifest
4. **Create Documents**: Use the Web UI or API
5. **Integrate with Chat Agents**: Use the MCP protocol

## 🎉 Success!

You now have a fully functional personal knowledge base with:
- Web UI for easy document management
- REST API for programmatic access
- MCP server for chat agent integration
- PostgreSQL for reliable storage
- Docker for easy deployment

Happy note-taking! 📝
