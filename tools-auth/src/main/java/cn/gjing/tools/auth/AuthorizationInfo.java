package cn.gjing.tools.auth;

import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * Authorization meta info
 *
 * @author Gjing
 **/
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "snow.auth")
public class AuthorizationInfo implements Serializable {
    /**
     * 加密盐
     */
    private String salt = "2b93fbdf27d43547bec8794054c28e00";

    /**
     * 需要校验的方法路径
     */
    private String[] path = {"/**"};

    /**
     * 请求头名称
     */
    private String header = "Authorization";

    /**
     * 排除校验的方法路径
     */
    private String[] filter = {"/error", "/swagger-resources/**", "/v2/**", "/webjars/**", "/login", "/logout"};

    /**
     * 加密方式
     */
    private SignatureAlgorithm type = SignatureAlgorithm.HS256;
}
