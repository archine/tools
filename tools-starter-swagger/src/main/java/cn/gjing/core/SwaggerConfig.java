package cn.gjing.core;

import cn.gjing.SwaggerBean;
import com.google.common.base.Predicate;
import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.Resource;

/**
 * @author Gjing
 **/
@EnableSwagger2
class SwaggerConfig {
    @Resource
    private SwaggerBean swaggerBean;
    @Resource
    private cn.gjing.Contact contact;

    @Bean
    @SuppressWarnings("all")
    public Docket createRestApi(ApiInfo apiInfo) {
        Predicate<String> predicate;
        if (swaggerBean.getPathType() == null) {
            predicate = PathSelectors.any();
        } else {
            switch (swaggerBean.getPathType()) {
                case ANT:
                    if (swaggerBean.getPathPattern() == null) {
                        throw new IllegalArgumentException("Swagger PathType is ANT,So pathPattern is cannot be null");
                    }
                    predicate = PathSelectors.ant(swaggerBean.getPathPattern());
                    break;
                case REGEX:
                    if (swaggerBean.getPathPattern() == null) {
                        throw new IllegalArgumentException("Swagger PathType is REGEX,So pathPattern is cannot be null");
                    }
                    predicate = PathSelectors.regex(swaggerBean.getPathPattern());
                    break;
                default:
                    predicate = PathSelectors.any();
            }
        }
        if (VerifyParam.verify(swaggerBean.getBasePackage())) {
            return new Docket(DocumentationType.SWAGGER_2)
                    .apiInfo(apiInfo)
                    .select()
                    .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                    .paths(predicate)
                    .build();
        } else {
            return new Docket(DocumentationType.SWAGGER_2)
                    .apiInfo(apiInfo)
                    .select()
                    .apis(RequestHandlerSelectors.basePackage(swaggerBean.getBasePackage()))
                    .paths(predicate)
                    .build();
        }
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
