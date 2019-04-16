package cn.gjing.http2;

import cn.gjing.ParamUtil;
import cn.gjing.UrlUtil;
import cn.gjing.enums.HttpType;
import cn.gjing.ex.HttpException;
import org.springframework.http.HttpMethod;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;

/**
 * @author Gjing
 **/
@SuppressWarnings("unchecked")
class HttpHandle {

    static <T> T invokeUrl(String url, Map<String, String> params, Map<String, String> headers, int connectTimeout, int readTimeout, HttpMethod method,
                           Class<T> responseType) {
        String paramsStr = null;
        if (ParamUtil.isNotEmpty(params)) {
            paramsStr = UrlUtil.unicodeSort(params, false, false);
            //只有POST方法才能通过OutputStream(即form的形式)提交参数
            if (method != HttpMethod.POST) {
                assert paramsStr != null;
                url += "?" + paramsStr;
            }
        }
        URL uUrl;
        HttpURLConnection conn = null;
        BufferedWriter out = null;
        BufferedReader in = null;
        try {
            uUrl = new URL(url);
            if (ParamUtil.split(url, ":")[0].equals(HttpType.HTTPS.getType())) {
                SSLContext sslContext = SSLContext.getInstance("SSL");
                TrustManager[] tm = {new HttpsProcess()};
                sslContext.init(null, tm, new java.security.SecureRandom());
                SSLSocketFactory ssf = sslContext.getSocketFactory();
                HttpsURLConnection httpsConn = (HttpsURLConnection) uUrl.openConnection();
                httpsConn.setSSLSocketFactory(ssf);
                conn = httpsConn;
            } else {
                conn = (HttpURLConnection) uUrl.openConnection();
            }
            setProperty(connectTimeout, readTimeout, method, conn);
            addHeaders(headers, conn);
            if (paramsStr != null && method == HttpMethod.POST) {
                out = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), Charset.defaultCharset()));
                out.write(paramsStr);
                out.flush();
            }
            StringBuilder result = new StringBuilder();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), Charset.defaultCharset()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
            return (T) result.toString();
        } catch (Exception e) {
            throw new HttpException(e.getMessage());
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void addHeaders(Map<String, String> headers, HttpURLConnection conn) {
        if (ParamUtil.isNotEmpty(headers)) {
            Set<String> headerSet = headers.keySet();
            for (String key : headerSet) {
                conn.setRequestProperty(key, headers.get(key));
            }
        }   
    }
//
//    /**
//     * 处理错误流
//     * @param conn HttpUrlConnection
//     */
//    private static void processException(HttpURLConnection conn) {
//        //处理错误流，提高http连接被重用的几率
//        try {
//            byte[] buf = new byte[256];
//            assert conn != null;
//            InputStream es = conn.getErrorStream();
//            if (es != null) {
//                while (es.read(buf) <= 0) {
//                    es.close();
//                }
//            }
//        } catch (Exception e) {
//            throw new HttpException(e.getMessage());
//        }
//    }

    /**
     * 设置属性
     * @param connectTimeout 超时时间
     * @param readTimeout 读超时
     * @param method 方法类型
     * @param conn 连接
     * @throws ProtocolException 协议异常
     */
    private static void setProperty(int connectTimeout, int readTimeout, HttpMethod method, HttpURLConnection conn) throws ProtocolException {
        conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");
        conn.setRequestMethod(method.toString());
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setConnectTimeout(connectTimeout);
        conn.setReadTimeout(readTimeout);
    }
}
