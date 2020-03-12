package cn.gjing.tools.excel.convert;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Gjing
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExcelDataConvert {
    /**
     * When the export of expression
     *
     * @return expr1
     */
    String expr1() default "";

    /**
     * When the import of expression
     * @return expr2
     */
    String expr2() default "";
}
