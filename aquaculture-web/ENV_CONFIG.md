# 环境变量配置说明

## 概述

项目已配置为使用环境变量进行配置管理，可以通过不同环境的 `.env` 文件来管理配置，避免硬编码配置值。

## 环境变量文件

Vite 会自动加载以下环境变量文件（按优先级从高到低）：

1. `.env.[mode].local` - 本地特定模式（如 `.env.development.local`）
2. `.env.local` - 本地环境（除 test 外的所有模式）
3. `.env.[mode]` - 特定模式（如 `.env.development`、`.env.production`）
4. `.env` - 默认环境变量

**注意**: `.env`、`.env.local`、`.env.[mode].local` 文件已被 `.gitignore` 排除，不会被提交到仓库。

## 配置步骤

### 1. 创建环境变量文件

在项目根目录（`aquaculture-web/`）下创建以下文件：

#### `.env.development` (开发环境)

```env
# 开发环境配置
# Development Environment Configuration

# 开发服务器端口
# Development server port
VITE_PORT=3000

# API 代理目标地址（后端服务地址）
# API proxy target (backend service address)
VITE_API_BASE_URL=http://localhost:8080

# API 前缀
# API prefix
VITE_API_PREFIX=/api

# 上传文件代理前缀
# Upload files proxy prefix
VITE_UPLOAD_PREFIX=/uploads

# 请求超时时间（毫秒）
# Request timeout (milliseconds)
VITE_REQUEST_TIMEOUT=10000
```

#### `.env.production` (生产环境)

```env
# 生产环境配置
# Production Environment Configuration

# API 基础地址（生产环境后端服务地址）
# API base URL (production backend service address)
VITE_API_BASE_URL=http://localhost:8080

# API 前缀
# API prefix
VITE_API_PREFIX=/api

# 上传文件地址前缀
# Upload files URL prefix
VITE_UPLOAD_PREFIX=/uploads

# 请求超时时间（毫秒）
# Request timeout (milliseconds)
VITE_REQUEST_TIMEOUT=10000
```

### 2. 环境变量说明

| 变量名 | 说明 | 默认值 | 示例 |
|--------|------|--------|------|
| `VITE_PORT` | 开发服务器端口（仅开发环境） | `3000` | `3000` |
| `VITE_API_BASE_URL` | 后端 API 基础地址 | `http://localhost:8080` | `http://localhost:8080` |
| `VITE_API_PREFIX` | API 请求前缀 | `/api` | `/api` |
| `VITE_UPLOAD_PREFIX` | 上传文件路径前缀 | `/uploads` | `/uploads` |
| `VITE_REQUEST_TIMEOUT` | 请求超时时间（毫秒） | `10000` | `10000` |

### 3. 使用说明

#### 在代码中访问环境变量

在客户端代码中，可以通过 `import.meta.env` 访问以 `VITE_` 开头的环境变量：

```javascript
// 访问环境变量
const apiBaseUrl = import.meta.env.VITE_API_BASE_URL
const apiPrefix = import.meta.env.VITE_API_PREFIX
const requestTimeout = import.meta.env.VITE_REQUEST_TIMEOUT

// 示例：在 request.js 中使用
const request = axios.create({
  baseURL: import.meta.env.VITE_API_PREFIX || '/api',
  timeout: parseInt(import.meta.env.VITE_REQUEST_TIMEOUT || '10000', 10)
})
```

#### 在 vite.config.js 中使用

在 `vite.config.js` 中，通过 `loadEnv()` 函数加载环境变量：

```javascript
import { defineConfig, loadEnv } from 'vite'

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '')
  const port = parseInt(env.VITE_PORT || '3000', 10)
  // ...
})
```

## 运行模式

### 开发模式

```bash
npm run dev
```

会自动加载 `.env.development` 文件。

### 生产模式

```bash
npm run build
```

会自动加载 `.env.production` 文件。

## 注意事项

1. **环境变量命名**: 在客户端代码中访问的环境变量必须以 `VITE_` 开头，否则无法在客户端代码中访问。

2. **敏感信息**: 不要在 `.env` 文件中存储敏感信息（如密码、密钥等），这些文件可能会被包含在构建产物中。

3. **默认值**: 代码中已为所有配置项设置了默认值，即使不创建环境变量文件，项目也能正常运行。

4. **本地覆盖**: 如果需要覆盖本地配置，可以创建 `.env.local` 文件，该文件会被 `.gitignore` 排除。

5. **生产环境**: 生产环境部署时，确保正确配置 `.env.production` 文件中的 `VITE_API_BASE_URL`，指向实际的后端服务地址。

## 示例配置

### 开发环境 - 本地后端

```env
VITE_PORT=3000
VITE_API_BASE_URL=http://localhost:8080
VITE_API_PREFIX=/api
VITE_UPLOAD_PREFIX=/uploads
VITE_REQUEST_TIMEOUT=10000
```

### 开发环境 - 远程后端

```env
VITE_PORT=3000
VITE_API_BASE_URL=http://192.168.1.100:8080
VITE_API_PREFIX=/api
VITE_UPLOAD_PREFIX=/uploads
VITE_REQUEST_TIMEOUT=30000
```

### 生产环境

```env
VITE_API_BASE_URL=https://api.example.com
VITE_API_PREFIX=/api
VITE_UPLOAD_PREFIX=/uploads
VITE_REQUEST_TIMEOUT=10000
```

