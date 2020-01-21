package cn.gjing.tools.swagger.core;

import cn.gjing.tools.swagger.Contact;
import cn.gjing.tools.swagger.SwaggerBean;
import cn.gjing.tools.swagger.SwaggerResources;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;

/**
 * @author Gjing
 **/
public class SwaggerSelector implements ImportSelector {
    @Override
    @NonNull
    public String[] selectImports(@NonNull AnnotationMetadata annotationMetadata) {
        return new String[]{
                SwaggerBean.class.getName(),
                Contact.class.getName(),
                SwaggerConfig.class.getName(),
                SwaggerResources.class.getName(),
                ResourcesRegister.class.getName()
        };
    }
}
