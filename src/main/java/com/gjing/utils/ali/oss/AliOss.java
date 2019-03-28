package com.gjing.utils.ali.oss;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.DeleteObjectsRequest;
import com.aliyun.oss.model.ObjectMetadata;
import com.gjing.enums.HttpStatus;
import com.gjing.ex.OssException;
import com.gjing.utils.ParamUtil;
import com.gjing.utils.TimeUtil;
import lombok.Getter;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Archine
 **/
public class AliOss {
    /**
     * 实例
     */
    private static OSSClient instance = null;

    /**
     * Oss 实例化
     *
     * @return 实例
     */
    private static OSSClient getOssClient(OssModel ossModel) {
        if (instance == null) {
            synchronized (AliOss.class) {
                if (instance == null) {
                    instance = new OSSClient(ossModel.getEndPoint(), ossModel.getAccessKeyId(), ossModel.getAccessKeySecret());
                }
            }
        }
        return instance;
    }

    /**
     * 当Bucket 不存在时候创建Bucket
     */
    private static void createBucket(OssModel ossModel) {
        try {
            if (!AliOss.getOssClient(ossModel).doesBucketExist(ossModel.getBucketName())) {
                AliOss.getOssClient(ossModel).createBucket(ossModel.getBucketName());
            }
        } catch (Exception e) {
            throw new OssException("创建Bucket失败,请核对Bucket名称(规则：只能包含小写字母、数字和短横线，必须以小写字母和数字开头和结尾，长度在3-63之间)");
        }
    }

    /**
     * 文件上传的文件后缀
     *
     * @param filenameExtension 文件扩展名
     * @return ''
     */
    private static String getContentType(String filenameExtension) {
        if (filenameExtension.equalsIgnoreCase("jpeg") ||
                filenameExtension.equalsIgnoreCase("jpg") ||
                filenameExtension.equalsIgnoreCase("png")) {
            return "image/jpeg";
        }
        return "multipart/form-data";
    }

    /**
     * 上传OSS服务器 如果同名文件会覆盖服务器上的
     *
     * @param file 文件
     * @return 文件地址
     */
    private static String uploadFile(MultipartFile file, OssModel ossModel, FileKey fileKey) {
        String fileName = String.format("%s.%s", UUID.randomUUID().toString(), FilenameUtils.getExtension(file.getOriginalFilename()));
        try (InputStream inputStream = file.getInputStream()) {

            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(inputStream.available());
            objectMetadata.setCacheControl("no-cache");
            objectMetadata.setHeader("Pragma", "no-cache");
            objectMetadata.setContentType(FilenameUtils.getExtension("." + file.getOriginalFilename()));
            objectMetadata.setContentDisposition("inline;filename=" + fileName);
            AliOss.getOssClient(ossModel).putObject(ossModel.getBucketName(), fileKey.getDir() + "/" + fileName, inputStream, objectMetadata);
            return fileKey.getDir() + "/" + fileName;
        } catch (OSSException oe) {
            throw new OssException(oe.getMessage());
        } catch (ClientException | IOException ce) {
            throw new OssException(ce.getMessage());
        }
    }


    /**
     * 获取文件路径
     *
     * @param fileUrl 文件地址
     * @param fileKey 文件key
     * @return 路径
     */
    private static String getFileUrl(String fileUrl, FileKey fileKey, OssModel ossModel) {
        if (ParamUtil.paramIsEmpty(fileUrl)) {
            throw new RuntimeException("文件地址为空!");
        }
        String[] split = ParamUtil.split(fileUrl, "/");
        if (ParamUtil.paramIsEmpty(split)) {
            throw new OssException(HttpStatus.INVALID_PARAMETER.getMsg());
        }
        URL url = AliOss.getOssClient(ossModel).generatePresignedUrl(ossModel.getBucketName(), fileKey.getDir() + "/" + split[split.length - 1], TimeUtil.addDay(new Date(), 365));
        if (url == null) {
            throw new RuntimeException("获取OSS文件URL失败!");
        }
        return url.toString();
    }

    /**
     * 获取路径地址
     *
     * @param fileName 文件名
     * @return 地址
     */
    private static String getPathUrl(String fileName, OssModel ossModel) {
        return fileName.substring(fileName.indexOf(ossModel.getEndPoint()) + ossModel.getEndPoint().length() + 1);
    }


    /**
     * 文件删除
     *
     * @param urls 文件地址集合
     * @return 返回true为删除成功
     */
    public static boolean delete(List<String> urls, OssModel ossModel) {
        List<String> urlList = urls.stream().map(e -> AliOss.getPathUrl(e, ossModel)).collect(Collectors.toList());
        try {
            AliOss.getOssClient(ossModel).deleteObjects(new DeleteObjectsRequest(ossModel.getBucketName()).withKeys(urlList));
            return true;
        } catch (OSSException oe) {
            throw new OssException(oe.getMessage());
        } catch (ClientException ce) {
            throw new OssException(ce.getMessage());
        }
    }

    /**
     * 文件上传
     *
     * @param file     文件
     * @param fileKey  文件key
     * @param ossModel oss模型
     * @return string
     */
    public static String upload(MultipartFile file, FileKey fileKey, OssModel ossModel) {
        AliOss.createBucket(ossModel);
        String fileName = AliOss.uploadFile(file, ossModel, fileKey);
        String fileOssUrl = AliOss.getFileUrl(fileName, fileKey, ossModel);
        //去掉URL中的?后的时间戳
        int firstChar = fileOssUrl.indexOf("?");
        if (firstChar > 0) {
            fileOssUrl = fileOssUrl.substring(0, firstChar);
        }
        return fileOssUrl;
    }

    /**
     * 文件key
     */
    @Getter
    public enum FileKey {
        /**
         * 文件路径key
         */
        IMAGES("images"), VIDEO("video");

        private String dir;

        FileKey(String dir) {
            this.dir = dir;
        }
    }

}
