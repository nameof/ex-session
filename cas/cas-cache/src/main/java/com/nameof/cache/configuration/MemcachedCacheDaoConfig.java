package com.nameof.cache.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.nameof.cache.CacheDao;
import com.nameof.cache.impl.MemcachedCacheDao;
import com.nameof.common.domain.SessionAccessor;
import com.whalin.MemCached.MemCachedClient;
import com.whalin.MemCached.SockIOPool;

@Configuration
@Profile(SessionAccessor.MEMCACHED)
public class MemcachedCacheDaoConfig {
	
	@Value("${memcached.host}")
	private String memcachedHost;

	@Value("${memcached.port}")
	private int memcachedPort;
	
	@Bean
	public MemCachedClient cacheClient() {
		SockIOPool sockIOPool = SockIOPool.getInstance();  
        sockIOPool.setServers(new String[]{memcachedHost + ":" + memcachedPort});
        sockIOPool.initialize();
        return new MemCachedClient();
	}
	
	@Bean
	public CacheDao cacheDao(MemCachedClient cacheClient) {
		return new MemcachedCacheDao(cacheClient);
	}
}
