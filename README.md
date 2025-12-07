# 二手交易平台系统

## 一、系统结构设计

采用MVC三层架构：

- **Controller层**: Servlet接收请求，调用Service
  - `UserServlet`: 处理用户相关请求（注册、登录、退出、资料管理）
  - `ItemServlet`: 处理物品相关请求（增删改查、搜索、我的物品）
- **Service层**: 业务逻辑处理
  - `UserService`: 用户注册、登录、密码加密等业务逻辑
  - `ItemService`: 物品发布、修改、删除、搜索等业务逻辑
- **DAO层**: 数据库访问
  - `BaseDAO`: 通用数据库操作基类（增删改查）
  - `UserDAO`: 用户数据访问操作
  - `ItemDAO`: 物品数据访问操作
- **View层**: JSP页面展示
  - `index.jsp`: 首页
  - `login.jsp`: 登录页面
  - `register.jsp`: 注册页面
  - `itemList.jsp`: 物品列表页面
  - `itemMy.jsp`: 我的物品页面
  - `itemAdd.jsp`: 添加物品页面
  - `itemEdit.jsp`: 编辑物品页面
  - `error.jsp`: 错误页面

## 二、数据库结构说明

### 数据库表设计

系统使用MySQL数据库，数据库名为`secondhand`

#### 1. users表（用户信息表）

存储所有用户信息，用于用户注册、登录和权限管理。

**字段说明：**

| 字段名 | 数据类型 | 约束 | 说明 |
|--------|----------|------|------|
| id | INT | PRIMARY KEY, AUTO_INCREMENT | 用户唯一标识，自增主键 |
| username | VARCHAR(50) | NOT NULL, UNIQUE | 用户名，唯一且不可为空 |
| password | VARCHAR(100) | NOT NULL | 用户密码（MD5加密存储） |
| email | VARCHAR(100) | DEFAULT NULL | 用户邮箱，可选 |
| phone | VARCHAR(20) | DEFAULT NULL | 用户手机号，可选 |
| create_time | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 用户注册时间，自动记录 |

**索引说明：**
- 主键索引：`id`
- 唯一索引：`username`
- 普通索引：`idx_username` (`username`)

#### 2. items表（物品信息表）

存储二手物品信息，包括物品详情、价格、状态等信息。

**字段说明：**

| 字段名 | 数据类型 | 约束 | 说明 |
|--------|----------|------|------|
| id | INT | PRIMARY KEY, AUTO_INCREMENT | 物品唯一标识，自增主键 |
| name | VARCHAR(100) | NOT NULL | 物品名称，最大长度100字符 |
| description | TEXT | DEFAULT NULL | 物品详细描述，支持长文本 |
| price | DECIMAL(10,2) | NOT NULL | 物品价格（10位整数，2位小数） |
| user_id | INT | NOT NULL | 发布者ID，关联用户表 |
| category | VARCHAR(50) | DEFAULT NULL | 物品分类（电子产品、书籍、家具等） |
| status | CHAR(1) | DEFAULT '1' | 物品状态：'0'=已售出, '1'=在售中 |
| create_time | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 物品发布时间，自动记录 |
| update_time | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 物品更新时间，自动更新 |

**索引说明：**
- 主键索引：`id`
- 普通索引：`idx_user_id` (`user_id`)
- 状态索引：`idx_status` (`status`)
- 名称索引：`idx_name` (`name`)
- 全文索引：`idx_search` (`name`, `description`) - 用于搜索功能

**外键约束：**
- `FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE`: 确保`user_id`引用`users`表的`id`字段，删除用户时级联删除其物品

## 三、使用方法

### 1. 访问
```
http://10.100.164.18:8080/Trading/
```

### 2. 功能操作

**未登录用户：**
- 浏览物品列表
- 搜索物品（按名称或描述模糊匹配）
- 查看物品详情（包括卖家信息）
- 注册新账号
- 登录账号

**已登录用户：**
- 发布新物品
- 查看和管理"我的物品"
- 编辑自己的物品
- 删除自己的物品（逻辑删除，标记为已售出状态）
- 退出登录


```

## 四、测试账号密码

系统已预设以下测试账号：

| 用户名 | 密码 | 邮箱 | 电话 |
|--------|------|------|------|
| admin | 123456 | admin@example.com | 13800138000 |
| user1 | 123456 | user1@example.com | 13800138001 |
| user2 | 123456 | user2@example.com | 13800138002 |

### 测试建议

#### 权限验证测试
- 使用user1登录，尝试编辑user2的物品（应显示无权限）
- 使用user1登录，删除自己的物品（应成功）
- 未登录时尝试访问"我的物品"页面（应跳转到登录页）

#### 功能测试
- 使用admin账号登录，发布新物品
- 使用user1账号搜索物品
- 测试物品分类筛选功能
- 验证价格输入格式验证

#### 搜索测试
- 输入"iPhone"进行搜索（应显示相关结果）
- 输入不存在的关键词（应显示无结果）
- 测试中文搜索功能

#### 数据完整性测试
- 删除用户时，验证其发布的物品是否也被删除（外键约束）
- 验证物品状态更新是否正确
- 测试时间戳字段是否自动更新
