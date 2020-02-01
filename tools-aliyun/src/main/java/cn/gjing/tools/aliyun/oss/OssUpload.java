package cn.gjing.tools.aliyun.oss;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

/**
 * @author Gjing
 **/
public interface OssUpload {
    /**
     * 批量删除文件
     * @param fileNames oss文件名集合
     * @return 被删除成功的oss文件名集合
     */
    List<String> deleteFiles(List<String> fileNames);

    /**
     * 删除文件
     * @param fileName oss文件名
     */
    void deleteFile(String fileName);

    /**
     * 上传
     * @param file 文件对象
     * @return oss文件名
     */
    String upload(MultipartFile file);

    /**
     * 上传
     * @param file 文件对象
     * @param dir 目录
     * @return oss文件名
     */
    String upload(MultipartFile file,String dir);

    /**
     * 上传
     * @param file 文件流
     * @param fileName oss文件名
     * @return oss文件名
     */
    String upload(InputStream file,String fileName);

    /**
     * 上传
     * @param file byte数组
     * @param fileName oss文件名
     * @return oss文件名
     */
    String upload(byte[] file,String fileName);
}
