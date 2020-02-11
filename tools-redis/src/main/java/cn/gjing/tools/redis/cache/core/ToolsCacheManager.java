package cn.gjing.tools.redis.cache.core;

import cn.gjing.tools.redis.cache.CaffeineCache;
import cn.gjing.tools.redis.cache.RedisCache;
import cn.gjing.tools.redis.cache.ToolsCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.lang.NonNull;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author Gjing
 * 二级缓存管理
 **/
class ToolsCacheManager implements CacheManager {

    @Resource
    private RedisTemplate<Object, Object> redisTemplate;

    private ConcurrentHashMap<String, Cache> cacheMap = new ConcurrentHashMap<>();
    private ToolsCache toolsCache;
    private boolean dynamic;
    private Set<String> cacheNames;
    private DefaultRedisScript<Boolean> setNxScript;
    private DefaultRedisScript<Boolean> setScript;
    private RedisCache redisCache;
    private CaffeineCache caffeineCache;

    ToolsCacheManager(ToolsCache toolsCache, DefaultRedisScript<Boolean> setNxScript, DefaultRedisScript<Boolean> setScript,
                      RedisCache redisCache, CaffeineCache caffeineCache) {
        super();
        this.toolsCache = toolsCache;
        this.dynamic = toolsCache.isDynamic();
        this.cacheNames = toolsCache.getCacheNames();
        this.setNxScript = setNxScript;
        this.setScript = setScript;
        this.redisCache = redisCache;
        this.caffeineCache = caffeineCache;
    }

    @Override
    public Cache getCache(@NonNull String s) {
        Cache cache = cacheMap.get(s);
        if (cache != null) {
            return cache;
        }
        if (!dynamic && cacheNames.contains(s)) {
            return null;
        }
        cache = new ToolsCacheAdapter(s, redisTemplate, caffeineCache(), toolsCache, setScript, setNxScript,redisCache);
        Cache newCache = cacheMap.putIfAbsent(s, cache);
        return newCache == null ? cache : newCache;
    }

    @Override
    @NonNull
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
        ToolsCacheAdapter toolsCacheAdapter = (ToolsCacheAdapter) cache;
        toolsCacheAdapter.clearLocal(key);
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
