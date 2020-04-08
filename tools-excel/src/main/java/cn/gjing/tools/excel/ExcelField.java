package cn.gjing.tools.excel;

import cn.gjing.tools.excel.convert.DataConvert;
import cn.gjing.tools.excel.convert.DefaultDataConvert;
import cn.gjing.tools.excel.write.merge.Merge;

import java.lang.annotation.*;

/**
 * Excel list headerers map to entity fields annotation
 *
 * @author Gjing
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExcelField {
    /**
     * Excel header names, If you have more than one, you have more than one headerer,
     * and if you want to merge headers, you need to open them on export
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
     * List headerer sort, if not set will default entity property order
     * If part of the headerer is set, it only appears behind the unset headerer
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
     * Data convert
     *
     * @return DefaultDataConvert.class
     */
    Class<? extends DataConvert<?>> convert() default DefaultDataConvert.class;
}
