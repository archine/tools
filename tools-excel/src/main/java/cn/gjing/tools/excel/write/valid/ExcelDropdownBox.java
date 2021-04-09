package cn.gjing.tools.excel.write.valid;

import cn.gjing.tools.excel.metadata.listener.DefaultCascadingDropdownBoxListener;

import java.lang.annotation.*;

/**
 * Excel dropdown box
 *
 * @author Gjing
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExcelDropdownBox {

    /**
     * The value in the dropdown box
     *
     * @return value
     */
    String[] combobox() default {};

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
     * If you specify a parent cell index,
     * then you need to add a cascading drop-down listener {@link DefaultCascadingDropdownBoxListener}
     *
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
