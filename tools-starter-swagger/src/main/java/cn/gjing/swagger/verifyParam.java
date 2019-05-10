package cn.gjing.swagger;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * @author Gjing
 **/
@Slf4j
public class verifyParam {
    public static void verify(String param) {
        if (StringUtils.isEmpty(param)) {
            log.error("Swagger basePackage value is default , Please set your own project interface path");
        }
    }
}
