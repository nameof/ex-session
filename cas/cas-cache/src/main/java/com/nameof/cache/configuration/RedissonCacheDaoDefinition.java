package com.nameof.cache.configuration;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.nameof.cache.CacheDao;
import com.nameof.cache.impl.RedissonCacheDao;
import com.nameof.common.domain.CacheDaoType;

@Configuration
@Profile(CacheDaoType.REDISSON)
public class RedissonCacheDaoDefinition {
	
	@Value("${redis.host}")
	private String redisHost;
	
	@Value("${redis.port}")
	private int redisPort;
	
	@Bean
	public RedissonClient redisson() {
		Config config = new Config();
		config.useSingleServer().setAddress("redis://" + redisHost + ":" + redisPort);
		return Redisson.create(config);
	}
	
	@Bean
	public CacheDao cacheDao(RedissonClient client) {
		return new RedissonCacheDao(client);
	}
}
