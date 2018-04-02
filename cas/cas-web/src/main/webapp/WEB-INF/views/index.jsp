<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<title>欢迎</title>
	<link href="https://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet"/>
</head>
<body>
	<nav class="navbar">
		<ul class="nav navbar-nav navbar-right" style="margin-right:25px;">
			<li><a href="#"><span class="glyphicon"/>欢迎，${user.name}</span></a></li>
			<li><a href="${pageContext.request.contextPath}/logout"><span class="glyphicon glyphicon-off"/>&nbsp;注销登录</a></li>
		</ul>
	</nav>
	
	<div class="container">
		<h3>
			<span class="">当前通道：</span>
			<span class="label label label-info">${applicationScope.sessionAccessor}</span>
		</h3>
		
		<br/>
		<a href="${applicationScope.monitorUrl}" class="btn btn-success">Session监控</a>
		
		<hr>
		<h1>Try it</h1>

	    <form class="form-inline" action="${pageContext.request.contextPath}/public/setAttribute" method="post">
	        <label for="attributeName">Attribute Name </label>
	        <input id="attributeName" type="text" name="attributeName" class="form-control" required/>
	        
	        <label for="attributeValue">Attribute Value </label>
	        <input id="attributeValue" type="text" name="attributeValue" class="form-control" required/>
	        
	        <input type="submit" value="Set Attribute" class="btn btn-primary"/>
	    </form>
	    
	    <hr>
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
	</div>
</body>
</html>

<script src="http://libs.baidu.com/jquery/2.1.1/jquery.min.js" ></script>
<script src="https://cdn.bootcss.com/bootstrap/3.3.7/js/bootstrap.min.js" ></script>