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
     * Numeric format
     * @return format
     */
    String format() default "#,#0";

    /**
     * Open the sum
     * @return boolean
     */
    boolean open() default false;
}
