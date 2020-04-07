package cn.gjing.tools.excel.write.merge;

import lombok.*;

/**
 * @author Gjing
 **/
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExcelOldModel {
    /**
     * The last cell value
     */
    private Object oldValue;
    /**
     * The ord row index
     */
    private int oldRowIndex;
}
