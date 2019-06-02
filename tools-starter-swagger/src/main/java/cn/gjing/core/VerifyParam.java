package cn.gjing.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;


/**
 * @author Gjing
 **/
@Slf4j
class VerifyParam {
    static boolean isDefault(String param) {
        if (StringUtils.isEmpty(param)) {
            log.warn("Swagger basePackage value is default , Please set your own project interface path");
            return true;
        }
        return false;
    }
}
