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
     * How many rows to add
     *
     * @return rows
     */
    int rows() default 100;

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
    ValidType validType() default ValidType.TEXT_LENGTH;

    /**
     * expr1
     *
     * @return expr1
     */
    String expr1() default "1";

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

    /**
     * Whether show cell tip
     *
     * @return false
     */
    boolean showTip() default false;

    /**
     * Tip title
     *
     * @return ""
     */
    String tipTitle() default "";

    /**
     * Tip content
     *
     * @return ""
     */
    String tipContent() default "请输入正确的文本长度";
}
