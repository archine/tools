package cn.gjing;

import com.google.gson.Gson;
import org.springframework.http.HttpMethod;
import org.springframework.util.StringUtils;

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
import java.util.Objects;
import java.util.Set;

/**
 * @author Gjing
 **/
class HttpHandle<T> {
    private static final Gson GSON = new Gson();

    /**
     * 响应类型
     */
    private final Class<T> responseType;
    /**
     * 请求头
     */
    private final Map<String, ?> headers;

    private HttpHandle(Class<T> responseType, Map<String, ?> headers) {
        this.responseType = Objects.requireNonNull(responseType);
        this.headers = headers;
    }

    static <T> HttpHandle<T> of(Class<T> responseType, Map<String, ?> headers) {
        return new HttpHandle<>(responseType, headers);
    }

    T invokeUrl(String url, Map<String, ?> params, Object body, Integer connectTimeout,
                Integer readTimeout, HttpMethod method) {
        Objects.requireNonNull(connectTimeout);
        Objects.requireNonNull(readTimeout);
        if (StringUtils.isEmpty(url)) {
            throw new NullPointerException("Url is not null");
        }
        String paramsStr = null;
        if (!StringUtils.isEmpty(params)) {
            paramsStr = UrlUtil.paramUnicodeSort(params, false, false);
            //只有POST方法才能通过OutputStream(即form的形式)提交参数
            if (method != HttpMethod.POST) {
                assert paramsStr != null;
                url += "?" + paramsStr;
            }
        }
        URL requestUrl;
        HttpURLConnection conn = null;
        BufferedWriter bw = null;
        BufferedReader br = null;
        try {
            requestUrl = new URL(url);
            if (url.split(":")[0].equals("https")) {
                SSLContext sslContext = SSLContext.getInstance("SSL");
                TrustManager[] tm = {new HttpsProcess()};
                sslContext.init(null, tm, new java.security.SecureRandom());
                SSLSocketFactory ssf = sslContext.getSocketFactory();
                HttpsURLConnection httpsConn = (HttpsURLConnection) requestUrl.openConnection();
                httpsConn.setSSLSocketFactory(ssf);
                conn = httpsConn;
            } else {
                conn = (HttpURLConnection) requestUrl.openConnection();
            }
            setProperty(connectTimeout, readTimeout, method, conn, body);
            addHeaders(conn);
            if (method == HttpMethod.POST) {
                if (paramsStr != null) {
                    bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), Charset.defaultCharset()));
                    bw.write(paramsStr);
                    bw.flush();
                }
                if (body != null) {
                    bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), Charset.defaultCharset()));
                    bw.write(GSON.toJson(body));
                    bw.flush();
                }
            }
            StringBuilder result = null;
            br = new BufferedReader(new InputStreamReader(conn.getInputStream(), Charset.defaultCharset()));
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                result = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    result.append(line);
                }
            }
            if (result != null) {
                return cast(result);
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new HttpException(e.getMessage());
        } finally {
            closeStream(conn, bw, br);
        }
    }

    /**
     * 关闭流
     * @param conn HttpURLConnection
     * @param bw BufferedWriter
     * @param br BufferedReader
     */
    private void closeStream(HttpURLConnection conn, BufferedWriter bw, BufferedReader br) {
        try {
            if (bw != null) {
                bw.close();
            }
            if (br != null) {
                br.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 结果转换
     *
     * @param result 结果
     * @return T
     */
    @SuppressWarnings("unchecked")
    private T cast(StringBuilder result) {
        try {
            return GSON.fromJson(result.toString(), responseType);
        } catch (RuntimeException e) {
            return (T) result.toString();
        }
    }

    /**
     * 增加请求头
     *
     * @param conn conn
     */
    private void addHeaders(HttpURLConnection conn) {
        if (headers != null) {
            Set<String> headerSet = headers.keySet();
            for (String key : headerSet) {
                conn.setRequestProperty(key, String.valueOf(headers.get(key)));
            }
        }
    }

    /**
     * 设置属性
     *
     * @param connectTimeout 超时时间
     * @param readTimeout    读超时
     * @param method         方法类型
     * @param conn           连接
     * @param body           body参数
     * @throws ProtocolException 协议异常
     */
    private void setProperty(int connectTimeout, int readTimeout, HttpMethod method, HttpURLConnection conn, Object body) throws ProtocolException {
        conn.setRequestProperty("Content-type", body == null ? "application/x-www-form-urlencoded" : "application/json");
        conn.setRequestProperty("Charset", "utf-8");
        conn.setRequestMethod(method.toString());
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setConnectTimeout(connectTimeout);
        conn.setReadTimeout(readTimeout);
    }
}
