<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<title>登录</title>
	<link href="https://v3.bootcss.com/examples/signin/signin.css" rel="stylesheet">
	<link href="https://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet"/>
</head>
<body>
	<div class="container">
		<form class="form-signin" action="${pageContext.request.contextPath}/processLogin" method="post">
			<input type="hidden" name="loginId" id="loginId" value="${loginId}" />
			<input type="hidden" name="userWebScoket" id="userWebScoket" value="${applicationScope.loginWithWebSocket}" />
			<input type="hidden" name="returnUrl" id="returnUrl" value="${returnUrl}" />
			<input type="hidden" name="logoutUrl" id="logoutUrl"  value="${logoutUrl}" />
			
			<label for="name" class="sr-only">用户名</label>
			<input type="text" name="name" class="form-control" placeholder="用户名" required autofocus/>

			<br/>
			<label for="password" class="sr-only">密码</label>			
			<input type="password" name="passwd" class="form-control" placeholder="密码" required/>
			
			
			<div class="checkbox">
	          <label>
	            <input type="checkbox" name="rememberMe" checked>记住我
	          </label>
	        </div>
			
			<button class="btn btn-lg btn-primary btn-block" type="submit">登录</button>
			<span style="color:red;">${error}</span>
			
			<br /><br /> 使用CAS客户端扫描下方二维码，快捷登录<br />
			<img src="${pageContext.request.contextPath}/public/loginQRCode?loginId=${loginId}" width="300px" height="300px" alt="二维码">
		</form>
	</div>
</body>
</html>
<script src="http://libs.baidu.com/jquery/2.1.1/jquery.min.js"></script>
<script src="https://cdn.bootcss.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<script type="text/javascript">

   	function queryLoginState() {
   		var data = "loginId=" + $('#loginId').val();
   		$.post("${pageContext.request.contextPath}/public/verifyQRCodeLogin", data, function (data) {
   			if (data) {
   				loginSuccess();
   			}
   		});
   	}
   	
   	function receiveLoginStateWithWebSocket() {
   		var ws = new WebSocket("ws://localhost:8080${pageContext.request.contextPath}/wsLogin");
   		ws.onopen = function () {
   			console.log("onpen");
   		};
   		
   		ws.onclose = function () {
   			console.log("onclose");
   		};
   		
   		ws.onmessage = function (msg) {
   			console.log(msg);
   			var result = JSON.parse(msg.data);
   			if (result.login) {
   				loginSuccess();
   			}
   		};
   	}
   	
   	function loginSuccess() {
   		var logoutUrl = $('#logoutUrl').val();
   		var returnUrl = $('#returnUrl').val();
   		window.location.href = "${pageContext.request.contextPath}/login?logoutUrl=" + logoutUrl + "&returnUrl=" + returnUrl;
   	}
   	
   	$(function () {
   		var userWs = $('#userWebScoket').val();
   		if (userWs == 'true') {
   			receiveLoginStateWithWebSocket();
   		} else {
   			window.setInterval("queryLoginState();", 3000);
   		}
   	});
</script>