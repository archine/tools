package cn.gjing.ali.sms;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

/**
 * @author Gjing
 **/
@AllArgsConstructor
@Data
@Builder
public class AliSmsModel {
    /**
     * 主账号AccessKey的ID
     */
    @NonNull
    private String accessKeyId;
    /**
     * 主账号AccessKey对应的secret
     */
    @NonNull
    private String accessKeySecret;
}
