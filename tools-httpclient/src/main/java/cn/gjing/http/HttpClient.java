package cn.gjing.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Objects;

/**
 * @author Gjing
 **/
public class HttpClient<T> implements AutoCloseable {
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
    private ObjectMapper objectMapper;
    private FallBackHelper<String> fallbackHelper;
    private int connectTimeout = 2000;
    private int readTimeout = 5000;

    private HttpClient(String url, HttpMethod method, Class<T> responseType) {
        this.requestUrl = url;
        this.httpMethod = method;
        this.responseType = responseType;
        this.objectMapper = new ObjectMapper();
        this.fallbackHelper = e -> {
            throw new HttpException(e);
        };
    }

    private HttpClient() {

    }

    /**
     * Build a httpClient
     *
     * @param url          Request url
     * @param method       Request method
     * @param responseType Response class
     * @param <T>          Response type
     * @return this
     */
    public static <T> HttpClient<T> builder(String url, HttpMethod method, Class<T> responseType) {
        try (final HttpClient<T> httpClient = new HttpClient<>(url, method, responseType)) {
            return httpClient;
        } catch (Exception e) {
            throw new HttpException("Create httpclient error");
        }
    }

    /**
     * Carry request header
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
     * Set request timeout time
     * @param connectTimeout Connect time
     * @return this
     */
    public HttpClient<T> connectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    /**
     * Set request timeout time
     * @param readTimeout Read time
     * @return this
     */
    public HttpClient<T> readTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    /**
     * Carry request parameter
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
     * Carry request body
     *
     * @param json json string or json object
     * @return HttpClient
     */
    public HttpClient<T> body(Object json) {
        if (httpMethod == HttpMethod.GET) {
            return this;
        }
        Objects.requireNonNull(json, "Request json cannot be null");
        try {
            this.json = json instanceof String ? json.toString() : objectMapper.writeValueAsString(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Send to request
     *
     * @return this
     */
    @SuppressWarnings("unchecked")
    public HttpClient<T> execute() {
        ConnectionFactory connectionFactory = new ConnectionFactory(this.requestUrl,this.connectTimeout,this.readTimeout);
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
            if (httpMethod == HttpMethod.POST || httpMethod == HttpMethod.PUT) {
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
                    this.data = objectMapper.readValue(result.toString(), this.responseType);
                } catch (Exception c) {
                    this.data = (T) result.toString();
                }
                return this;
            }
            this.bufferedReader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), Charset.defaultCharset()));
            while ((line = this.bufferedReader.readLine()) != null) {
                result.append(line);
            }
            this.fallbackHelper.fallback(result.toString());
        } catch (IOException e) {
            this.fallbackHelper.fallback(e.getMessage());
        }
        return this;
    }

    /**
     * Fallback in case of an error
     *
     * @param fallbackHelper FallbackHelper
     * @return this
     */
    public HttpClient<T> fallback(FallBackHelper<String> fallbackHelper) {
        this.fallbackHelper = fallbackHelper;
        return this;
    }

    /**
     * Get Result
     *
     * @return T
     */
    public T get() {
        return this.data;
    }

    /**
     * Listen to the result and process it
     *
     * @param resultListener listener
     */
    public void listener(Listener<T> resultListener) {
        resultListener.notify(this.data);
    }

    /**
     * Set request header
     *
     * @param urlConnection HttpUrlConnection
     */
    private void setHeader(HttpURLConnection urlConnection) {
        for (Map.Entry<String, String> headerEntry : header.entrySet()) {
            urlConnection.setRequestProperty(headerEntry.getKey(), headerEntry.getValue());
        }
    }

    /**
     * Close stream
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
