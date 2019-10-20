package cn.gjing.tools.excel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.poi.ss.usermodel.CellStyle;

/**
 * @author Gjing
 **/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MetaStyle {
    private CellStyle headStyle;
    private CellStyle bodyStyle;
    private CellStyle titleStyle;
}
