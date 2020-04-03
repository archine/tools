package cn.gjing.tools.excel.convert;

import java.lang.annotation.*;

/**
 * @author Gjing
 **/
@Documented
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
