package cn.gjing.tools.redis.lock.core;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author Gjing
 **/
class ToolsRedisLockSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        return new String[]{
                RedisLockConfiguration.class.getName(),
        };
    }
}
