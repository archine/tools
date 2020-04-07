package cn.gjing.tools.excel.write.valid;

import java.lang.annotation.*;

/**
 * @author Gjing
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExcelAssert {
    /**
     * EL expression
     * @return boolean expression
     */
    String expr();

    /**
     * Error message
     * @return message
     */
    String message() default "填写的内容不符";
}
