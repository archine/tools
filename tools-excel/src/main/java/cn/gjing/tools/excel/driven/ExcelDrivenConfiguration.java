package cn.gjing.tools.excel.driven;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;

/**
 * @author Gjing
 **/
class ExcelDrivenConfiguration implements ImportSelector {
    @Override
    @NonNull
    public String[] selectImports(@NonNull AnnotationMetadata annotationMetadata) {
        return new String[]{
                ExcelDrivenHandler.class.getName(),
                ExcelDrivenHelper.class.getName()
        };
    }
}
