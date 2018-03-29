package com.nameof.web.custom.component.request;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.nameof.common.domain.Constants;
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
    
    @Autowired
    private CacheHttpSessionFactory sessionFactory;
    
    public CustomHttpServletRequest(HttpServletRequest request, HttpServletResponse response) {  
        super(request);
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
        String token = CookieUtil.getCookieValue(this, Constants.GLOBAL_SESSION_ID);
        if (StringUtils.isBlank(token)) {
        	token = UUID.randomUUID().toString();
        	CookieUtil.addCookie(response, Constants.GLOBAL_SESSION_ID, token);
        }
        HttpSessionWrapper session = sessionFactory.newSessionInstance(super.getSession(), token);
        
        this.session = session;
        return session;
    }
    
    @Override
    public HttpSession getSession() {  
        return this.getSession(true);
    }
    
}  