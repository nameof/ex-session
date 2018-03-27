package com.nameof.common.domain;

public interface CacheDaoType {

	String REDIS = "redis";
	String REDISSON = "redisson";
	String REDIS_TEMPLATE = "redis-template";
	String MEMCACHED = "memcached";
	String EHCACHE = "ehcache";
}
