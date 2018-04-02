<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<title>首页</title>
	<link href="https://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet"/>
</head>

<body>
	<div class="container">
		<h1>welcome to sso-client! ${sessionScope.user.name}</h1>
		<br>
		<a href="${sessionScope.CasLogoutUrl}">注销登录</a>
	</div>
</body>
</html>
<script src="http://libs.baidu.com/jquery/2.1.1/jquery.min.js" ></script>
<script src="https://cdn.bootcss.com/bootstrap/3.3.7/js/bootstrap.min.js" ></script>