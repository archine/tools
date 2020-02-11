package cn.gjing.tools.redis.cache;

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
@ConfigurationProperties("tools.cache.caffeine")
@Component
public class CaffeineCache {
    /**
     * 访问后过期时间，单位毫秒
     */
    @Builder.Default
    private Integer expireAfterAccess = -1;

    /**
     * 写入后过期时间，单位毫秒
     */
    @Builder.Default
    private Integer expireAfterWrite = -1;

    /**
     * 写入后刷新时间，单位毫秒
     */
    @Builder.Default
    private Integer refreshAfterWrite = -1;

    /**
     * 初始化大小
     */
    @Builder.Default
    private Integer initialCapacity = -1;

    /**
     * 最大缓存对象个数，超过此数量时之前放入的缓存将失效
     */
    @Builder.Default
    private Integer maximumSize = -1;
}
