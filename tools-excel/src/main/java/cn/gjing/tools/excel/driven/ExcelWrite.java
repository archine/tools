package cn.gjing.tools.excel.driven;

import java.lang.annotation.*;

/**
 * @author Gjing
 **/
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelWrite {
    Class<?> value();

    String sheet() default "sheet1";

    boolean multiHead() default false;

    boolean valid() default false;

    boolean initDefaultStyle() default true;

    boolean needHead() default true;
}
