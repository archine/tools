package cn.gjing.tools.excel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Gjing
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Sum {
    /**
     * The description of the sum will only take the value that appears for the first time in the Excel entity
     *
     * @return 合计
     */
    String value() default "合计: ";

    /**
     * Numeric format
     *
     * @return format
     */
    String format() default "0";

    /**
     * Sum row height
     *
     * @return 300
     */
    short height() default 300;

    /**
     * Open the sum
     *
     * @return boolean
     */
    boolean open() default false;
}
