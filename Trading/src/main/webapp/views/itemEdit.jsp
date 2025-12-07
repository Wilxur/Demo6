<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>编辑物品</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
    <script>
        // 获取物品数据（实际项目中应该从后端获取）
        function loadItemData() {
            // 这里可以从sessionStorage或localStorage获取数据
            // 或者通过ajax请求获取
        }
    </script>
</head>
<body onload="loadItemData()">
<div class="header">
    <h2>编辑物品</h2>
    <div class="user-menu">
        <a href="${pageContext.request.contextPath}/item?action=myItems">返回我的物品</a>
    </div>
</div>

<div class="container">
    <form action="${pageContext.request.contextPath}/item?action=update" method="post" class="item-form">
        <input type="hidden" name="id" value="${param.id}">

        <div class="form-group">
            <label>物品名称：</label>
            <input type="text" name="name" value="${item.name}" required>
        </div>

        <div class="form-group">
            <label>描述：</label>
            <textarea name="description" rows="4" cols="50" required>${item.description}</textarea>
        </div>

        <div class="form-group">
            <label>价格：</label>
            <input type="number" step="0.01" name="price" value="${item.price}" required>
        </div>

        <div class="form-group">
            <label>分类：</label>
            <select name="category">
                <option value="电子产品" <c:if test="${item.category == '电子产品'}">selected</c:if>>电子产品</option>
                <option value="书籍" <c:if test="${item.category == '书籍'}">selected</c:if>>书籍</option>
                <option value="家具" <c:if test="${item.category == '家具'}">selected</c:if>>家具</option>
                <option value="服装" <c:if test="${item.category == '服装'}">selected</c:if>>服装</option>
                <option value="其他" <c:if test="${item.category == '其他'}">selected</c:if>>其他</option>
            </select>
        </div>

        <div class="form-group">
            <label>状态：</label>
            <select name="status">
                <option value="1" <c:if test="${item.status == '1'}">selected</c:if>>在售</option>
                <option value="0" <c:if test="${item.status == '0'}">selected</c:if>>已售出</option>
            </select>
        </div>

        <div class="form-group">
            <input type="submit" value="更新">
        </div>
    </form>

    <div class="message error">${msg}</div>
</div>
</body>
</html>