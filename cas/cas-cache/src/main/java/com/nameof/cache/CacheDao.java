package com.nameof.cache;

import java.util.Collection;
import java.util.Map;
/**
 * 提供将servlet session属性存储到缓存中的数据访问对象接口
 * @author ChengPan
 */
public interface CacheDao {

	Map<String, Object> getAllAttribute(String key);
	
	void setAllAttributes(String key, Map<String, Object> attributes);
	
	Object getAttribute(String key, String fieldName);
	
	void setAttribute(String key, String fieldName, Object value);
	
	void removeAttribute(String key, String fieldName);
	
	Collection<String> getAttributeKeys(String key);
	
	void del(String key);
	
	void setExpire(String key, int expire);
	
	Long getExpire(String key);
	
	/**
	 * 设置key永不过期
	 * @param key
	 */
	void setPersist(String key);
	
	boolean exists(String key);
}
