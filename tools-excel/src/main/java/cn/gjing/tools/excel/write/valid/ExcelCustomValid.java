package cn.gjing.tools.excel.write.valid;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom validation
 *
 * @author Gjing
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExcelCustomValid {
    /**
     * Custom validation formula
     *
     * @return formula
     */
    String formula();

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
    String errorContent() default "输入的内容不符合要求";

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
