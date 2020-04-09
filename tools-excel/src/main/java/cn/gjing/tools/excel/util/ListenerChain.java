package cn.gjing.tools.excel.util;

import cn.gjing.tools.excel.ExcelField;
import cn.gjing.tools.excel.read.listener.ExcelEmptyReadListener;
import cn.gjing.tools.excel.read.listener.ExcelReadListener;
import cn.gjing.tools.excel.read.listener.ExcelResultReadListener;
import cn.gjing.tools.excel.read.listener.ExcelRowReadListener;
import cn.gjing.tools.excel.write.ExcelWriterContext;
import cn.gjing.tools.excel.write.listener.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Excel listener link call tool
 *
 * @author Gjing
 **/
public final class ListenerChain {

    /**
     * Execute write cell listener
     *
     * @param excelListeners excelListeners
     * @param sheet          Current sheet
     * @param row            Current row
     * @param cell           Current cell
     * @param index          Line index, index type according to isHead, Starting from 0
     * @param colIndex       Current cell index
     * @param isHead         Whether is excel head
     * @param excelField     ExcelField annotation of current field
     * @param field          Current field
     * @param value          Cell value
     * @param headName       The header name of the list where the cell resides
     */
    public static void doCompleteCell(Map<Class<? extends ExcelWriteListener>, List<ExcelWriteListener>> excelListeners, Sheet sheet, Row row, Cell cell,
                                      ExcelField excelField, Field field, String headName, int index, int colIndex, boolean isHead, Object value) {
        List<ExcelWriteListener> cellListeners = excelListeners.get(ExcelCellWriteListener.class);
        if (cellListeners != null) {
            cellListeners.forEach(e -> ((ExcelCellWriteListener) e).completeCell(sheet, row, cell, excelField, field, headName, index, colIndex, isHead, value));
        }
    }

    /**
     * Execute write row listener
     *
     * @param excelListeners excelListeners
     * @param sheet          Current sheet
     * @param row            Create the finished row
     * @param index          Line index, index type according to isHead，Starting from 0
     * @param isHead         Whether is excel head
     */
    public static void doCompleteRow(Map<Class<? extends ExcelWriteListener>, List<ExcelWriteListener>> excelListeners, Sheet sheet, Row row, int index, boolean isHead) {
        List<ExcelWriteListener> rowListeners = excelListeners.get(ExcelRowWriteListener.class);
        if (rowListeners != null) {
            rowListeners.forEach(e -> ((ExcelRowWriteListener) e).completeRow(sheet, row, index, isHead));
        }
    }

    /**
     * Execute write sheet listener
     *
     * @param context Excel write context
     */
    public static void doCompleteSheet(ExcelWriterContext context) {
        List<ExcelWriteListener> sheetListeners = context.getWriteListenerCache().get(ExcelSheetWriteListener.class);
        if (sheetListeners != null) {
            sheetListeners.forEach(e -> ((ExcelSheetWriteListener) e).completeSheet(context));
        }
    }

    /**
     * Execute write workbook listener
     *
     * @param context Excel write context
     */
    public static void doWorkbookFlushBefore(ExcelWriterContext context) {
        List<ExcelWriteListener> workbookListeners = context.getWriteListenerCache().get(ExcelWorkbookWriteListener.class);
        if (workbookListeners != null) {
            workbookListeners.forEach(e -> ((ExcelWorkbookWriteListener) e).flushBefore(context));
        }
    }

    /**
     * Execute read row listener
     *
     * @param rowReadListeners rowReadListeners
     * @param r                Generated Java object
     * @param rowIndex         The index of the current row
     * @param isHead           Whether is excel head
     * @param hasNext          Whether has next row
     * @param headNames        All the table headers of the current row
     * @param <R>              R
     * @return Whether to stop reading
     */
    @SuppressWarnings("unchecked")
    public static <R> boolean doReadRow(List<ExcelReadListener> rowReadListeners, R r, List<String> headNames, int rowIndex, boolean isHead, boolean hasNext) {
        boolean stop = false;
        if (rowReadListeners != null) {
            for (ExcelReadListener rowReadListener : rowReadListeners) {
                stop = ((ExcelRowReadListener<R>) rowReadListener).readRow(r, headNames, rowIndex, isHead, hasNext);
            }
        }
        return stop;
    }

