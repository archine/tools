package cn.gjing.tools.aliyun.oss;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Gjing
 **/
@Configuration
public class ToolsOssConfiguration {
    @Bean
    @ConditionalOnMissingBean(OssMeta.class)
    public OssMeta ossMeta() {
        return new OssMeta();
    }

    @Bean
    @ConditionalOnMissingBean(OssUpload.class)
    public OssUpload baseOssUpload(OssMeta ossMeta) {
        return new SimpleOssUpload(ossMeta);
    }

    @Bean
    @ConditionalOnMissingBean(OssDownload.class)
    public OssDownload ossDownload(OssMeta ossMeta) {
        return new OssDownload(ossMeta);
    }
}
