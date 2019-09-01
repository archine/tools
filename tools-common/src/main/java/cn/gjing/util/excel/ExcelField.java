package cn.gjing.util.excel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Excel字段名
 * @author Gjing
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExcelField {
    /**
     * excel列表名
     */
    String name();

    /**
     * 如果是时间,转换的规则
     */
    String pattern() default "";

    /**
     * 单元格宽
     */
    int width() default 20*256;

    /**
     * 生成id的策略, 支持UUID和分布式唯一ID
     */
    Generate strategy() default Generate.NONE;
}
