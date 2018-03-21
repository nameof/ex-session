package com.nameof.cache.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.nameof.cache.CacheDao;
import com.nameof.cache.impl.RedisCacheDao;

@Configuration
@Profile("redis")
public class RedisCacheDaoDefinition {
	
	@Bean
	public CacheDao cacheDao() {
		return new RedisCacheDao();
	}
}
