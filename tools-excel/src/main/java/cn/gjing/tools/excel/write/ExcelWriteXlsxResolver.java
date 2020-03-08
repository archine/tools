package cn.gjing.tools.excel.write;

import cn.gjing.tools.excel.Excel;
import cn.gjing.tools.excel.MetaObject;
import cn.gjing.tools.excel.exception.ExcelResolverException;
import cn.gjing.tools.excel.resolver.ExcelWriterResolver;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.List;

/**
 * XLSX处理器
 *
 * @author Gjing
 **/
class ExcelWriteXlsxResolver implements ExcelWriterResolver, Closeable {
    private SXSSFWorkbook workbook;
    private OutputStream outputStream;
    private ExcelHelper excelHelper;

    @Override
    public void write(List<?> data, Workbook workbook, String sheetName, List<Field> headFieldList, MetaObject metaObject, boolean changed, Excel excel) {
        this.workbook = (SXSSFWorkbook) workbook;
        SXSSFSheet sheet = this.workbook.getSheet(sheetName);
        if (sheet == null) {
            sheet = this.workbook.createSheet(sheetName);
            changed = true;
        }
        if (excelHelper == null) {
            this.excelHelper = new ExcelHelper(this.workbook);
        }
        int rowIndex = this.excelHelper.setBigTitle(headFieldList, metaObject, sheet);
        rowIndex = this.excelHelper.setHead(data, headFieldList, sheet, changed, rowIndex, metaObject, excel);
        if (data == null || data.isEmpty()) {
            return;
        }
        this.excelHelper.setValue(data, headFieldList, sheet, rowIndex, excel);
    }

    @Override
    public void flush(HttpServletResponse response, String fileName) {
        response.setContentType("application/vnd.ms-excel");
        try {
            response.setHeader("Content-disposition",
                    "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8") + ".xlsx");
            this.outputStream = response.getOutputStream();
            this.workbook.write(outputStream);
        } catch (IOException e) {
            throw new ExcelResolverException("Excel cache data refresh failure");
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
