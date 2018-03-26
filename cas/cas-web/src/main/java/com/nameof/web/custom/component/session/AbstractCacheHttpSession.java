package com.nameof.web.custom.component.session;

import java.util.Enumeration;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 由于session为全局共享，creationTime、lastAccessedTime等属性取决于多个webapp的访问
 * @author ChengPan
 */
@SuppressWarnings("deprecation")
public abstract class AbstractCacheHttpSession extends HttpSessionWrapper {

	private static final long serialVersionUID = 2182000609010787307L;
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	/** 存储maxInactiveInterval key */
	protected static final String CACHE_INTERVAL_KEY = "@maxInactiveInterval";
	
	/** 存储createTime key */
	protected static final String CACHE_CREATE_TIME_KEY = "@sessionCreateTime";
	
	/** 存储lastAccessTime key */
	protected static final String CACHE_LAST_ACCESSED_TIME_KEY = "@lastAccessedTime";
	
	/** 默认过期时间为30分钟  */
	protected static final int DEFAULT_INTERVAL = 60 * 30;
	
	private long creationTime = System.currentTimeMillis();
    
	private long lastAccessedTime = System.currentTimeMillis();
	
	private long accessedTime = System.currentTimeMillis();//本次访问时间
	
	private int maxInactiveInterval = DEFAULT_INTERVAL;
    
	private boolean isNew = false;
    
	private boolean isInvalid = false;
    
    /** 是否为永久性session
     *  TODO 不建议真的直接设置为永不失效的缓存，可以设置一个较长的过期时间
     */
	private boolean isPersist = false;
    
	/** session id */
    protected final String token;
    
    public AbstractCacheHttpSession(HttpSession session, String token) {
    	super(session);
    	this.token = token;
    }
    
    protected void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}

	@Override
    public long getCreationTime() {
    	checkValid();
        return creationTime;
    }
    
    @Override  
    public String getId() {
        return token;
    }
    
    public boolean isPersist() {
		return isPersist;
	}

	public void setPersist(boolean isPersist) {
		this.isPersist = isPersist;
	}

	protected void setLastAccessedTime(long lastAccessedTime) {
		this.lastAccessedTime = lastAccessedTime;
	}

	@Override  
    public long getLastAccessedTime() {  
        return lastAccessedTime;
    }  
	
    public long getAccessedTime() {
		return accessedTime;
	}

	@Override  
    public void setMaxInactiveInterval(int interval) {  
        this.maxInactiveInterval = interval;
        this.isPersist = interval == -1;
    }  
  
    @Override  
    public int getMaxInactiveInterval() {
        return maxInactiveInterval; 
    }
  
    @Override  
    public HttpSessionContext getSessionContext() {  
    	throw new UnsupportedOperationException("getSessionContext");
    }
  
    @Override
    public Object getAttribute(String name) {  
    	checkValid();
        return getAttributeInterval(name);  
    }
  
    protected abstract Object getAttributeInterval(String name);

	@Override  
    public Object getValue(String name) {  
        return getAttribute(name);
    }  
  
    @Override  
    public Enumeration<String> getAttributeNames() {
    	checkValid();
        return getAttributeNamesInterval();
    }  
  
    protected abstract Enumeration<String> getAttributeNamesInterval();

	@Override  
    public String[] getValueNames() {
		checkValid();
        return getValueNamesInterval();
    }  
  
	protected abstract String[] getValueNamesInterval();

	@Override  
    public void setAttribute(String name, Object value) {
		checkValid();
    	setAttributeInterval(name,value);
    }
  
	protected abstract void setAttributeInterval(String name, Object value);
  
    @Override  
    public void putValue(String name, Object value) {  
        setAttribute(name, value);
    }

	@Override  
    public void removeAttribute(String name) {
		checkValid();
        removeAttributeInterval(name);  
    }
  
	protected abstract void removeAttributeInterval(String name);
  
    @Override
    public void removeValue(String name) {  
    	removeAttribute(name);
    }

	@Override
    public void invalidate() {
		checkValid();
    	this.isInvalid = true;
    	session.invalidate();
        invalidateInterval();
    }
  
    protected abstract void invalidateInterval();

	@Override
    public boolean isNew() {  
    	checkValid();
        return this.isNew;
    }
    
    protected void setNew(boolean isNew) {
    	checkValid();
    	this.isNew = isNew;
    }
	
	public boolean isInvalid() {
		return this.isInvalid;
	}
	
	/**
	 * 如果当前会话已被invalidate，则抛出IllegalStateException异常
	 */
	protected void checkValid() {
		if(isInvalid) {
			throw new IllegalStateException("attempt to access session data after the session has been invalidated!");
		}
	}
	
	/**
	 * 获取Servlet原始的HttpSession
	 * @return 原始的HttpSession
	 */
	protected HttpSession getHttpSession() {
		return this.session;
	}
}
