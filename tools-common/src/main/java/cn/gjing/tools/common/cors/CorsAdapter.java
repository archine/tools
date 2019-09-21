package cn.gjing.tools.common.cors;

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
class CorsAdapter {
    @Resource
    private Cors cors;

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(cors.getAllowCredentials());
        corsConfiguration.setAllowedHeaders(Arrays.asList(cors.getAllowedHeaders()));
        corsConfiguration.setAllowedOrigins(Arrays.asList(cors.getAllowedOrigins()));
        corsConfiguration.setAllowedMethods(Arrays.asList(cors.getAllowedMethods()));
        source.registerCorsConfiguration(cors.getPath(), corsConfiguration);
        return new CorsFilter(source);
    }
}
