package com.nameof.common.utils;

import java.io.IOException;
import java.util.Properties;

public class ConfigHolder {
	
	private ConfigHolder() {}

	private static final Properties properties = new Properties();
	
	static {
		try {
			properties.load(ConfigHolder.class.getResourceAsStream("/cas-config.properties"));
		} catch (IOException e) {
			throw new RuntimeException("load config error", e);
		}
	}
	
	public static String getConfig(String key) {
		return properties.getProperty(key);
	}
}
