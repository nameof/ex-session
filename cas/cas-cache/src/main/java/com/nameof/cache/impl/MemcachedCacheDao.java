package com.nameof.cache.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.nameof.cache.CacheDao;
import com.whalin.MemCached.MemCachedClient;

/**
 * FIXME 直接读写整个Map属性，存在明显的并发问题
 * @author ChengPan
 */
public class MemcachedCacheDao implements CacheDao {

	private MemCachedClient cachedClient;
	
    public MemcachedCacheDao(MemCachedClient cachedClient) {
    	this.cachedClient = cachedClient;
    }
	
	@Override
	public Map<String, Object> getAllAttribute(String key) {
		return getMap(key);
	}

	@Override
	public void setAllAttributes(String key, Map<String, Object> attributes) {
		Map<String, Object> map = getMap(key);
		map.putAll(attributes);
		cachedClient.set(key, map);
	}

	@Override
	public Object getAttribute(String key, String fieldName) {
		Map<String, Object> map = getMap(key);
		return map.get(fieldName);
	}

	@Override
	public void setAttribute(String key, String fieldName, Object value) {
		Map<String, Object> map = getMap(key);
		map.put(fieldName, value);
		cachedClient.set(key, map);
	}

	@Override
	public Collection<String> getAttributeKeys(String key) {
		Map<String, Object> map = getMap(key);
		return map.keySet();
	}

	@Override
	public void del(String key) {
		cachedClient.delete(key);
	}

	@Override
	public void setExpire(String key, int expire) {
		//FIXME UnsupportedOperation
	}

	@Override
	public Long getExpire(String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setPersist(String key) {
		//FIXME UnsupportedOperation
	}

	@Override
	public boolean exists(String key) {
		return cachedClient.keyExists(key);
	}
	
	private Map<String, Object> getMap(String key) {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) cachedClient.get(key);
		if (map == null) {
			map = new HashMap<String, Object>();
		}
		return map;
	}

}
