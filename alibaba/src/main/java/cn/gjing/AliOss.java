package cn.gjing;

import cn.gjing.annotation.NotNull2;
import cn.gjing.enums.HttpStatus;
import cn.gjing.ex.GjingException;
import cn.gjing.ex.ParamException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.DeleteObjectsRequest;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Gjing
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
            throw new GjingException("创建Bucket失败,请核对Bucket名称(规则：只能包含小写字母、数字和短横线，必须以小写字母和数字开头和结尾，长度在3-63之间)");
        }
    }

    /**
     * 通过文件名判断并获取OSS服务文件上传时文件的contentType
     *
     * @param fileName 文件名
     * @return 文件的contentType
     */
    private static String getContentType(String fileName) {
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
        if ("bmp".equalsIgnoreCase(fileExtension)) {
            return "image/bmp";
        }
        if ("gif".equalsIgnoreCase(fileExtension)) {
            return "image/gif";
        }
        if ("jpeg".equalsIgnoreCase(fileExtension) || "jpg".equalsIgnoreCase(fileExtension)) {
            return "image/jpeg";
        }
        if ("png".equalsIgnoreCase(fileExtension)) {
            return "image/png";
        }
        if ("html".equalsIgnoreCase(fileExtension)) {
            return "text/html";
        }
        if ("txt".equalsIgnoreCase(fileExtension)) {
            return "text/plain";
        }
        if ("vsd".equalsIgnoreCase(fileExtension)) {
            return "application/vnd.visio";
        }
        if ("ppt".equalsIgnoreCase(fileExtension) || "pptx".equalsIgnoreCase(fileExtension)) {
            return "application/vnd.ms-powerpoint";
        }
        if ("doc".equalsIgnoreCase(fileExtension) || "docx".equalsIgnoreCase(fileExtension)) {
            return "application/msword";
        }
        if ("xml".equalsIgnoreCase(fileExtension)) {
            return "text/xml";
        }
        return "text/html";
    }

    /**
     * 上传OSS服务器 如果同名文件会覆盖服务器上的
     *
     * @param file 文件
     * @return 文件地址
     */
    private static String uploadFile(MultipartFile file, OssModel ossModel) {
        String fileName = String.format("%s.%s", UUID.randomUUID().toString(), FilenameUtils.getExtension(file.getOriginalFilename()));
        try (InputStream inputStream = file.getInputStream()) {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(inputStream.available());
            objectMetadata.setCacheControl("no-cache");
            objectMetadata.setHeader("Pragma", "no-cache");
            objectMetadata.setContentType(FilenameUtils.getExtension("." + file.getOriginalFilename()));
            objectMetadata.setContentDisposition("inline;filename=" + fileName);
            AliOss.getOssClient(ossModel).putObject(ossModel.getBucketName(), fileName, inputStream, objectMetadata);
            return fileName;
        } catch (Exception oe) {
            throw new GjingException(oe.getMessage());
        }
    }


    /**
     * 获取文件路径
     *
     * @param fileUrl 文件地址
     * @return 路径
     */
    private static String getFileUrl(String fileUrl, OssModel ossModel) {
        if (ParamUtil.isEmpty(fileUrl)) {
            throw new ParamException("The parameter fileUrl cannot be null!");
        }
        String[] split = ParamUtil.split(fileUrl, "/");
        if (ParamUtil.isEmpty(split)) {
            throw new ParamException(HttpStatus.INVALID_PARAMETER.getMsg());
        }
        URL url = AliOss.getOssClient(ossModel).generatePresignedUrl(ossModel.getBucketName(), split[split.length - 1], TimeUtil.addDay(new Date(), 365 * 10));
        if (url == null) {
            throw new GjingException("get oss file url error");
        }
        return url.toString();
    }

    /**
     * 获取路径地址
     *
     * @param fileOssUrl oss对应文件地址
     * @return 地址
     */
    private static String getPathUrl(String fileOssUrl, OssModel ossModel) {
        return fileOssUrl.substring(fileOssUrl.indexOf(ossModel.getEndPoint()) + ossModel.getEndPoint().length() + 1);
    }


    /**
     * 文件删除
     *
     * @param ossModel    oss模型
     * @param fileOssUrls 文件地址集合
     * @return 返回true为删除成功
     */
    @NotNull2
    public static boolean delete(List<String> fileOssUrls, OssModel ossModel) {
        List<String> urlList = fileOssUrls.stream().map(e -> AliOss.getPathUrl(e, ossModel)).collect(Collectors.toList());
        try {
            AliOss.getOssClient(ossModel).deleteObjects(new DeleteObjectsRequest(ossModel.getBucketName()).withKeys(urlList));
            return true;
        } catch (RuntimeException e) {
            throw new GjingException(e.getMessage());
        }
    }

    /**
     * 文件简单上传（最大文件不能超过5G）
     *
     * @param file     文件
     * @param ossModel oss模型
     * @return string
     */
    @NotNull2
    public static String upload(MultipartFile file, OssModel ossModel) {
        if (file.getSize() > 5 * 1024 * 1024 * 1024) {
            throw new ParamException("Upload failed. file size cannot more than 5 gb");
        }
        AliOss.createBucket(ossModel);
        String fileName = AliOss.uploadFile(file, ossModel);
        String fileOssUrl = AliOss.getFileUrl(fileName, ossModel);
        //去掉URL中的?后的时间戳
        int firstChar = fileOssUrl.indexOf("?");
        if (firstChar > 0) {
            fileOssUrl = fileOssUrl.substring(0, firstChar);
        }
        return fileOssUrl;
    }

    /**
     * @param ossModel   oss模型
     * @param fileOssUrl 要下载oss服务器上的文件地址
     * @param mkdir      本地文件夹
     * @return boolean
     */
    @NotNull2
    public static boolean downloadFile(OssModel ossModel, String fileOssUrl, String mkdir) {
        try {
            if (ParamUtil.isNotEmpty(mkdir)) {
                File file = new File(mkdir);
                if (!file.exists()) {
                    if (!file.mkdir()) {
                        throw new GjingException("create file exception");
                    }
                }
                OSSClient ossClient = getOssClient(ossModel);
                Predicate<String> predicate = e -> e.indexOf("/", e.length() - 1) != -1;
                String pathUrl = getPathUrl(fileOssUrl, ossModel);
                ossClient.getObject(new GetObjectRequest(ossModel.getBucketName(), pathUrl), new File(predicate.test(mkdir) ? mkdir + pathUrl : mkdir + "/" + pathUrl));
                return true;
            }
            throw new ParamException("mkdir cannot be null");
        } catch (Exception e) {
            throw new GjingException(e.getMessage());
        }
    }

    /**
     * 阿里oss文件流式下载
     *
     * @param ossModel   oss模型
     * @param fileOssUrl 上传文件返回的url
     * @param response   response
     * @return true为成功
     */
    @NotNull2
    public static boolean downloadStream(OssModel ossModel, String fileOssUrl, HttpServletResponse response) {
        try {
            String pathUrl = getPathUrl(fileOssUrl, ossModel);
            OSSObject object = getOssClient(ossModel).getObject(ossModel.getBucketName(), pathUrl);
            InputStream is = object.getObjectContent();
            OutputStream os = new BufferedOutputStream(response.getOutputStream());
            response.setCharacterEncoding("utf-8");
            // 设置返回类型
            response.setContentType("multipart/form-data");
            // 文件名转码一下，不然会出现中文乱码
            response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(
                    getPathUrl(fileOssUrl, ossModel), "UTF-8"));
            // 设置返回的文件的大小
            response.setContentLength((int) object.getObjectMetadata().getContentLength());
            byte[] b = new byte[1024];
            int len;
            while (-1 != (len = is.read(b))) {
                os.write(b, 0, len);
            }
            os.flush();
            os.close();
            is.close();
        } catch (Exception e) {
            throw new GjingException(e.getMessage());
        }
        return true;
    }
}
