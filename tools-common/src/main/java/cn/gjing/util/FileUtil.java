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
            this.close(fos, br, null, null, inputStream);
        }
    }

    /**
     * 将指定文件通过流下载
     *
     * @param response response
     * @param file     完整的文件名(目录, 文件名, 后缀)
     */
    public void downloadByStream(HttpServletResponse response, String file) {
        InputStream is = null;
        OutputStream os = null;
        try {
            // path是指欲下载的文件的路径。
            File file1 = new File(file);
            if (!file1.exists()) {
                throw new FileNotFoundException("This file not found: " + file);
            }
            is = new BufferedInputStream(new FileInputStream(file1));
            os = new BufferedOutputStream(response.getOutputStream());
            response.setCharacterEncoding("utf-8");
            // 设置返回类型
            response.setContentType("multipart/form-data");
            // 文件名转码一下，不然会出现中文乱码
            response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(file1.getName(), "UTF-8"));
            // 设置返回的文件的大小
            response.setContentLength(file.length());
            byte[] b = new byte[1024];
            int len;
            while (-1 != (len = is.read(b))) {
                os.write(b, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (os != null) {
                    os.flush();
                    os.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取指定路径下的文件的Byte数组
     *
     * @param file 文件名，包括存放目录、文件名、拓展名
     * @return byte数组
     */
    public byte[] getBytes(String file) {
        byte[] buffer = null;
        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;
        try {
            File file1 = new File(file);
            if (!file1.exists()) {
                return null;
            }
            fis = new FileInputStream(file1);
            bos = new ByteArrayOutputStream(1024);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            buffer = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            this.close(null, null, fis, bos,null);
        }
        return buffer;
    }

    /**
     * 将字节数组写入文件
     *
     * @param bytes    要写入的字节数组
     * @param path     存放的目录
     * @param fileName 文件名+扩展名
     * @return true为写入成功，false写入失败
     */
    public boolean writeFile(byte[] bytes, String path, String fileName) {
        File mkdirPath = new File(path);
        if (!mkdirPath.exists()) {
            mkdirPath.mkdirs();
        }
        FileOutputStream fileOutputStream = null;
        BufferedOutputStream bufferedOutputStream = null;
        File file = new File(mkdirPath + File.separator + fileName);
        try {
            fileOutputStream = new FileOutputStream(file);
            bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            bufferedOutputStream.write(bytes);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            this.close(fileOutputStream, bufferedOutputStream, null,null,null);
        }
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
    public String getExtension(String fileName) {
        int pos = fileName.lastIndexOf(".");
        return fileName.substring(pos);
    }

    /**
     * 关闭流
     *
     * @param fileOutputStream     文件输出流
     * @param bufferedOutputStream 输出缓冲流
     * @param fileInputStream      输入流
     */
    private void close(FileOutputStream fileOutputStream, BufferedOutputStream bufferedOutputStream, FileInputStream fileInputStream,
                       ByteArrayOutputStream byteArrayOutputStream, InputStream inputStream) {
        try {
            if (bufferedOutputStream != null) {
                bufferedOutputStream.close();
            }
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
            if (fileInputStream != null) {
                fileInputStream.close();
            }
            if (byteArrayOutputStream != null) {
                byteArrayOutputStream.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
