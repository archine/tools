package cn.gjing.tools.excel.util;

import cn.gjing.tools.excel.ExcelField;
import cn.gjing.tools.excel.read.listener.EmptyReadListener;
import cn.gjing.tools.excel.read.listener.ReadListener;
import cn.gjing.tools.excel.read.listener.ResultReadListener;
import cn.gjing.tools.excel.read.listener.RowReadListener;
import cn.gjing.tools.excel.write.ExcelWriterContext;
import cn.gjing.tools.excel.write.listener.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * @author Gjing
 **/
public final class ListenerChain {

    public static void doCompleteCell(Map<Class<? extends ExcelWriteListener>, List<ExcelWriteListener>> excelListeners, Sheet sheet, Row row, Cell cell,
                                    ExcelField excelField, Field field, String headName, int index, int colIndex, boolean isHead, Object value) {
        List<ExcelWriteListener> cellListeners = excelListeners.get(ExcelCellWriteListener.class);
        if (cellListeners != null) {
            cellListeners.forEach(e -> ((ExcelCellWriteListener) e).completeCell(sheet, row, cell, excelField, field, headName, index, colIndex, isHead, value));
        }
    }

    public static void doCompleteRow(Map<Class<? extends ExcelWriteListener>, List<ExcelWriteListener>> excelListeners, Sheet sheet, Row row, int index, boolean isHead) {
        List<ExcelWriteListener> rowListeners = excelListeners.get(ExcelRowWriteListener.class);
        if (rowListeners != null) {
            rowListeners.forEach(e -> ((ExcelRowWriteListener) e).completeRow(sheet, row, index, isHead));
        }
    }

    public static void doCompleteSheet(ExcelWriterContext context) {
        List<ExcelWriteListener> sheetListeners = context.getWriteListenerCache().get(ExcelSheetWriteListener.class);
        if (sheetListeners != null) {
            sheetListeners.forEach(e -> ((ExcelSheetWriteListener) e).completeSheet(context));
        }
    }

    public static void doWorkbookFlushBefore(ExcelWriterContext context) {
        List<ExcelWriteListener> workbookListeners = context.getWriteListenerCache().get(ExcelWorkbookWriteListener.class);
        if (workbookListeners != null) {
            workbookListeners.forEach(e -> ((ExcelWorkbookWriteListener) e).flushBefore(context));
        }
    }

    @SuppressWarnings("unchecked")
    public static <R> boolean doReadRow(List<ReadListener> rowReadListeners, R r, List<String> headNames, int rowIndex, boolean isHead, boolean hasNext) {
        boolean stop = false;
        if (rowReadListeners != null) {
            for (ReadListener rowReadListener : rowReadListeners) {
                stop = ((RowReadListener<R>) rowReadListener).readRow(r, headNames, rowIndex, isHead, hasNext);
            }
        }
        return stop;
    }

    @SuppressWarnings("unchecked")
    public static <R> void doReadCell(List<ReadListener> rowReadListeners, R r, Object cellValue, Field field, int rowIndex, int colIndex, boolean isHead) {
        if (rowReadListeners != null) {
            rowReadListeners.forEach(e -> ((RowReadListener<R>) e).readCell(r, cellValue, field, rowIndex, colIndex, isHead));
        }
    }

    @SuppressWarnings("unchecked")
    public static <R> boolean doReadEmpty(List<ReadListener> emptyReadListeners, R r, Field field, ExcelField excelField, int rowIndex, int colIndex, boolean hasNext) {
        boolean isSave = false;
        if (emptyReadListeners != null) {
            for (ReadListener emptyReadListener : emptyReadListeners) {
                isSave = ((EmptyReadListener<R>) emptyReadListener).readEmpty(r, field, excelField, rowIndex, colIndex, hasNext);
            }
        }
        return isSave;
    }

    @SuppressWarnings("unchecked")
    public static <R> void doResultNotify(List<ReadListener> resultReadListeners, List<R> data) {
        if (resultReadListeners != null) {
            resultReadListeners.forEach(e -> ((ResultReadListener<R>) e).notify(data));
        }
    }
}
