package cn.gjing.tools.excel.read.listener;

import cn.gjing.tools.excel.ExcelField;
import cn.gjing.tools.excel.metadata.listener.ExcelReadListener;

import java.lang.reflect.Field;

/**
 * Empty value listener
 *
 * @author Gjing
 **/
@FunctionalInterface
public interface ExcelEmptyReadListener<R> extends ExcelReadListener {

    /**
     * When a body cell is read, if the cell does not exist or the value is empty,
     * and header is set as required in the mapping entity {@link ExcelField#required()}
     *
     * @param r          Current Java object
     * @param field      Current field
     * @param excelField ExcelField annotation on that field
     * @param rowIndex   The index of the current row
     * @param colIndex   The index of the current col
     * @return Whether to continue reading this line, or start the next line if false
     */
    boolean readEmpty(R r, Field field, ExcelField excelField, int rowIndex, int colIndex);
}
