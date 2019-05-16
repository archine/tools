package cn.gjing.doc;

import cn.gjing.swagger.Resources;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author Gjing
 **/
class SwaggerDocSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        return new String[]{Resources.class.getName(), SwaggerDocConfig.class.getName()};
    }
}
