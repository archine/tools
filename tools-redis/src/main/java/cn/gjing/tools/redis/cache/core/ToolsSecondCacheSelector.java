package cn.gjing.tools.redis.cache.core;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;

/**
 * @author Gjing
 **/
class ToolsSecondCacheSelector implements ImportSelector {
    @Override
    @NonNull
    public String[] selectImports(@NonNull AnnotationMetadata annotationMetadata) {
        return new String[]{
                ToolsCacheConfiguration.class.getName()
        };
    }
}
