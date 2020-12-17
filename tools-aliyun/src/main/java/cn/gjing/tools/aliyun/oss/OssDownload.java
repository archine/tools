package cn.gjing.tools.aliyun.oss;

import cn.gjing.tools.aliyun.AliyunMeta;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.OSSObject;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.function.Predicate;

/**
 * @author Gjing
 **/
public class OssDownload {
    private final OSS ossClient;
    private final OssMeta ossMeta;

    public OssDownload(OssMeta ossMeta, AliyunMeta aliyunMeta) {
        this.ossMeta = ossMeta;
        this.ossClient = ossMeta.getOssClient(aliyunMeta);
    }

    /**
     * Determine if the file exists
     *
     * @param fileName Oss file name
     * @return true is exist
     */
    public boolean isExist(String fileName) {
        return this.ossClient.doesObjectExist(this.ossMeta.getBucket(), fileName);
    }

    /**
     * Stream download
     *
     * @param fileName Oss file name
     * @param response HttpServletResponse
     */
    public void downByStream(String fileName, HttpServletResponse response) {
        OSSObject object = this.ossClient.getObject(this.ossMeta.getBucket(), fileName);
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = object.getObjectContent();
            outputStream = new BufferedOutputStream(response.getOutputStream());
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(fileName, "UTF-8"));
            response.setContentLength((int) object.getObjectMetadata().getContentLength());
            byte[] b = new byte[1024];
            int len;
            while (-1 != (len = inputStream.read(b))) {
                outputStream.write(b, 0, len);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Download the file to the local file
     *
     * @param dir      Local dir
     * @param fileName Oss file name
     */
    public void downByLocal(String dir, String fileName) {
        File f = new File(dir);
        if (!f.exists()) {
            f.mkdir();
        }
        Predicate<String> predicate = e -> e.indexOf("/", e.length() - 1) != -1;
        this.ossClient.getObject(new GetObjectRequest(this.ossMeta.getBucket(), fileName), new File(predicate.test(dir) ? dir + fileName : dir + "/" + fileName));
    }
}
