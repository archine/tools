package cn.gjing.tools.swagger.core;

import cn.gjing.tools.swagger.Contact;
import cn.gjing.tools.swagger.SwaggerResources;
import cn.gjing.tools.swagger.SwaggerBean;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author Gjing
 **/
class SwaggerSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        return new String[]{SwaggerBean.class.getName(), SwaggerResources.class.getName(), Contact.class.getName(),
                SwaggerConfig.class.getName(), ResourcesRegister.class.getName()};
    }
}

