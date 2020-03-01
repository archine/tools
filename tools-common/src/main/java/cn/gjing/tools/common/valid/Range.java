package cn.gjing.tools.common.valid;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Gjing
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.PARAMETER})
public @interface Range {
    /**
     * Small integer
     * @return min
     */
    int min() default 0;

    /**
     * Biggest integer
     * @return max
     */
    long max();

    /**
     * Exception information
     * @return message
     */
    String message() default "请输入正确范围的整数";
}
