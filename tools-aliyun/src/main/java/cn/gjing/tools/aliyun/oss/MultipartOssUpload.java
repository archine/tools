package cn.gjing.tools.aliyun.oss;

import cn.gjing.tools.aliyun.AliyunMeta;
import cn.gjing.tools.aliyun.ToolsAliyunException;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;
import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Multipart upload
 *
 * @author Gjing
 **/
public class MultipartOssUpload extends OssUpload {
    private final long partSize;
    private final OSS ossClient;

    public MultipartOssUpload(AliyunMeta aliyunMeta, OssMeta ossMeta) {
        super(aliyunMeta, ossMeta);
        this.partSize = 1024 * 1024;
        this.ossClient = super.ossMeta.getOssClient(aliyunMeta);
    }

    /**
     * Upload file
     *
     * @param file file data
     * @return Oss file meta data
     */
    public FileMeta upload(MultipartFile file) {
        return this.upload(file, file.getOriginalFilename(), this.partSize, CannedAccessControlList.Default);
    }

    /**
     * Upload file
     *
     * @param file     file data
     * @param fileName Oss file meta data
     * @return Oss file meta data
     */
    public FileMeta upload(MultipartFile file, String fileName) {
        return this.upload(file, fileName, this.partSize, CannedAccessControlList.Default);
    }

    /**
     * Upload file
     *
     * @param file     file data
     * @param fileName Oss file meta data
     * @param bucket   bucket name
     * @return Oss file meta data
     */
    public FileMeta upload(MultipartFile file, String fileName, String bucket) {
        return this.upload(file, fileName, this.partSize, CannedAccessControlList.Default, bucket);
    }

    /**
     * Upload file
     *
     * @param file     file data
     * @param fileName Oss file meta data
     * @param partSize part size
     * @return Oss file meta data
     */
    public FileMeta upload(MultipartFile file, String fileName, long partSize) {
        return this.upload(file, fileName, partSize, CannedAccessControlList.Default);
    }

    /**
     * Upload file
     *
     * @param file     file data
     * @param fileName Oss file meta data
     * @param partSize part size
     * @param bucket   bucket name
     * @return Oss file meta data
     */
    public FileMeta upload(MultipartFile file, String fileName, long partSize, String bucket) {
        return this.upload(file, fileName, partSize, CannedAccessControlList.Default, bucket);
    }

    /**
     * Upload file
     *
     * @param file     file data
     * @param partSize part size
     * @return Oss file meta data
     */
    public FileMeta upload(MultipartFile file, long partSize) {
        return this.upload(file, file.getOriginalFilename(), partSize, CannedAccessControlList.Default);
    }

    /**
     * Upload file
     *
     * @param file     file data
     * @param partSize part size
     * @param bucket   bucket name
     * @return Oss file meta data
     */
    public FileMeta upload(MultipartFile file, long partSize, String bucket) {
        return this.upload(file, file.getOriginalFilename(), partSize, CannedAccessControlList.Default, bucket);
    }

    /**
     * Upload file
     *
     * @param file     file data
     * @param fileName Oss file meta data
     * @param partSize part size
     * @param access   Access permission
     * @return Oss file meta data
     */
    public FileMeta upload(MultipartFile file, String fileName, long partSize, CannedAccessControlList access) {
        try {
            return this.upload(file.getInputStream(), file.getSize(), fileName, partSize, access, super.ossMeta.getBucket());
        } catch (IOException e) {
            throw new ToolsAliyunException(e.getMessage());
        }
    }

    /**
     * Upload file
     *
     * @param file     file data
     * @param fileName Oss file meta data
     * @param partSize part size
     * @param access   Access permission
     * @param bucket   bucket name
     * @return Oss file meta data
     */
    public FileMeta upload(MultipartFile file, String fileName, long partSize, CannedAccessControlList access, String bucket) {
        try {
            return this.upload(file.getInputStream(), file.getSize(), fileName, partSize, access, bucket);
        } catch (IOException e) {
            throw new ToolsAliyunException(e.getMessage());
        }
    }

