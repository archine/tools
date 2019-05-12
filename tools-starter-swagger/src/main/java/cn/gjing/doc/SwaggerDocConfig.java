package cn.gjing.doc;

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
@SuppressWarnings("unchecked")
@Primary
public class SwaggerDocConfig implements SwaggerResourcesProvider {
    @Value("${spring.application.name:default}")
    private String applicationName;
    @Resource
    private SwaggerDoc swaggerDoc;

    @Override
    public List<SwaggerResource> get() {
        List<Map<String, SwaggerDoc.detail>> docList = swaggerDoc.getDocList();
        boolean isEmpty = docList.isEmpty();
        List resources = new ArrayList<>();
        if (swaggerDoc.isRegisterMe()) {
            resources.add(swaggerResource(applicationName, "/v2/api-docs", "1.0"));
        } else {
            if (isEmpty) {
                throw new IllegalArgumentException("Swagger service list cannot be empty, please set register-me to true or add other services swagger path");
            }
        }
        if (!isEmpty) {
            for (Map<String, SwaggerDoc.detail> map : docList) {
                for (String name : map.keySet()) {
                    if (StringUtils.isEmpty(name) || StringUtils.isEmpty(map.get(name).getLocation())) {
                        continue;
                    }
                    resources.add(swaggerResource(name, map.get(name).getLocation(), map.get(name).getVersion()));
                }
            }
        }

        return resources;
    }

    private SwaggerResource swaggerResource(String name, String location, String version) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setSwaggerVersion(version);
        swaggerResource.setLocation(location);
        return swaggerResource;
    }
}
