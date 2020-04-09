package cn.gjing.tools.excel.write;

import lombok.*;

/**
 * Excel big title
 *
 * @author Gjing
 **/
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class BigTitle {

    /**
     * How many lines
     */
    @Builder.Default
    private int lines = 2;

    /**
     * First col index
     */
    @Builder.Default
    private int firstCol = 0;

    /**
     * Last col index, 0 is the number of following excel header size
     */
    @Builder.Default
    private int lastCols = 0;

    /**
     * Fill content
     */
    @Builder.Default
    private String content = "";

    public BigTitle(int lines, String content) {
        this.lines = lines;
        this.content = content;
        this.firstCol = 0;
        this.lastCols = 0;
    }

    public BigTitle(int lines, int lastCols, String content) {
        this.lines = lines;
        this.lastCols = lastCols;
        this.content = content;
        this.firstCol = 0;
    }
}
