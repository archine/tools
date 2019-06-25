package cn.gjing.cache.core;

import cn.gjing.cache.SecondCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author Gjing
 * 二级缓存配置类
 **/
@Configuration
class SecondCacheConfiguration {
    @Resource
    private SecondCache secondCache;
    @Resource
    private DefaultRedisScript<Boolean> setScript;
    @Resource
    private DefaultRedisScript<Boolean> setNxScript;
    @Resource
    private RedisTemplate<Object, Object> redisTemplate;

    @Bean
    public SecondCacheManager secondCacheManager() {
        return new SecondCacheManager(secondCache, redisTemplate, setNxScript, setScript);
    }

    @Bean
    public RedisMessageListenerContainer listenerContainer(SecondCacheManager secondCacheManager) {
        RedisMessageListenerContainer listenerContainer = new RedisMessageListenerContainer();
        listenerContainer.setConnectionFactory(Objects.requireNonNull(redisTemplate.getConnectionFactory()));
        CacheMessageListener listener = new CacheMessageListener(redisTemplate, secondCacheManager);
        listenerContainer.addMessageListener(listener, new PatternTopic(secondCache.getRedisCache().getTopic()));
        return listenerContainer;
    }
}
