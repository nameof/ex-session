<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>登录</title>
    <script src="http://libs.baidu.com/jquery/2.1.1/jquery.min.js"></script>
    <script type="text/javascript">
    	function queryLoginState() {
    		$.post("${pageContext.request.contextPath}/verifyQRCodeLogin","",function (data) {
    			if (data) {
    				window.location.href = "${pageContext.request.contextPath}/index";
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
    				window.location.href = "${pageContext.request.contextPath}/index";
    			}
    		};
    	}
    	
    	$(function () {
    		<c:if test="${applicationScope.loginWithWebSocket}">
    			receiveLoginStateWithWebSocket();
    		</c:if>
    		<c:if test="${!applicationScope.loginWithWebSocket}">
    			window.setInterval("queryLoginState();", 3000);
    		</c:if>
    	});
    </script>
  </head>
  <body>
  		<form action="${pageContext.request.contextPath}/processLogin" method="post">
  			<input type="hidden" name="returnUrl" value="${returnUrl}"/>
  			<input type="hidden" name="logoutUrl" value="${logoutUrl}"/>
			用户名:<input type="text" name="name"/><br /><br />
			密码:<input type="password" name="passwd"/><br /><br />
			<input id="rememberMe" name="rememberMe" type="checkbox" checked="checked"/>
			<label for="rememberMe">记住我</label> 
			<br /><br />
			<input type="submit" value="登陆"/><span style="color:red;" >${error}</span>
			<br /><br />
			使用CAS客户端扫描下方二维码，快捷登录<br />
			<img src="${pageContext.request.contextPath}/public/loginQRCode" width="300px" height="300px" alt="二维码" >
		</form>
  </body>
</html>
