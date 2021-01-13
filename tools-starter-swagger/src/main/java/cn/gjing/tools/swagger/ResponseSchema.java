package cn.gjing.tools.swagger;

import lombok.*;

/**
 * Global response structure
 *
 * @author Gjing
 **/
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ResponseSchema {
    /**
     * Response status
     */
    private Integer code;
    /**
     * Response message
     */
    private String message;
    /**
     * Result bean name
     */
    private String schema;
}
