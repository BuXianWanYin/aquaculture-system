# 水产养殖管理系统 - 前端项目

## 项目简介

水产养殖管理系统前端项目，基于 Vue 3 构建的现代化 Web 应用。本项目采用前后端分离架构，提供直观友好的用户界面，支持养殖计划管理、产量统计、数据分析等核心业务功能。

---

## 技术栈

### 核心框架

| 技术 | 版本 | 说明 |
|------|------|------|
| **Vue** | 3.5.13 | 渐进式 JavaScript 框架，采用 Composition API |
| **Vite** | 6.0.5 | 新一代前端构建工具，提供快速开发体验 |

### 路由管理

| 技术 | 版本 | 说明 |
|------|------|------|
| **Vue Router** | 4.4.5 | Vue.js 官方路由管理器，支持路由守卫和权限控制 |

### 状态管理

| 技术 | 版本 | 说明 |
|------|------|------|
| **Pinia** | 2.1.7 | Vue 3 官方推荐的状态管理库，用于管理用户信息和全局状态 |

### UI 组件库

| 技术 | 版本 | 说明 |
|------|------|------|
| **Element Plus** | 2.8.8 | 基于 Vue 3 的企业级 UI 组件库 |
| **@element-plus/icons-vue** | 2.3.1 | Element Plus 官方图标库 |

### HTTP 请求

| 技术 | 版本 | 说明 |
|------|------|------|
| **Axios** | 1.7.7 | 基于 Promise 的 HTTP 客户端，用于 API 请求 |

### 数据可视化

| 技术 | 版本 | 说明 |
|------|------|------|
| **ECharts** | 5.5.1 | 百度开源的数据可视化图表库，用于仪表盘和报表展示 |

### 开发依赖

| 技术 | 版本 | 说明 |
|------|------|------|
| **@vitejs/plugin-vue** | 5.2.1 | Vite 的 Vue 3 插件 |

---

## 项目结构

```
aquaculture-web/
├── public/                    # 静态资源目录
│   └── vite.svg
├── src/                       # 源代码目录
│   ├── api/                   # API 接口封装
│   │   ├── user.js           # 用户相关接口
│   │   ├── role.js           # 角色相关接口
│   │   ├── permission.js     # 权限相关接口
│   │   ├── plan.js           # 计划相关接口
│   │   ├── yield.js          # 产量相关接口
│   │   ├── dashboard.js      # 仪表盘接口
│   │   ├── statistic.js      # 统计接口
│   │   ├── area.js           # 区域接口
│   │   ├── breed.js          # 品种接口
│   │   ├── department.js     # 部门接口
│   │   ├── equipment.js      # 设备接口
│   │   ├── diseaseRecord.js  # 病害记录接口
│   │   ├── diseasePrevention.js  # 病害预防接口
│   │   ├── feedInventory.js  # 饲料库存接口
│   │   ├── feedPurchase.js   # 饲料采购接口
│   │   ├── feedUsage.js      # 饲料使用接口
│   │   ├── feedingRecord.js  # 投喂记录接口
│   │   ├── dailyInspection.js # 日常巡检接口
│   │   ├── medicineUsage.js  # 用药记录接口
│   │   ├── customer.js       # 客户接口
│   │   ├── salesRecord.js    # 销售记录接口
│   │   ├── yieldEvidence.js  # 产量凭证接口
│   │   ├── planAdjust.js     # 计划调整接口
│   │   ├── message.js        # 消息接口
│   │   └── operLog.js        # 操作日志接口
│   ├── composables/          # 组合式函数
│   │   ├── usePermission.js  # 权限判断工具
│   │   └── useRole.js        # 角色判断工具
│   ├── constants/            # 常量定义
│   │   ├── permission.js     # 权限代码常量
│   │   └── role.js           # 角色常量
│   ├── layout/               # 布局组件
│   │   └── Index.vue         # 主布局组件
│   ├── router/               # 路由配置
│   │   └── index.js          # 路由定义和守卫
│   ├── stores/               # Pinia 状态管理
│   │   └── user.js           # 用户状态管理
│   ├── utils/                # 工具函数
│   │   ├── request.js        # Axios 请求封装
│   │   ├── permission.js     # 权限工具函数
│   │   └── date.js           # 日期工具函数
│   ├── views/                # 页面组件
│   │   ├── Login.vue         # 登录页面
│   │   ├── Register.vue      # 注册页面
│   │   ├── Dashboard.vue     # 仪表盘页面
│   │   ├── user/             # 用户管理
│   │   │   └── UserList.vue
│   │   ├── role/             # 角色管理
│   │   │   └── RoleList.vue
│   │   ├── permission/       # 权限管理
│   │   │   └── PermissionList.vue
│   │   ├── department/       # 部门管理
│   │   │   └── DepartmentList.vue
│   │   ├── area/             # 区域管理
│   │   │   └── AreaList.vue
│   │   ├── breed/            # 品种管理
│   │   │   └── BreedList.vue
│   │   ├── equipment/        # 设备管理
│   │   │   └── EquipmentList.vue
│   │   ├── plan/             # 计划管理
│   │   │   └── PlanList.vue
│   │   ├── yield/            # 产量管理
│   │   │   └── YieldList.vue
│   │   ├── production/       # 生产管理
│   │   │   ├── ProductionManagement.vue
│   │   │   ├── DailyInspection.vue
│   │   │   └── FeedingRecord.vue
│   │   ├── feed/             # 饲料管理
│   │   │   ├── FeedManagement.vue
│   │   │   ├── FeedInventory.vue
│   │   │   ├── FeedPurchase.vue
│   │   │   └── FeedUsage.vue
│   │   ├── disease/          # 病害管理
│   │   │   ├── DiseaseManagement.vue
│   │   │   ├── DiseaseRecord.vue
│   │   │   ├── DiseasePrevention.vue
│   │   │   └── MedicineUsage.vue
│   │   ├── sales/            # 销售管理
│   │   │   ├── SalesManagement.vue
│   │   │   ├── SalesRecord.vue
│   │   │   └── Customer.vue
│   │   ├── statistic/        # 统计分析
│   │   │   └── StatisticList.vue
│   │   ├── message/          # 消息通知
│   │   │   └── MessageList.vue
│   │   ├── operLog/          # 操作日志
│   │   │   └── OperLogList.vue
│   │   └── profile/          # 个人中心
│   │       └── Profile.vue
│   ├── App.vue               # 根组件
│   ├── main.js               # 入口文件
│   └── style.css             # 全局样式
├── index.html                # HTML 模板
├── vite.config.js            # Vite 配置文件
├── package.json              # 项目依赖配置
└── README.md                 # 项目说明文档
```

