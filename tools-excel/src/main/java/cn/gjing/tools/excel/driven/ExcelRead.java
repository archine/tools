package cn.gjing.tools.excel.driven;

import java.lang.annotation.*;

/**
 * @author Gjing
 **/
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelRead {
    Class<?> value();

    String sheet() default "sheet1";

    int headIndex() default 0;

    boolean collect() default false;
}
