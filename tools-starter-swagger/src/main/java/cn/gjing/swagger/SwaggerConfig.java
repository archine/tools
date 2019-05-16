package cn.gjing.swagger;

import com.google.common.base.Predicate;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
class SwaggerConfig {
    @Resource
    private SwaggerBean swaggerBean;

    @Bean
    public Docket createRestApi(ApiInfo apiInfo) {
        Predicate<String> predicate = PathSelectors.any();
        if (swaggerBean.getPathType().equals(PathType.NONE)) {
            predicate = PathSelectors.none();
        }
        if (swaggerBean.getPathType().equals(PathType.ANT)) {
            if (swaggerBean.getPathPattern() == null) {
                throw new IllegalArgumentException("Swagger PathType is ANT,So pathPattern is no null");
            }
            predicate = PathSelectors.ant(swaggerBean.getPathPattern());
        }
        if (swaggerBean.getPathType().equals(PathType.REGEX)) {
            if (swaggerBean.getPathPattern() == null) {
                throw new IllegalArgumentException("Swagger PathType is REGEX,So pathPattern is no null");
            }
            predicate = PathSelectors.regex(swaggerBean.getPathPattern());
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
        cn.gjing.swagger.Contact contact = swaggerBean.getContact();
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
