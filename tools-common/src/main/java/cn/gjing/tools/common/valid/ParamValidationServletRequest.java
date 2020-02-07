package cn.gjing.tools.common.valid;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;

/**
 * @author Gjing
 **/
class ParamValidationServletRequest extends HttpServletRequestWrapper {
    private final byte[] body;

    public ParamValidationServletRequest(HttpServletRequest request) throws IOException {
        super(request);
        this.body = this.readBytes(request.getInputStream());
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

    public String getBody() {
        return new String(this.body);
    }

    private byte[] readBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int length;
        byte[] bytes = new byte[1024];
        while ((length = inputStream.read(bytes)) != -1) {
            byteArrayOutputStream.write(bytes, 0, length);
        }
        return byteArrayOutputStream.toByteArray();
    }
}
