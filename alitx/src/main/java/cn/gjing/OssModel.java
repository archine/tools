package cn.gjing;

import lombok.*;

/**
 * @author Gjing
 **/
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class OssModel {
    /**
     * 域名
     */
    @NonNull
    private String endPoint;

    /**
     * 账号
     */
    @NonNull
    private String accessKeyId;

    /**
     * 密匙
     */
    @NonNull
    private String accessKeySecret;

    /**
     * 存储空间名称
     */
    @NonNull
    private String bucketName;

    /**
     * 文件目录
     */
    private String fileDir;
}
