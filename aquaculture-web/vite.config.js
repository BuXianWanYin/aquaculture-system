import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

// https://vite.dev/config/
export default defineConfig(({ command, mode }) => {
  // 加载环境变量
  // Load environment variables
  const env = loadEnv(mode, process.cwd(), '')

  // 从环境变量获取配置，如果没有则使用默认值
  // Get configuration from environment variables, use defaults if not set
  const port = parseInt(env.VITE_PORT || '3000', 10)
  const apiBaseUrl = env.VITE_API_BASE_URL || 'http://localhost:8080'
  const apiPrefix = env.VITE_API_PREFIX || '/api'
  const uploadPrefix = env.VITE_UPLOAD_PREFIX || '/uploads'
  const requestTimeout = parseInt(env.VITE_REQUEST_TIMEOUT || '10000', 10)

  return {
    plugins: [vue()],
    resolve: {
      alias: {
        '@': path.resolve(__dirname, 'src')
      }
    },
    server: {
      port: port,
      open: true,  // 启动后自动打开浏览器
      proxy: {
        [apiPrefix]: {
          target: apiBaseUrl,
          changeOrigin: true
        },
        [uploadPrefix]: {
          target: apiBaseUrl,
          changeOrigin: true
        }
      }
    },
    // 定义全局常量，可在代码中通过 import.meta.env 访问
    // Define global constants, accessible via import.meta.env in code
    define: {
      __APP_API_BASE_URL__: JSON.stringify(apiBaseUrl),
      __APP_API_PREFIX__: JSON.stringify(apiPrefix),
      __APP_UPLOAD_PREFIX__: JSON.stringify(uploadPrefix),
      __APP_REQUEST_TIMEOUT__: JSON.stringify(requestTimeout)
    }
  }
})
