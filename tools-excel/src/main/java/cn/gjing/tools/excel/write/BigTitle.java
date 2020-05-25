package cn.gjing.tools.excel.write;

import cn.gjing.tools.excel.metadata.ExcelColor;
import lombok.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

/**
 * Excel big title
 *
 * @author Gjing
 **/
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public final class BigTitle {
    /**
     * How many lines
     */
    @Builder.Default
    private int lines = 2;

    /**
     * First col index, default 0
     */
    private int firstCol;

    /**
     * Last col index, default is the number of following excel header size
     */
    private int lastCols;

    /**
     * Style index, if the style of the index exists,
     * it will take the existing one,
     * otherwise it will create a new one
     */
    private int index;

    /**
     * Fill content
     */
    @Builder.Default
    private String content = "";

    /**
     * Fill color
     */
    @Builder.Default
    private ExcelColor color = ExcelColor.TAN;

    /**
     * Row height
     */
    @Builder.Default
    private short rowHeight = 350;

    /**
     * Font color
     */
    @Builder.Default
    private ExcelColor fontColor = ExcelColor.BLACK;

    /**
     * Font height
     */
    @Builder.Default
    private short fontHeight = 250;

    /**
     * Horizontal alignment
     */
    @Builder.Default
    private HorizontalAlignment alignment = HorizontalAlignment.LEFT;

    /**
     * Font bold
     */
    @Builder.Default
    private boolean bold = false;

    public BigTitle(String content) {
        this.lines = 2;
        this.content = content;
        this.color = ExcelColor.TAN;
        this.rowHeight = 350;
        this.fontColor = ExcelColor.BLACK;
        this.alignment = HorizontalAlignment.LEFT;
        this.bold = false;
        this.fontHeight = 280;
    }
}
