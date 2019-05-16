package cn.gjing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Gjing
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Serve {

    /**
     * 目标文档展示的提示名
     */
    private String name;

    /**
     * 目标地址: 如/demo/v2/api-docs,也可以直接传服务名
     */
    private String location;
}
