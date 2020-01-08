package cn.gjing.tools.excel.write;

import lombok.*;

/**
 * @author Gjing
 **/
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
class ExcelModel {
    private Object oldValue;
    private int rowIndex;
}
