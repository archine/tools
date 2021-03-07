package cn.gjing.tools.excel.write.callback;

import java.lang.reflect.Field;

/**
 * Default auto merge callback of export excel
 * Whenever adjacent cells in the same column have the same content, they are merged
 *
 * @author Gjing
 **/
public final class DefaultExcelAutoMergeCallback implements ExcelAutoMergeCallback<Object> {
    @Override
    public boolean mergeY(Object o, Field field, String headerName, int colIndex, int index) {
        return true;
    }
}
