package cn.gjing.tools.aliyun.oss;

import cn.gjing.tools.aliyun.AliyunMeta;
import com.aliyun.oss.model.DeleteObjectsRequest;

import java.util.List;

/**
 * @author Gjing
 **/
public abstract class OssUpload {
    protected AliyunMeta aliyunMeta;
    protected OssMeta ossMeta;

    protected OssUpload(AliyunMeta aliyunMeta, OssMeta ossMeta) {
        this.aliyunMeta = aliyunMeta;
        this.ossMeta = ossMeta;
    }

    /**
     * Create oss bucket
     *
     * @param bucket bucket name
     */
    public void createBucket(String bucket) {
        try {
            if (!this.ossMeta.getOssClient(this.aliyunMeta).doesBucketExist(bucket)) {
                this.ossMeta.getOssClient(this.aliyunMeta).createBucket(bucket);
            }
        } catch (Exception e) {
            throw new IllegalStateException("创建Bucket失败,请核对Bucket名称(规则：只能包含小写字母、数字和短横线，必须以小写字母和数字开头和结尾，长度在3-63之间)");
        }
    }

    /**
     * Batch deletion of files
     *
     * @param fileNames Collection of oss file names
     * @return The set of oss file names that were successfully deleted
     */
    public List<String> deleteFiles(List<String> fileNames) {
        return this.deleteFiles(fileNames, this.ossMeta.getBucket());
    }

    /**
     * Batch deletion of files
     *
     * @param fileNames Collection of oss file names
     * @param bucket    Bucket
     * @return The set of oss file names that were successfully deleted
     */
    public List<String> deleteFiles(List<String> fileNames, String bucket) {
        if (fileNames.size() > 1000) {
            throw new IllegalArgumentException("No more than 1000 files are deleted at the same time");
        }
        return this.ossMeta.getOssClient(this.aliyunMeta).deleteObjects(new DeleteObjectsRequest(bucket).withKeys(fileNames)).getDeletedObjects();
    }

    /**
     * Delete the file
     *
     * @param fileName Oss file name
     */
    public void deleteFile(String fileName) {
        this.deleteFile(fileName, this.ossMeta.getBucket());
    }

    /**
     * Delete the file
     *
     * @param fileName Oss file name
     * @param bucket   Bucket
     */
    public void deleteFile(String fileName, String bucket) {
        this.ossMeta.getOssClient(this.aliyunMeta).deleteObject(bucket, fileName);
    }
}
