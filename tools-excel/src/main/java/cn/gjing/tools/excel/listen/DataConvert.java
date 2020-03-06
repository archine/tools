package cn.gjing.tools.excel.listen;

import cn.gjing.tools.excel.ExcelField;
import org.apache.poi.ss.usermodel.Cell;

import java.lang.reflect.Field;

/**
 * @author Gjing
 **/
public interface DataConvert<F,E> {
    /**
     * Convert to an entity field
     *
     * @param value      Excel cell value
     * @param excelField ExcelField
     * @param field      Current field
     * @return T
     */
    F toEntityAttribute(Object value, Field field, ExcelField excelField);

    /**
     * Convert to excel cell value
     *
     * @param cell       Excel cell
     * @param obj        Current excel entity
     * @param value      The value of the current field
     * @param field      Current field
     * @param excelField ExcelField
     */
    void toExcelAttribute(Cell cell, E obj, Object value, Field field, ExcelField excelField);
}
