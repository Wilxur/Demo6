
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>购买物品</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="header">
    <h2>购买物品</h2>
    <div class="user-menu">
        <a href="${pageContext.request.contextPath}/item?action=list">返回列表</a>
    </div>
</div>

<div class="container">
    <c:if test="${not empty item}">
        <div class="item-info">
            <h3>${item.name}</h3>
            <p>描述：${item.description}</p>
            <p>价格：¥${item.price}</p>
            <p>卖家：${item.seller.username} (${item.seller.phone})</p>
        </div>

        <form action="${pageContext.request.contextPath}/order?action=create" method="post" class="buy-form">
            <input type="hidden" name="itemId" value="${item.id}">

            <div class="form-group">
                <label>您的联系方式：</label>
                <input type="text" name="contactInfo" required placeholder="请输入您的手机号">
            </div>

            <div class="form-group">
                <label>备注信息（可选）：</label>
                <textarea name="note" rows="3" placeholder="可填写您的其他要求"></textarea>
            </div>

            <div class="form-group">
                <input type="submit" value="确认购买">
            </div>
        </form>
    </c:if>

    <div class="message error">${msg}</div>
</div>
</body>
</html>