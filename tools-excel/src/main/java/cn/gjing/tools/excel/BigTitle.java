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
     * Title content
     */
    @Builder.Default
    private String content = "";
}
