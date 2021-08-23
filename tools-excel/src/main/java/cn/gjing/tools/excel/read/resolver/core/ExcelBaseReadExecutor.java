package cn.gjing.tools.excel.read.resolver.core;

import cn.gjing.tools.excel.exception.ExcelResolverException;
import cn.gjing.tools.excel.metadata.ExecType;
import cn.gjing.tools.excel.metadata.RowType;
import cn.gjing.tools.excel.metadata.listener.ExcelListener;
import cn.gjing.tools.excel.read.ExcelReaderContext;
import cn.gjing.tools.excel.util.JsonUtils;
import cn.gjing.tools.excel.util.ListenerChain;
import cn.gjing.tools.excel.util.ParamUtils;
import com.monitorjbl.xlsx.impl.StreamingWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Excel base reader executor
 *
 * @author Gjing
 **/
public abstract class ExcelBaseReadExecutor<R> {
    protected final ExcelReaderContext<R> context;
    protected Boolean saveCurrentRowObj;

    public ExcelBaseReadExecutor(ExcelReaderContext<R> context) {
        this.context = context;
    }

    /**
     * Start Import excel
     *
     * @param headerIndex Excel header index
     * @param sheetName   sheetName
     */
    public abstract void read(int headerIndex, String sheetName);

    /**
     * Read head row
     *
     * @param rowReadListeners Row read listener
     * @param row              Current row
     * @return Continue read next row
     */
    protected boolean readHead(List<ExcelListener> rowReadListeners, Row row) {
        for (Cell cell : row) {
            String value = cell.getStringCellValue();
            if (ParamUtils.contains(this.context.getIgnores(), value)) {
                value = "ignored";
            }
            this.context.getHeadNames().add(String.valueOf(ListenerChain.doReadCell(rowReadListeners, value, cell, row.getRowNum(), cell.getColumnIndex(), RowType.HEAD)));
        }
        return ListenerChain.doReadRow(rowReadListeners, null, row, RowType.HEAD);
    }

    /**
     * Reads all rows before the table header
     *
     * @param rowReadListeners Row read listener
     * @param row              Current row
     * @return Continue read next row
     */
    protected boolean readHeadBefore(List<ExcelListener> rowReadListeners, Row row) {
        if (this.context.isHeadBefore()) {
            for (Cell cell : row) {
                Object value = this.getValue(null, cell, null, null, false, false, RowType.OTHER, ExecType.SIMPLE);
                ListenerChain.doReadCell(rowReadListeners, value, cell, row.getRowNum(), cell.getColumnIndex(), RowType.OTHER);
            }
            return ListenerChain.doReadRow(rowReadListeners, null, row, RowType.OTHER);
        }
        return true;
    }

    /**
     * Check sheet is exists
     *
     * @param sheetName Sheet name
     */
    protected void checkSheet(String sheetName) {
        if (this.context.getWorkbook() instanceof StreamingWorkbook) {
            try {
                this.context.setSheet(this.context.getWorkbook().getSheet(sheetName));
            } catch (Exception e) {
                throw new ExcelResolverException("The" + sheetName + " is not found in the workbook");
            }
        } else {
            Sheet sheet = this.context.getWorkbook().getSheet(sheetName);
            if (sheet == null) {
                throw new ExcelResolverException("The" + sheetName + " is not found in the workbook");
            }
            this.context.setSheet(sheet);
        }
    }

    /**
     * Get the value of the cell
     *
     * @param cell     cell
     * @param trim     Remove white space on both sides of the string
     * @param required Cell content required
     * @param field    Current field
     * @param header   Current header
     * @param r        Current row generated row
     * @param rowType  rowType Current row type
     * @param execType Executor type
     * @return value
     */
    protected Object getValue(R r, Cell cell, Field field, String header, boolean trim, boolean required, RowType rowType, ExecType execType) {
        switch (cell.getCellType()) {
            case _NONE:
            case BLANK:
            case ERROR:
                if (rowType == RowType.BODY) {
                    if (required) {
                        this.saveCurrentRowObj = ListenerChain.doReadEmpty(this.context.getListenerCache(), r, header, cell);
                    }
                }
                break;
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                }
                if (execType == ExecType.BIND) {
                    return rowType == RowType.BODY ? JsonUtils.toObj(JsonUtils.toJson(cell.getNumericCellValue()), field.getType()) : cell.getNumericCellValue();
                }
                return cell.getNumericCellValue();
            case FORMULA:
                if (execType == ExecType.BIND) {
                    return rowType == RowType.BODY ? JsonUtils.toObj(JsonUtils.toJson(cell.getStringCellValue()), field.getType()) : cell.getStringCellValue();
                }
                return cell.getStringCellValue();
            default:
                return trim ? cell.getStringCellValue().trim() : cell.getStringCellValue();
        }
        return null;
    }
}
