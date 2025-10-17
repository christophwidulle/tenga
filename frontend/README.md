# Tenga Web UI

Vue.js 3 frontend for the Tenga Knowledge Base.

## Setup

```bash
# Install dependencies
npm install

# Run development server
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview
```

## Configuration

The frontend is configured to proxy API requests to the backend at `http://localhost:8080`.

Update `vite.config.ts` if your backend runs on a different port.

## Features

- API key authentication
- Document list with tag filtering
- Document viewing with Markdown rendering
- Document editing with live preview
- Version history viewing and restoration
- Responsive design

## Tech Stack

- Vue.js 3 with Composition API
- TypeScript
- Vite
- Pinia (state management)
- Vue Router
- Axios (HTTP client)
