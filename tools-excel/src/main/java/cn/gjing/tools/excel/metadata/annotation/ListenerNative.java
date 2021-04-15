package cn.gjing.tools.excel.metadata.annotation;

import cn.gjing.tools.excel.write.resolver.ExcelBindWriter;

import java.lang.annotation.*;

/**
 * Marks the listener as native,
 * and when a collection of listeners of the specified type is deleted by  {@link ExcelBindWriter#resetExcelClass(Class, boolean, boolean, String...)}
 * listeners marked with the annotation are skipped
 *
 * @author Gjing
 **/
@Target(ElementType.TYPE)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface ListenerNative {
    /**
     * The listener's mapped entity needs to be removed
     *
     * @return Entity class
     */
    Class<?>[] value() default {};
}
