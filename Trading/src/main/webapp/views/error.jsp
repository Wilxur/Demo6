<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<html>
<head>
    <title>错误页面</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="error-container">
    <h1>出错了！</h1>
    <div class="error-details">
        <p><strong>错误信息：</strong>${pageContext.exception.message}</p>
        <p><strong>请求URI：</strong>${pageContext.errorData.requestURI}</p>
        <p><strong>状态码：</strong>${pageContext.errorData.statusCode}</p>
    </div>
    <div class="actions">
        <a href="${pageContext.request.contextPath}/">返回首页</a>
        <a href="javascript:history.back()">返回上一页</a>
    </div>
</div>
</body>
</html>