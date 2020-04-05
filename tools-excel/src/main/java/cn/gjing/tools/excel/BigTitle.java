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

    /**
     * How many columns, 0 is the number of following excel header size
     */
    @Builder.Default
    private int cols = 0;

    /**
     * Title content
     */
    @Builder.Default
    private String content = "";

    public BigTitle(int lines, String content) {
        this.lines = lines;
        this.content = content;
    }

    public BigTitle(String content) {
        this.content = content;
        this.lines = 2;
    }
}
