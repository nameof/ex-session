package com.nameof.cache.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nameof.cache.CacheDao;
import com.nameof.common.utils.RedisUtil;

/**
 * 基于Redis的缓存数据访问层
 * @author ChengPan
 */
@Deprecated
public class RedisCacheDao implements CacheDao {

	private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
	
	private static final Logger logger = LoggerFactory.getLogger(RedisCacheDao.class);
	
	@Override
	public Map<String, Object> getAllAttribute(String key) {
		Map<String, Object> attributes = new HashMap<>();
		Map<byte[], byte[]> all = RedisUtil.getJedis().hgetAll(key.getBytes(DEFAULT_CHARSET));
		if (all != null) {
			Set<Entry<byte[],byte[]>> set = all.entrySet();
			for (Entry<byte[],byte[]> entry : set) {
				String byteKey = new String(entry.getKey(), DEFAULT_CHARSET);
				Object value = deserizlize(entry.getValue());
				attributes.put(byteKey, value);
			}
		}
		return attributes;
	}
	
	@Override
	public void setAllAttributes(String key, Map<String, Object> attributes) {
		//提交Session属性到缓存中
		Map<byte[], byte[]> serializedMap = new HashMap<>();
		for (Entry<String, Object> entry : attributes.entrySet()) {
			byte[] byteKey = entry.getKey().getBytes(DEFAULT_CHARSET);
			byte[] serializedValue = serialize(entry.getValue());
			serializedMap.put(byteKey, serializedValue);
		}
		RedisUtil.getJedis().hmset(key.getBytes(DEFAULT_CHARSET), serializedMap);	
	}

	@Override
	public Object getAttribute(String key, String fieldName) {
		byte[] value = null;
		value = RedisUtil.getJedis().hget(key.getBytes(DEFAULT_CHARSET), fieldName.getBytes(DEFAULT_CHARSET));
		return deserizlize(value);
	}


	@Override
	public void setAttribute(String key, String fieldName, Object value) {
		RedisUtil.getJedis().hset(key.getBytes(DEFAULT_CHARSET), fieldName.getBytes(DEFAULT_CHARSET),
				serialize(value));
	}

	@Override
	public Collection<String> getAttributeKeys(String key) {
		Set<byte[]> keys = null;
		keys = RedisUtil.getJedis().hkeys(key.getBytes(DEFAULT_CHARSET));
		
		if (keys == null) {
			return Collections.emptySet();
		}
		
		Set<String> skeys = new HashSet<String>();
		for (byte[] k : keys) {
			skeys.add(new String(k));
		}
		return skeys;
	}

	@Override
	public void del(String key) {
		RedisUtil.getJedis().del(key.getBytes(DEFAULT_CHARSET));
	}

	@Override
	public void setExpire(String key, int expire) {
		RedisUtil.getJedis().expire(key.getBytes(DEFAULT_CHARSET), expire);
	}

	@Override
	public Long getExpire(String key) {
		return RedisUtil.getJedis().ttl(key.getBytes(DEFAULT_CHARSET));
	}

	@Override
	public void setPersist(String key) {
		RedisUtil.getJedis().persist(key.getBytes(DEFAULT_CHARSET));
	}

	@Override
	public boolean exists(String key) {
		return RedisUtil.getJedis().exists(key.getBytes(DEFAULT_CHARSET));
	}

	private static byte [] serialize(Object obj) {
    	if (obj == null) {
    		return null;
    	}
        ObjectOutputStream obi=null;
        ByteArrayOutputStream bai=null;
        try {
            bai=new ByteArrayOutputStream();
            obi=new ObjectOutputStream(bai);
            obi.writeObject(obj);
            byte[] byt=bai.toByteArray();
            return byt;
        }
        catch (IOException e) {
        	logger.error("IOException thrown from RedisCacheDao on object serialize", e);
        }
        return null;
    }
    
    private static Object deserizlize(byte[] byt) {
    	if (byt == null) {
    		return null;
    	}
        ObjectInputStream oii=null;
        ByteArrayInputStream bis=null;
        bis=new ByteArrayInputStream(byt);
        try {
            oii=new ObjectInputStream(bis);
            Object obj=oii.readObject();
            return obj;
        }
        catch (IOException e) {
        	logger.error("IOException thrown from RedisCacheDao on object deserizlize", e);
        } catch (ClassNotFoundException e) {
        	logger.error("ClassNotFoundException thrown from RedisCacheDao on object deserizlize", e);
		}
        return null;
    }
}