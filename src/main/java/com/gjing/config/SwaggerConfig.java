package com.gjing.config;

import com.gjing.ex.RegisterBeanException;
import com.gjing.utils.ParamUtil;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.Resource;

/**
 * @author Gjind
 **/
@EnableSwagger2
public class SwaggerConfig {
    @Resource
    private SwaggerBean swaggerBean;
    @Bean
    public Docket createRestApi(ApiInfo apiInfo) throws RegisterBeanException {
        if (ParamUtil.paramIsEmpty(swaggerBean.getBasePackage())) {
            throw new RegisterBeanException("basePackage cannot be null");
        }
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo)
                .select()
                .apis(RequestHandlerSelectors.basePackage(swaggerBean.getBasePackage()))
                .paths(PathSelectors.any())
                .build();
    }

    @Bean
    public ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(swaggerBean.getTitle())
                .description(swaggerBean.getDescription())
                .version(swaggerBean.getVersion())
                .build();
    }
}
