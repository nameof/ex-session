<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>欢迎</title>
</head>
<body>
	welcome ! ${user.name}
	<br>
	<br> 当前通道：${applicationScope.sessionAccessor}
	<br>
	<br>
	<a href="${applicationScope.monitorUrl}">Session监控</a>
	<br>
	<br>
	<a href="${pageContext.request.contextPath}/logout">注销登录</a>
	<br>
	<br>
	<h1>Try it</h1>

    <form class="form-inline" action="${pageContext.request.contextPath}/public/setAttribute" method="post">
        <label for="attributeName">Attribute Name</label>
        <input id="attributeName" type="text" name="attributeName"/>
        <label for="attributeValue">Attribute Value</label>
        <input id="attributeValue" type="text" name="attributeValue"/>
        <input type="submit" value="Set Attribute"/>
    </form>
	<table class="table table-striped">
		<thead>
			<tr>
				<th>Attribute Name</th>
				<th>Attribute Value</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${sessionScope}" var="attr">
				<tr>
					<td><c:out value="${attr.key}" /></td>
					<td><c:out value="${attr.value}" /></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</body>
</html>
