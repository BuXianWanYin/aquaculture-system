# MCP MySQL 自动连接指南

## 问题说明

在使用 MCP MySQL 工具查询数据库时，每次都需要先调用 `mcp_mysql_connect_db` 建立连接，这可能会比较繁琐。

## 解决方案

### 方案 1：在 MCP 配置中使用连接字符串（推荐）

您的 MCP 配置文件 (`~/.cursor/mcp.json`) 已经包含了连接字符串：

```json
{
  "mcpServers": {
    "mysql": {
      "command": "npx",
      "args": ["-y", "@f4ww4z/mcp-mysql-server", "mysql://root:123456@localhost:3306/fish-dish-server"]
    }
  }
}
```

**理论上，MCP 服务器应该能够自动使用这个连接字符串建立连接。** 如果仍然需要手动连接，可能是以下原因：

1. MCP 服务器需要显式调用 `connect_db` 来初始化连接
2. 连接可能不是持久化的，每次会话都需要重新连接

### 方案 2：在 Cursor 中创建快捷指令

您可以在与 Cursor AI 对话时，使用以下模式：

```
请先连接数据库，然后查询...
```

或者：

```
连接数据库并列出所有表
```

Cursor AI 会自动识别需要先建立连接。

### 方案 3：创建查询模板

创建一个包含连接和查询的完整示例：

```markdown
## 标准查询流程

1. 连接数据库：
   - 使用 `mcp_mysql_connect_db` 工具
   - host: localhost
   - user: root
   - password: 123456
   - database: fish-dish-server

2. 执行查询：
   - 使用 `mcp_mysql_query` 工具执行 SELECT
   - 使用 `mcp_mysql_execute` 工具执行 INSERT/UPDATE/DELETE
   - 使用 `mcp_mysql_list_tables` 列出所有表
   - 使用 `mcp_mysql_describe_table` 查看表结构
```

### 方案 4：修改 MCP 服务器配置（高级）

如果 MCP MySQL 服务器支持持久化连接，可以尝试：

1. 检查 `@f4ww4z/mcp-mysql-server` 的文档
2. 查看是否有自动连接或连接池的配置选项
3. 考虑使用其他支持自动连接的 MCP MySQL 服务器

## 当前最佳实践

**推荐做法：**

1. **在每次会话开始时**，先执行一次连接：
   ```
   请连接数据库：localhost:3306/fish-dish-server，用户：root
   ```

2. **在查询请求中明确说明**：
   ```
   请先连接数据库，然后查询 sys_menu 表的所有数据
   ```

3. **使用组合请求**：
   ```
   连接数据库，列出所有表，然后查看 sys_menu 表的结构
   ```

## 自动化脚本示例

如果您想创建一个自动化脚本，可以在项目根目录创建：

```bash
# connect-and-query.sh
# 注意：这需要 MCP 工具支持命令行调用
```

但由于 MCP 工具是通过 Cursor 集成的，最好的方式还是：

1. **在对话中明确说明需要连接**
2. **让 Cursor AI 自动处理连接逻辑**
3. **使用清晰的查询指令**

## 注意事项

- MCP 连接通常是会话级别的，每次新的 Cursor 会话可能需要重新连接
- 连接信息已保存在 MCP 配置中，Cursor AI 可以自动读取
- 如果遇到连接问题，检查数据库服务是否运行，以及连接信息是否正确

## 快速参考

**连接数据库：**
```
使用 mcp_mysql_connect_db 工具
- host: localhost
- user: root  
- password: 123456
- database: fish-dish-server
```

**常用查询：**
- 列出所有表：`mcp_mysql_list_tables`
- 查看表结构：`mcp_mysql_describe_table` (table: "表名")
- 查询数据：`mcp_mysql_query` (sql: "SELECT ...")
- 修改数据：`mcp_mysql_execute` (sql: "INSERT/UPDATE/DELETE ...")