    /**
     * Upload file
     *
     * @param file file data
     * @return Oss file meta data
     */
    public FileMeta upload(File file) {
        return this.upload(file, file.getName(), this.partSize, CannedAccessControlList.Default);
    }

    /**
     * Upload file
     *
     * @param file     file data
     * @param fileName Oss file meta data
     * @return Oss file meta data
     */
    public FileMeta upload(File file, String fileName) {
        return this.upload(file, fileName, this.partSize, CannedAccessControlList.Default);
    }

    /**
     * Upload file
     *
     * @param file     file data
     * @param fileName Oss file meta data
     * @param bucket   bucket name
     * @return Oss file meta data
     */
    public FileMeta upload(File file, String fileName, String bucket) {
        return this.upload(file, fileName, this.partSize, CannedAccessControlList.Default, bucket);
    }

    /**
     * Upload file
     *
     * @param file     file data
     * @param fileName Oss file meta data
     * @param partSize part size
     * @return Oss file meta data
     */
    public FileMeta upload(File file, String fileName, long partSize) {
        return this.upload(file, fileName, partSize, CannedAccessControlList.Default);
    }

    /**
     * Upload file
     *
     * @param file     file data
     * @param fileName Oss file meta data
     * @param partSize part size
     * @param bucket   bucket name
     * @return Oss file meta data
     */
    public FileMeta upload(File file, String fileName, long partSize, String bucket) {
        return this.upload(file, fileName, partSize, CannedAccessControlList.Default, bucket);
    }

    /**
     * Upload file
     *
     * @param file     file data
     * @param partSize part size
     * @return Oss Oss file meta data
     */
    public FileMeta upload(File file, long partSize) {
        return this.upload(file, file.getName(), partSize, CannedAccessControlList.Default);
    }

    /**
     * Upload file
     *
     * @param file     file data
     * @param partSize part size
     * @param bucket   bucket name
     * @return Oss Oss file meta data
     */
    public FileMeta upload(File file, long partSize, String bucket) {
        return this.upload(file, file.getName(), partSize, CannedAccessControlList.Default, bucket);
    }

    /**
     * Upload file
     *
     * @param file     file data
     * @param fileName Oss file meta data
     * @param partSize part size
     * @param access   Access permission
     * @return Oss file meta data
     */
    public FileMeta upload(File file, String fileName, long partSize, CannedAccessControlList access) {
        try {
            return this.upload(new FileInputStream(file), file.length(), fileName, partSize, access, super.ossMeta.getBucket());
        } catch (IOException e) {
            throw new ToolsAliyunException(e.getMessage());
        }
    }

    /**
     * Upload file
     *
     * @param file     file data
     * @param fileName Oss file meta data
     * @param partSize part size
     * @param access   Access permission
     * @param bucket   bucket name
     * @return Oss file meta data
     */
    public FileMeta upload(File file, String fileName, long partSize, CannedAccessControlList access, String bucket) {
        try {
            return this.upload(new FileInputStream(file), file.length(), fileName, partSize, access, bucket);
        } catch (IOException e) {
            throw new ToolsAliyunException(e.getMessage());
        }
    }

    /**
     * Upload file
     *
     * @param fileData   file data
     * @param fileLength file length
     * @param fileName   file name
     * @param access     Access permission
     * @return Oss file meta data
     */
    public FileMeta upload(InputStream fileData, long fileLength, String fileName, CannedAccessControlList access) {
        return this.upload(fileData, fileLength, fileName, this.partSize, access);
    }

    /**
     * Upload file
     *
     * @param fileData   file data
     * @param fileLength file length
     * @param fileName   file name
     * @return Oss file meta data
     */
    public FileMeta upload(InputStream fileData, long fileLength, String fileName) {
        return this.upload(fileData, fileLength, fileName, this.partSize, CannedAccessControlList.Default);
    }

    /**
     * Upload file
     *
     * @param fileData   file data
     * @param fileLength file length
     * @param fileName   file name
     * @param partSize   part size
     * @return Oss file meta data
     */
    public FileMeta upload(InputStream fileData, long fileLength, String fileName, long partSize) {
        return this.upload(fileData, fileLength, fileName, partSize, CannedAccessControlList.Default);
    }

