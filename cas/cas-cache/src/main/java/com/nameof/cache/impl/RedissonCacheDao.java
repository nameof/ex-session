package com.nameof.cache.impl;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.codec.FstCodec;
import org.springframework.beans.factory.annotation.Autowired;

import com.nameof.cache.CacheDao;

public class RedissonCacheDao implements CacheDao {
	
	@Autowired
	private RedissonClient redis;
	
	private Codec codec;
	
	public RedissonCacheDao(RedissonClient redis) {
		this.redis = redis;
		//default use https://github.com/RuedigerMoeller/fast-serialization
		//文档表示 10倍于JDK序列化性能而且100%兼容的编码
		this.codec = new FstCodec();
	}
	
	public RedissonCacheDao(RedissonClient redis, Codec codec) {
		this.redis = redis;
		this.codec = codec;
	}
	
	@Override
	public Map<String, Object> getAllAttribute(String key) {
		return redis.getMap(key, codec);
	}
	
	@Override
	public void setAllAttributes(String key, Map<String, Object> attributes) {
		redis.getMap(key, codec).putAll(attributes);
	}
	
	@Override
	public Collection<String> getAttributeKeys(String key) {
		RMap<String, Object> map = redis.getMap(key, codec);
		return map.keySet();
	}

	@Override
	public Object getAttribute(String key, String fieldName) {
		return redis.getMap(key, codec).get(fieldName);
	}

	@Override
	public void setAttribute(String key, String fieldName, Object value) {
		redis.getMap(key, codec).put(fieldName, value);
	}
	
	@Override
	public void removeAttribute(String key, String fieldName) {
		redis.getMap(key, codec).remove(fieldName);
	}

	@Override
	public void del(String key) {
		redis.getMap(key, codec).delete();
	}

	@Override
	public void setExpire(String key, int expire) {
		redis.getMap(key, codec).expire(expire, TimeUnit.SECONDS);
	}

	@Override
	public Long getExpire(String key) {
		return redis.getMap(key, codec).remainTimeToLive();
	}

	@Override
	public void setPersist(String key) {
		redis.getMap(key, codec).clearExpire();
	}

	@Override
	public boolean exists(String key) {
		return redis.getMap(key, codec).isExists();
	}
}
