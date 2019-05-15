package cn.gjing;

import lombok.*;

/**
 * @author Gjing
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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



