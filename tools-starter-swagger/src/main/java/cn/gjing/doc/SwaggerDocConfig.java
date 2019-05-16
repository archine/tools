package cn.gjing.doc;

import cn.gjing.swagger.Resources;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.util.StringUtils;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Gjing
 **/
@SuppressWarnings("unchecked")
@Primary
class SwaggerDocConfig implements SwaggerResourcesProvider {
    @Value("${spring.application.name:default}")
    private String applicationName;
    @Resource
    private Resources resources;

    @Override
    public List<SwaggerResource> get() {
        List<String> serveNameList = resources.getServeList();
        boolean isEmpty = serveNameList.isEmpty();
        List resources = new ArrayList<>();
        if (this.resources.isRegisterMe()) {
            resources.add(swaggerResource(applicationName,"/v2/api-docs"));
        } else {
            if (isEmpty) {
                throw new IllegalArgumentException("Swagger serve list cannot be empty, please set register-me to true or add other serve name");
            }
        }
        if (!isEmpty) {
            for (String name : serveNameList) {
                if (StringUtils.isEmpty(name)) {
                    continue;
                }
                resources.add(swaggerResource(name,buildLocation(name)));
            }
        }
        return resources;
    }

    private SwaggerResource swaggerResource(String name,String location) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setSwaggerVersion("1.0");
        swaggerResource.setLocation(location);
        return swaggerResource;
    }

    private static String buildLocation(String name) {
        return "/" + name + "/v2/api-docs";
    }
}
