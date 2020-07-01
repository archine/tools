package cn.gjing.tools.excel.write.merge;

import lombok.*;

/**
 * Excel front row
 *
 * @author Gjing
 **/
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class ExcelOldRowModel {
    /**
     * The last row cell value
     */
    private Object oldRowCellValue;

    /**
     * The ord row index
     */
    private int oldRowIndex;
}
