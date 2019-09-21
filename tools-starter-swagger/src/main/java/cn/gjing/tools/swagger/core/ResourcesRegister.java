package cn.gjing.tools.swagger.core;

import cn.gjing.tools.swagger.SwaggerResources;
import cn.gjing.tools.swagger.SwaggerService;
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
class ResourcesRegister implements SwaggerResourcesProvider {

    private final static String SWAGGER_PATH = "/v2/api-docs";
    @Value("${spring.application.name:default}")
    private String applicationName;
    @Resource
    private SwaggerResources swaggerResources;

    @Override
    public List<SwaggerResource> get() {
        List<SwaggerResource> resourceList = new ArrayList<>();
        List<Map<String, SwaggerService>> applicationList = swaggerResources.getServiceList();
        if (swaggerResources.isEnable()) {
            if (swaggerResources.isRegisterMe()) {
                registerMe(resourceList);
            } else {
                if (applicationList.isEmpty()) {
                    throw new IllegalArgumentException("Swagger resources serveList cannot be empty, " +
                            "Please set register-me to true or add other serve name");
                }
            }
            for (Map<String, SwaggerService> applicationMap : applicationList) {
                for (String serveName : applicationMap.keySet()) {
                    if (StringUtils.isEmpty(serveName) || applicationMap.get(serveName).getService() == null) {
                        continue;
                    }
                    resourceList.add(swaggerResource(applicationMap.get(serveName).getView() == null
                                    ? serveName : applicationMap.get(serveName).getView(),
                            buildLocation(applicationMap.get(serveName).getService())));
                }
            }
            return resourceList;
        }
        registerMe(resourceList);
        return resourceList;
    }

    /**
     * 配置swagger的资源
     * @param name 展示名
     * @param location 服务文档地址
     * @return swaggerResource
     */
    private SwaggerResource swaggerResource(String name, String location) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setSwaggerVersion("2.0");
        swaggerResource.setLocation(location);
        return swaggerResource;
    }

    /**
     * 注册当前项目本身
     * @param resourceList 资源列表
     */
    private void registerMe(List<SwaggerResource> resourceList) {
        resourceList.add(swaggerResource(applicationName, SWAGGER_PATH));
    }

    /**
     * 构建地址
     * @param name 服务名
     * @return /服务名/swagger_path
     */
    private static String buildLocation(String name) {
        return "/" + name + SWAGGER_PATH;
    }
}
