package com.nameof.cache.configuration;

import org.redisson.Redisson;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import redis.clients.jedis.JedisPoolConfig;

import com.nameof.cache.CacheDao;
import com.nameof.cache.impl.RedisCacheDao;
import com.nameof.cache.impl.RedisTemplateCacheDao;
import com.nameof.cache.impl.RedissonCacheDao;

/**
 * 使用profile来选择缓存实现的另一种替代方式是使用FactoryBean
 * @author FactoryBean
 */
@Deprecated
public class CacheDaoFactoryBean implements FactoryBean<CacheDao> {

	@Value("${cache.dao}")
	private String cacehDaoName;
	
	@Override
	public CacheDao getObject() throws Exception {
		switch (cacehDaoName) {
			case "redisson":
				return new RedissonCacheDao(Redisson.create());
			case "redis":
				return new RedisCacheDao();
			case "redistemplate":
				JedisConnectionFactory jcf = new JedisConnectionFactory();
				JedisPoolConfig config = new JedisPoolConfig();
				jcf.setPoolConfig(config);
				jcf.afterPropertiesSet();
				RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
				redisTemplate.setConnectionFactory(jcf);
				redisTemplate.afterPropertiesSet();
				HashOperations<String, String, Object> opsForHash = redisTemplate.opsForHash();
				return new RedisTemplateCacheDao(redisTemplate, opsForHash);
			default:
				break;
		}
		return null;
	}

	@Override
	public Class<?> getObjectType() {
		return CacheDao.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
