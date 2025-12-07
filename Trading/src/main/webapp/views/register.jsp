<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>用户注册</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="register-container">
    <h2>用户注册</h2>
    <form action="${pageContext.request.contextPath}/user?action=register" method="post">
        <div class="form-group">
            <label>用户名：</label>
            <input type="text" name="username" value="${username}" required>
        </div>
        <div class="form-group">
            <label>密码：</label>
            <input type="password" name="password" required>
        </div>
        <!-- 添加确认密码字段 -->
        <div class="form-group">
            <label>确认密码：</label>
            <input type="password" name="confirmPassword" required>
        </div>
        <div class="form-group">
            <label>邮箱：</label>
            <input type="email" name="email" value="${email}">
        </div>
        <div class="form-group">
            <label>电话：</label>
            <input type="text" name="phone" value="${phone}">
        </div>
        <div class="form-group">
            <input type="submit" value="注册">
        </div>
    </form>

    <!-- 显示错误信息 -->
    <c:if test="${not empty msg}">
        <div class="message error">${msg}</div>
    </c:if>

    <div class="link">
        <a href="${pageContext.request.contextPath}/views/login.jsp">已有账号？立即登录</a>
    </div>
</div>
</body>
</html>