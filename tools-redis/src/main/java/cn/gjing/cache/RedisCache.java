package cn.gjing.cache;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Gjing
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Component
@ConfigurationProperties("second.cache.redis")
public class RedisCache {
    /**
     * 全局过期时间，单位毫秒，默认不过期
     */
    @Builder.Default
    private Integer expire = -1;
    /**
     * 每个cacheName的过期时间，单位毫秒，优先级比defaultExpiration高
     */
    @Builder.Default
    private Map<String, Integer> everyCacheExpire = new HashMap<>();
    /**
     * 缓存更新时通知其他节点的topic名称
     */
    @Builder.Default
    private String topic = "second-cache";
}
