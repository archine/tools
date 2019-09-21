package cn.gjing.tools.excel;

import org.apache.poi.ss.usermodel.CellStyle;

/**
 * Excel样式
 * @author Gjing
 **/
public interface ExcelStyle {
    CellStyle style(CellStyle cellStyle);
}
