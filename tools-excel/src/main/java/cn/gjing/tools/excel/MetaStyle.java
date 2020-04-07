package cn.gjing.tools.excel;

import lombok.*;
import org.apache.poi.ss.usermodel.CellStyle;

/**
 * Excel meta style
 *
 * @author Gjing
 **/
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetaStyle {
    private CellStyle headStyle;
    private CellStyle bodyStyle;
    private CellStyle titleStyle;
}
