import { defineConfig } from "vite"
import react from "@vitejs/plugin-react"

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      // Proxy all requests to /api to your backend
      "/api": {
        target: "https://localhost:8081",
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path.replace(/^\/api/, ""),
      },
      // Proxy authentication endpoints
      "/oauth2": {
        target: "https://localhost:8081",
        changeOrigin: true,
        secure: false,
      },
      // Proxy user endpoints
      "/users": {
        target: "https://localhost:8081",
        changeOrigin: true,
        secure: false,
      },
      // Proxy auctions endpoints
      "/auctions": {
        target: "https://localhost:8081",
        changeOrigin: true,
        secure: false,
      },
      // Proxy categories endpoints
      "/categories": {
        target: "https://localhost:8081",
        changeOrigin: true,
        secure: false,
      },
      // Proxy logout endpoint
      "/logout": {
        target: "https://localhost:8081",
        changeOrigin: true,
        secure: false,
      },
      // Proxy notifications endpoints
      "/notifications": {
        target: "https://localhost:8081",
        changeOrigin: true,
        secure: false,
      },
      // Proxy agent endpoints
      "/agent": {
        target: "https://localhost:8081",
        changeOrigin: true,
        secure: false,
      },
    },
  },
});
