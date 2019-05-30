package cn.gjing.core;

import cn.gjing.Resources;
import cn.gjing.Serve;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.util.StringUtils;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Gjing
 **/
@Primary
class ResourcesConfig implements SwaggerResourcesProvider {

    private final static String SWAGGER_PATH = "/v2/api-docs";
    @Value("${spring.application.name:default}")
    private String applicationName;
    @Resource
    private Resources resources;

    @Override
    public List<SwaggerResource> get() {
        List<SwaggerResource> resourceList = new ArrayList<>();
        List<Map<String, Serve>> serveList = resources.getServeList();
        if (resources.isEnable()) {
            if (resources.isRegisterMe()) {
                registerMe(resourceList);
            } else {
                if (serveList.isEmpty()) {
                    throw new IllegalArgumentException("Swagger resources serveList cannot be empty, " +
                            "Please set register-me to true or add other serve name");
                }
            }
            for (Map<String, Serve> serveMap : serveList) {
                for (String serveName : serveMap.keySet()) {
                    if (StringUtils.isEmpty(serveName) || serveMap.get(serveName).getLocation() == null) {
                        continue;
                    }
                    resourceList.add(swaggerResource(serveMap.get(serveName).getName() == null
                                    ? serveName : serveMap.get(serveName).getName(),
                            buildLocation(serveMap.get(serveName).getLocation())));
                }
            }
            return resourceList;
        }
        registerMe(resourceList);
        return resourceList;
    }

    private SwaggerResource swaggerResource(String name, String location) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setSwaggerVersion("2.0");
        swaggerResource.setLocation(location);
        return swaggerResource;
    }

    private void registerMe(List<SwaggerResource> resourceList) {
        resourceList.add(swaggerResource(applicationName, SWAGGER_PATH));
    }

    private static String buildLocation(String name) {
        if (name.endsWith(SWAGGER_PATH)) {
            return name;
        }
        return "/" + name + SWAGGER_PATH;
    }
}
