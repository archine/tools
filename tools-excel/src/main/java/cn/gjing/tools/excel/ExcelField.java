package cn.gjing.tools.excel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Excel文档字段
 * @author Gjing
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExcelField {
    /**
     * Excel列表头名
     * @return name
     */
    String value() default "";

    /**
     * 如果是时间需要转换指定格式，需要指定
     * @return pattern
     */
    String pattern() default "";

    /**
     * 这个列表头单元格的宽度
     * @return cell width
     */
    int width() default 20*256;

}
