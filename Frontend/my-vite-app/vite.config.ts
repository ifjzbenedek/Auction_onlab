import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api': {
        target: 'https://localhost:8081', 
        changeOrigin: true,
        secure: false,
      },
      '/oauth2': {
        target: 'https://localhost:8081', 
        changeOrigin: true,
        secure: false, 
      },
      '/users': {
        target: 'https://localhost:8081', 
        changeOrigin: true,
        secure: false, 
      },
    },
  },
});