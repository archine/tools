package cn.gjing.tools.aliyun.oss;

import cn.gjing.tools.aliyun.AliyunMeta;
import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.DeleteObjectsRequest;
import com.aliyun.oss.model.PutObjectRequest;
import org.springframework.util.StringUtils;
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
    private final OssMeta ossMeta;
    private final AliyunMeta aliyunMeta;

    public SimpleOssUpload(OssMeta ossMeta, AliyunMeta aliyunMeta) {
        this.ossMeta = ossMeta;
        this.aliyunMeta = aliyunMeta;
        this.ossInit();
    }

    @Override
    public List<String> deleteFiles(List<String> fileNames) {
        return this.deleteFiles(fileNames, this.ossMeta.getBucket());
    }

    @Override
    public List<String> deleteFiles(List<String> fileNames, String bucket) {
        this.createBucket(bucket);
        if (fileNames.size() > 1000) {
            throw new IllegalArgumentException("No more than 1000 files are deleted at the same time");
        }
        return this.ossClient.deleteObjects(new DeleteObjectsRequest(bucket).withKeys(fileNames)).getDeletedObjects();
    }

    @Override
    public void deleteFile(String fileName) {
        this.deleteFile(fileName, this.ossMeta.getBucket());
    }

    @Override
    public void deleteFile(String fileName, String bucket) {
        this.createBucket(bucket);
        this.ossClient.deleteObject(bucket, fileName);
    }

    @Override
    public String upload(MultipartFile file) {
        return this.upload(file, "");
    }

    @Override
    public String upload(MultipartFile file, String dir) {
        return this.upload(file, dir, this.ossMeta.getBucket());
    }

    @Override
    public String upload(MultipartFile file, String dir, String bucket) {
        this.createBucket(bucket);
        if (file.getOriginalFilename() == null) {
            throw new NullPointerException("The file name cannot be empty");
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
        return this.upload(file, fileName, this.ossMeta.getBucket());
    }

    @Override
    public String upload(InputStream file, String fileName, String bucket) {
        this.createBucket(bucket);
        this.ossClient.putObject(this.ossMeta.getBucket(), fileName, file);
        return fileName;
    }

    @Override
    public String upload(byte[] file, String fileName) {
        return this.upload(file, fileName, this.ossMeta.getBucket());
    }

    @Override
    public String upload(byte[] file, String fileName, String bucket) {
        this.createBucket(bucket);
        this.ossClient.putObject(this.ossMeta.getBucket(), fileName, new ByteArrayInputStream(file));
        return fileName;
    }

    private void ossInit() {
        ClientBuilderConfiguration conf = new ClientBuilderConfiguration();
        conf.setMaxConnections(this.ossMeta.getMaxConnections());
        conf.setSocketTimeout(this.ossMeta.getSocketTimeout());
        conf.setConnectionTimeout(this.ossMeta.getConnectionTimeout());
        conf.setIdleConnectionTime(this.ossMeta.getIdleTime());
        this.ossClient = new OSSClientBuilder().build(this.ossMeta.getEndPoint(), StringUtils.isEmpty(this.ossMeta.getAccessKey()) ? this.aliyunMeta.getAccessKey() : this.ossMeta.getAccessKey(),
                StringUtils.isEmpty(this.ossMeta.getAccessKeySecret()) ? this.aliyunMeta.getAccessKeySecret() : this.ossMeta.getAccessKeySecret(), conf);
    }

    private void createBucket(String bucket) {
        try {
            if (!ossClient.doesBucketExist(bucket)) {
                this.ossClient.createBucket(bucket);
            }
        } catch (Exception e) {
            throw new IllegalStateException("创建Bucket失败,请核对Bucket名称(规则：只能包含小写字母、数字和短横线，必须以小写字母和数字开头和结尾，长度在3-63之间)");
        }
    }
}
