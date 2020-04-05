package cn.gjing.tools.excel.write;

import cn.gjing.tools.excel.BigTitle;
import cn.gjing.tools.excel.Excel;
import cn.gjing.tools.excel.MetaStyle;
import cn.gjing.tools.excel.exception.ExcelResolverException;
import cn.gjing.tools.excel.listen.CustomWrite;
import cn.gjing.tools.excel.resolver.ExcelWriterResolver;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
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
    private HSSFWorkbook workbook;
    private OutputStream outputStream;
    private ExcelHelper excelHelper;

    ExcelWriteXlsResolver(HSSFWorkbook workbook) {
        this.workbook = workbook;
        this.excelHelper = new ExcelHelper(workbook);
    }

    @Override
    public ExcelWriterResolver writeTitle(BigTitle bigTitle, MetaStyle metaStyle, Sheet sheet) {
        this.excelHelper.setBigTitle(bigTitle, metaStyle, sheet);
        return this;
    }

    @Override
    public ExcelWriterResolver writeHead(boolean noContent, List<Field> headFieldList, Sheet sheet, boolean needHead, MetaStyle metaStyle,
                                         Map<String, String[]> dropdownBoxValues, Excel excel) {
        this.excelHelper.setHead(noContent, headFieldList, sheet, needHead, metaStyle, dropdownBoxValues, excel);
        return this;
    }

    @Override
    public ExcelWriterResolver write(List<?> data, Sheet sheet, List<Field> headFieldList, MetaStyle metaStyle, boolean needInit) {
        this.excelHelper.setValue(data, headFieldList, sheet, metaStyle, needInit);
        return this;
    }

    @Override
    public void customWrite(CustomWrite processor) {
        processor.process();
    }

    @Override
    public void flush(HttpServletResponse response, String fileName) {
        response.setContentType("application/vnd.ms-excel");
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        try {
            if (request.getHeader("User-Agent").toLowerCase().indexOf("firefox") > 0) {
                fileName = new String(fileName.getBytes(StandardCharsets.UTF_8), "ISO8859-1");
            } else {
                fileName = URLEncoder.encode(fileName, "UTF-8");
            }
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xls");
            this.outputStream = response.getOutputStream();
            this.workbook.write(this.outputStream);
        } catch (IOException e) {
            throw new ExcelResolverException("Excel cache data refresh failure, " + e.getMessage());
        } finally {
            try {
                if (this.outputStream != null) {
                    this.outputStream.flush();
                    this.outputStream.close();
                }
                if (this.workbook != null) {
                    this.workbook.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
