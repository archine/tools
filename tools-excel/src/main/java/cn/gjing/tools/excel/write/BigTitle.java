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
     * First col index
     */
    private int firstCol = 0;

    /**
     * Last col index, 0 is the number of following excel header size
     */
    private int lastCols = 0;

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
