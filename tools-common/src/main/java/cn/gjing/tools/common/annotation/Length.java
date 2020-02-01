package cn.gjing.tools.common.annotation;

import java.lang.annotation.*;

/**
 * @author Gjing
 * Parameter length check，Use with @NotEmpty
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.PARAMETER})
public @interface Length {
    /**
     * Max length
     * @return int
     */
    int value();

    /**
     * Exception information
     * @return String
     */
    String message() default "Invalid length";
}
