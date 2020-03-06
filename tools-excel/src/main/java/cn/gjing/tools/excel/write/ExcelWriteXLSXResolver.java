package cn.gjing.tools.excel.write;

import cn.gjing.tools.excel.MetaObject;
import cn.gjing.tools.excel.resolver.ExcelWriterResolver;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.List;

/**
 * XLSX处理器
 *
 * @author Gjing
 **/
class ExcelWriteXLSXResolver implements ExcelWriterResolver, Closeable {
    private SXSSFWorkbook workbook;
    private OutputStream outputStream;
    private ExcelHelper excelHelper;

    @Override
    public void write(List<?> data, Workbook workbook, String sheetName, List<Field> headFieldList, MetaObject metaObject, boolean changed) {
        this.workbook = (SXSSFWorkbook) workbook;
        SXSSFSheet sheet = this.workbook.getSheet(sheetName);
        if (sheet == null) {
            changed = true;
            sheet = this.workbook.createSheet(sheetName);
        }
        if (excelHelper == null) {
            this.excelHelper = new ExcelHelper(this.workbook);
        }
        int offset = this.excelHelper.setBigTitle(headFieldList, metaObject, sheet);
        this.excelHelper.setVal(data, headFieldList, sheet, changed, offset, metaObject);
    }

    @Override
    public void flush(HttpServletResponse response, String fileName) {
        response.setContentType("application/vnd.ms-excel");
        try {
            response.setHeader("Content-disposition",
                    "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8") + ".xlsx");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            this.outputStream = response.getOutputStream();
            this.workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException {
        if (this.outputStream != null) {
            this.outputStream.flush();
            this.outputStream.close();
        }
        if (workbook != null) {
            this.workbook.close();
        }
    }
}
