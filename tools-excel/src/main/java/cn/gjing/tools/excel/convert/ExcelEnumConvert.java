package cn.gjing.tools.excel.convert;

import java.lang.annotation.*;

/**
 * Enumeration transformation annotation
 *
 * @author Gjing
 **/
@Documented
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
