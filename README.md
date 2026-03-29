# Fund-Security-Management-System-for-Blockchain-Exchanges
# 基于区块链的加密货币交易安全监控系统

这是一个简单的区块链技术的加密货币交易安全监控系统，旨在保障加密货币交易过程的透明性、不可篡改性及用户资金的安全性。系统采用前后端分离架构，结合智能合约实现自动化交易验证与资金管理，并具备实时异常检测与告警能力。


## 技术栈

### 后端
- **语言**：Java
- **框架**：Spring Boot
- **ORM**：MyBatis
- **数据库**：MySQL 8.x
- **开发工具**：IntelliJ IDEA

### 前端
- **语言**：JavaScript
- **框架**：Vue.js
- **HTTP 通信**：Axios
- **区块链钱包**：MetaMask
- **开发工具**：Visual Studio Code
- **运行环境**：Windows 10

### 区块链
- **智能合约语言**：Solidity
- **开发与测试工具**：Remix IDE
- **测试网络**：Sepolia（以太坊测试网）

## 核心功能模块

| 模块 | 功能描述 |
|------|----------|
| 用户管理模块 | 用户登录、注册、信息修改，确保身份认证可信与可追溯 |
| 资金管理模块 | 充值、提现、余额查询，结合区块链保障资金透明与安全 |
| 交易管理模块 | 订单创建、执行、取消，通过智能合约实现自动化交易验证 |
| 安全监控模块 | 实时异常检测、警报推送、日志记录，构建多层防御体系 |

## 快速开始

### 环境要求

- JDK 11+
- MySQL 8.x
- Node.js 14+
- MetaMask 浏览器插件
- 以太坊测试网账户（Sepolia）

### 后端启动

```bash
# 克隆项目
git clone https://github.com/yourusername/your-repo-name.git
cd your-repo-name/backend

# 配置数据库（application.yml）
# 创建数据库并执行 SQL 脚本

# 运行 Spring Boot 应用
mvn spring-boot:run
结果展示：
<img width="735" height="404" alt="image" src="https://github.com/user-attachments/assets/bcbb3bdc-5edf-41fe-9aad-b3c108c9f224" />
<img width="735" height="404" alt="image" src="https://github.com/user-attachments/assets/164b0146-34ba-4072-b0e9-279889b76d34" />
<img width="703" height="664" alt="image" src="https://github.com/user-attachments/assets/dde13776-f983-4cbc-b2de-2dbd53b485f0" />
<img width="689" height="413" alt="image" src="https://github.com/user-attachments/assets/d230acfb-b27a-4b16-962a-3dd774e8f97b" />
<img width="635" height="422" alt="image" src="https://github.com/user-attachments/assets/bb383e4b-d72d-40ec-a413-ee67fcd5b8f6" />
<img width="693" height="436" alt="image" src="https://github.com/user-attachments/assets/d9ee0d2d-ccb0-4ebd-9d68-570b983d4d31" />

