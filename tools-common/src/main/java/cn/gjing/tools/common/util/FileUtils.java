package cn.gjing.tools.common.util;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * @author Gjing
 **/
public final class FileUtils {
    /**
     * 从网络Url中下载文件
     *
     * @param fileUrl  文件URL地址
     * @param fileName 文件名
     * @param savePath 保存的地址
     */
    public static void downloadByUrl(String fileUrl, String fileName, String savePath) {
        HttpURLConnection conn;
        URL url;
        InputStream inputStream = null;
        FileOutputStream fos = null;
        BufferedOutputStream br = null;
        try {
            url = new URL(fileUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5 * 1000);
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            inputStream = conn.getInputStream();
            byte[] data = readInputStream(inputStream);
            File mkdirPath = new File(savePath);
            if (!mkdirPath.exists()) {
                mkdirPath.mkdirs();
            }
            File file = new File(mkdirPath + File.separator + fileName);
            fos = new FileOutputStream(file);
            br = new BufferedOutputStream(fos);
            br.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将指定文件通过流下载
     *
     * @param response response
     * @param file     文件
     */
    public static void downloadByStream(HttpServletResponse response, File file) {
        InputStream is = null;
        BufferedOutputStream bos = null;
        try {
            is = new BufferedInputStream(new FileInputStream(file));
            bos = new BufferedOutputStream(response.getOutputStream());
            response.setCharacterEncoding("utf-8");
            // 设置返回类型
            response.setContentType("multipart/force-download");
            // 文件名转码一下，不然会出现中文乱码
            response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(file.getName(), "UTF-8"));
            // 设置返回的文件的大小
            response.setContentLength((int) file.length());
            byte[] b = new byte[1024];
            int len;
            while (-1 != (len = is.read(b))) {
                bos.write(b, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bos != null) {
                    bos.close();
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
     * 获取文件的Byte数组
     *
     * @param file 文件
     * @return byte数组
     */
    public static byte[] getBytes(File file) {
        byte[] buffer = null;
        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;
        try {
            fis = new FileInputStream(file);
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
            try {
                if (fis != null) {
                    fis.close();
                }
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return buffer;
    }

    /**
     * 将字节数组写入文件
     *
     * @param bytes 要写入的字节数组
     * @param file  文件
     * @return true为写入成功，false写入失败
     */
    public static boolean writeFile(byte[] bytes, File file) {
        FileOutputStream fileOutputStream = null;
        BufferedOutputStream bufferedOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            bufferedOutputStream.write(bytes);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                if (bufferedOutputStream != null) {
                    bufferedOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 从输入流中获取字节数组
     *
     * @param inputStream 输入流
     * @return 字节数组
     */
    public static byte[] readInputStream(InputStream inputStream) {
        byte[] buffer = new byte[1024 * 1024];
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
    public static String getExtension(String fileName) {
        int pos = fileName.lastIndexOf(".");
        return fileName.substring(pos);
    }
}
