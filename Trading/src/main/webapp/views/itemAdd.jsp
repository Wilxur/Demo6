<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>发布物品</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="header">
    <h2>发布二手物品</h2>
    <div class="user-menu">
        <a href="${pageContext.request.contextPath}/item?action=list">返回列表</a>
    </div>
</div>

<div class="container">
    <form action="${pageContext.request.contextPath}/item?action=add" method="post" class="item-form">
        <div class="form-group">
            <label>物品名称：</label>
            <input type="text" name="name" required>
        </div>

        <div class="form-group">
            <label>描述：</label>
            <textarea name="description" rows="4" cols="50" required></textarea>
        </div>

        <div class="form-group">
            <label>价格：</label>
            <input type="number" step="0.01" name="price" required>
        </div>

        <div class="form-group">
            <label>分类：</label>
            <select name="category">
                <option value="电子产品">电子产品</option>
                <option value="书籍">书籍</option>
                <option value="家具">家具</option>
                <option value="服装">服装</option>
                <option value="其他">其他</option>
            </select>
        </div>

        <div class="form-group">
            <input type="submit" value="发布">
        </div>
    </form>

    <div class="message ${empty msg ? '' : 'success'}">${msg}</div>
</div>
</body>
</html>