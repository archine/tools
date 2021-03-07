package cn.gjing.tools.excel.exception;

import cn.gjing.tools.excel.ExcelField;
import cn.gjing.tools.excel.read.valid.ExcelAssert;
import lombok.Getter;

import java.lang.reflect.Field;

/**
 * Excel assert exception, thrown by {@link ExcelAssert}
 *
 * @author Gjing
 **/
@Getter
public class ExcelAssertException extends RuntimeException {
    /**
     * ExcelFiled annotation on current filed
     */
    private final ExcelField excelField;
    /**
     * Current field
     */
    private final Field field;
    /**
     * Current row index
     */
    private final int rowIndex;
    /**
     * Current column index
     */
    private final int colIndex;

    public ExcelAssertException(String message, ExcelField excelField, Field field, int rowIndex, int colIndex) {
        super(message);
        this.excelField = excelField;
        this.field = field;
        this.rowIndex = rowIndex;
        this.colIndex = colIndex;
    }
}