---

## 核心功能模块

### 1. 用户认证与权限管理
- 用户登录/注册
- JWT Token 管理
- 基于权限的页面和按钮控制
- 用户信息管理

### 2. 基础数据管理
- 养殖品种管理
- 养殖区域管理
- 设备信息管理
- 部门管理

### 3. 养殖计划管理
- 计划录入与编辑
- 计划审批流程
- 计划调整管理
- 计划查询与筛选

### 4. 产量统计管理
- 产量录入
- 产量审核
- 产量凭证上传
- 产量统计分析

### 5. 生产管理
- 日常巡检记录
- 投喂记录管理
- 饲料库存管理
- 饲料采购与使用

### 6. 病害管理
- 病害记录
- 病害预防
- 用药记录管理

### 7. 销售管理
- 客户信息管理
- 销售记录管理

### 8. 数据报表与分析
- 仪表盘数据展示
- 多维度统计分析
- 数据可视化图表

### 9. 系统管理
- 操作日志查询
- 消息通知管理

---

## 开发指南

### 环境要求

- **Node.js**: 16.0 或更高版本
- **npm**: 7.0 或更高版本（或使用 yarn/pnpm）

### 安装依赖

```bash
npm install
```

### 开发模式

```bash
npm run dev
```

启动后访问：http://localhost:3000

### 生产构建

```bash
npm run build
```

构建产物将输出到 `dist` 目录。

### 预览构建结果

```bash
npm run preview
```

---

## 配置文件说明

### vite.config.js

```javascript
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/uploads': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

**关键配置：**
- 开发服务器端口：3000
- API 代理：`/api` -> `http://localhost:8080`
- 文件上传代理：`/uploads` -> `http://localhost:8080`
- 路径别名：`@` 指向 `src` 目录

---

## 核心代码说明

### 请求封装 (utils/request.js)

```javascript
// 创建 Axios 实例
const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器：自动添加 Token
request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 响应拦截器：统一处理错误
request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 200) {
      return res
    } else {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
  },
  error => {
    // 处理 HTTP 错误状态码
    // 401: 未授权，跳转登录
    // 403: 拒绝访问
    // 404: 资源不存在
    // 500: 服务器错误
  }
)
```

### 状态管理 (stores/user.js)

使用 Pinia 管理用户状态：

```javascript
export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))

  const loginAction = async (loginData) => {
    const res = await login(loginData)
    if (res.code === 200 && res.data?.token) {
      setToken(res.data.token)
      setUserInfo(res.data)
      return true
    }
    throw new Error(res.message || '登录失败')
  }

  return { token, userInfo, loginAction, logout }
})
```

### 权限控制 (composables/usePermission.js)

```javascript
export const usePermission = () => {
  const userStore = useUserStore()
  
  const hasPermission = (permissionCode) => {
    const permissions = userStore.userInfo?.permissions || []
    return permissions.includes(permissionCode)
  }
  
  return { hasPermission }
}
```

