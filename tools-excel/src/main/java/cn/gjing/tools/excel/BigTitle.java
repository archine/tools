package cn.gjing.tools.excel;

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
public class BigTitle {

    /**
     * How many lines
     */
    @Builder.Default
    private int lines = 2;

    @Builder.Default
    private int firstCol = 0;

    /**
     * How many columns, 0 is the number of following excel header size
     */
    @Builder.Default
    private int lastCols = 0;

    /**
     * Title content
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
