package cn.gjing.tools.excel.write.resolver;

import cn.gjing.tools.excel.exception.ExcelResolverException;
import cn.gjing.tools.excel.metadata.ExcelWriterResolver;
import cn.gjing.tools.excel.write.BigTitle;
import cn.gjing.tools.excel.write.ExcelWriterContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * XLS resolver
 *
 * @author Gjing
 **/
class ExcelWriteXlsResolver implements ExcelWriterResolver {
    private ExcelWriteExecutor excelWriteExecutor;

    @Override
    public void init(ExcelWriterContext context) {
        this.excelWriteExecutor = new ExcelWriteExecutor(context);
    }

    @Override
    public void writeTitle(BigTitle bigTitle) {
        this.excelWriteExecutor.setBigTitle(bigTitle);
    }

    @Override
    public ExcelWriterResolver writeHead(boolean needHead, Map<String, String[]> dropdownBoxValues) {
        this.excelWriteExecutor.setHead(needHead, dropdownBoxValues);
        return this;
    }

    @Override
    public ExcelWriterResolver write(List<?> data) {
        this.excelWriteExecutor.setValue(data);
        return this;
    }

    @Override
    public void flush(HttpServletResponse response, ExcelWriterContext context) {
        response.setContentType("application/vnd.ms-excel");
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        OutputStream outputStream = null;
        try {
            if (request.getHeader("User-Agent").toLowerCase().indexOf("firefox") > 0) {
                context.setFileName(new String(context.getFileName().getBytes(StandardCharsets.UTF_8), "ISO8859-1"));
            } else {
                context.setFileName(URLEncoder.encode(context.getFileName(), "UTF-8"));
            }
            response.setHeader("Content-disposition", "attachment;filename=" + context.getFileName() + ".xls");
            outputStream = response.getOutputStream();
            context.getWorkbook().write(outputStream);
        } catch (IOException e) {
            throw new ExcelResolverException("Excel cache data refresh failure, " + e.getMessage());
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.flush();
                    outputStream.close();
                }
                if (context.getWorkbook() != null) {
                    context.getWorkbook().close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
