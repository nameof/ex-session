package com.nameof.cache.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import redis.clients.jedis.JedisPoolConfig;

import com.nameof.cache.CacheDao;
import com.nameof.cache.impl.RedisTemplateCacheDao;
import com.nameof.common.domain.CacheDaoType;

@Configuration
@Profile(CacheDaoType.REDIS_TEMPLATE)
public class RedisTemplateCacheDaoDefinition {
	
	@Value("${redis.host}")
	private String redisHost;
	
	@Value("${redis.port}")
	private int redisPort;
	
	@Bean
	public JedisConnectionFactory jedisConnectionFactory() {
		JedisConnectionFactory jcf = new JedisConnectionFactory();
		JedisPoolConfig config = new JedisPoolConfig();
		jcf.setPoolConfig(config);
		jcf.setUsePool(true);
		jcf.setHostName(redisHost);
		jcf.setPort(redisPort);
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
