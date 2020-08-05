package cn.gjing.tools.excel.exception;

/**
 * Excel template exception
 *
 * @author Gjing
 **/
public class ExcelTemplateException extends ExcelResolverException {
    public ExcelTemplateException() {
        super("Excel template mismatch");
    }

    public ExcelTemplateException(String message) {
        super(message);
    }
}
