<%-- buySuccess.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>购买成功</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="success-container">
    <h1>购买成功！</h1>
    <div class="success-message">
        <p>${msg}</p>
        <p>卖家将通过您提供的联系方式与您联系</p>
    </div>
    <div class="actions">
        <a href="${pageContext.request.contextPath}/item?action=list">继续浏览</a>
        <a href="${pageContext.request.contextPath}/order?action=myOrders">查看我的订单</a>
    </div>
</div>
</body>
</html>