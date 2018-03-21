package com.nameof.common.utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public final class RedisUtil {
    
    private static String ADDR = "127.0.0.1";
    
    private static int PORT = 6379;
    
    private static JedisPool jedisPool = null;
    
    private static ThreadLocal<Jedis> threadLocal = new ThreadLocal<Jedis>();
    
    static {
        try {
        	ADDR = ConfigHolder.getConfig("redis.host");
        	PORT = Integer.valueOf(ConfigHolder.getConfig("redis.port"));
            jedisPool = new JedisPool(ADDR, PORT);
        }
        catch (Exception e) {
            throw new IllegalStateException("failed to init jedis pool",e); 
        }
    }
    
    public static Jedis getJedis() {
    	Jedis resource = null;
        try {
        	resource = threadLocal.get();
        	if(resource != null){
        		return resource;
        	}
        	resource = jedisPool.getResource();
        	threadLocal.set(resource);
        	return resource;
        }
        catch (Exception e) {
        	throw new IllegalStateException("failed to get JedisResource from pool",e); 
        }
    }

    public static void returnResource() {
    	Jedis jedis = threadLocal.get();
        if (jedis != null) {
        	threadLocal.remove();
        	jedis.close();
        }
    }
}