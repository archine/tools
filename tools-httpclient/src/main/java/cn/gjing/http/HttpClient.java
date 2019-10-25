package cn.gjing.http;

import com.google.gson.Gson;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Objects;

/**
 * @author Gjing
 **/
public class HttpClient<T> implements Closeable {
    private String requestUrl;
    private Class<T> responseType;
    private T data;
    private Map<String, String> header;
    private String paramsStr;
    private HttpMethod httpMethod;
    private HttpURLConnection connection;
    private String json;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private static Gson gson = new Gson();

    private HttpClient(String url, HttpMethod method, Class<T> responseType) {
        this.requestUrl = url;
        this.httpMethod = method;
        this.responseType = responseType;
    }

    private HttpClient() {

    }

    /**
     * build a httpClient
     *
     * @param url          Request url
     * @param method       Request method
     * @param responseType Response class
     * @param <T>          Response type
     * @return this
     */
    public static <T> HttpClient<T> builder(String url, HttpMethod method, Class<T> responseType) {
        return new HttpClient<>(url, method, responseType);
    }

    /**
     * carry request header
     *
     * @param header request header map
     * @return HttpClient
     */
    public HttpClient<T> header(Map<String, String> header) {
        Objects.requireNonNull(header, "Request Header cannot be null");
        this.header = header;
        return this;
    }

    /**
     * carry request parameter
     *
     * @param queryMap parameter map
     * @return HttpClient
     */
    public HttpClient<T> param(Map<String, ?> queryMap) {
        Objects.requireNonNull(queryMap, "Request parameter cannot be null");
        String paramString = this.paramsStr = UrlUtils.paramUnicodeSort(queryMap, true, false);
        if (httpMethod == HttpMethod.GET) {
            this.requestUrl = requestUrl + "?" + paramString;
        }
        this.paramsStr = paramString;
        return this;
    }

    /**
     * carry request body
     *
     * @param json json string or json object
     * @return HttpClient
     */
    public HttpClient<T> body(Object json) {
        if (httpMethod != HttpMethod.POST) {
            return this;
        }
        Objects.requireNonNull(json, "Request json cannot be null");
        this.json = json instanceof String ? json.toString() : gson.toJson(json);
        return this;
    }

    /**
     * send to request
     */
    @SuppressWarnings("unchecked")
    public HttpClient<T> execute() {
        ConnectionFactory connectionFactory = new ConnectionFactory(this.requestUrl);
        HttpURLConnection connection = requestUrl.startsWith("http") ? connectionFactory.getHttp() : connectionFactory.getHttps();
        connection.setRequestProperty("Content-type", json == null ? "application/x-www-form-urlencoded" : "application/json");
        try {
            connection.setRequestMethod(httpMethod.toString());
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        this.connection = connection;
        if (header != null) {
            this.setHeader(connection);
        }
        StringBuilder result;
        try {
            if (httpMethod == HttpMethod.POST) {
                if (paramsStr != null) {
                    this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), Charset.defaultCharset()));
                    this.bufferedWriter.write(paramsStr);
                    this.bufferedWriter.flush();
                } else if (json != null) {
                    this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), Charset.defaultCharset()));
                    this.bufferedWriter.write(json);
                    this.bufferedWriter.flush();
                }
            }
            result = new StringBuilder();
            String line;
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                this.bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.defaultCharset()));
                while ((line = this.bufferedReader.readLine()) != null) {
                    result.append(line);
                }
                try {
                    this.data = gson.fromJson(result.toString(), this.responseType);
                } catch (Exception c) {
                    this.data = (T) result.toString();
                }
                return this;
            }
            this.bufferedReader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), Charset.defaultCharset()));
            while ((line = this.bufferedReader.readLine()) != null) {
                result.append(line);
            }
            throw new HttpException(result.toString());
        } catch (IOException e) {
            throw new HttpException(e.getMessage());
        }
    }

    /**
     * Get Result
     * @return T
     */
    public T get() {
        return this.data;
    }

    /**
     * Listen to the result and process it
     * @param listener listener
     */
    public void listener(Listener<T> listener) {
        listener.notify(this.data);
    }

    /**
     * set request header
     *
     * @param urlConnection HttpUrlConnection
     */
    private void setHeader(HttpURLConnection urlConnection) {
        for (Map.Entry<String, String> headerEntry : header.entrySet()) {
            urlConnection.setRequestProperty(headerEntry.getKey(), headerEntry.getValue());
        }
    }

    /**
     * close stream
     *
     * @throws IOException io
     */
    @Override
    public void close() throws IOException {
        if (this.connection != null) {
            this.connection.disconnect();
        }
        if (this.bufferedReader != null) {
            this.bufferedReader.close();
        }
        if (this.bufferedWriter != null) {
            this.bufferedWriter.flush();
            this.bufferedWriter.close();
        }
    }
}
