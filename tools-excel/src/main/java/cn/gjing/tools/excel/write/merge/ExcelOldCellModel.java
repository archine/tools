package cn.gjing.tools.excel.write.merge;

import lombok.*;

/**
 * @author Gjing
 **/
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExcelOldCellModel {
    /**
     * The last cell value
     */
    private Object oldCellValue;

    /**
     * The last cell index
     */
    private int oldCellIndex;
}
