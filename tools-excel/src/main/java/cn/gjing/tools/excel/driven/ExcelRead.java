package cn.gjing.tools.excel.driven;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Excel imports annotations that are used on methods to import Excel.
 * The return value of this method needs to specify a {@link ExcelReadWrapper} to wrap the data,
 *
 * @author Gjing
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExcelRead {
    /**
     * Sheet name
     *
     * @return Sheet1
     */
    String value() default "Sheet1";

    /**
     * Whether to check whether the Excel template matches when importing
     *
     * @return check
     */
    boolean check() default false;

    /**
     * Whether is need meta info(Such as header,title)
     *
     * @return metaInfo
     */
    boolean metaInfo() default false;

    /**
     * The name of the header to be ignored during import.
     * If it is the parent header, all children below it will be ignored
     *
     * @return ignores
     */
    String[] ignores() default {};

    /**
     * The actual subscript of the Excel header,
     * subscript is evaluated from 0
     *
     * @return headerIndex
     */
    int headerIndex() default 0;
}
