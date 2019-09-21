package cn.gjing.tools.feign;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Gjing
 **/
@Component
class FeignProcess {
    @Resource
    private FeignService feignService;

    FeignBean getByName(String targetName) {
        return feignService.newInstanceByName(targetName);
    }

    FeignBean getByUrl(String targetUrl) {
        return this.feignService.newInstanceByUrl(targetUrl);
    }
}
