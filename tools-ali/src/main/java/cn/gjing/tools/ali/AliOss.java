package cn.gjing.tools.ali;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.DeleteObjectsRequest;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.OSSObject;
import org.apache.commons.io.FilenameUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Gjing
 **/
public class AliOss {

    /**
     * 域名
     */
    private String endPoint;

    /**
     * 存储空间名称
     */
    private String bucketName;
    /**
     * 实例
     */
    private OSSClient instance;

    private AliOss(String endPoint, String accessKeyId, String accessKeySecret, String bucketName) {
        this.endPoint = endPoint;
        this.bucketName = bucketName;
        this.instance = new OSSClient(endPoint, accessKeyId, accessKeySecret);
    }

    /**
     * Oss 实例化
     *
     * @return 实例
     */
    public static AliOss of(String endPoint, String accessKeyId, String accessKeySecret, String bucketName) {
        Objects.requireNonNull(endPoint, "endPoint cannot be null");
        Objects.requireNonNull(accessKeyId, "accessKeyId cannot be null");
        Objects.requireNonNull(accessKeySecret, "accessKeySecret cannot be null");
        Objects.requireNonNull(bucketName, "bucketName cannot be null");
        return new AliOss(endPoint, accessKeyId, accessKeySecret, bucketName);
    }

    /**
     * 文件删除
     *
     * @param fileOssUrls 文件地址集合
     * @return 返回删除文件列表
     */
    public List<String> delete(List<String> fileOssUrls) {
        List<String> urlList = fileOssUrls.stream().map(this::getPathUrl).collect(Collectors.toList());
        try {
            return instance.deleteObjects(new DeleteObjectsRequest(this.bucketName).withKeys(urlList)).getDeletedObjects();
        } catch (RuntimeException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    /**
     * 文件简单上传（最大文件不能超过5G）
     *
     * @param file 文件
     * @return string
     */
    public String upload(MultipartFile file) {
        if (file.getSize() > 5 * 1024 * 1024 * 1024) {
            throw new IllegalArgumentException("Upload failed. file size cannot more than 5 gb");
        }
        this.createBucket();
        String fileName = this.uploadFile(file);
        String fileOssUrl = this.getFileUrl(fileName);
        //去掉URL中的?后的时间戳
        int firstChar = fileOssUrl.indexOf("?");
        if (firstChar > 0) {
            fileOssUrl = fileOssUrl.substring(0, firstChar);
        }
        return fileOssUrl;
    }

    /**
     * @param fileOssUrl 要下载oss服务器上的文件地址
     * @param mkdir      本地文件夹
     * @return boolean
     */
    public boolean downloadFile(String fileOssUrl, String mkdir) {
        try {
            if (!StringUtils.isEmpty(mkdir)) {
                File file = new File(mkdir);
                if (!file.exists()) {
                    if (!file.mkdir()) {
                        throw new IllegalStateException("Create file exception");
                    }
                }
                Predicate<String> predicate = e -> e.indexOf("/", e.length() - 1) != -1;
                String pathUrl = this.getPathUrl(fileOssUrl);
                instance.getObject(new GetObjectRequest(this.bucketName, pathUrl), new File(predicate.test(mkdir) ? mkdir + pathUrl : mkdir + "/" + pathUrl));
                return true;
            }
            throw new IllegalArgumentException("Mkdir cannot be null");
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    /**
     * 阿里oss文件流式下载
     *
     * @param fileOssUrl 上传文件返回的url
     * @param response   response
     * @return true为成功
     */
    public boolean downloadStream(String fileOssUrl, HttpServletResponse response) {
        String pathUrl = this.getPathUrl(fileOssUrl);
        OSSObject object = instance.getObject(this.bucketName, pathUrl);
        try {
            InputStream is = object.getObjectContent();
            OutputStream os = new BufferedOutputStream(response.getOutputStream());
            response.setCharacterEncoding("utf-8");
            // 设置返回类型
            response.setContentType("multipart/form-data");
            // 文件名转码一下，不然会出现中文乱码
            response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(this.getPathUrl(fileOssUrl), "UTF-8"));
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
            throw new IllegalStateException(e.getMessage());
        }
        return true;
    }

    /**
     * 当Bucket 不存在时候创建Bucket
     */
    private void createBucket() {
        try {
            if (!instance.doesBucketExist(this.bucketName)) {
                instance.createBucket(this.bucketName);
            }
        } catch (Exception e) {
            throw new IllegalStateException("创建Bucket失败,请核对Bucket名称(规则：只能包含小写字母、数字和短横线，必须以小写字母和数字开头和结尾，长度在3-63之间)");
        }
    }

    /**
     * 通过文件名判断并获取OSS服务文件上传时文件的contentType
     *
     * @param fileName 文件名
     * @return 文件的contentType
     */
    private String getContentType(String fileName) {
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
    private String uploadFile(MultipartFile file) {
        String fileName = String.format("%s.%s", UUID.randomUUID().toString(), FilenameUtils.getExtension(file.getOriginalFilename()));
        try {
            instance.putObject(this.bucketName, fileName, new ByteArrayInputStream(file.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileName;
    }


    /**
     * 获取文件路径
     *
     * @param fileUrl 文件地址
     * @return 路径
     */
    private String getFileUrl(String fileUrl) {
        if (StringUtils.isEmpty(fileUrl)) {
            throw new IllegalArgumentException("The parameter fileUrl cannot be null!");
        }
        String[] split = fileUrl.split("/");
        URL url = instance.generatePresignedUrl(this.bucketName, split[split.length - 1], addDay(new Date()));
        if (url == null) {
            throw new IllegalStateException("Get oss file url error");
        }
        return url.toString();
    }

    /**
     * 获取路径地址
     *
     * @param fileOssUrl oss对应文件地址
     * @return 地址
     */
    private String getPathUrl(String fileOssUrl) {
        return fileOssUrl.substring(fileOssUrl.indexOf(endPoint) + endPoint.length() + 1);
    }

    private Date addDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, 3650);
        return cal.getTime();
    }

}
