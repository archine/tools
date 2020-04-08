package cn.gjing.tools.excel;

import cn.gjing.tools.excel.convert.DataConvert;
import cn.gjing.tools.excel.convert.DefaultDataConvert;
import cn.gjing.tools.excel.write.merge.Merge;

import java.lang.annotation.*;

/**
 * This annotation is used to declare that an attribute is an Excel header,
 * and if it is not annotated through this annotation,
 * it will not be exported or imported as a header
 *
 * @author Gjing
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExcelField {
    /**
     * Array of Excel header names.
     * If multiple headers appear in the array and you want to merge the same header when exporting,
     * you need to activate the multi-header mode before the export is executed.
     * The last one in the array belongs to the real header,
     * and if you are importing a multilevel header Excel file,
     * you need to specify the real header start subscript
     *
     * @return header names
     */
    String[] value() default {};

    /**
     * Excel header width
     *
     * @return cell width
     */
    int width() default 5120;

    /**
     * List header sort, if not set will default entity property order
     * If part of the header is set, it only appears behind the unset header
     *
     * @return sort index
     */
    int sort() default -1;

    /**
     * Cell format when exported
     *
     * @return format
     */
    String format() default "@";

    /**
     * Whether to enable automatic vertical merge when export
     *
     * @return autoMerge
     */
    Merge autoMerge() default @Merge;

    /**
     * Whether null values are supported when imported (including cells that do not exist or empty strings)
     *
     * @return boolean
     */
    boolean allowEmpty() default true;

    /**
     * Data converter, which you can use to change data during import and export,
     * occurs before cell assignment and before Java object assignment
     *
     * @return DefaultDataConvert.class
     */
    Class<? extends DataConvert<?>> convert() default DefaultDataConvert.class;
}
