package cn.gjing.tools.excel.write.valid;

import java.lang.annotation.*;

/**
 * Data type validation annotation
 *
 * @author Gjing
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExcelNumericValid {

    /**
     * Check the number of rows
     *
     * @return 200
     */
    int rows() default 200;

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
