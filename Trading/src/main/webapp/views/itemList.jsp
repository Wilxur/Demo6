<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>物品列表</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="header">
    <h2>二手物品列表</h2>

    <!-- 搜索表单 -->
    <div class="search-form">
        <form action="${pageContext.request.contextPath}/item?action=search" method="post">
            <input type="text" name="keyword" value="${keyword}" placeholder="输入物品名称或描述">
            <input type="submit" value="搜索">
        </form>
    </div>

    <div class="user-menu">
        <c:if test="${not empty sessionScope.user_session}">
            欢迎，${sessionScope.user_session.username} |
            <a href="${pageContext.request.contextPath}/views/itemAdd.jsp">发布物品</a> |
            <a href="${pageContext.request.contextPath}/item?action=myItems">我的物品</a> |
            <a href="${pageContext.request.contextPath}/user?action=logout">退出</a>
        </c:if>
        <c:if test="${empty sessionScope.user_session}">
            <a href="${pageContext.request.contextPath}/views/login.jsp">登录</a> |
            <a href="${pageContext.request.contextPath}/views/register.jsp">注册</a>
        </c:if>
    </div>
</div>

<div class="container">
    <c:if test="${empty items}">
        <div class="empty-message">暂无物品信息</div>
    </c:if>

    <c:if test="${not empty items}">
        <table class="item-table">
            <thead>
            <tr>
                <th>物品名称</th>
                <th>描述</th>
                <th>价格</th>
                <th>分类</th>
                <th>卖家</th>
                <th>发布时间</th>
                <c:if test="${not empty sessionScope.user_session}">
                    <th>操作</th>
                </c:if>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${items}" var="item">
                <tr>
                    <td>${item.name}</td>
                    <td class="description">${item.description}</td>
                    <td class="price">¥${item.price}</td>
                    <td>${item.category}</td>
                    <td>${item.seller.username} (${item.seller.phone})</td>
                    <td>${item.createTime}</td>
                    <c:if test="${not empty sessionScope.user_session}">
                        <td>
                            <c:if test="${sessionScope.user_session.id == item.userId}">
                                <a href="${pageContext.request.contextPath}/views/itemEdit.jsp?id=${item.id}"
                                   class="btn-edit">编辑</a>
                                <a href="${pageContext.request.contextPath}/item?action=delete&id=${item.id}"
                                   class="btn-delete"
                                   onclick="return confirm('确定删除吗？')">删除</a>
                            </c:if>
                        </td>
                    </c:if>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:if>
</div>
</body>
</html>