<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="header">
    <div class="logo">
        <h1>二手交易平台</h1>
    </div>
    <div class="nav">
        <a href="${pageContext.request.contextPath}/item?action=list">首页</a>
        <c:if test="${not empty sessionScope.user_session}">
            <a href="${pageContext.request.contextPath}/View/itemAdd.jsp">发布物品</a>
            <a href="${pageContext.request.contextPath}/item?action=myItems">我的物品</a>
            <span class="user-info">欢迎，${sessionScope.user_session.username}</span>
            <a href="${pageContext.request.contextPath}/user?action=logout">退出</a>
        </c:if>
        <c:if test="${empty sessionScope.user_session}">
            <a href="${pageContext.request.contextPath}/View/login.jsp">登录</a>
            <a href="${pageContext.request.contextPath}/View/register.jsp">注册</a>
        </c:if>
    </div>
</div>