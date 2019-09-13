package cn.gjing.http;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Connection generation factory
 * @author Gjing
 **/
class ConnectionFactory {

    /**
     * request url
     */
    private String requestUrl;

    ConnectionFactory(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    /**
     * get httpsConnection instance
     * @return HttpsURLConnection
     */
    HttpsURLConnection getHttps() {
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            TrustManager[] tm = {new HttpsManager()};
            sslContext.init(null, tm, new java.security.SecureRandom());
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            URL url = new URL(this.requestUrl);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setSSLSocketFactory(ssf);
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(10000);
            return connection;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * get httpConnection instance
     * @return HttpURLConnection
     */
    HttpURLConnection getHttp() {
        try {
            URL url = new URL(this.requestUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(10000);
            return connection;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
