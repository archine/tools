package cn.gjing.tools.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Gjing
 * 排除参数,与NotNull搭配使用,可以指定对应的参数不进行非空校验
 **/
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.SOURCE)
public @interface Exclude {
}
