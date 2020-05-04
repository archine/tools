package cn.gjing.tools.cache.core;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author Gjing
 **/
class CacheMessageListener implements MessageListener {
    private final RedisTemplate<Object, Object> redisTemplate;
    private final ToolsCacheManager toolsCacheManager;

    CacheMessageListener(ToolsCacheManager toolsCacheManager, RedisTemplate<Object,Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.toolsCacheManager = toolsCacheManager;
    }

    @Override
    public void onMessage(Message message, byte[] bytes) {
        cn.gjing.tools.cache.Message message1 = (cn.gjing.tools.cache.Message) this.redisTemplate.getValueSerializer().deserialize(message.getBody());
        if (message1 == null) {
            return;
        }
        toolsCacheManager.clearLocal(message1.getCacheName(), message1.getKey());
    }
}
