package com.nameof.cache.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;

import com.nameof.common.domain.DataFormatEnum;
import com.nameof.common.domain.SessionAccessor;

@EnableRedisHttpSession
@Profile(SessionAccessor.SPRING_SESSION)
public class SpringSessionConfig extends AbstractHttpSessionApplicationInitializer {
	
	@Value("${redis.host}")
	private String redisHost;
	
	@Value("${redis.port}")
	private int redisPort;
	
	@Value("${session.format}")
	private DataFormatEnum format = DataFormatEnum.BINARY;
	
	@Bean
    public JedisConnectionFactory connectionFactory() {
        JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setHostName(redisHost);
        factory.setPort(redisPort);
        return factory;
    }
	
	@Bean(name = "springSessionDefaultRedisSerializer")
	public RedisSerializer<Object> serializer() {
        switch (format) {
			case JSON:
				return new GenericJackson2JsonRedisSerializer();
			default:
				return new JdkSerializationRedisSerializer();
		}
    }
}
