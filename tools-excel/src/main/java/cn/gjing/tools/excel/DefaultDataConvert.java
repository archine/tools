package cn.gjing.tools.excel;

import cn.gjing.tools.excel.listen.DataConvert;
import org.apache.poi.ss.usermodel.Cell;

import java.lang.reflect.Field;

/**
 * @author Gjing
 **/
public class DefaultDataConvert implements DataConvert<Object,Object> {
    @Override
    public Object toEntityAttribute(Object value, Field field, ExcelField excelField) {
        return null;
    }

    @Override
    public void toExcelAttribute(Cell cell, Object obj, Object value, Field field, ExcelField excelField) {

    }
}
