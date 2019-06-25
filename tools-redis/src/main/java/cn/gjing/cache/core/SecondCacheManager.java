package cn.gjing.cache.core;

import cn.gjing.cache.SecondCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author Gjing
 * 二级缓存管理
 **/
class SecondCacheManager implements CacheManager {

    private ConcurrentHashMap<String, Cache> cacheMap = new ConcurrentHashMap<>();
    private SecondCache secondCache;
    private RedisTemplate<Object, Object> redisTemplate;
    private boolean dynamic;
    private Set<String> cacheNames;
    private DefaultRedisScript<Boolean> setNxScript;
    private DefaultRedisScript<Boolean> setScript;

    SecondCacheManager(SecondCache secondCache, RedisTemplate<Object, Object> redisTemplate, DefaultRedisScript<Boolean> setNxScript, DefaultRedisScript<Boolean> setScript) {
        super();
        this.secondCache = secondCache;
        this.redisTemplate = redisTemplate;
        this.dynamic = secondCache.isDynamic();
        this.cacheNames = secondCache.getCacheNames();
        this.setNxScript = setNxScript;
        this.setScript = setScript;
    }

    @Override
    public Cache getCache(String s) {
        Cache cache = cacheMap.get(s);
        if (cache != null) {
            return cache;
        }
        if (!dynamic && cacheNames.contains(s)) {
            return null;
        }
        cache = new SecondCacheAdapter(s, redisTemplate, caffeineCache(), secondCache, setScript, setNxScript);
        Cache newCache = cacheMap.putIfAbsent(s, cache);
        return newCache == null ? cache : newCache;
    }

    @Override
    public Collection<String> getCacheNames() {
        return this.cacheNames;
    }

    /**
     * 清除本地缓存
     * @param cacheName 缓存名
     * @param key key
     */
    void clearLocal(String cacheName, Object key) {
        Cache cache = cacheMap.get(cacheName);
        if (cache == null) {
            return;
        }
        SecondCacheAdapter secondCacheAdapter = (SecondCacheAdapter) cache;
        secondCacheAdapter.clearLocal(key);
    }

    /**
     * 构建caffeine缓存
     *
     * @return caffeine cache
     */
    private com.github.benmanes.caffeine.cache.Cache<Object, Object> caffeineCache() {
        com.github.benmanes.caffeine.cache.Caffeine<Object, Object> cacheBuilder = Caffeine.newBuilder();
        if (secondCache.getCaffeineCache().getExpireAfterAccess() > 0) {
            cacheBuilder.expireAfterAccess(secondCache.getCaffeineCache().getExpireAfterAccess(), TimeUnit.MILLISECONDS);
        }
        if (secondCache.getCaffeineCache().getExpireAfterWrite() > 0) {
            cacheBuilder.expireAfterWrite(secondCache.getCaffeineCache().getExpireAfterWrite(), TimeUnit.MILLISECONDS);
        }
        if (secondCache.getCaffeineCache().getInitialCapacity() > 0) {
            cacheBuilder.initialCapacity(secondCache.getCaffeineCache().getInitialCapacity());
        }
        if (secondCache.getCaffeineCache().getMaximumSize() > 0) {
            cacheBuilder.maximumSize(secondCache.getCaffeineCache().getMaximumSize());
        }
        if (secondCache.getCaffeineCache().getRefreshAfterWrite() > 0) {
            cacheBuilder.refreshAfterWrite(secondCache.getCaffeineCache().getRefreshAfterWrite(), TimeUnit.MILLISECONDS);
        }
        return cacheBuilder.build();
    }
}
