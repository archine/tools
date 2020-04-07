package cn.gjing.tools.excel.write.valid;

import java.lang.annotation.*;

/**
 * Excel clear scope data validation annotations
 *
 * @author Gjing
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExcelDropdownBox {

    /**
     * The value in the drop-down box
     *
     * @return value
     */
    String[] combobox() default {};

    /**
     * Check the number of rows
     *
     * @return 200
     */
    int rows() default 200;

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
     * Number of parent cell index
     * @return link
     */
    String link() default "";

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
    String errorContent() default "请选择下拉框内的值";
}
