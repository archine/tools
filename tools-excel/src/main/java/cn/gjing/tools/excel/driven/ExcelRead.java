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
     * @return check
     */
    boolean check() default false;

    /**
     * Whether is need meta info(Such as header,title)
     * @return metaInfo
     */
    boolean metaInfo() default false;

    /**
     * Table headers to be ignored when importing, the number of table headers typically
     * used in Excel files does not match the number of mapped entity fields,
     * or some table headers are ignored when exporting templates
     *
     * @return ignores
     */
    String[] ignores() default {};

    /**
     * Excel real header start index
     *
     * @return headerIndex
     */
    int headerIndex() default 0;
}
