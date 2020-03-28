package cn.gjing.tools.swagger;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gjing
 **/
@Data
@Builder
@Component
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties("tools.doc.group")
public class DocGroup {
    /**
     * 是否开启文档聚合
     */
    @Builder.Default
    private boolean enable = false;

    /**
     * 聚合类型
     */
    @Builder.Default
    private GroupType type = GroupType.URL;

    /**
     * 服务列表
     */
    @Builder.Default
    private List<GroupService> serviceList = new ArrayList<>();
}
