package cn.gjing.tools.excel.driven;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enable excel annotation-driven mode
 * After use, it can be easily imported and exported through {@link ExcelWrite} and {@link ExcelRead}
 *
 * @author Gjing
 * @deprecated For flexibility, it was decided to start deprecating in version 2021.8 and will be completely removed in version 2021.9
 **/
@Deprecated
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({ExcelDrivenConfiguration.class})
public @interface EnableExcelDrivenMode {
}
