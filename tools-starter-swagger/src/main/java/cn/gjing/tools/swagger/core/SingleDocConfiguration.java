package cn.gjing.tools.swagger.core;

import cn.gjing.tools.swagger.Doc;
import cn.gjing.tools.swagger.DocContact;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;

/**
 * @author Gjing
 **/
class SingleDocConfiguration implements ImportSelector {
    @NonNull
    @Override
    public String[] selectImports(@NonNull AnnotationMetadata annotationMetadata) {
        return new String[]{
                Doc.class.getName(),
                DocContact.class.getName(),
                DefaultSingleDocHandler.class.getName(),
        };
    }
}
