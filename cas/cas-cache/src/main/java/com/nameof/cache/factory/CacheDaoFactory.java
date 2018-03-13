package com.nameof.cache.factory;

import com.nameof.cache.CacheDao;
import com.nameof.common.config.ConfigLoader;

/**
 * 根据{@link cas.support.ConfigLoader}获取CacheDao实现类配置，实例化CacheDao
 * 
 * @author ChengPan
 */
public class CacheDaoFactory {
	private CacheDaoFactory() {}

	private static final Class<?> clazz;
	
	private static final String CACHE_DAO_IMPL_CLASS_KEY = "cache.dao.impl.class";

	static {
		try {
			clazz = Class
					.forName(ConfigLoader.getConfig(CACHE_DAO_IMPL_CLASS_KEY));
		}
		catch (ClassNotFoundException e) {
			throw new RuntimeException("can not found CacheDao class", e);
		}
	}

	public static CacheDao newCacheDaoInstance() {
		try {
			return (CacheDao) clazz.newInstance();
		}
		catch (InstantiationException e) {
			throw new RuntimeException(e);
		}
		catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
