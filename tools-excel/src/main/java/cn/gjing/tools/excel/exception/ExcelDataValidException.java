package cn.gjing.tools.excel.exception;

import cn.gjing.tools.excel.ExcelField;
import lombok.Getter;

import java.lang.reflect.Field;

/**
 * @author Gjing
 **/
@Getter
public class ExcelDataValidException extends RuntimeException {
    private ExcelField excelField;
    private Field field;
    private int rowIndex;
    private int colIndex;

    public ExcelDataValidException(String message, ExcelField excelField, Field field, int rowIndex, int colIndex) {
        super(message);
        this.excelField = excelField;
        this.field = field;
        this.rowIndex = rowIndex;
        this.colIndex = colIndex;
    }
}