在组件中使用：

```vue
<script setup>
import { usePermission } from '@/composables/usePermission'

const { hasPermission } = usePermission()
</script>

<template>
  <el-button v-if="hasPermission('plan:add')" @click="handleAdd">
    新增计划
  </el-button>
</template>
```

---

## API 接口说明

所有 API 接口都封装在 `src/api/` 目录下，统一使用 `request` 工具发送请求。

### 接口响应格式

```typescript
interface Result<T> {
  code: number      // 200 表示成功，其他表示失败
  message: string   // 响应消息
  data: T          // 响应数据
}
```

### 接口示例

```javascript
// 用户登录
export const login = (data) => {
  return request({
    url: '/user/login',
    method: 'post',
    data
  })
}

// 分页查询用户列表
export const getUserList = (params) => {
  return request({
    url: '/user/page',
    method: 'get',
    params
  })
}
```

---

## 路由配置

路由配置在 `src/router/index.js`，支持：
- 路由守卫（登录验证、权限验证）
- 动态路由加载
- 路由元信息（meta）配置

### 路由守卫示例

```javascript
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  
  if (to.path === '/login') {
    if (token) {
      next('/dashboard')
    } else {
      next()
    }
  } else {
    if (!token) {
      next('/login')
    } else {
      // 权限验证
      if (to.meta.permissions) {
        const hasPermission = checkPermission(to.meta.permissions)
        if (!hasPermission) {
          ElMessage.warning('您没有权限访问该页面')
          next('/dashboard')
          return
        }
      }
      next()
    }
  }
})
```

---

## 权限控制

### 权限代码格式

- 格式：`模块:操作` 或 `模块:子模块:操作`
- 示例：
  - `system:user:view` - 系统管理-用户-查看
  - `plan:add` - 计划-新增
  - `yield:evidence:delete` - 产量-凭证-删除

### 权限使用方式

1. **页面级权限**：在路由配置中设置 `meta.permissions`
2. **按钮级权限**：使用 `v-if="hasPermission('xxx')"` 控制显示
3. **菜单级权限**：根据用户权限动态生成菜单

---

## 开发规范

### 文件命名规范

- 组件文件：PascalCase（如 `UserList.vue`）
- 工具文件：camelCase（如 `request.js`）
- API 文件：camelCase（如 `user.js`）

### 代码规范

- 使用 Vue 3 Composition API
- 组件单一职责原则
- Props 和 Emits 明确类型
- 统一使用 ESLint 进行代码检查

### 组件开发规范

```vue
<script setup>
import { ref, computed, onMounted } from 'vue'
import { usePermission } from '@/composables/usePermission'
import { getUserList } from '@/api/user'

const { hasPermission } = usePermission()

const userList = ref([])
const loading = ref(false)

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getUserList({ current: 1, size: 10 })
    userList.value = res.data.records
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchData()
})
</script>

<template>
  <div>
    <el-button 
      v-if="hasPermission('user:add')" 
      @click="handleAdd"
    >
      新增用户
    </el-button>
  </div>
</template>
```

---

## 部署说明

### 构建生产版本

```bash
npm run build
```

### 部署到服务器

1. 将 `dist` 目录内容上传到 Web 服务器（如 Nginx）
2. 配置 Nginx 代理后端 API：

```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    location / {
        root /path/to/dist;
        try_files $uri $uri/ /index.html;
    }
    
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
    
    location /uploads {
        proxy_pass http://localhost:8080;
    }
}
```

---

## 依赖清单

### 生产依赖

```json
{
  "vue": "^3.5.13",
  "vue-router": "^4.4.5",
  "pinia": "^2.1.7",
  "axios": "^1.7.7",
  "element-plus": "^2.8.8",
  "@element-plus/icons-vue": "^2.3.1",
  "echarts": "^5.5.1"
}
```

### 开发依赖

```json
{
  "@vitejs/plugin-vue": "^5.2.1",
  "vite": "^6.0.5"
}
```

---

## 常见问题

### 1. 跨域问题

开发环境已配置代理，生产环境需要在 Nginx 中配置代理。

### 2. Token 过期

Token 过期后会自动跳转到登录页面，用户需要重新登录。

### 3. 权限控制

前端权限控制主要用于 UI 展示，真正的权限验证在后端进行。

---

## 参考资料

- [Vue 3 官方文档](https://cn.vuejs.org/)
- [Vite 官方文档](https://cn.vitejs.dev/)
- [Element Plus 官方文档](https://element-plus.org/zh-CN/)
- [Vue Router 官方文档](https://router.vuejs.org/zh/)
- [Pinia 官方文档](https://pinia.vuejs.org/zh/)
- [ECharts 官方文档](https://echarts.apache.org/zh/index.html)

---

*文档更新时间: 2025年*
