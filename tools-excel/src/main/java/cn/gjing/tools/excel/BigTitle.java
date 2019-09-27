package cn.gjing.tools.excel;

import lombok.*;

/**
 * Excel大标题
 * @author Gjing
 **/
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BigTitle {

    /**
     * 占几行
     */
    @Builder.Default
    private int lastRow = 2;

    /**
     * 大标题内容
     */
    @Builder.Default
    private String content="";

}
