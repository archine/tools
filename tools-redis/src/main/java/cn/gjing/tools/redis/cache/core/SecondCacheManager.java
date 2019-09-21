package cn.gjing.tools.redis.cache.core;

import cn.gjing.tools.redis.cache.CaffeineCache;
import cn.gjing.tools.redis.cache.RedisCache;
import cn.gjing.tools.redis.cache.SecondCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author Gjing
 * 二级缓存管理
 **/
class SecondCacheManager implements CacheManager {

    @Resource
    private RedisTemplate<Object, Object> redisTemplate;

    private ConcurrentHashMap<String, Cache> cacheMap = new ConcurrentHashMap<>();
    private SecondCache secondCache;
    private boolean dynamic;
    private Set<String> cacheNames;
    private DefaultRedisScript<Boolean> setNxScript;
    private DefaultRedisScript<Boolean> setScript;
    private RedisCache redisCache;
    private CaffeineCache caffeineCache;

    SecondCacheManager(SecondCache secondCache,DefaultRedisScript<Boolean> setNxScript, DefaultRedisScript<Boolean> setScript,
                       RedisCache redisCache,CaffeineCache caffeineCache) {
        super();
        this.secondCache = secondCache;
        this.dynamic = secondCache.isDynamic();
        this.cacheNames = secondCache.getCacheNames();
        this.setNxScript = setNxScript;
        this.setScript = setScript;
        this.redisCache = redisCache;
        this.caffeineCache = caffeineCache;
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
        cache = new SecondCacheAdapter(s, redisTemplate, caffeineCache(), secondCache, setScript, setNxScript,redisCache);
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
        if (caffeineCache.getExpireAfterAccess() > 0) {
            cacheBuilder.expireAfterAccess(caffeineCache.getExpireAfterAccess(), TimeUnit.MILLISECONDS);
        }
        if (caffeineCache.getExpireAfterWrite() > 0) {
            cacheBuilder.expireAfterWrite(caffeineCache.getExpireAfterWrite(), TimeUnit.MILLISECONDS);
        }
        if (caffeineCache.getInitialCapacity() > 0) {
            cacheBuilder.initialCapacity(caffeineCache.getInitialCapacity());
        }
        if (caffeineCache.getMaximumSize() > 0) {
            cacheBuilder.maximumSize(caffeineCache.getMaximumSize());
        }
        if (caffeineCache.getRefreshAfterWrite() > 0) {
            cacheBuilder.refreshAfterWrite(caffeineCache.getRefreshAfterWrite(), TimeUnit.MILLISECONDS);
        }
        return cacheBuilder.build();
    }
}
