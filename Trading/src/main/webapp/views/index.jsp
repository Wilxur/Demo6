<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>二手交易平台</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<h1>欢迎来到二手交易平台</h1>
<div>
    <a href="${pageContext.request.contextPath}/views/login.jsp">登录</a> |
    <a href="${pageContext.request.contextPath}/views/register.jsp">注册</a> |
    <a href="${pageContext.request.contextPath}/item?action=list">浏览物品</a>
</div>
</body>
</html>