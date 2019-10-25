package cn.gjing.tools.excel;

import lombok.*;

/**
 * Excel big title
 *
 * @author Gjing
 **/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BigTitle {

    /**
     * a few lines
     */
    private int lastRow = 2;

    /**
     * content
     */
    private String content = "";

}
