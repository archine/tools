package cn.gjing.cache.core;

import cn.gjing.cache.SecondCache;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author Gjing
 **/
class ToolsSecondCacheSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        return new String[]{
                SecondCache.class.getName(),
                SecondCacheScriptConfiguration.class.getName(),
                SecondCacheConfiguration.class.getName()
        };
    }
}
