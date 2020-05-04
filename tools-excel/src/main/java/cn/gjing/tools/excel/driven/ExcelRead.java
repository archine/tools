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
     * Whether to check excel template when excel import
     */
    boolean check() default true;

    /**
     * Whether is need meta info(Such as header,title)
     */
    boolean metaInfo() default false;

    /**
     * The imported field is to be ignored
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
