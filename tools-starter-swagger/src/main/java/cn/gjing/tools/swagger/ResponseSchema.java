package cn.gjing.tools.swagger;

import lombok.*;

/**
 * Global response structure
 *
 * @author Gjing
 **/
@Builder
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ResponseSchema {
    /**
     * Response status
     */
    private String code;
    /**
     * Response message
     */
    private String message;
    /**
     * Result bean name
     */
    @Deprecated
    private String schema;
    /**
     * Whether is default
     */
    @Builder.Default
    private boolean isDefault = false;
}
