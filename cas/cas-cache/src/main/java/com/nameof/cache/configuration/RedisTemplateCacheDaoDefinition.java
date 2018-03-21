package com.nameof.cache.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import redis.clients.jedis.JedisPoolConfig;

import com.nameof.cache.CacheDao;
import com.nameof.cache.impl.RedisTemplateCacheDao;

@Configuration
@Profile("redistemplate")
public class RedisTemplateCacheDaoDefinition {
	
	@Bean
	public JedisConnectionFactory jedisConnectionFactory() {
		JedisConnectionFactory jcf = new JedisConnectionFactory();
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxIdle(10);
		config.setMaxTotal(64);
		jcf.setPoolConfig(config);
		jcf.setUsePool(true);
		jcf.setHostName("127.0.0.1");
		jcf.setPort(6379);
		jcf.setTimeout(3000);
		jcf.afterPropertiesSet();
		return jcf;
	}
	
	@Bean
	public RedisTemplate<String, Object> redisTemplate(JedisConnectionFactory jcf) {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(jcf);
		redisTemplate.afterPropertiesSet();
		return redisTemplate;
	}
	
	@Bean
	public CacheDao cacheDao(RedisTemplate<String, Object> redisTemplate) {
		HashOperations<String, String, Object> opsForHash = redisTemplate.opsForHash();
		return new RedisTemplateCacheDao(redisTemplate, opsForHash);
	}
}
