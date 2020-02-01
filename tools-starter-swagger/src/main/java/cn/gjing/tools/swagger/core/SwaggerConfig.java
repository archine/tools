package cn.gjing.tools.swagger.core;

import cn.gjing.tools.swagger.SwaggerBean;
import com.google.common.base.Predicates;
import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.ApiSelectorBuilder;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Gjing
 **/
@EnableSwagger2
class SwaggerConfig {
    @Resource
    private SwaggerBean swaggerBean;
    @Resource
    private cn.gjing.tools.swagger.Contact contact;

    @Bean
    @SuppressWarnings("all")
    public Docket createRestApi() {
        PathSelectContext pathSelectContext = new PathSelectContext();
        final Docket docket = new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).enable(swaggerBean.isEnable());
        if (!swaggerBean.getGlobalResponseSchemas().isEmpty()) {
            List<ResponseMessage> responseMessageList = new ArrayList<>();
            swaggerBean.getGlobalResponseSchemas().forEach(e -> {
                responseMessageList.add(new ResponseMessageBuilder().code(e.getCode()).message(e.getMessage())
                        .responseModel(new ModelRef(e.getSchema())).build());
            });
            docket.globalResponseMessage(RequestMethod.GET, responseMessageList)
                    .globalResponseMessage(RequestMethod.DELETE, responseMessageList)
                    .globalResponseMessage(RequestMethod.POST, responseMessageList)
                    .globalResponseMessage(RequestMethod.PUT, responseMessageList)
                    .globalResponseMessage(RequestMethod.PATCH, responseMessageList);
        }
        if (!swaggerBean.getGlobalHeaders().isEmpty()) {
            List<Parameter> parameterList = new ArrayList<>();
            swaggerBean.getGlobalHeaders().forEach(e -> {
                parameterList.add(new ParameterBuilder().name(e.getName()).description(e.getDescription()).required(e.isRequired())
                        .modelRef(new ModelRef("String")).parameterType("header").build());
            });
            docket.globalOperationParameters(parameterList);
        }
        ApiSelectorBuilder builder = docket.select();
        builder.paths(pathSelectContext.getPredicate(swaggerBean, swaggerBean.getPathPattern())).build();
        if (VerifyParam.isDefault(swaggerBean.getBasePackage())) {
            builder.apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class));
        } else {
            builder.apis(RequestHandlerSelectors.basePackage(swaggerBean.getBasePackage()));
        }
        for (String exclude : swaggerBean.getExcludePattern()) {
            builder.paths(Predicates.not(pathSelectContext.getPredicate(swaggerBean, exclude)));
        }
        return builder.build();
    }

    @Bean
    public ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(swaggerBean.getTitle())
                .description(swaggerBean.getDescription())
                .version(swaggerBean.getVersion())
                .contact(new Contact(contact.getName(), contact.getUrl(), contact.getEmail()))
                .license(swaggerBean.getLicense())
                .licenseUrl(swaggerBean.getLicenseUrl())
                .termsOfServiceUrl(swaggerBean.getTermsOfServiceUrl())
                .build();
    }
}
