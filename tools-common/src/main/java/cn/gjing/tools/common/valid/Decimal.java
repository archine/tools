package cn.gjing.tools.common.valid;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Gjing
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface Decimal {
    /**
     * Integer number
     *
     * @return number
     */
    int scale() default 1;

    /**
     * Decimal number
     *
     * @return number
     */
    int prec() default 2;

    /**
     * Exception information
     *
     * @return String
     */
    String message() default "请输入正确的小数范围";
}
