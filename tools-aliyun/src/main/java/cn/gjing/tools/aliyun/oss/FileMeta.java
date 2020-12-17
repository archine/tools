package cn.gjing.tools.aliyun.oss;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Gjing
 **/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileMeta {
    /**
     * Oss file name
     */
    private String fileName;
    /**
     * Bucket name
     */
    private String bucket;
    /**
     * upload id
     */
    private String uploadId;
}
