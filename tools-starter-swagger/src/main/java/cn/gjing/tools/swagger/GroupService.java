package cn.gjing.tools.swagger;

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
public class GroupService {
    /**
     * 描述
     */
    private String desc;
    /**
     * 目标服务url或者服务名
     */
    private String url;
}
