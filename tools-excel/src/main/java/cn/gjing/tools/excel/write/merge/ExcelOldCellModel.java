package cn.gjing.tools.excel.write.merge;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Excel front cell
 *
 * @author Gjing
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public final class ExcelOldCellModel {
    /**
     * The last cell value
     */
    private Object lastCellValue;

    /**
     * The last cell index
     */
    private int lastCellIndex;
}
