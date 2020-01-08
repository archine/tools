package cn.gjing.tools.excel;

import lombok.*;
import org.apache.poi.ss.usermodel.CellStyle;

/**
 * @author Gjing
 **/
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MetaStyle {
    private CellStyle headStyle;
    private CellStyle bodyStyle;
    private CellStyle titleStyle;
}
