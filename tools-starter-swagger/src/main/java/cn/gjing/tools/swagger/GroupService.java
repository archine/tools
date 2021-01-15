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
     * The service description
     */
    private String desc;
    /**
     * The application name of the target service,
     * if the "spring.application.name" parameter is configured in the target project
     */
    private String target;
    /**
     * Document address, the default can be
     */
    @Builder.Default
    private String location = "/v2/api-docs";
    /**
     * The context path of the target service,
     * if the "servlet.context-path" parameter is configured in the target project
     */
    @Builder.Default
    private String contextPath = "";
}
