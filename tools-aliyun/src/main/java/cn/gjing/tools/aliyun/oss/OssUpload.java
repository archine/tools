package cn.gjing.tools.aliyun.oss;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

/**
 * @author Gjing
 **/
public interface OssUpload {
    /**
     * Batch deletion of files
     *
     * @param fileNames Collection of oss file names
     * @return The set of oss file names that were successfully deleted
     */
    List<String> deleteFiles(List<String> fileNames);

    /**
     * Batch deletion of files
     *
     * @param fileNames Collection of oss file names
     * @param bucket    Bucket
     * @return The set of oss file names that were successfully deleted
     */
    List<String> deleteFiles(List<String> fileNames, String bucket);

    /**
     * Delete the file
     *
     * @param fileName Oss file name
     */
    void deleteFile(String fileName);

    /**
     * Delete the file
     *
     * @param fileName Oss file name
     * @param bucket   Bucket
     */
    void deleteFile(String fileName, String bucket);

    /**
     * Upload the file
     *
     * @param file MultipartFile
     * @return Oss file name
     */
    String upload(MultipartFile file);

    /**
     * Upload the file
     *
     * @param file MultipartFile
     * @param dir  Oss storage directory
     * @return Oss file name
     */
    String upload(MultipartFile file, String dir);

    /**
     * Upload the file
     *
     * @param file   MultipartFile
     * @param dir    Oss storage directory
     * @param bucket Bucket
     * @return Oss file name
     */
    String upload(MultipartFile file, String dir, String bucket);

    /**
     * Upload the file
     *
     * @param file     MultipartFile
     * @param fileName Custom file name. If the Oss file name change already exists, the original file will be overwritten
     * @return Oss file name
     */
    String upload(InputStream file, String fileName);

    /**
     * Upload the file
     *
     * @param file     MultipartFile
     * @param fileName Custom file name. If the Oss file name change already exists, the original file will be overwritten
     * @param bucket   Bucket
     * @return Oss file name
     */
    String upload(InputStream file, String fileName, String bucket);

    /**
     * Upload the file
     *
     * @param file     File byte array
     * @param fileName Custom file name. If the Oss file name change already exists, the original file will be overwritten
     * @return Oss file name
     */
    String upload(byte[] file, String fileName);

    /**
     * Upload the file
     *
     * @param file     File byte array
     * @param fileName Custom file name. If the Oss file name change already exists, the original file will be overwritten
     * @param bucket   Bucket
     * @return Oss file name
     */
    String upload(byte[] file, String fileName, String bucket);
}
