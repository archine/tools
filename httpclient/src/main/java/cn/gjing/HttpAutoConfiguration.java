package cn.gjing;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Gjing
 **/
@Configuration
public class HttpAutoConfiguration {
    @Bean
    public HttpClient httpClient() {
        return new HttpClient();
    }
}
