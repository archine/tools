package cn.gjing.tools.swagger.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;


/**
 * @author Gjing
 **/
class VerifyParam {
    static boolean isDefault(String param) {
        if (StringUtils.isEmpty(param)) {
            Logger logger = LoggerFactory.getLogger(VerifyParam.class);
            logger.warn("Swagger basePackage value is default , Please set your own project interface path");
            return true;
        }
        return false;
    }
}
