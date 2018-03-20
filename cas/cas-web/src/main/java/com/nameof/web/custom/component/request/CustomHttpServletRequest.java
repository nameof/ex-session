package com.nameof.web.custom.component.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.nameof.common.utils.CookieUtil;
import com.nameof.web.custom.component.factory.CacheHttpSessionFactory;
import com.nameof.web.custom.component.session.HttpSessionWrapper;

/**
 * HttpServletRequest的包装类，用于包装获取和创建HttpSession操作
 * 
 * @author ChengPan
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CustomHttpServletRequest extends HttpServletRequestWrapper {  
  
    private HttpSession session;
    
    private HttpServletResponse response;
    
    public static final String COOKIE_SESSION_KEY = "token";
    
    private boolean isNewSession = false;
    
    @Autowired
    private CacheHttpSessionFactory sessionFactory;
    
    public CustomHttpServletRequest(HttpServletRequest request, HttpServletResponse response) {  
        super(request);
        this.response = response;
    }
    
    public CustomHttpServletRequest() {
    	super(new UnimplementRequest());
    }
    
    public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	@Override
    public HttpSession getSession(boolean create) {  
        if (session != null) {  
            return session;
        }
        if (!create) {
        	return null;
        }
        String token = CookieUtil.getCookieValue(this, COOKIE_SESSION_KEY);
        if (StringUtils.isBlank(token)) {
        	isNewSession = true;
        	token = UUID.randomUUID().toString();
        	CookieUtil.addCookie(response, COOKIE_SESSION_KEY, token);
        }
        HttpSessionWrapper session = sessionFactory.newSessionInstance(super.getSession(), token);
        session.setNew(isNewSession);
        
        this.session = session;
        return session;
    }
    
    @Override
    public HttpSession getSession() {  
        return this.getSession(true);
    }
    
    private static class UnimplementRequest implements HttpServletRequest {

		@Override
		public Object getAttribute(String name) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Enumeration<String> getAttributeNames() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getCharacterEncoding() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setCharacterEncoding(String env)
				throws UnsupportedEncodingException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public int getContentLength() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public long getContentLengthLong() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public String getContentType() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ServletInputStream getInputStream() throws IOException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getParameter(String name) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Enumeration<String> getParameterNames() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String[] getParameterValues(String name) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Map<String, String[]> getParameterMap() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getProtocol() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getScheme() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getServerName() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getServerPort() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public BufferedReader getReader() throws IOException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getRemoteAddr() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getRemoteHost() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setAttribute(String name, Object o) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void removeAttribute(String name) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Locale getLocale() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Enumeration<Locale> getLocales() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isSecure() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public RequestDispatcher getRequestDispatcher(String path) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getRealPath(String path) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getRemotePort() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public String getLocalName() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getLocalAddr() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getLocalPort() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public ServletContext getServletContext() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public AsyncContext startAsync() throws IllegalStateException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public AsyncContext startAsync(ServletRequest servletRequest,
				ServletResponse servletResponse) throws IllegalStateException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isAsyncStarted() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isAsyncSupported() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public AsyncContext getAsyncContext() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public DispatcherType getDispatcherType() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getAuthType() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Cookie[] getCookies() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getDateHeader(String name) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public String getHeader(String name) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Enumeration<String> getHeaders(String name) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Enumeration<String> getHeaderNames() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getIntHeader(String name) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public String getMethod() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getPathInfo() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getPathTranslated() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getContextPath() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getQueryString() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getRemoteUser() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isUserInRole(String role) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Principal getUserPrincipal() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getRequestedSessionId() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getRequestURI() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public StringBuffer getRequestURL() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getServletPath() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public HttpSession getSession(boolean create) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public HttpSession getSession() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String changeSessionId() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isRequestedSessionIdValid() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isRequestedSessionIdFromCookie() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isRequestedSessionIdFromURL() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isRequestedSessionIdFromUrl() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean authenticate(HttpServletResponse response)
				throws IOException, ServletException {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void login(String username, String password)
				throws ServletException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void logout() throws ServletException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Collection<Part> getParts() throws IOException, ServletException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Part getPart(String name) throws IOException, ServletException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass)
				throws IOException, ServletException {
			// TODO Auto-generated method stub
			return null;
		}
    	
    }
}  