package com.nameof.cache.impl;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import com.nameof.cache.CacheDao;

public class RedisTemplateCacheDao implements CacheDao {
	
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	@Resource(name = "redisTemplate")
	private HashOperations<String, String, Object> hashOpt;
	
	public RedisTemplateCacheDao(RedisTemplate<String, Object> redisTemplate,
			HashOperations<String, String, Object> hashOpt) {
		this.redisTemplate = redisTemplate;
		this.hashOpt = hashOpt;
	}

	@Override
	public Map<String, Object> getAllAttribute(String key) {
		return hashOpt.entries(key);
	}

	@Override
	public void setAllAttributes(String key, Map<String, Object> attributes) {
		hashOpt.putAll(key, attributes);
	}

	@Override
	public Object getAttribute(String key, String fieldName) {
		return hashOpt.get(key, fieldName);
	}

	@Override
	public void setAttribute(String key, String fieldName, Object value) {
		hashOpt.put(key, fieldName, value);
	}

	@Override
	public Collection<String> getAttributeKeys(String key) {
		return hashOpt.keys(key);
	}

	@Override
	public void del(String key) {
		redisTemplate.delete(key);
	}

	@Override
	public void setExpire(String key, int expire) {
		redisTemplate.expire(key, expire, TimeUnit.SECONDS);
	}

	@Override
	public Long getExpire(String key) {
		return redisTemplate.getExpire(key);
	}

	@Override
	public void setPersist(String key) {
		redisTemplate.persist(key);
	}

	@Override
	public boolean exists(String key) {
		return redisTemplate.hasKey(key);
	}

}
