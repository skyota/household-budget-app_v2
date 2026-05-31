import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// Docker環境では backend、ローカル実行では VITE_PROXY_TARGET で上書き可能
const proxyTarget = process.env.VITE_PROXY_TARGET ?? 'http://backend:8080'

export default defineConfig({
  plugins: [react()],
  server: {
    host: '0.0.0.0',
    port: 5173,
    watch: {
      usePolling: true,
    },
    hmr: {
      host: 'localhost',
      port: 5173,
    },
    proxy: {
      '/user': proxyTarget,
      '/expenses': proxyTarget,
      '/categories': proxyTarget,
      '/api': proxyTarget,
      '/quick': proxyTarget,
    },
  },
})
