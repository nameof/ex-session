package com.nameof.cache.configuration;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.FactoryConfiguration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.nameof.cache.CacheDao;
import com.nameof.cache.impl.EhCacheDao;
import com.nameof.common.constant.SessionAccessor;

@Configuration
@Profile(SessionAccessor.EHCACHE)
public class EhCacheDaoConfig {
	
	private String cacheName = "sessionCache";
	
	@Value("${ehcache.monitor.address}")
	private String monitorAddress;
	
	@Value("${ehcache.monitor.port}")
	private int monitorPort;

	@Bean
	public CacheManager cacheManager() {
		net.sf.ehcache.config.Configuration cfg = new net.sf.ehcache.config.Configuration();
		
		CacheConfiguration ccfig = new CacheConfiguration();
		ccfig.setEternal(false);
		ccfig.setMaxEntriesLocalHeap(Integer.MAX_VALUE);
		ccfig.setName(cacheName);
		
		cfg.addCache(ccfig);
		
		//添加ehcache监控
		FactoryConfiguration<?> fc = new FactoryConfiguration<>();
		fc.setClass("org.terracotta.ehcachedx.monitor.probe.ProbePeerListenerFactory");
		fc.setProperties(String.format("monitorAddress=%s,monitorPort=%d", monitorAddress, monitorPort));
		fc.setPropertySeparator(",");

		cfg.addCacheManagerPeerListenerFactory(fc);
		
		return CacheManager.create(cfg);
	}
	
	@Bean
	public CacheDao cacheDao(CacheManager cacheManager) {
		return new EhCacheDao(cacheManager.getCache(cacheName));
	}
}
