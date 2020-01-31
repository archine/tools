package cn.gjing.tools.aliyun.oss;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
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
    private OSS ossClient;
    private OssMeta ossMeta;

    public OssDownload(OssMeta ossMeta) {
        this.ossMeta = ossMeta;
        this.ossInit();
    }

    /**
     * 判断文件是否存在
     *
     * @param fileName oss文件名
     * @return true为存在
     */
    public boolean isExist(String fileName) {
        return this.ossClient.doesObjectExist(this.ossMeta.getBucket(), fileName);
    }

    /**
     * 通过流瞎子啊
     *
     * @param fileName oss文件名
     * @param response response
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
        }finally {
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
     * 下载文件到本地文件
     *
     * @param dir      本地目录
     * @param fileName oss文件名
     */
    public void downByLocal(String dir, String fileName) {
        File f = new File(dir);
        if (!f.exists()) {
            f.mkdir();
        }
        Predicate<String> predicate = e -> e.indexOf("/", e.length() - 1) != -1;
        this.ossClient.getObject(new GetObjectRequest(this.ossMeta.getBucket(), fileName), new File(predicate.test(dir) ? dir + fileName : dir + "/" + fileName));
    }

    private void ossInit() {
        ClientBuilderConfiguration conf = new ClientBuilderConfiguration();
        conf.setMaxConnections(this.ossMeta.getMaxConnections());
        conf.setSocketTimeout(this.ossMeta.getSocketTimeout());
        conf.setConnectionTimeout(this.ossMeta.getConnectionTimeout());
        conf.setIdleConnectionTime(this.ossMeta.getIdleTime());
        this.ossClient = new OSSClientBuilder().build(this.ossMeta.getEndPoint(), this.ossMeta.getAccessKey(), this.ossMeta.getAccessKeySecret(), conf);
    }
}
