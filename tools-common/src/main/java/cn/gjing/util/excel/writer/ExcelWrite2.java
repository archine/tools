package cn.gjing.util.excel.writer;

import com.onesports.framework.wolf.common.annotation.Exclude;
import com.onesports.framework.wolf.common.annotation.NotNull;
import com.onesports.framework.wolf.common.util.ParamUtil;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import javax.servlet.http.HttpServletResponse;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Excel导出
 * @author Gjing
 **/
@Deprecated
@SuppressWarnings("unused")
public class ExcelWrite2 implements Closeable {
    /**
     * 要导出到excel的内容
     */
    private List<Object[]> data;
    /**
     * excel列表头
     */
    private String[] headers;
    /**
     * excel文件名
     */
    private String fileName;
    /**
     * excel描述
     */
    private String description;

    /**
     * 单元格合并对象
     */
    private CellRangeAddress cellAddresses;

    /**
     * 输出流
     */
    private OutputStream outputStream;

    /**
     * excel对象
     */
    private HSSFWorkbook workbook;

    private ExcelWrite2() {
    }

    private ExcelWrite2(List<Object[]> data, String[] headers, String fileName, String description,
                        CellRangeAddress cellAddresses) {
        this.data = data;
        this.headers = headers;
        this.fileName = fileName;
        this.description = description;
        this.cellAddresses = cellAddresses;
    }

    /**
     * 生成ExcelWrite2实例
     *
     * @param data          要导出的数据
     * @param headers       列表头
     * @param fileName      文件名
     * @param description   描述
     * @param cellAddresses 描述区域的样式
     * @return ExcelWrite2
     */
    @NotNull
    public static ExcelWrite2 of(@Exclude List<Object[]> data, String[] headers, String fileName, String description,
                                 CellRangeAddress cellAddresses) {
        return new ExcelWrite2(data, headers, fileName, description, cellAddresses);
    }

    /**
     * 生成ExcelWrite2实例
     *
     * @param data     要导出的数据
     * @param headers  列表头
     * @param fileName 文件名
     * @return ExcelWrite2
     */
    @NotNull
    public static ExcelWrite2 of(@Exclude List<Object[]> data, String[] headers, String fileName) {
        return new ExcelWrite2(data, headers, fileName, null, null);
    }

    /**
     * @param response 响应头
     */
    public void doWrite(HttpServletResponse response) {
        HSSFWorkbook wb = this.write();
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-disposition",
                "attachment;filename=" + new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1) + ".xls");
        try {
            this.outputStream = response.getOutputStream();
            wb.write(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成excel
     *
     * @return excel
     */
    private HSSFWorkbook write() {
        //获取excel的样式
        HSSFWorkbook wb = new HSSFWorkbook();
        this.workbook = wb;
        HSSFSheet sheet = wb.createSheet(fileName);
        // 列表头的样式
        HSSFCellStyle headerStyle = this.setHeaderStyle(wb);
        // 单元格内容的样式
        HSSFCellStyle valueStyle = this.setValueStyle(wb);
        //设置描述
        if (ParamUtil.isNotEmpty(description)) {
            HSSFRow row = sheet.createRow(0);
            HSSFCell cell = row.createCell(0);
            cell.setCellStyle(setOutlineStyle(wb));
            sheet.addMergedRegion(this.cellAddresses);
            cell.setCellValue(description);
            //添加excel的列表头
            HSSFRow row1 = sheet.createRow(cellAddresses.getLastRow() + 1);
            for (int i = 0; i < headers.length; i++) {
                //设置列表头
                row1.setHeight((short) 300);
                sheet.setColumnWidth(0,20*256);
                HSSFCell cell1 = row1.createCell(i);
                cell1.setCellStyle(headerStyle);
                cell1.setCellValue(headers[i]);
            }
            // 设置单元格内容
            if (data != null) {
                int alreadyRow = cellAddresses.getLastRow() + 2;
                for (int i = 0; i < data.size(); i++) {
                    Object[] obj = data.get(i);
                    row = sheet.createRow(i + alreadyRow);
                    addCellValue(valueStyle, obj, row);
                }
            }
        } else {
            //添加excel的列表头
            HSSFRow row = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                //设置列表头
                row.setHeight((short) 300);
                sheet.setColumnWidth(0,20*256);
                HSSFCell cell = row.createCell(i);
                cell.setCellStyle(headerStyle);
                cell.setCellValue(headers[i]);
            }
            // 设置单元格内容
            if (data != null) {
                for (int i = 0; i < data.size(); i++) {
                    Object[] obj = data.get(i);
                    HSSFRow row1 = sheet.createRow(i + 1);
                    addCellValue(valueStyle, obj, row1);
                }
            }
        }
        return wb;
    }

    /**
     * 添加单元格内容
     *
     * @param style 单元格样式
     * @param obj    内容集
     * @param row    excel列
     */
    private void addCellValue(HSSFCellStyle style, Object[] obj, HSSFRow row) {
        for (int j = 0; j < obj.length; j++) {
            HSSFCell cell;
            cell = row.createCell(j);
            cell.setCellStyle(style);
            if (ParamUtil.isNotEmpty(obj[j])) {
                cell.setCellValue(obj[j].toString());
            } else {
                cell.setCellValue("");
            }
        }
    }

    /**
     * 设置excel表头样式
     *
     * @param hssfWorkbook HSSFWorkbook
     * @return HSSFCellStyle
     */
    private HSSFCellStyle setHeaderStyle(HSSFWorkbook hssfWorkbook) {
        HSSFCellStyle style = hssfWorkbook.createCellStyle();
        HSSFDataFormat format = hssfWorkbook.createDataFormat();
        style.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
        style.setDataFormat(format.getFormat("@"));
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    /**
     * 设置单元格内容样式
     *
     * @param hssfWorkbook HSSFWorkbook
     * @return HSSFCellStyle
     */
    private HSSFCellStyle setValueStyle(HSSFWorkbook hssfWorkbook) {
        HSSFCellStyle style = hssfWorkbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        return style;
    }

    /**
     * 设置excel额外内容的样式
     *
     * @param hssfWorkbook HSSFWorkbook
     * @return HSSFCellStyle
     */
    private HSSFCellStyle setOutlineStyle(HSSFWorkbook hssfWorkbook) {
        HSSFCellStyle style = hssfWorkbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    @Override
    public void close() throws IOException {
        if (outputStream != null) {
            outputStream.flush();
            outputStream.close();
        }
        if (workbook != null) {
            workbook.close();
        }
    }
}
