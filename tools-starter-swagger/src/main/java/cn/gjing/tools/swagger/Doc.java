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
@ConfigurationProperties("tools.doc")
public class Doc {
    /**
     * Open the Swagger document
     */
    @Builder.Default
    private boolean enable = true;

    /**
     * Scanned package full path (controller package path)
     */
    @Builder.Default
    private String basePackage = "";

    /**
     * Interface matching rules, ANT (path matching), REGEX (regular matching)
     */
    @Builder.Default
    private PathType pathType = PathType.ANT;

    /**
     * Path expression
     */
    @Builder.Default
    private String pathPattern = "/**";

    /**
     * Interface excludes path expressions
     */
    @Builder.Default
    private String[] excludePattern = new String[]{};

    /**
     * Document title
     */
    @Builder.Default
    private String title = "Api documentation";
    /**
     * Document description
     */
    @Builder.Default
    private String description = "";
    /**
     * Document version
     */
    @Builder.Default
    private String version = "3.0";
    /**
     * Document license
     */
    @Builder.Default
    private String license = "";

    /**
     * Document license address
     */
    @Builder.Default
    private String licenseUrl = "";

    /**
     * The terms of service
     */
    @Builder.Default
    private String termsOfServiceUrl = "";

    /**
     * Global response information
     */
    @Builder.Default
    private List<ResponseSchema> globalResponseSchemas = new ArrayList<>();

    /**
     * Global request header
     */
    @Builder.Default
    private List<RequestHeader> globalHeaders = new ArrayList<>();
}


