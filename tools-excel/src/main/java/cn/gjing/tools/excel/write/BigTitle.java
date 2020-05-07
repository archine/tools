package cn.gjing.tools.excel.write;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;

/**
 * Excel big title
 *
 * @author Gjing
 **/
@Getter
@Setter
@Builder
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
    private IndexedColors color = IndexedColors.TAN;

    /**
     * Row height
     */
    @Builder.Default
    private short rowHeight = 350;

    /**
     * Font color
     */
    @Builder.Default
    private IndexedColors fontColor = IndexedColors.BLACK;

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

    public BigTitle() {
    }

    public BigTitle(String content) {
        this.lines = 2;
        this.content = content;
        this.color = IndexedColors.TAN;
        this.rowHeight = 350;
        this.fontColor = IndexedColors.BLACK;
        this.alignment = HorizontalAlignment.LEFT;
        this.bold = false;
    }
    public BigTitle(int lines, String content) {
        this.lines = lines;
        this.content = content;
        this.color = IndexedColors.TAN;
        this.rowHeight = 350;
        this.fontColor = IndexedColors.BLACK;
        this.alignment = HorizontalAlignment.LEFT;
        this.bold = false;
    }

    public BigTitle(int lines, int lastCols, String content) {
        this.lines = lines;
        this.lastCols = lastCols;
        this.content = content;
        this.color = IndexedColors.TAN;
        this.rowHeight = 350;
        this.fontColor = IndexedColors.BLACK;
        this.alignment = HorizontalAlignment.LEFT;
        this.bold = false;
    }

    public BigTitle(int lines, int firstCol, int lastCols, String content, IndexedColors color, short rowHeight,
                    IndexedColors fontColor, HorizontalAlignment alignment, boolean bold) {
        this.lines = lines;
        this.firstCol = firstCol;
        this.lastCols = lastCols;
        this.content = content;
        this.color = color;
        this.rowHeight = rowHeight;
        this.fontColor = fontColor;
        this.alignment = alignment;
        this.bold = bold;
    }
}
