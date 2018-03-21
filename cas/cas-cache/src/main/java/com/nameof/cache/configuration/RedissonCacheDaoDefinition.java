package com.nameof.cache.configuration;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.nameof.cache.CacheDao;
import com.nameof.cache.impl.RedissonCacheDao;

@Configuration
@Profile("redisson")
public class RedissonCacheDaoDefinition {
	
	@Bean
	public RedissonClient redisson() {
		Config config = new Config();
		config.useSingleServer()
			.setAddress("redis://127.0.0.1:6379")
			.setConnectionPoolSize(64)
			.setIdleConnectionTimeout(10000)
			.setConnectTimeout(3000)
			.setConnectionMinimumIdleSize(10)
			.setTimeout(3000)
			.setPingTimeout(3000);
		return Redisson.create(config);
	}
	
	@Bean
	public CacheDao cacheDao(RedissonClient client) {
		return new RedissonCacheDao(client);
	}
}
