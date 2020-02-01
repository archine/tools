package cn.gjing.tools.aliyun;

import cn.gjing.tools.aliyun.oss.OssDownload;
import cn.gjing.tools.aliyun.oss.OssMeta;
import cn.gjing.tools.aliyun.oss.OssUpload;
import cn.gjing.tools.aliyun.oss.SimpleOssUpload;
import cn.gjing.tools.aliyun.sms.SmsHelper;
import cn.gjing.tools.aliyun.sms.SmsMeta;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Gjing
 **/
@Configuration
class ToolsAliyunConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public AliyunMeta aliyunMeta() {
        return new AliyunMeta();
    }

    @Bean
    @ConditionalOnMissingBean
    public OssMeta ossMeta() {
        return new OssMeta();
    }

    @Bean
    @ConditionalOnMissingBean
    public SmsMeta smsMeta() {
        return new SmsMeta();
    }

    @Bean
    @ConditionalOnMissingBean(OssUpload.class)
    public OssUpload baseOssUpload(OssMeta ossMeta, AliyunMeta aliyunMeta) {
        return new SimpleOssUpload(ossMeta,aliyunMeta);
    }

    @Bean
    @ConditionalOnMissingBean(OssDownload.class)
    public OssDownload ossDownload(OssMeta ossMeta,AliyunMeta aliyunMeta) {
        return new OssDownload(ossMeta,aliyunMeta);
    }

    @Bean
    @ConditionalOnMissingBean(SmsHelper.class)
    public SmsHelper smsHelper() {
        return new SmsHelper(aliyunMeta(), smsMeta());
    }
}
