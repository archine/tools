package com.gjing.utils.ali.oss;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Archine
 **/
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class OssModel {
    /**
     * 域名
     */
    private String endPoint;

    /**
     * 账号
     */
    private String accessKeyId;

    /**
     * 密匙
     */
    private String accessKeySecret;

    /**
     * 存储空间名称
     */
    private String bucketName;
}
