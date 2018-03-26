package com.nameof.cache.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import com.nameof.cache.CacheDao;

public class EhCacheDao implements CacheDao {
	
	private Cache cache;

	public EhCacheDao(Cache cache) {
		this.cache = cache;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getAllAttribute(String key) {
		Element element = cache.get(key);
		if (element == null) {
			return Collections.emptyMap();
		}
		Map<String, Object> map = (Map<String, Object>) element.getObjectValue();
		return map;
	}

	@Override
	public void setAllAttributes(String key, Map<String, Object> attributes) {
		Element element = cache.get(key);
		if (element == null) {
			Map<String, Object> map = new ConcurrentHashMap<>(attributes);
			cache.put(new Element(key, map));
		} else {
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>) element.getObjectValue();
			map.putAll(attributes);
		}
	}

	@Override
	public Object getAttribute(String key, String fieldName) {
		Element element = cache.get(key);
		if (element == null) {
			return null;
		}
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) element.getObjectValue();
		return map.get(fieldName);
	}

	@Override
	public void setAttribute(String key, String fieldName, Object value) {
		Element element = cache.get(key);
		if (element == null) {
			Map<String, Object> map = new ConcurrentHashMap<>();
			map.put(fieldName, value);
			cache.put(new Element(key, map));
		} else {
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>) element.getObjectValue();
			map.put(fieldName, value);
		}
	}

	@Override
	public void removeAttribute(String key, String fieldName) {
		Element element = cache.get(key);
		if (element != null) {
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>) element.getObjectValue();
			map.remove(fieldName);
		}
	}

	@Override
	public Collection<String> getAttributeKeys(String key) {
		Element element = cache.get(key);
		if (element == null) {
			return Collections.emptySet();
		}
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) element.getObjectValue();
		return map.keySet();
	}

	@Override
	public void del(String key) {
		cache.remove(key);
	}

	@Override
	public void setExpire(String key, int expire) {
		Element element = cache.get(key);
		if (element != null) {
			element.setTimeToIdle(expire);
		}
	}

	@Override
	public Long getExpire(String key) {
		Element element = cache.get(key);
		if (element != null) {
			return (long) element.getTimeToIdle();
		}
		return -2L;
	}

	@Override
	public void setPersist(String key) {
		Element element = cache.get(key);
		if (element != null) {
			element.setEternal(true);
		}
	}

	@Override
	public boolean exists(String key) {
		return cache.get(key) != null;
	}
}
