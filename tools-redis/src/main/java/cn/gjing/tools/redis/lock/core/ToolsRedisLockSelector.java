package cn.gjing.tools.redis.lock.core;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;

/**
 * @author Gjing
 **/
class ToolsRedisLockSelector implements ImportSelector {
    @Override
    @NonNull
    public String[] selectImports(@NonNull AnnotationMetadata annotationMetadata) {
        return new String[]{
                RedisLockConfiguration.class.getName(),
        };
    }
}
