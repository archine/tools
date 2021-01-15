package cn.gjing.tools.swagger.core;

import cn.gjing.tools.swagger.Doc;
import cn.gjing.tools.swagger.DocContact;
import cn.gjing.tools.swagger.PathType;
import io.swagger.annotations.ApiOperation;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.util.AntPathMatcher;
import springfox.documentation.builders.*;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.RequestParameter;
import springfox.documentation.service.Response;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.ApiSelectorBuilder;
import springfox.documentation.spring.web.plugins.Docket;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author Gjing
 **/
@EnableOpenApi
class SingleDocumentHandler {
    @Resource
    private Doc doc;
    @Resource
    private DocContact contact;

    @Bean
    public ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(doc.getTitle())
                .description(doc.getDescription())
                .version(doc.getVersion())
                .contact(new Contact(contact.getName(), contact.getUrl(), contact.getEmail()))
                .license(doc.getLicense())
                .licenseUrl(doc.getLicenseUrl())
                .termsOfServiceUrl(doc.getTermsOfServiceUrl())
                .build();
    }

    @Bean
    @SuppressWarnings("all")
    public Docket restApiDocket() {
        final Docket docket = new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .enable(doc.isEnable())
                .groupName(this.doc.getGroup())
                .protocols(this.doc.getProtocols());
        if (!doc.getGlobalResponseSchemas().isEmpty()) {
            List<Response> responseMessageList = new ArrayList<>();
            doc.getGlobalResponseSchemas().forEach(e -> {
                responseMessageList.add(new ResponseBuilder()
                        .code(e.getCode())
                        .description(e.getMessage())
                        .isDefault(e.isDefault())
                        .build());
            });
            docket.globalResponses(HttpMethod.GET, responseMessageList)
                    .globalResponses(HttpMethod.DELETE, responseMessageList)
                    .globalResponses(HttpMethod.POST, responseMessageList)
                    .globalResponses(HttpMethod.PUT, responseMessageList)
                    .globalResponses(HttpMethod.PATCH, responseMessageList);
        }
        if (!doc.getGlobalParameters().isEmpty()) {
            List<RequestParameter> parameterList = new ArrayList<>();
            doc.getGlobalParameters().forEach(e -> {
                parameterList.add(new RequestParameterBuilder()
                        .description(e.getDesc())
                        .name(e.getName())
                        .parameterIndex(e.getIndex())
                        .deprecated(e.isDeprecated())
                        .hidden(e.isHidden())
                        .in(e.getType())
                        .required(e.isRequired())
                        .build());
            });
            docket.globalRequestParameters(parameterList);
        }
        ApiSelectorBuilder builder = docket.select();
        if (this.doc.getBasePackage().isEmpty()) {
            LoggerFactory.getLogger(SingleDocumentHandler.class).warn("Swagger basePackage value is default , Please set your own project interface path");
            builder.apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class));
        } else {
            builder.apis(RequestHandlerSelectors.basePackage(doc.getBasePackage()));
        }
        if (this.doc.getPathType() == PathType.ANT) {
            builder.paths(PathSelectors.ant(this.doc.getPathPattern()));
            for (String exclude : this.doc.getExcludePattern()) {
                builder.paths(notAnt(exclude));
            }
        } else {
            builder.paths(PathSelectors.regex(this.doc.getPathPattern()));
            for (String exclude : this.doc.getExcludePattern()) {
                builder.paths(notRegex(exclude));
            }
        }
        return builder.build();
    }

    /**
     * No ant match
     *
     * @param antPattern ant pattern
     * @return Predicate
     */
    public static Predicate<String> notAnt(final String antPattern) {
        return input -> {
            AntPathMatcher matcher = new AntPathMatcher();
            return !matcher.match(antPattern, input);
        };
    }

    /**
     * No regex match
     *
     * @param pathRegex path regex
     * @return Predicate
     */
    public static Predicate<String> notRegex(final String pathRegex) {
        return input -> !input.matches(pathRegex);
    }
}
