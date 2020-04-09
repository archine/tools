package cn.gjing.tools.excel.driven;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Gjing
 **/
@Documented
@Import({ExcelDrivenConfiguration.class})
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableExcelDriven {
}
