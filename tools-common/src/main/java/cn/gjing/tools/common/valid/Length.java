package cn.gjing.tools.common.valid;

import java.lang.annotation.*;

/**
 * @author Gjing
 * Parameter length check
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.PARAMETER})
public @interface Length {
    /**
     * Min length
     * @return int
     */
    int min() default 0;
    /**
     * Max length
     * @return int
     */
    int max() default 999999999;
    /**
     * Exception information
     * @return String
     */
    String message() default "无效的文本长度";
}
