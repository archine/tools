package com.gjing.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Archine
 * 排除参数,与NonNull搭配使用,可以指定对应的参数不进行非空校验
 **/
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.SOURCE)
public @interface ExcludeParam {
}
