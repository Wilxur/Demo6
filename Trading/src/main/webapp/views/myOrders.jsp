<%-- myOrders.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>我的订单</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="header">
    <h2>我的订单</h2>
    <div class="user-menu">
        <a href="${pageContext.request.contextPath}/item?action=list">浏览物品</a> |
        <a href="${pageContext.request.contextPath}/item?action=myItems">我的物品</a> |
        <a href="${pageContext.request.contextPath}/user?action=logout">退出</a>
    </div>
</div>

<div class="container">
    <c:if test="${empty orders}">
        <div class="empty-message">
            您还没有任何订单。
        </div>
    </c:if>

    <c:if test="${not empty orders}">
        <table class="order-table">
            <thead>
            <tr>
                <th>物品名称</th>
                <th>价格</th>
                <th>卖家</th>
                <th>订单时间</th>
                <th>状态</th>
                <th>操作</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${orders}" var="order">
                <tr>
                    <td>${order.item.name}</td>
                    <td>¥${order.price}</td>
                    <td>${order.seller.username}</td>
                    <td>${order.orderTime}</td>
                    <td>
                        <c:choose>
                            <c:when test="${order.status == 'pending'}">
                                <span class="status-pending">待确认</span>
                            </c:when>
                            <c:when test="${order.status == 'completed'}">
                                <span class="status-completed">已完成</span>
                            </c:when>
                            <c:otherwise>${order.status}</c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <a href="${pageContext.request.contextPath}/order?action=detail&orderId=${order.id}"
                           class="btn-view">查看详情</a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:if>
</div>
</body>
</html>