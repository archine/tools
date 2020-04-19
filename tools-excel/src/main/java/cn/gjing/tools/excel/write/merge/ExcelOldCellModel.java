package cn.gjing.tools.excel.write.merge;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Excel front cell
 * @author Gjing
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public final class ExcelOldCellModel {
    /**
     * Last cell value
     */
    private Object lastCellValue;

    /**
     * last cell index
     */
    private int lastCellIndex;
}
