package cn.gjing.tools.common.valid;

import cn.gjing.tools.common.util.FileUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.util.Map;

/**
 * @author Gjing
 **/
@SuppressWarnings("rawtypes")
class ToolsParamValidationServletRequest extends HttpServletRequestWrapper {
    private final byte[] body;
    private final HttpServletRequest request;
    private final Map map;

    public ToolsParamValidationServletRequest(HttpServletRequest request) throws IOException {
        super(request);
        this.request = request;
        this.map = request.getParameterMap();
        this.body = FileUtils.readInputStream(request.getInputStream());
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(this.request.getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body);
        return new ServletInputStream() {
            @Override
            public int read() throws IOException {
                return byteArrayInputStream.read();
            }
        };
    }

    @Override
    public Map getParameterMap() {
        return this.map;
    }

    @Override
    public String getParameter(String name) {
        return this.request.getParameter(name);
    }
}
