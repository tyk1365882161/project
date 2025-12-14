# E-Commerce Website (Java Spring Boot)

> 华南理工大学《网络应用开发》课程期末项目

这是一个基于 **Spring Boot 3.x** 和 **Thymeleaf** 开发的简易电子商务网站系统。项目实现了从用户浏览、下单到结算的完整购物流程，并包含一个功能完善的后台管理系统，用于商品管理和销售数据统计。

---

## 学生信息

* **姓名**：谭彦铿
* **学号**：202330451611
* **班级**：23级计科2班

---

## 在线测试

* **访问地址**：http://8.138.56.217:8080
* **测试账号**：
  
  **普通用户**：`user` / `123456`  (用于购物流程测试)
  
  **管理员**：`admin` / `123456` (用于后台管理测试)

---

##  项目结构说明

本项目基于 Maven 标准目录结构，采用经典的分层架构设计：

```text
src/main/java/com/example/shop
├── config              // 全局配置类 (如 WebMvc 配置)
├── controller          // 控制层 (处理 HTTP 请求)
├── entity              // 实体层 (对应数据库表结构)
├── interceptor         // 拦截器 (处理登录拦截等)
├── repository          // 持久层 (Spring Data JPA 接口)
├── service             // 业务逻辑层 (接口与实现)
├── DataInit.java       // 数据初始化脚本 (项目启动时预加载数据)
└── ShopApplication.java // Spring Boot 项目启动入口类

src/main/resources
├── templates           // Thymeleaf 前端模板
│   ├── admin         // 后台管理系统页面 (admin/index.html 等)
│   └── *.html      // 前台商城页面 (index.html, login.html 等)
└── application.properties // 项目核心配置文件