    /**
     * Execute read row listener
     *
     * @param rowReadListeners rowReadListeners
     * @param r                Current Java object
     * @param cellValue        Current cell value
     * @param field            Current field
     * @param rowIndex         Current row index
     * @param colIndex         Current col index
     * @param isHead           Whether is excel header
     * @param <R>              R
     */
    @SuppressWarnings("unchecked")
    public static <R> void doReadCell(List<ExcelReadListener> rowReadListeners, R r, Object cellValue, Field field, int rowIndex, int colIndex, boolean isHead) {
        if (rowReadListeners != null) {
            rowReadListeners.forEach(e -> ((ExcelRowReadListener<R>) e).readCell(r, cellValue, field, rowIndex, colIndex, isHead));
        }
    }

    /**
     * Execute read empty listener
     *
     * @param emptyReadListeners emptyReadListeners
     * @param r                  Current Java object
     * @param field              Current field
     * @param excelField         ExcelField annotation on that field
     * @param rowIndex           The index of the current row
     * @param colIndex           The index of the current col
     * @param hasNext            Whether has next row
     * @param <R>                R
     * @return Whether to save this data
     */
    @SuppressWarnings("unchecked")
    public static <R> boolean doReadEmpty(List<ExcelReadListener> emptyReadListeners, R r, Field field, ExcelField excelField, int rowIndex, int colIndex, boolean hasNext) {
        boolean isSave = false;
        if (emptyReadListeners != null) {
            for (ExcelReadListener emptyReadListener : emptyReadListeners) {
                isSave = ((ExcelEmptyReadListener<R>) emptyReadListener).readEmpty(r, field, excelField, rowIndex, colIndex, hasNext);
            }
        }
        return isSave;
    }

    /**
     * Execute result notify listener
     *
     * @param resultReadListener resultReadListener
     * @param data               Import all the Java objects generated after success
     * @param <R>                R
     */
    public static <R> void doResultNotify(ExcelResultReadListener<R> resultReadListener, List<R> data) {
        if (data != null) {
            resultReadListener.notify(data);
        }
    }

    /**
     * Add a write listener
     *
     * @param writeListenerCache writeListenerCache
     * @param listener           Write listener
     */
    public static void addWriteListener(Map<Class<? extends ExcelWriteListener>, List<ExcelWriteListener>> writeListenerCache, ExcelWriteListener listener) {
        if (listener instanceof ExcelSheetWriteListener) {
            List<ExcelWriteListener> listeners = writeListenerCache.computeIfAbsent(ExcelSheetWriteListener.class, k -> new ArrayList<>());
            listeners.add(listener);
        }
        if (listener instanceof ExcelRowWriteListener) {
            List<ExcelWriteListener> listeners = writeListenerCache.computeIfAbsent(ExcelRowWriteListener.class, k -> new ArrayList<>());
            listeners.add(listener);
        }
        if (listener instanceof ExcelCellWriteListener) {
            List<ExcelWriteListener> listeners = writeListenerCache.computeIfAbsent(ExcelCellWriteListener.class, k -> new ArrayList<>());
            listeners.add(listener);
        }
        if (listener instanceof ExcelCascadingDropdownBoxListener) {
            List<ExcelWriteListener> listeners = writeListenerCache.computeIfAbsent(ExcelCascadingDropdownBoxListener.class, k -> new ArrayList<>());
            listeners.add(listener);
        }
        if (listener instanceof ExcelWorkbookWriteListener) {
            List<ExcelWriteListener> listeners = writeListenerCache.computeIfAbsent(ExcelWorkbookWriteListener.class, k -> new ArrayList<>());
            listeners.add(listener);
        }
    }

    /**
     * Add a read listener
     *
     * @param readListenersCache readListenersCache
     * @param readListener       read Listener
     */
    public static void addReadListener(Map<Class<? extends ExcelReadListener>, List<ExcelReadListener>> readListenersCache, ExcelReadListener readListener) {
        if (readListener instanceof ExcelRowReadListener) {
            List<ExcelReadListener> readListeners = readListenersCache.computeIfAbsent(ExcelRowReadListener.class, k -> new ArrayList<>());
            readListeners.add(readListener);
        }
        if (readListener instanceof ExcelEmptyReadListener) {
            List<ExcelReadListener> readListeners = readListenersCache.computeIfAbsent(ExcelEmptyReadListener.class, k -> new ArrayList<>());
            readListeners.add(readListener);
        }
    }
}
