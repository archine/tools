package cn.gjing.tools.excel.util;

import cn.gjing.tools.excel.ExcelField;
import cn.gjing.tools.excel.read.listener.EmptyReadListener;
import cn.gjing.tools.excel.read.listener.ReadListener;
import cn.gjing.tools.excel.read.listener.ResultReadListener;
import cn.gjing.tools.excel.read.listener.RowReadListener;
import cn.gjing.tools.excel.write.listener.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * @author Gjing
 **/
public class ListenerUtils {

    public static void completeCell(Map<Class<? extends WriteListener>, List<WriteListener>> excelListeners, Sheet sheet, Row row, Cell cell,
                                    ExcelField excelField, Field field, String headName, int index, int colIndex, boolean isHead, Object value) {
        List<WriteListener> cellListeners = excelListeners.get(CellWriteListener.class);
        if (cellListeners != null) {
            cellListeners.forEach(e -> ((CellWriteListener) e).completeCell(sheet, row, cell, excelField, field, headName, index, colIndex, isHead, value));
        }
    }

    public static void completeRow(Map<Class<? extends WriteListener>, List<WriteListener>> excelListeners, Sheet sheet, Row row, int index, boolean isHead) {
        List<WriteListener> rowListeners = excelListeners.get(RowWriteListener.class);
        if (rowListeners != null) {
            rowListeners.forEach(e -> ((RowWriteListener) e).completeRow(sheet, row, index, isHead));
        }
    }

    public static void completeSheet(Map<Class<? extends WriteListener>, List<WriteListener>> excelListeners, Sheet sheet) {
        List<WriteListener> sheetListeners = excelListeners.get(SheetWriteListener.class);
        if (sheetListeners != null) {
            sheetListeners.forEach(e -> ((SheetWriteListener) e).completeSheet(sheet));
        }
    }

    public static void workbookFlushBefore(Map<Class<? extends WriteListener>, List<WriteListener>> excelListeners, Workbook workbook, String fileName) {
        List<WriteListener> workbookListeners = excelListeners.get(WorkbookWriteListener.class);
        if (workbookListeners != null) {
            workbookListeners.forEach(e -> ((WorkbookWriteListener) e).flushBefore(workbook, fileName));
        }
    }

    @SuppressWarnings("unchecked")
    public static <R> boolean readRow(List<ReadListener> rowReadListeners, R r, List<String> headNames, int rowIndex, boolean isHead, boolean hasNext) {
        boolean stop = false;
        if (rowReadListeners != null) {
            for (ReadListener rowReadListener : rowReadListeners) {
                stop = ((RowReadListener<R>) rowReadListener).readRow(r, headNames, rowIndex, isHead, hasNext);
            }
        }
        return stop;
    }

    @SuppressWarnings("unchecked")
    public static <R> void readCell(List<ReadListener> rowReadListeners, R r, Object cellValue, Field field, int rowIndex, int colIndex, boolean isHead) {
        if (rowReadListeners != null) {
            rowReadListeners.forEach(e -> ((RowReadListener<R>) e).readCell(r, cellValue, field, rowIndex, colIndex, isHead));
        }
    }

    @SuppressWarnings("unchecked")
    public static <R> boolean readEmpty(List<ReadListener> emptyReadListeners, R r, Field field, ExcelField excelField, int rowIndex, int colIndex, boolean hasNext) {
        boolean isSave = false;
        if (emptyReadListeners != null) {
            for (ReadListener emptyReadListener : emptyReadListeners) {
                isSave = ((EmptyReadListener<R>) emptyReadListener).readEmpty(r, field, excelField, rowIndex, colIndex, hasNext);
            }
        }
        return isSave;
    }

    @SuppressWarnings("unchecked")
    public static <R> void resultNotify(List<ReadListener> resultReadListeners, List<R> data) {
        if (resultReadListeners != null) {
            resultReadListeners.forEach(e -> ((ResultReadListener<R>) e).notify(data));
        }
    }
}
