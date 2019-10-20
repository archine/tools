package cn.gjing.tools.excel;

import lombok.*;

/**
 * Excel大标题
 * @author Gjing
 **/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BigTitle {

    /**
     * 占几行
     */
    private int lastRow = 2;

    /**
     * 大标题内容
     */
    private String content="";

}
