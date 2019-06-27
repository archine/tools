package cn.gjing.cache.core;

import cn.gjing.cache.CaffeineCache;
import cn.gjing.cache.RedisCache;
import cn.gjing.cache.Script;
import cn.gjing.cache.SecondCache;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author Gjing
 * 二级缓存配置类
 **/
@Configuration
class SecondCacheConfiguration {
    @Bean
    @SuppressWarnings("unchecked")
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public DefaultRedisScript<Boolean> setNxScript() {
        DefaultRedisScript<Boolean> defaultRedisScript = new DefaultRedisScript<>();
        defaultRedisScript.setScriptText(Script.SET_NX.getScript());
        defaultRedisScript.setResultType(Boolean.class);
        return defaultRedisScript;
    }

    @Bean
    public DefaultRedisScript<Boolean> setScript() {
        DefaultRedisScript<Boolean> defaultRedisScript = new DefaultRedisScript<>();
        defaultRedisScript.setScriptText(Script.SET.getScript());
        defaultRedisScript.setResultType(Boolean.class);
        return defaultRedisScript;
    }

    @Bean
    @ConditionalOnMissingBean(SecondCache.class)
    public SecondCache secondCache() {
        return new SecondCache();
    }

    @Bean
    @ConditionalOnMissingBean(RedisCache.class)
    public RedisCache redisCache() {
        return new RedisCache();
    }

    @Bean
    @ConditionalOnMissingBean(CaffeineCache.class)
    public CaffeineCache caffeineCache() {
        return new CaffeineCache();
    }

    @Bean
    public SecondCacheManager secondCacheManager(SecondCache secondCache,CaffeineCache caffeineCache,RedisCache redisCache) {
        return new SecondCacheManager(secondCache, setNxScript(), setScript(),redisCache,caffeineCache);
    }

    @Bean
    public RedisMessageListenerContainer listenerContainer(RedisConnectionFactory connectionFactory,SecondCacheManager secondCacheManager,RedisCache redisCache
            ,RedisTemplate<Object,Object> redisTemplate) {
        RedisMessageListenerContainer listenerContainer = new RedisMessageListenerContainer();
        listenerContainer.setConnectionFactory(connectionFactory);
        CacheMessageListener listener = new CacheMessageListener(secondCacheManager,redisTemplate);
        listenerContainer.addMessageListener(listener, new PatternTopic(redisCache.getTopic()));
        return listenerContainer;
    }

}
