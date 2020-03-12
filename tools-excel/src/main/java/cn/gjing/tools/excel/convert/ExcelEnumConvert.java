package cn.gjing.tools.excel.convert;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enumeration transformation annotation
 *
 * @author Gjing
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExcelEnumConvert {
    /**
     * Enumeration converter Class
     *
     * @return EnumConvert
     */
    Class<? extends EnumConvert<? extends Enum<?>,?>> convert();
}
