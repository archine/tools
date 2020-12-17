package cn.gjing.tools.aliyun.oss;

import cn.gjing.tools.aliyun.AliyunMeta;
import cn.gjing.tools.aliyun.ToolsAliyunException;
import com.aliyun.oss.OSS;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Simple upload
 *
 * @author Gjing
 **/
public final class SimpleOssUpload extends OssUpload {
    private final OSS ossClient;

    public SimpleOssUpload(OssMeta ossMeta, AliyunMeta aliyunMeta) {
        super(aliyunMeta, ossMeta);
        this.ossClient = super.ossMeta.getOssClient(aliyunMeta);
    }

    /**
     * Upload the file
     *
     * @param file File data
     * @return Oss file name
     */
    public String upload(File file) {
        try {
            return this.upload(new FileInputStream(file), file.getName(), super.ossMeta.getBucket());
        } catch (IOException e) {
            throw new ToolsAliyunException(e.getMessage());
        }
    }

    /**
     * Upload the file
     *
     * @param file     File data
     * @param fileName Custom file name
     * @return Oss file name
     */
    public String upload(File file, String fileName) {
        try {
            return this.upload(new FileInputStream(file), fileName, super.ossMeta.getBucket());
        } catch (IOException e) {
            throw new ToolsAliyunException(e.getMessage());
        }
    }

    /**
     * Upload the file
     *
     * @param file     File data
     * @param fileName Custom file name
     * @return Oss file name
     */
    public String upload(File file, String fileName, String bucket) {
        try {
            return this.upload(new FileInputStream(file), fileName, bucket);
        } catch (IOException e) {
            throw new ToolsAliyunException(e.getMessage());
        }
    }

    /**
     * Upload the file
     *
     * @param file File data
     * @return Oss file name
     */
    public String upload(MultipartFile file) {
        try {
            return this.upload(file.getInputStream(), file.getOriginalFilename(), super.ossMeta.getBucket());
        } catch (IOException e) {
            throw new ToolsAliyunException(e.getMessage());
        }
    }

    /**
     * Upload the file
     *
     * @param file     File data
     * @param fileName Custom file name
     * @return Oss file name
     */
    public String upload(MultipartFile file, String fileName) {
        try {
            return this.upload(file.getInputStream(), fileName, super.ossMeta.getBucket());
        } catch (IOException e) {
            throw new ToolsAliyunException(e.getMessage());
        }
    }

    /**
     * Upload the file
     *
     * @param file     File data
     * @param fileName Custom file name
     * @return Oss file name
     */
    public String upload(MultipartFile file, String fileName, String bucket) {
        try {
            return this.upload(file.getInputStream(), fileName, bucket);
        } catch (IOException e) {
            throw new ToolsAliyunException(e.getMessage());
        }
    }

    /**
     * Upload the file
     *
     * @param fileData File data
     * @param fileName Custom file name.
     * @param bucket   Bucket name
     * @return Oss file name
     */
    public String upload(InputStream fileData, String fileName, String bucket) {
        this.createBucket(bucket);
        this.ossClient.putObject(super.ossMeta.getBucket(), fileName, fileData);
        return fileName;
    }
}
