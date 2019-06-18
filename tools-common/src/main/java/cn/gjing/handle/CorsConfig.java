package cn.gjing.handle;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * @author Gjing
 **/
@Configuration
public class CorsConfig {
    @Resource
    private CorsBean corsBean;

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(corsBean.getAllowCredentials());
        corsConfiguration.setAllowedHeaders(Arrays.asList(corsBean.getAllowedHeaders()));
        corsConfiguration.setAllowedOrigins(Arrays.asList(corsBean.getAllowedOrigins()));
        corsConfiguration.setAllowedMethods(Arrays.asList(corsBean.getAllowedMethods()));
        source.registerCorsConfiguration(corsBean.getPath(), corsConfiguration);
        return new CorsFilter(source);
    }
}
