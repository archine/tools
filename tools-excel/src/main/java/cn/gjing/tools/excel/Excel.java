package cn.gjing.tools.excel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记这个类是与Excel关联的
 *
 * @author Gjing
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Excel {
    /**
     * Excel文件名，优先级低于方法传入
     *
     * @return name
     */
    String value() default "";

    /**
     * Excel文档类型
     *
     * @return Type
     */
    Type type() default Type.XLS;
}
