package cn.gjing.tools.excel.read.valid;

import cn.gjing.tools.excel.exception.ExcelAssertException;

import java.lang.annotation.*;

/**
 * Assert when Excel import, An assertion failure throws An {@link ExcelAssertException},
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
     * Error message
     *
     * @return message
     */
    String message() default "The cell value does not meet the requirements";
}
