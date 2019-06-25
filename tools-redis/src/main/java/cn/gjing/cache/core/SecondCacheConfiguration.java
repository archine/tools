package cn.gjing.cache.core;

import cn.gjing.cache.CaffeineCache;
import cn.gjing.cache.RedisCache;
import cn.gjing.cache.Script;
import cn.gjing.cache.SecondCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import javax.annotation.Resource;

/**
 * @author Gjing
 * 二级缓存配置类
 **/
@Configuration
class SecondCacheConfiguration {

    @Resource
    private RedisTemplate<Object, Object> redisTemplate;

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
    public SecondCache secondCache() {
        return new SecondCache();
    }

    @Bean
    public CaffeineCache caffeineCache() {
        return new CaffeineCache();
    }

    @Bean
    public RedisCache redisCache() {
        return new RedisCache();
    }


    @Bean
    public SecondCacheManager secondCacheManager() {
        return new SecondCacheManager(secondCache(), redisTemplate, setNxScript(), setScript(),redisCache(),caffeineCache());
    }

    @Bean
    public RedisMessageListenerContainer listenerContainer(RedisConnectionFactory connectionFactory,SecondCacheManager secondCacheManager) {
        RedisMessageListenerContainer listenerContainer = new RedisMessageListenerContainer();
        listenerContainer.setConnectionFactory(connectionFactory);
        CacheMessageListener listener = new CacheMessageListener(redisTemplate, secondCacheManager);
        listenerContainer.addMessageListener(listener, new PatternTopic(redisCache().getTopic()));
        return listenerContainer;
    }


}
