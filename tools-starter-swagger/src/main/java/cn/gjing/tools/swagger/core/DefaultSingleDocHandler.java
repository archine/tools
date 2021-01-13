package cn.gjing.tools.swagger.core;

import cn.gjing.tools.swagger.Doc;
import cn.gjing.tools.swagger.DocContact;
import cn.gjing.tools.swagger.PathType;
import io.swagger.annotations.ApiOperation;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.*;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.ResponseMessage;
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
class DefaultSingleDocHandler {
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
    public Docket createRestApi() {
        final Docket docket = new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).enable(doc.isEnable());
        if (!doc.getGlobalResponseSchemas().isEmpty()) {
            List<ResponseMessage> responseMessageList = new ArrayList<>();
            doc.getGlobalResponseSchemas().forEach(e -> {
                responseMessageList.add(new ResponseMessageBuilder().code(e.getCode()).message(e.getMessage())
                        .responseModel(new ModelRef(e.getSchema())).build());
            });
            docket.globalResponseMessage(RequestMethod.GET, responseMessageList)
                    .globalResponseMessage(RequestMethod.DELETE, responseMessageList)
                    .globalResponseMessage(RequestMethod.POST, responseMessageList)
                    .globalResponseMessage(RequestMethod.PUT, responseMessageList)
                    .globalResponseMessage(RequestMethod.PATCH, responseMessageList);
        }
        if (!doc.getGlobalHeaders().isEmpty()) {
            List<Parameter> parameterList = new ArrayList<>();
            doc.getGlobalHeaders().forEach(e -> {
                parameterList.add(new ParameterBuilder().name(e.getName()).description(e.getDesc()).required(e.isRequired())
                        .modelRef(new ModelRef("String")).parameterType("header").build());
            });
            docket.globalOperationParameters(parameterList);
        }
        ApiSelectorBuilder builder = docket.select();
        if (this.doc.getBasePackage().isEmpty()) {
            LoggerFactory.getLogger(DefaultSingleDocHandler.class).warn("Swagger basePackage value is default , Please set your own project interface path");
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
