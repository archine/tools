package cn.gjing.cache.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author Gjing
 **/
@Slf4j
public class CacheMessageListener implements MessageListener {
    private RedisTemplate<Object, Object> redisTemplate;
    private SecondCacheManager secondCacheManager;

    CacheMessageListener(SecondCacheManager secondCacheManager,RedisTemplate<Object,Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.secondCacheManager = secondCacheManager;
    }

    @Override
    public void onMessage(Message message, byte[] bytes) {
        cn.gjing.cache.Message message1 = (cn.gjing.cache.Message) this.redisTemplate.getValueSerializer().deserialize(message.getBody());
        log.info("收到消息：message:{}", message1);
        if (message1 == null) {
            return;
        }
        secondCacheManager.clearLocal(message1.getCacheName(), message1.getKey());
    }
}
