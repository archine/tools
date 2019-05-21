package cn.gjing.core;

import cn.gjing.PathType;
import cn.gjing.SwaggerBean;
import com.google.common.base.Predicates;
import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.ApiSelectorBuilder;
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

    private static PathSelectContext pathSelectContext = new PathSelectContext();

    @Bean
    @SuppressWarnings("all")
    public Docket createRestApi() {
        ApiSelectorBuilder builder = new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select();
        builder.paths(pathSelectContext.getPredicate(swaggerBean, swaggerBean.getPathPattern())).build();
        if (VerifyParam.verify(swaggerBean.getBasePackage())) {
            builder.apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class));
        } else {
            builder.apis(RequestHandlerSelectors.basePackage(swaggerBean.getBasePackage()));
        }
        for (String exclude : swaggerBean.getExcludePattern()) {
            if (swaggerBean.getPathType().equals(PathType.ALL)) {
                builder.paths(Predicates.not(PathSelectors.regex(exclude)));
            } else {
                builder.paths(Predicates.not(pathSelectContext.getPredicate(swaggerBean, exclude)));
            }
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
