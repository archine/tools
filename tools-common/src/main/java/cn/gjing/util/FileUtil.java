package cn.gjing.util;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * @author Gjing
 * 文件工具类
 **/
public class FileUtil {

    private FileUtil() {
    }

    /**
     * 生成FileUtil实例
     *
     * @return fileUtil
     */
    public static FileUtil of() {
        return new FileUtil();
    }

    /**
     * 从网络Url中下载文件
     *
     * @param fileUrl  文件URL地址
     * @param fileName 文件名
     * @param savePath 保存的地址
     */
    public void downloadByUrl(String fileUrl, String fileName, String savePath) {
        HttpURLConnection conn;
        URL url;
        InputStream inputStream = null;
        FileOutputStream fos = null;
        BufferedOutputStream br = null;
        try {
            url = new URL(fileUrl);
            conn = (HttpURLConnection) url.openConnection();
            //设置超时间为3秒
            conn.setConnectTimeout(5 * 1000);
            //防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            //得到输入流
            inputStream = conn.getInputStream();
            //获取字节数组
            byte[] data = this.readInputStream(inputStream);
            //文件保存目录
            File mkdirPath = new File(savePath);
            if (!mkdirPath.exists()) {
                boolean mkdirs = mkdirPath.mkdirs();
            }
            File file = new File(mkdirPath + File.separator + fileName);
            fos = new FileOutputStream(file);
            br = new BufferedOutputStream(fos);
            br.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
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
     * 将指定文件通过流下载
     * @param response response
     * @param fileName 完整的文件名(目录, 文件名, 后缀)
     */
    public void downloadByStream(HttpServletResponse response, String fileName) {
        try {
            // path是指欲下载的文件的路径。
            File file = new File(fileName);
            InputStream is = new BufferedInputStream(new FileInputStream(file));
            OutputStream os = new BufferedOutputStream(response.getOutputStream());
            response.setCharacterEncoding("utf-8");
            // 设置返回类型
            response.setContentType("multipart/form-data");
            // 文件名转码一下，不然会出现中文乱码
            response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(file.getName(), "UTF-8"));
            // 设置返回的文件的大小
            response.setContentLength((int) file.length());
            byte[] b = new byte[1024];
            int len;
            while (-1 != (len = is.read(b))) {
                os.write(b, 0, len);
            }
            os.flush();
            os.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取指定路径下的文件的Byte数组
     *
     * @param filePath 文件路径
     * @return byte数组
     */
    public byte[] getBytes(String filePath) {
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    /**
     * 从输入流中获取字节数组
     *
     * @param inputStream 输入流
     * @return 字节数组
     */
    public byte[] readInputStream(InputStream inputStream) {
        byte[] buffer = new byte[1024];
        int len;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            while ((len = inputStream.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bos.toByteArray();
    }

    /**
     * 获取文件拓展名
     *
     * @param fileName 文件名
     * @return 拓展名
     */
    public String getExtention(String fileName) {
        int pos = fileName.lastIndexOf(".");
        return fileName.substring(pos);
    }

}
