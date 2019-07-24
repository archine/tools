package cn.gjing.cors;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author Gjing
 **/
class CorsSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        return new String[]{
                CorsAdapter.class.getName(),
                Cors.class.getName()
        };
    }
}
