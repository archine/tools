package cn.gjing.tools.excel;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import java.lang.annotation.*;

/**
 * @author Gjing
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Sum {
    /**
     * The description of the sum will only take the value that appears for the first time in the Excel entity
     *
     * @return 合计
     */
    String value() default "合计:";

    /**
     * Numeric format
     *
     * @return format
     */
    String format() default "0";

    /**
     * Sum row height
     *
     * @return 300
     */
    short height() default 300;

    /**
     * Horizontal alignment
     *
     * @return CENTER
     */
    HorizontalAlignment align() default HorizontalAlignment.CENTER;

    /**
     * Vertical alignment
     * @return CENTER
     */
    VerticalAlignment verticalAlign() default VerticalAlignment.CENTER;

    /**
     * Font bold
     * @return false
     */
    boolean bold() default true;

    /**
     * Open the sum
     *
     * @return boolean
     */
    boolean open() default false;
}
