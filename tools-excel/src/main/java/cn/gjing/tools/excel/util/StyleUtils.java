package cn.gjing.tools.excel.util;

import cn.gjing.tools.excel.metadata.ExcelColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

/**
 * Excel style utils
 *
 * @author Gjing
 **/
public class StyleUtils {
    /**
     * Sets the cell style coordinates
     *
     * @param cellStyle cellStyle
     */
    public static void setAlignment(CellStyle cellStyle) {
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setWrapText(true);
    }

    /**
     * Set the border
     *
     * @param cellStyle cellStyle
     * @param color     border color
     */
    public static void setBorder(CellStyle cellStyle, ExcelColor color) {
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBottomBorderColor(color.index);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setLeftBorderColor(color.index);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setRightBorderColor(color.index);
    }
}
