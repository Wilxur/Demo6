<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>我的物品</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="header">
    <h2>我的物品</h2>
    <div class="user-menu">
        <a href="${pageContext.request.contextPath}/views/itemAdd.jsp">发布新物品</a> |
        <a href="${pageContext.request.contextPath}/item?action=list">浏览所有物品</a> |
        <a href="${pageContext.request.contextPath}/user?action=logout">退出</a>
    </div>
</div>

<div class="container">
    <c:if test="${empty items}">
        <div class="empty-message">
            您还没有发布任何物品。<br>
            <a href="${pageContext.request.contextPath}/views/itemAdd.jsp">立即发布</a>
        </div>
    </c:if>

    <c:if test="${not empty items}">
        <table class="item-table">
            <thead>
            <tr>
                <th>物品名称</th>
                <th>描述</th>
                <th>价格</th>
                <th>分类</th>
                <th>状态</th>
                <th>发布时间</th>
                <th>操作</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${items}" var="item">
                <tr>
                    <td>${item.name}</td>
                    <td class="description">${item.description}</td>
                    <td class="price">¥${item.price}</td>
                    <td>${item.category}</td>
                    <td>
                        <c:choose>
                            <c:when test="${item.status == '1'}">
                                <span class="status-on">在售</span>
                            </c:when>
                            <c:when test="${item.status == '0'}">
                                <span class="status-off">已售出</span>
                            </c:when>
                            <c:otherwise>
                                ${item.status}
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td>${item.createTime}</td>
                    <td>
                        <a href="${pageContext.request.contextPath}/views/itemEdit.jsp?id=${item.id}"
                           class="btn-edit">编辑</a>
                        <a href="${pageContext.request.contextPath}/item?action=delete&id=${item.id}"
                           class="btn-delete"
                           onclick="return confirm('确定删除吗？')">删除</a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:if>
</div>
</body>
</html>