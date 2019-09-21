package cn.gjing.tools.redis.cache.core;

import cn.gjing.tools.redis.cache.Message;
import cn.gjing.tools.redis.cache.RedisCache;
import cn.gjing.tools.redis.cache.SecondCache;
import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * @author Gjing
 **/
class SecondCacheAdapter extends AbstractValueAdaptingCache {
    private String name;
    private RedisTemplate<Object, Object> redisTemplate;
    private Cache<Object, Object> caffeineCache;
    private String cachePrefix;
    private Integer expire;
    private Map<String, Integer> everyCacheExpire;
    private String topic;
    private DefaultRedisScript<Boolean> setNxScript;
    private DefaultRedisScript<Boolean> setScript;

    SecondCacheAdapter(String name, RedisTemplate<Object, Object> redisTemplate, Cache<Object, Object> caffeineCache, SecondCache secondCache,
                       DefaultRedisScript<Boolean> setScript, DefaultRedisScript<Boolean> setNxScript, RedisCache redisCache) {
        super(secondCache.isCacheValueNullable());
        this.name = name;
        this.redisTemplate = redisTemplate;
        this.caffeineCache = caffeineCache;
        this.cachePrefix = secondCache.getCachePrefix();
        this.expire = redisCache.getExpire();
        this.everyCacheExpire = redisCache.getEveryCacheExpire();
        this.topic = redisCache.getTopic();
        this.setScript = setScript;
        this.setNxScript = setNxScript;
    }

    /**
     * @param key key
     * @return o
     */
    @Override
    protected Object lookup(Object key) {
        key = getKey(key);
        Object value = caffeineCache.getIfPresent(key);
        if (value != null) {
            return value;
        }
        value = redisTemplate.opsForValue().get(key);
        if (value != null) {
            caffeineCache.put(key, value);
        }
        return value;
    }

    @Override
    public String getName() {
        return this.name;
    }

    /**
     * 获取本地缓存
     *
     * @return this
     */
    @Override
    public Object getNativeCache() {
        return this;
    }

    /**
     * 获取缓存
     *
     * @param key      keu
     * @param callable callable
     * @param <T>      t
     * @return t
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Object key, Callable<T> callable) {
        key = getKey(key);
        Object value = this.lookup(key);
        if (value != null) {
            return (T) value;
        }
        try {
            value = callable.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Object storeValue = toStoreValue(value);
        this.put(key, storeValue);
        return (T) value;
    }

    /**
     * 插入值
     *
     * @param key key
     * @param val val
     */
    @Override
    public void put(Object key, Object val) {
        key = getKey(key);
        if (!super.isAllowNullValues() && val == null) {
            this.evict(key);
            return;
        }
        List<Object> keys = Collections.singletonList(key);
        this.redisTemplate.execute(this.setScript, keys, val, this.getExpire());
        this.caffeineCache.put(key, val);
    }

    /**
     * 判断是否存在，存在则插入
     *
     * @param key key
     * @param val value
     * @return ValueWrapper
     */
    @Override
    public ValueWrapper putIfAbsent(Object key, Object val) {
        key = getKey(key);
        List<Object> keys = Collections.singletonList(key);
        Boolean execute = this.redisTemplate.execute(this.setNxScript, keys, val, this.getExpire());
        assert execute != null;
        if (execute) {
            caffeineCache.put(key, toStoreValue(val));
        }
        return toValueWrapper(val);
    }

    /**
     * 清除指定key缓存
     *
     * @param key key
     */
    @Override
    public void evict(Object key) {
        key = getKey(key);
        this.redisTemplate.delete(key);
        this.publish(Message.builder().cacheName(this.name).key(key).build());
        caffeineCache.invalidate(key);
    }

    /**
     * 清楚所有缓存
     */
    @Override
    public void clear() {
        Set<Object> keys = redisTemplate.keys(this.name);
        if (keys == null) {
            return;
        }
        keys.forEach(e -> redisTemplate.delete(e.toString()));
        this.publish(new Message(this.name, null));
        caffeineCache.invalidateAll();
    }

    /**
     * 缓存变更时通知其他节点清理本地缓存
     *
     * @param message 消息对象
     */
    private void publish(Message message) {
        this.redisTemplate.convertAndSend(topic, message);
    }

    /**
     * 获取指定缓存的过期时间
     *
     * @return int
     */
    private int getExpire() {
        Integer expire = this.expire;
        Integer cacheNameExpire = everyCacheExpire.get(this.name);
        return cacheNameExpire == null ? expire : cacheNameExpire.intValue();
    }

    private String getKey(Object key) {
        return key == null ? this.cachePrefix + "" : this.cachePrefix + key.toString();
    }

    /**
     * 清除本地缓存
     *
     * @param key key
     */
    void clearLocal(Object key) {
        if (key == null) {
            this.caffeineCache.invalidateAll();
            return;
        }
        caffeineCache.invalidate(this.getKey(key));
    }
}
