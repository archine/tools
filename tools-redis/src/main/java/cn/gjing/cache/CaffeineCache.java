package cn.gjing.cache;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Gjing
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties("second.cache.caffeine")
@Component
public class CaffeineCache {
    /** 访问后过期时间，单位毫秒*/
    private long expireAfterAccess;

    /** 写入后过期时间，单位毫秒*/
    private long expireAfterWrite;

    /** 写入后刷新时间，单位毫秒*/
    private long refreshAfterWrite;

    /** 初始化大小*/
    private int initialCapacity;

    /** 最大缓存对象个数，超过此数量时之前放入的缓存将失效*/
    private long maximumSize;
}
