package cn.gjing.tools.excel.exception;

/**
 * @author Gjing
 **/
public class ExcelTemplateException extends ExcelResolverException {
    public ExcelTemplateException() {
        super("Excel template mismatch");
    }
}
