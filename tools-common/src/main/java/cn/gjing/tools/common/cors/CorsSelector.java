package cn.gjing.tools.common.cors;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;

/**
 * @author Gjing
 **/
class CorsSelector implements ImportSelector {
    @Override
    @NonNull
    public String[] selectImports(@NonNull AnnotationMetadata annotationMetadata) {
        return new String[]{
                CommonCorsConfiguration.class.getName(),
                CommonCors.class.getName()
        };
    }
}
