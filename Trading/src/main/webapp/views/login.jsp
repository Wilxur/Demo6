<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>用户登录</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="login-container">
    <h2>用户登录</h2>
    <form action="${pageContext.request.contextPath}/user?action=login" method="post">
        <div class="form-group">
            <label>用户名：</label>
            <input type="text" name="username" required>
        </div>
        <div class="form-group">
            <label>密码：</label>
            <input type="password" name="password" required>
        </div>
        <div class="form-group">
            <input type="submit" value="登录">
        </div>
    </form>
    <div class="message">${msg}</div>
    <div class="link">
        <a href="${pageContext.request.contextPath}/views/register.jsp">没有账号？立即注册</a>
    </div>
</div>
</body>
</html>