package cn.gjing.tools.swagger;

import lombok.*;
import springfox.documentation.service.ParameterType;

/**
 * Global request header
 *
 * @author Gjing
 **/
@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RequestParameters {
    /**
     * Parameter name
     */
    private String name;
    /**
     * Parameter type, include query,headers,path,body,cookie,form,formData
     */
    @Builder.Default
    private ParameterType type = ParameterType.QUERY;
    /**
     * Parameter description
     */
    @Builder.Default
    private String desc = "";
    /**
     * Whether is must
     */
    @Builder.Default
    private boolean required = false;
    /**
     * Whether is deprecated
     */
    @Builder.Default
    private boolean deprecated = false;
    /**
     * Whether is hidden
     */
    @Builder.Default
    private boolean hidden = false;
    /**
     * Parameter index
     */
    @Builder.Default
    private int index = 0;
}
