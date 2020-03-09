package cn.gjing.tools.excel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Gjing
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExcelAssert {
    /**
     * EL expression
     * @return expr
     */
    String expr();

    /**
     * Error message
     * @return message
     */
    String message() default "填写的内容不符";
}
