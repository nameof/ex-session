package com.nameof.cache.configuration;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.nameof.cache.CacheDao;
import com.nameof.cache.impl.EhcacheDao;
import com.nameof.common.domain.CacheDaoType;

@Configuration
@Profile(CacheDaoType.EHCACHE)
public class EhCacheDaoDefinition {
	
	private String cacheName = "sessionCache";

	@Bean
	public CacheManager cacheManager() {
		net.sf.ehcache.config.Configuration cfg = new net.sf.ehcache.config.Configuration();
		CacheConfiguration ccfig = new CacheConfiguration();
		ccfig.setEternal(false);
		ccfig.setMaxEntriesLocalHeap(Integer.MAX_VALUE);
		ccfig.setName(cacheName);
		cfg.addCache(ccfig);
		return CacheManager.create(cfg);
	}
	
	@Bean
	public CacheDao cacheDao(CacheManager cacheManager) {
		return new EhcacheDao(cacheManager.getCache(cacheName));
	}
}
