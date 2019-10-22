package cn.gjing.tools.excel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Excel列表头与实体字段映射
 * @author Gjing
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExcelField {
    /**
     * Excel列表头名称
     * @return name
     */
    String value() default "";

    /**
     * 时间类型的字段，如果需要转化自定义格式，则需要写，如：yyyy-MM-dd
     * @return pattern
     */
    String pattern() default "";

    /**
     * 列表头单元格的宽度
     * @return cell width
     */
    int width() default 20*256;

}
