package com.nameof.common.constant;

import java.util.Arrays;
import java.util.List;

public interface SessionAccessor {

	String REDIS = "redis";
	
	String REDISSON = "redisson";
	
	String REDIS_TEMPLATE = "redis-template";
	
	String MEMCACHED = "memcached";
	
	String EHCACHE = "ehcache";
	
	String SPRING_SESSION = "spring-session";
	
	List<String> ALL = Arrays.asList(new String[] {REDIS, REDISSON, REDIS_TEMPLATE
			, MEMCACHED, EHCACHE, SPRING_SESSION});
}
