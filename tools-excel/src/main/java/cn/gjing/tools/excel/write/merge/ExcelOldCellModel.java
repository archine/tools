package cn.gjing.tools.excel.write.merge;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Gjing
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public final class ExcelOldCellModel {
    private Object lastCellValue;

    private int lastCellIndex;
}
