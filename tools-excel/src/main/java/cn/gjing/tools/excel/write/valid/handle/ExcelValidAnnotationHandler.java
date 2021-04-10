package cn.gjing.tools.excel.write.valid.handle;

import cn.gjing.tools.excel.write.ExcelWriterContext;
import org.apache.poi.ss.usermodel.Row;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * Excel validation annotation handler
 *
 * @author Gjing
 **/
public abstract class ExcelValidAnnotationHandler {
    /**
     * Annotation class
     */
    protected final Class<? extends Annotation> annotationClass;

    protected ExcelValidAnnotationHandler(Class<? extends Annotation> annotationClass) {
        this.annotationClass = annotationClass;
    }

    /**
     * Get the validation annotation type of the handler
     *
     * @return Annotation Class
     */
    public Class<? extends Annotation> getAnnotationClass() {
        return this.annotationClass;
    }

    /**
     * Validation handle
     *
     * @param validAnnotation Excel valid annotation
     * @param writerContext   Write context
     * @param field     Current field
     * @param row       Current row
     * @param colIndex  Current col index
     * @param boxValues Dropdown box value map
     */
    public abstract void handle(Annotation validAnnotation, ExcelWriterContext writerContext, Field field, Row row, int colIndex, Map<String, String[]> boxValues);
}
