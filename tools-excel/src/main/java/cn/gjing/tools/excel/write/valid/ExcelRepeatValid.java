package cn.gjing.tools.excel.write.valid;

import cn.gjing.tools.excel.ExcelField;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Column duplicate data check
 *
 * @author Gjing
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExcelRepeatValid {
    /**
     * How many rows to add
     *
     * @return rows
     */
    int rows() default 100;

    /**
     * Whether the error box pops up
     *
     * @return boolean
     */
    boolean showErrorBox() default true;

    /**
     * If the number of long text exceeds 15 digits,
     * the Excel file will automatically convert the number after 15 digits to 0,
     * indirectly causing duplicate content check error.
     * Once set to true, the cell of the current column needs to be formatted as text {@link ExcelField#format()}
     *
     * @return boolean
     */
    boolean longTextNumber() default false;

    /**
     * Error box level
     *
     * @return level
     */
    Rank rank() default Rank.STOP;

    /**
     * Error box title
     *
     * @return title
     */
    String errorTitle() default "";

    /**
     * Error content
     *
     * @return content
     */
    String errorContent() default "输入的内容出现重复";

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
    String tipContent() default "";
}
