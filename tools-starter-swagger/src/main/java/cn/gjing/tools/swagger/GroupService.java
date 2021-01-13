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
     * The target service name is the name of the "spring.application.name" parameter
     * set in your project configuration file
     */
    private String target;

    /**
     * Document address, the default can be
     */
    @Builder.Default
    private String location = "/v2/api-docs";
}
