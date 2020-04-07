package cn.gjing.tools.excel.write.valid;

import java.lang.annotation.*;

/**
 * Time check annotation
 *
 * @author Gjing
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExcelDateValid {

    /**
     * Check the number of rows
     *
     * @return 200
     */
    int rows() default 200;

    /**
     * Time format of valid
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
    String errorContent() default "请输入1970-01-01到2999-01-01范围内的时间";
}
