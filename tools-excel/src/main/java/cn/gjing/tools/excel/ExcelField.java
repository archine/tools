package cn.gjing.tools.excel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Excel list headers map to entity fields annotation
 *
 * @author Gjing
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExcelField {
    /**
     * Excel header name
     *
     * @return name
     */
    String value() default "";

    /**
     * The field is of time type. If the custom time format needs to be converted, it needs to be written, such as yyyy-mm-dd
     *
     * @return pattern
     */
    String pattern() default "";

    /**
     * Excel header width
     *
     * @return cell width
     */
    int width() default 20 * 256;

}
