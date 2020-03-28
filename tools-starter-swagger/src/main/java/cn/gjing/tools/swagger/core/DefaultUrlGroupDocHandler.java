package cn.gjing.tools.swagger.core;

import cn.gjing.tools.swagger.DocGroup;
import cn.gjing.tools.swagger.DocGroupHandler;
import cn.gjing.tools.swagger.GroupService;
import springfox.documentation.swagger.web.SwaggerResource;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Gjing
 **/
class DefaultUrlGroupDocHandler implements DocGroupHandler {
    @Resource
    private DocGroup resources;

    @Override
    public List<SwaggerResource> get() {
        List<SwaggerResource> swaggerResourceList = new ArrayList<>();
        if (!this.resources.isEnable()) {
            return swaggerResourceList;
        }
        List<GroupService> serviceList = resources.getServiceList();
        SwaggerResource resource;
        for (GroupService service : serviceList) {
            resource = new SwaggerResource();
            resource.setLocation(service.getUrl().lastIndexOf("/") == -1 ? service.getUrl() + "/v2/api-docs" : service.getUrl() + "v2/api-docs");
            resource.setName(service.getDesc());
            resource.setSwaggerVersion("2.0");
            swaggerResourceList.add(resource);
        }
        return swaggerResourceList;
    }
}
