package cn.gjing.tools.aliyun.oss;

import cn.gjing.tools.aliyun.AliyunMeta;
import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.DeleteObjectsRequest;
import com.aliyun.oss.model.PutObjectRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

/**
 * 简单上传
 *
 * @author Gjing
 **/
public final class SimpleOssUpload implements OssUpload {
    private OSS ossClient;
    private OssMeta ossMeta;
    private AliyunMeta aliyunMeta;

    public SimpleOssUpload(OssMeta ossMeta, AliyunMeta aliyunMeta) {
        this.ossMeta = ossMeta;
        this.aliyunMeta = aliyunMeta;
        this.ossInit();
    }

    @Override
    public List<String> deleteFiles(List<String> fileUrls) {
        if (fileUrls.size() > 1000) {
            throw new IllegalArgumentException("最多同时删除1000个");
        }
        return this.ossClient.deleteObjects(new DeleteObjectsRequest(this.ossMeta.getBucket()).withKeys(fileUrls)).getDeletedObjects();
    }

    @Override
    public void deleteFile(String fileName) {
        this.ossClient.deleteObject(this.ossMeta.getBucket(), fileName);
    }

    @Override
    public String upload(MultipartFile file) {
        return this.upload(file, "");
    }

    @Override
    public String upload(MultipartFile file, String dir) {
        this.createBucket();
        if (file.getOriginalFilename() == null) {
            throw new NullPointerException("文件名不能为空");
        }
        if (!"".equals(dir)) {
            dir = dir + "/";
        }
        String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        String fileName = String.format("%s%s", dir + UUID.randomUUID().toString().replaceAll("-", ""), extension);
        try {
            this.ossClient.putObject(new PutObjectRequest(this.ossMeta.getBucket(), fileName, file.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileName;
    }

    @Override
    public String upload(InputStream file, String fileName) {
        this.createBucket();
        this.ossClient.putObject(this.ossMeta.getBucket(), fileName, file);
        return fileName;
    }

    @Override
    public String upload(byte[] file, String fileName) {
        this.createBucket();
        this.ossClient.putObject(this.ossMeta.getBucket(), fileName, new ByteArrayInputStream(file));
        return fileName;
    }

    private void ossInit() {
        ClientBuilderConfiguration conf = new ClientBuilderConfiguration();
        conf.setMaxConnections(this.ossMeta.getMaxConnections());
        conf.setSocketTimeout(this.ossMeta.getSocketTimeout());
        conf.setConnectionTimeout(this.ossMeta.getConnectionTimeout());
        conf.setIdleConnectionTime(this.ossMeta.getIdleTime());
        this.ossClient = new OSSClientBuilder().build(this.ossMeta.getEndPoint(), this.aliyunMeta.getAccessKey(), this.aliyunMeta.getAccessKeySecret(), conf);
    }

    private void createBucket() {
        try {
            if (!ossClient.doesBucketExist(this.ossMeta.getBucket())) {
                this.ossClient.createBucket(this.ossMeta.getBucket());
            }
        } catch (Exception e) {
            throw new IllegalStateException("创建Bucket失败,请核对Bucket名称(规则：只能包含小写字母、数字和短横线，必须以小写字母和数字开头和结尾，长度在3-63之间)");
        }
    }
}
