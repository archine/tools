package cn.gjing.tools.excel.valid;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Excel clear scope data validation annotations
 *
 * @author Gjing
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExplicitValid {

    /**
     * Validator Class
     *
     * @return ExcelValidation
     */
    Class<? extends ExcelValidation> validClass() default DefaultExplicitValidation.class;

    /**
     * The value in the drop-down box
     *
     * @return value
     */
    String[] combobox() default {};

    /**
     * Check the number of rows
     *
     * @return rows
     */
    int boxLastRow() default 0;

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
    String errorContent() default "Please select the value in the drop-down box";

    /**
     * Whether it pops up immediately
     *
     * @return boolean
     */
    boolean showPromptBox() default true;

}
