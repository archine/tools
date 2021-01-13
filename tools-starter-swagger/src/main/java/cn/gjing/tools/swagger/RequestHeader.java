package cn.gjing.tools.swagger;

import lombok.*;

/**
 * Global request header
 *
 * @author Gjing
 **/
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RequestHeader {
    /**
     * Request header name
     */
    private String name;
    /**
     * Header description
     */
    private String desc;
    /**
     * Whether must
     */
    private boolean required = false;
}
