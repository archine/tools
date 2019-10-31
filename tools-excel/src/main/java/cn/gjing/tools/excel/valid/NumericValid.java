package cn.gjing.tools.excel.valid;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Data type validation annotation
 *
 * @author Gjing
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NumericValid {

    /**
     * Validator Class
     *
     * @return ExcelValidation
     */
    Class<? extends ExcelValidation> validClass() default DefaultNumericValidation.class;

    /**
     * Check the number of rows
     *
     * @return rows
     */
    int boxLastRow() default 0;

    /**
     * Operator type
     *
     * @return OperatorType
     */
    OperatorType operatorType() default OperatorType.GREATER_OR_EQUAL;

    /**
     * Valid type
     *
     * @return ValidType
     */
    ValidType validationType() default ValidType.INTEGER;

    /**
     * expr1
     *
     * @return expr1
     */
    String expr1() default "0";

    /**
     * expr2
     *
     * @return expr2
     */
    String expr2() default "";

    /**
     * Whether the error box pops up
     *
     * @return boolean
     */
    boolean showErrorBox() default true;

    /**
     * Prompt box level
     *
     * @return level
     */
    Rank rank() default Rank.WARNING;

    /**
     * Error box title
     *
     * @return title
     */
    String errorTitle() default "Error Message";

    /**
     * Error content
     *
     * @return content
     */
    String errorContent() default "Please enter Numbers greater than or equal to 0";

    /**
     * Whether it pops up immediately
     *
     * @return boolean
     */
    boolean showPromptBox() default true;
}
