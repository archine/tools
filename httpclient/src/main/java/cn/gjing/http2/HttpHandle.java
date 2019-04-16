package cn.gjing.http2;

import cn.gjing.ParamUtil;
import cn.gjing.UrlUtil;
import cn.gjing.enums.HttpStatus;
import cn.gjing.enums.HttpType;
import cn.gjing.ex.HttpException;
import com.google.gson.Gson;
import org.springframework.http.HttpMethod;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;

/**
 * @author Gjing
 **/
class HttpHandle {
    private static Gson gson = new Gson();

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
            if (ParamUtil.split(url, ":")[0].equals(HttpType.HTTPS.getType())) {
                //创建SSLContext
                SSLContext sslContext=SSLContext.getInstance("SSL");
                TrustManager[] tm={new HttpsProcess()};
                //初始化
                sslContext.init(null, tm, new java.security.SecureRandom());;
                //获取SSLSocketFactory对象
                SSLSocketFactory ssf=sslContext.getSocketFactory();
                conn.set
            }
            uUrl = new URL(url);
            conn = (HttpURLConnection) uUrl.openConnection();
            conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            conn.setRequestMethod(method.toString());
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout(readTimeout);
            if (ParamUtil.isNotEmpty(headers)) {
                Set<String> headerSet = headers.keySet();
                for (String key : headerSet) {
                    conn.setRequestProperty(key, headers.get(key));
                }
            }
            if (paramsStr != null && method == HttpMethod.POST) {
                //发送请求参数
                out = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), Charset.defaultCharset()));
                out.write(paramsStr);
                out.flush();
            }
            //接收返回结果
            StringBuilder result = new StringBuilder();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), Charset.defaultCharset()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
            return gson.fromJson(result.toString(), responseType);
        } catch (Exception e) {
            //处理错误流，提高http连接被重用的几率
            try {
                byte[] buf = new byte[256];
                assert conn != null;
                InputStream es = conn.getErrorStream();
                if (es != null) {
                    while (es.read(buf) <= 0) {
                        es.close();
                    }
                }
            } catch (Exception d) {
                d.printStackTrace();
                throw new HttpException(d.getMessage());
            }

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
        throw new HttpException(HttpStatus.INTERNAL_SERVER_ERROR.getMsg());
    }
}
