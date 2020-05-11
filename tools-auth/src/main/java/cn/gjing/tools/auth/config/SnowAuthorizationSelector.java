package cn.gjing.tools.auth.config;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;

/**
 * Permission configuration selector
 *
 * @author Gjing
 **/
class SnowAuthorizationSelector implements ImportSelector {
    @NonNull
    @Override
    public String[] selectImports(@NonNull AnnotationMetadata annotationMetadata) {
        return new String[]{SnowAuthorizationConfiguration.class.getName()};
    }
}
