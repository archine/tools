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
@SuppressWarnings("unchecked")
class ToolsParamValidationServletRequest extends HttpServletRequestWrapper {
    private final byte[] body;
    private final Map<String,String[]> map;

    public ToolsParamValidationServletRequest(HttpServletRequest request) throws IOException {
        super(request);
        this.map = request.getParameterMap();
        this.body = FileUtils.readInputStream(request.getInputStream());
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
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
    public Map<String,String[]> getParameterMap() {
        return this.map;
    }

    @Override
    public String[] getParameterValues(String name) {
        return getParameterMap().get(name);
    }
}
