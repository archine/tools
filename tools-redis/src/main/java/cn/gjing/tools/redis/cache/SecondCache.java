package cn.gjing.tools.redis.cache;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Gjing
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties("second.cache")
@Component
public class SecondCache {
    /**
     * 缓存key名
     */
    @Builder.Default
    private Set<String> cacheNames = new HashSet<>();

    /**
     * 是否存储控制，默认true，防止缓存穿透
     */
    @Builder.Default
    private boolean cacheValueNullable = true;

    /**
     * 是否动态根据cacheName创建Cache实现
     */
    @Builder.Default
    private boolean dynamic = true;

    /**
     * 缓存key的前缀
     */
    @Builder.Default
    private String cachePrefix = "";

}
