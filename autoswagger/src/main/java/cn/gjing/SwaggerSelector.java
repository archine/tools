package cn.gjing;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author Gjing
 **/
public class SwaggerSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        return new String[]{SwaggerBean.class.getName(), SwaggerConfig.class.getName(), GlobalExceptionHandler.class.getName()};
    }
}
