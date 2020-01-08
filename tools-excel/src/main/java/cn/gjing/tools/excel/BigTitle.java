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
     * Title last row
     */
    private int lastRow = 2;

    /**
     * Title content
     */
    private String content = "";
}
