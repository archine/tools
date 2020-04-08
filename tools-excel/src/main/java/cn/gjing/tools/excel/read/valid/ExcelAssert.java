package cn.gjing.tools.excel.read.valid;

import cn.gjing.tools.excel.exception.ExcelAssertException;

import java.lang.annotation.*;

/**
 * @author Gjing
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExcelAssert {
    /**
     * Check the rule (EL expression) that the expression result must be of type Boolean
     *
     * @return boolean expression
     */
    String expr();

    /**
     * A validation failure throws an {@link ExcelAssertException},
     * which is used to configure the information to be thrown
     *
     * @return message
     */
    String message() default "填写的内容有误";
}
