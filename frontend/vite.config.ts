import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    host: '0.0.0.0',        // コンテナ外（ホストブラウザ）からアクセス可能にする
    port: 5173,
    watch: {
      usePolling: true,      // Docker bind mount では inotify が効かないため polling を使用
    },
    hmr: {
      host: 'localhost',     // HMR WebSocket 接続先をホストの localhost に固定
      port: 5173,
    },
  },
})
