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
    Class<? extends ExcelNumericValidation> validClass() default DefaultNumericValidation.class;

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
    OperatorType operatorType() default OperatorType.LESS_OR_EQUAL;

    /**
     * Valid type
     *
     * @return ValidType
     */
    ValidType validationType() default ValidType.TEXT_LENGTH;

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
    Rank rank() default Rank.STOP;

    /**
     * Error box title
     *
     * @return title
     */
    String errorTitle() default "错误提示";

    /**
     * Error content
     *
     * @return content
     */
    String errorContent() default "请输入正确的文本长度";
}