    /**
     * Upload file
     *
     * @param fileData   file data
     * @param fileLength file length
     * @param fileName   file name
     * @param partSize   part size
     * @param access     Access permission
     * @return Oss file meta data
     */
    public FileMeta upload(InputStream fileData, long fileLength, String fileName, long partSize, CannedAccessControlList access) {
        return this.upload(fileData, fileLength, fileName, partSize, access, super.ossMeta.getBucket());
    }

    /**
     * Upload file
     *
     * @param fileData   file data
     * @param fileLength file length
     * @param fileName   file name
     * @param partSize   part size
     * @param access     Access permission
     * @param bucket     bucket name
     * @return Oss file meta data
     */
    public FileMeta upload(InputStream fileData, long fileLength, String fileName, long partSize, CannedAccessControlList access, String bucket) {

        try {
            return this.upload(IOUtils.toByteArray(fileData), fileLength, fileName, partSize, access, bucket);
        } catch (IOException e) {
            throw new ToolsAliyunException(e.getMessage());
        }
    }

    /**
     * Upload file
     *
     * @param fileBytes  file data
     * @param fileLength file length
     * @param fileName   file name
     * @param partSize   part size
     * @param access     Access permission
     * @param bucket     bucket name
     * @return Oss file meta data
     */
    private FileMeta upload(byte[] fileBytes, long fileLength, String fileName, long partSize, CannedAccessControlList access, String bucket) {
        this.createBucket(bucket);
        InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucket, fileName);
        InitiateMultipartUploadResult result = this.ossClient.initiateMultipartUpload(request);
        String uploadId = result.getUploadId();
        List<PartETag> partETags = new ArrayList<>();
        int partCount = (int) (fileLength / partSize);
        if (fileLength % partSize != 0) {
            partCount++;
        }
        UploadPartRequest uploadPartRequest;
        try {
            for (int i = 0; i < partCount; i++) {
                long startPos = i * partSize;
                long currentPartSize = (i + 1 == partCount) ? (fileLength - startPos) : partSize;
                InputStream fileData = new ByteArrayInputStream(fileBytes);
                fileData.skip(startPos);
                uploadPartRequest = new UploadPartRequest();
                uploadPartRequest.setBucketName(super.ossMeta.getBucket());
                uploadPartRequest.setKey(fileName);
                uploadPartRequest.setUploadId(uploadId);
                uploadPartRequest.setInputStream(fileData);
                uploadPartRequest.setPartSize(currentPartSize);
                uploadPartRequest.setPartNumber(i + 1);
                UploadPartResult uploadPartResult = this.ossClient.uploadPart(uploadPartRequest);
                partETags.add(uploadPartResult.getPartETag());
            }
        } catch (IOException e) {
            throw new ToolsAliyunException(e.getMessage());
        }
        CompleteMultipartUploadRequest completeMultipartUploadRequest =
                new CompleteMultipartUploadRequest(super.ossMeta.getBucket(), fileName, uploadId, partETags);
        completeMultipartUploadRequest.setObjectACL(access);
        this.ossClient.completeMultipartUpload(completeMultipartUploadRequest);
        return new FileMeta(fileName, bucket, uploadId);
    }

    /**
     * Cancel the Sharding upload event
     * <p>
     * When a shard upload event is canceled,
     * uploadId cannot be used to do anything else,
     * and the uploaded shard data will be deleted
     *
     * @param fileMeta Sharding is returned after uploading successfully
     */
    public void cancel(FileMeta fileMeta) {
        AbortMultipartUploadRequest abortMultipartUploadRequest =
                new AbortMultipartUploadRequest(fileMeta.getBucket(), fileMeta.getFileName(), fileMeta.getUploadId());
        try {
            ossClient.abortMultipartUpload(abortMultipartUploadRequest);
        } catch (OSSException | ClientException e) {
            throw new ToolsAliyunException(e.getMessage());
        }
    }
}
