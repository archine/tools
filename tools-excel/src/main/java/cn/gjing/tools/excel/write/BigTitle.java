package cn.gjing.tools.excel.write;

import cn.gjing.tools.excel.metadata.ExcelColor;
import lombok.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.function.BiFunction;

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
     * The starting row index, -1 is the latest row added to the current Excel file
     */
    @Builder.Default
    private int firstRow = -1;

    /**
     * Occupied rows, less than 1 is automatically set to 1
     */
    @Builder.Default
    private int lines = 2;

    /**
     * First col index
     */
    @Builder.Default
    private int firstCol = 0;

    /**
     * Last col index, -1 is the number of following excel header size
     */
    @Builder.Default
    private int lastCol = -1;

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
    private boolean bold;

    /**
     * Title value callback
     */
    @Builder.Default
    private BiFunction<Workbook, Object, Object> callback = (workbook, value) -> value;

    public static BigTitle of(String content) {
        return BigTitle.builder()
                .content(content)
                .build();
    }

    public static BigTitle of(String content, int lines) {
        return BigTitle.builder()
                .content(content)
                .lines(lines)
                .build();
    }

    public static BigTitle of(String content, int lines, int firstRow, int firstCol) {
        return BigTitle.builder()
                .content(content)
                .lines(lines)
                .firstRow(firstRow)
                .firstCol(firstCol)
                .build();
    }
}
