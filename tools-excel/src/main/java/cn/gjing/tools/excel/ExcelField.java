package cn.gjing.tools.excel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Excel list headers map to entity fields annotation
 *
 * @author Gjing
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExcelField {
    /**
     * Excel header name
     *
     * @return name
     */
    String value();

    /**
     * The field is of time type. If the custom time format needs to be converted, it needs to be written, such as yyyy-mm-dd
     *
     * @return pattern
     */
    String pattern() default "";

    /**
     * Excel header width
     *
     * @return cell width
     */
    int width() default 5120;

    /**
     * The list headers are sorted by default by entity field if the Numbers are the same
     * @return sort index
     */
    int sort() default 99;

    /**
     * If a cell has the same number of rows, merge automatically
     * @return autoMerge
     */
    boolean autoMerge() default false;

    /**
     * Cell sum
     * @return Sum
     */
    Sum sum() default @Sum;

    /**
     * Excel style
     *
     * @return ExcelStyle
     */
    Class<? extends ExcelStyle> style() default DefaultExcelStyle.class;

    /**
     * Is allow empty
     * @return boolean
     */
    boolean allowEmpty() default true;

    /**
     * Equal to the policy executed when empty
     * @return EmptyStrategy
     */
    EmptyStrategy strategy() default EmptyStrategy.JUMP;

    /**
     * This message is read if the policy is error
     * @return String
     */
    String message() default "参数不能为空";
}
