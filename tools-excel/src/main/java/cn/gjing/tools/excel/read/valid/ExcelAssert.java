package cn.gjing.tools.excel.read.valid;

import cn.gjing.tools.excel.exception.ExcelAssertException;

import java.lang.annotation.*;

/**
 * Assert when Excel import
 *
 * @author Gjing
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExcelAssert {
    /**
     * Predicate expressions.With an EL expression,
     * the expression must satisfy a Boolean value
     *
     * @return boolean expression
     */
    String expr();

    /**
     * A validation failure throws an {@link ExcelAssertException},
     * which is used to configure the error information to be thrown
     *
     * @return message
     */
    String message() default "填写的内容有误";
}
