package cn.gjing.tools.excel.write;

import cn.gjing.tools.excel.metadata.ExcelColor;
import cn.gjing.tools.excel.metadata.function.TFunction;
import lombok.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Workbook;

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
    private int firstCol;

    /**
     * Last col index, 0 is the number of following excel header size
     */
    private int lastCol;

    /**
     * Style index, if the style of the index exists, it will take the existing one,
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

    /**
     * Big title callback, which starts after the title style is set
     */
    @Builder.Default
    private TFunction<Workbook, Cell, BigTitle, Object> callback = (workbook, cell, bigTitle) -> bigTitle.getContent();

    /**
     * @param content Big title value
     * @deprecated Please use BigTitle.builder().build()
     */
    @Deprecated
    public BigTitle(String content) {
        this.lines = 2;
        this.content = content;
        this.color = ExcelColor.TAN;
        this.rowHeight = 350;
        this.fontColor = ExcelColor.BLACK;
        this.alignment = HorizontalAlignment.LEFT;
        this.bold = false;
        this.fontHeight = 280;
        this.callback = (workbook, cell, bigTitle) -> bigTitle.getContent();
    }
}
