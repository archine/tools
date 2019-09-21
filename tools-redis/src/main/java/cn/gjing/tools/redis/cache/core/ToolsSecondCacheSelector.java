package cn.gjing.tools.redis.cache.core;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author Gjing
 **/
class ToolsSecondCacheSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        return new String[]{
                SecondCacheConfiguration.class.getName()
        };
    }
}
