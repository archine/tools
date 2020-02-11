package cn.gjing.tools.common.cors;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
class CommonCorsConfiguration {
    @Resource
    private CommonCors commonCors;

    @Bean
    @ConditionalOnMissingBean(name = "corsFilter")
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(commonCors.getAllowCredentials());
        corsConfiguration.setAllowedHeaders(Arrays.asList(commonCors.getAllowedHeaders()));
        corsConfiguration.setAllowedOrigins(Arrays.asList(commonCors.getAllowedOrigins()));
        corsConfiguration.setAllowedMethods(Arrays.asList(commonCors.getAllowedMethods()));
        source.registerCorsConfiguration(commonCors.getPath(), corsConfiguration);
        return new CorsFilter(source);
    }
}
