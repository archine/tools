package cn.gjing.core;

import cn.gjing.Contact;
import cn.gjing.Resources;
import cn.gjing.SwaggerBean;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author Gjing
 **/
class SwaggerSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        return new String[]{SwaggerBean.class.getName(), Resources.class.getName(), Contact.class.getName(),
                SwaggerConfig.class.getName(), ResourcesConfig.class.getName()};
    }
}

