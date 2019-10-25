package cn.gjing.tools.excel.valid;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Time check annotation
 *
 * @author Gjing
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DateValid {

    /**
     * Validator Class
     *
     * @return ExcelValidation
     */
    Class<? extends ExcelValidation> validClass() default DefaultDateValidation.class;

    /**
     * Check the number of rows
     *
     * @return rows
     */
    int boxLastRow() default 0;

    /**
     * Time format
     *
     * @return expr
     */
    String pattern() default "yyyy-MM-dd";

    /**
     * Operator type
     *
     * @return OperatorType
     */
    OperatorType operatorType() default OperatorType.BETWEEN;

    /**
     * expr1
     *
     * @return expr1
     */
    String expr1() default "1970-01-01";

    /**
     * expr2
     *
     * @return expr2
     */
    String expr2() default "2999-01-01";

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
    String errorTitle() default "Error message";

    /**
     * Error content
     *
     * @return content
     */
    String errorContent() default "Please fill in the correct time range: 1970-01-01 to 2999-01-01";

    /**
     * Whether it pops up immediately
     *
     * @return boolean
     */
    boolean showPromptBox() default true;

    /**
     * Whether null values are allowed
     *
     * @return boolean
     */
    boolean allowEmpty() default true;
}
