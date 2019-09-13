package cn.gjing.http;

import com.google.gson.Gson;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * @author Gjing
 **/
class HttpClientImpl implements HttpClient, Closeable {
    /**
     * request url
     */
    private String requestUrl;

    /**
     * request header map
     */
    private Map<String, String> header;
    /**
     * parameter string
     */
    private String paramsStr;

    /**
     * http method
     */
    private HttpMethod httpMethod;

    /**
     * connection
     */
    private HttpURLConnection connection;

    /**
     * json string
     */
    private String json;

    /**
     * writer
     */
    private BufferedWriter bufferedWriter;
    /**
     * reader
     */
    private BufferedReader bufferedReader;

    /**
     * gson
     */
    private Gson gson = new Gson();

    HttpClientImpl(String url, HttpMethod method) {
        this.requestUrl = url;
        this.httpMethod = method;
    }

    /**
     * carry request header
     *
     * @param header request header map
     * @return HttpClient
     */
    @Override
    public HttpClient withHeader(Map<String, String> header) {
        assert header != null;
        this.header = header;
        return this;
    }

    /**
     * carry request parameter map
     *
     * @param queryMap parameter map
     * @return HttpClient
     */
    @Override
    public HttpClient withParam(Map<String, ?> queryMap) {
        assert queryMap != null;
        String paramString = this.paramsStr = UrlUtil.paramUnicodeSort(queryMap, true, false);
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
    @Override
    public HttpClient withBody(Object json) {
        if (httpMethod != HttpMethod.POST) {
            return this;
        }
        assert json != null;
        this.json = json instanceof String ? json.toString() : gson.toJson(json);
        return this;
    }

    /**
     * send request
     *
     * @param responseType response type
     * @param <T>          T
     * @return T
     */
    @Override
    public <T> T execute(Class<T> responseType) {
        ConnectionFactory connectionFactory = new ConnectionFactory(requestUrl);
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
                return resultCast(result.toString(), responseType);
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
     * result change
     *
     * @param result       result string
     * @param responseType response type
     * @param <T>          T
     * @return T
     */
    @SuppressWarnings("unchecked")
    private <T> T resultCast(String result, Class<T> responseType) {
        try {
            return gson.fromJson(result, responseType);
        } catch (Exception e) {
            return (T) result;
        }
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
