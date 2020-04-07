package cn.gjing.tools.excel.util;

import cn.gjing.tools.excel.ExcelField;
import cn.gjing.tools.excel.write.listener.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author Gjing
 **/
public class ListenerUtils {

    public static void completeCell(List<WriteListener> listeners, Sheet sheet, Row row, Cell cell, ExcelField excelField, Field field, String headName, int rowIndex, int colIndex, boolean isHead, Object value) {
        if (listeners != null) {
            listeners.forEach(e -> ((BaseCellWriteListener) e).completeCell(sheet, row, cell, excelField, field, headName, rowIndex, colIndex, isHead, value));
        }
    }

    public static void completeRow(List<WriteListener> listeners, Sheet sheet, Row row, int rowIndex, boolean isHead) {
        if (listeners != null) {
            listeners.forEach(e -> ((BaseRowWriteListener) e).completeRow(sheet, row, rowIndex, isHead));
        }
    }

    public static void completeSheet(List<WriteListener> listeners, Sheet sheet) {
        if (listeners != null) {
            listeners.forEach(e -> ((BaseSheetWriteListener) e).completeSheet(sheet));
        }
    }
}
