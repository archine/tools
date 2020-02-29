package cn.gjing.tools.excel;

import org.apache.poi.ss.usermodel.Cell;

import java.lang.reflect.Field;

/**
 * @author Gjing
 **/
public class DefaultDataConvert implements DataConvert<Object> {
    @Override
    public Object toEntityAttribute(Object value, Field field, ExcelField excelField) {
        return null;
    }

    @Override
    public void toExcelAttribute(Cell cell, Object value, Field field, ExcelField excelField) {

    }
}
