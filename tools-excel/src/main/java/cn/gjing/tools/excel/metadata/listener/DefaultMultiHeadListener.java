package cn.gjing.tools.excel.metadata.listener;

import cn.gjing.tools.excel.metadata.RowType;
import cn.gjing.tools.excel.util.ExcelUtils;
import cn.gjing.tools.excel.metadata.aware.ExcelWriteContextAware;
import cn.gjing.tools.excel.write.ExcelWriterContext;
import cn.gjing.tools.excel.write.listener.ExcelRowWriteListener;
import cn.gjing.tools.excel.write.resolver.ExcelBindWriter;
import cn.gjing.tools.excel.write.resolver.simple.ExcelSimpleWriter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * Multi-level header merge listener
 * Automatically merge adjacent cells with the same content
 * It needs to be turned on using {@link ExcelBindWriter#multiHead(boolean)}
 * .                              {@link ExcelSimpleWriter#multiHead(boolean)}
 *
 * @author Gjing
 **/
public class DefaultMultiHeadListener implements ExcelRowWriteListener, ExcelWriteContextAware {
    private ExcelWriterContext writerContext;

    public DefaultMultiHeadListener() {
    }

    @Override
    public void setContext(ExcelWriterContext writerContext) {
        this.writerContext = writerContext;
    }

    @Override
    public void completeRow(Sheet sheet, Row row, Object obj, int index, RowType rowType) {
        if (rowType != RowType.HEAD) {
            return;
        }
        if (index == 0) {
            int startCol = 0;
            int endCol = 0;
            int startRow = row.getRowNum();
            int endRow = row.getRowNum();
            // The depth of the header
            int level;
            // The next header column is indexed
            int nextHeadIndex = 1;
            for (int i = 0; i < this.writerContext.getHeaderSeries(); i++) {
                level = i;
                for (int j = 0, len = this.writerContext.getHeadNames().size(); j < len; j++) {
                    if (nextHeadIndex - j < 1) {
                        nextHeadIndex++;
                    }
                    if (ExcelUtils.isMerge(sheet, startRow, j)) {
                        startCol = nextHeadIndex;
                        endCol = nextHeadIndex;
                        continue;
                    }
                    // Wide search
                    while (len > nextHeadIndex && this.writerContext.getHeadNames().get(nextHeadIndex)[level].equals(this.writerContext.getHeadNames().get(nextHeadIndex - 1)[level])) {
                        endCol = nextHeadIndex;
                        nextHeadIndex++;
                        j = endCol;
                    }
                    // Deep search
                    while (this.writerContext.getHeaderSeries() - 1 > level && this.writerContext.getHeadNames().get(j)[level].equals(this.writerContext.getHeadNames().get(j)[level + 1])) {
                        endRow++;
                        level++;
                    }
                    if (startCol != endCol || startRow != endRow) {
                        ExcelUtils.merge(this.writerContext.getSheet(), startCol, endCol, startRow, endRow);
                        level = i;
                        startCol = nextHeadIndex;
                        endCol = nextHeadIndex;
                        endRow = startRow;
                        continue;
                    }
                    startCol = nextHeadIndex;
                    endCol = nextHeadIndex;
                }
                // Init param
                startCol = 0;
                endCol = 0;
                startRow = startRow + 1;
                endRow = startRow;
                nextHeadIndex = 1;
            }
        }
    }
}
