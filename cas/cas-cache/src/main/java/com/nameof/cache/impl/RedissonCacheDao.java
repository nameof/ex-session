package com.nameof.cache.impl;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;

import com.nameof.cache.CacheDao;

public class RedissonCacheDao implements CacheDao {
	
	@Autowired
	private RedissonClient redis;
	
	public RedissonCacheDao(RedissonClient redis) {
		this.redis = redis;
	}
	
	@Override
	public Map<String, Object> getAllAttribute(String key) {
		return redis.getMap(key);
	}
	
	@Override
	public void setAllAttributes(String key, Map<String, Object> attributes) {
		redis.getMap(key).putAll(attributes);
	}
	
	@Override
	public Collection<String> getAttributeKeys(String key) {
		RMap<String, Object> map = redis.getMap(key);
		return map.keySet();
	}

	@Override
	public Object getAttribute(String key, String fieldName) {
		return redis.getMap(key).get(fieldName);
	}

	@Override
	public void setAttribute(String key, String fieldName, Object value) {
		redis.getMap(key).put(fieldName, value);
	}

	@Override
	public void del(String key) {
		redis.getMap(key).delete();
	}

	@Override
	public void setExpire(String key, int expire) {
		redis.getMap(key).expire(expire, TimeUnit.SECONDS);
	}

	@Override
	public Long getExpire(String key) {
		return redis.getMap(key).remainTimeToLive();
	}

	@Override
	public void setPersist(String key) {
		redis.getMap(key).clearExpire();
	}

	@Override
	public boolean exists(String key) {
		return redis.getMap(key).isExists();
	}
}
