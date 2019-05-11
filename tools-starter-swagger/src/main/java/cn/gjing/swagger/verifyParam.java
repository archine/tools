package cn.gjing.swagger;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * @author Gjing
 **/
@Slf4j
class verifyParam {
    static boolean verify(String param) {
        if (StringUtils.isEmpty(param)) {
            log.error("Swagger basePackage value is default , Please set your own project interface path");
            return true;
        }
        return false;
    }
}
