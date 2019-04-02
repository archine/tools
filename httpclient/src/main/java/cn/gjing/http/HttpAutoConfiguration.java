package cn.gjing.http;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Gjing
 **/
@Configuration
public class HttpAutoConfiguration {
    @Bean
    public HttpRequest httpRequest() {
        return new HttpRequest();
    }
}
