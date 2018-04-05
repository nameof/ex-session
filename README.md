# ex-session-sso
&emsp;使用memcached、redis、ehcache等几种方式实现Servlet HttpSession会话数据的存储。没错，它的功能类似于[spring-session](https://projects.spring.io/spring-session)，所以ex-session-sso还提供了spring-session的整合和支持，核心原理就是通过servlet拦截器将默认的HttpSession替换为缓存实现。  
&emsp;提取出Session的好处是显而易见的：
* 会话数据不再是一个黑盒，我们可以对Session进行监控和自由访问。为此ex-session-sso集成了redis、memcached的监控系统[TreeNMS](http://www.treesoft.cn/dms.html)和ehcache的系统[ehcache-monitor-kit](http://terracotta.org/downloads/open-source/)。
* 集成WebSocket，实现android客户端扫码登录。
* 实现跨域单点登录。

&emsp;但是同时带来的弊端就是丧失了Servlet原生Session相关Listener的处理能力。spring-session提供了自定义的[SessionEventHttpSessionListenerAdapter](https://docs.spring.io/spring-session/docs/current/reference/html5/#httpsession-httpsessionlistener)实现事件监听，ex-session-sso目前并没有提供类似的实现。

# 使用
&emsp;在核心配置文件cas-config.properties中配置redis，mecached相关连接信息，运行cas-web即可。

# session数据存储
&emsp;ex-session-sso使用[spring profile](https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html#beans-definition-profiles)机制实现多种session存储方式的切换，支持配置`redis`，`redisson`，`redis-template`，`memcached`，`ehcache`，`spring-session`。
``` 
  <context-param>
		<param-name>spring.profiles.active</param-name>
		<param-value>spring-session</param-value>
  </context-param>
```
&emsp;不同的session存储配置，支持不同的session序列化方式，参考cas-config.properties文件，支持`JSON`，JDK序列化的`BINARY`配置项。
```
  session.format=JSON
```
&emsp;在没有使用spring-session的情况下，session数据从缓存中的加载和提交默认有两种策略：
* 在构造时从缓存中加载所有的用户会话数据（包括所有属性和maxInactiveInterval、lastAccessedTime等元信息）到本地的Map中，在当前会话期间，每一次对Session中Attribute的操作都是对于Map属性的操作，当前请求处理完成之后，再整体提交Map属性，maxInactiveInterval、lastAccessedTime等元信息和更新过期时间到缓存中。可配置cas-config.properties启用：
```
session.bean.name=bufferedCacheHttpSession
```
* 在构造时只从缓存中加载maxInactiveInterval、lastAccessedTime等元信息（如果有的话），后续每一次对Session中Attribute都会直接导致与缓存进行直接交互。当前请求完成之后，只需提交maxInactiveInterval、lastAccessedTime等元信息和更新过期时间到缓存中。可配置cas-config.properties启用：
```
session.bean.name=defaultCacheHttpSession
```
&emsp;spring-session的做法类似于`bufferedCacheHttpSession`。

# 单点登录Single-Sign-On
&emsp;cas-web作为认证中心，负责全局登录和注销控制中心。在登陆时接入客户端站点，并颁发授权票据（授权票据基于[JJWT](https://github.com/jwtk/jjwt)实现），传递用户信息；注销时使用基于[redis list](https://redis.io/topics/data-types-intro#redis-lists)的消息队列发送注销消息到客户端站点,实现局部会话的销毁。
&emsp;整个认证过程可以看作实现了简易的[CAS协议](https://apereo.github.io/cas/4.2.x/protocol/CAS-Protocol.html)。

# 扫码登录
&emsp;扫码登录的web端支持websocket和HTTP轮询，可在cas-config.properties中配置。
```
  login.websocket.enable=true
```
&emsp;为android端提供的web接口同样基于[JJWT](https://github.com/jwtk/jjwt)实现授权。

# 参考
* [spring-session](https://projects.spring.io/spring-session)
* [tomcat-redis-session-manager](https://github.com/jcoleman/tomcat-redis-session-manager)
* [redis-session-manager](https://github.com/chexagon/redis-session-manager)
* [memcached-session-manager](https://github.com/magro/memcached-session-manager)
* [org.apache.catalina.session.StandardSession](http://www.docjar.com/html/api/org/apache/catalina/session/StandardSession.java.html)
* [Servlet HttpSession](https://docs.oracle.com/javaee/6/api/javax/servlet/http/HttpSession.html)
