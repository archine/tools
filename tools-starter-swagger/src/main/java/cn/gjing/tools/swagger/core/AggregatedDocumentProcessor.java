package cn.gjing.tools.swagger.core;

import cn.gjing.tools.swagger.BaseAggregatedDocumentProcessor;
import cn.gjing.tools.swagger.DocGroup;
import cn.gjing.tools.swagger.GroupService;
import springfox.documentation.swagger.web.SwaggerResource;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Aggregate document handlers to generate target service access addresses
 *
 * @author Gjing
 **/
class AggregatedDocumentProcessor implements BaseAggregatedDocumentProcessor {
    @Resource
    private DocGroup resources;

    @Override
    public List<SwaggerResource> get() {
        List<SwaggerResource> swaggerResourceList = new ArrayList<>();
        SwaggerResource swaggerResource;
        List<GroupService> serviceList = this.resources.getServiceList();
        for (GroupService service : serviceList) {
            swaggerResource = new SwaggerResource();
            swaggerResource.setName(service.getDesc());
            swaggerResource.setSwaggerVersion("v3.0");
            swaggerResource.setLocation("/" + service.getTarget() + service.getContextPath() + service.getLocation());
            swaggerResourceList.add(swaggerResource);
        }
        return swaggerResourceList;
    }
}
