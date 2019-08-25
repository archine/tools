package cn.gjing.util.excel;

import cn.gjing.util.ParamUtil;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.List;

/**
 * @author Gjing
 * excel导出
 **/
class ExportExcel {

    /**
     * excel单元格内容集合,导出空白excel时传null
     */
    private List<Object[]> valueList;
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
     * 描述样式
     */
    private CellRangeAddress cellAddresses;

    private ExportExcel(List<Object[]> valueList, String[] headers, String fileName, String description,
                        CellRangeAddress cellAddresses) {
        this.valueList = valueList;
        this.headers = headers;
        this.fileName = fileName;
        this.description = description;
        this.cellAddresses = cellAddresses;
    }

    static ExportExcel of(List<Object[]> valueList, String[] headers, String fileName, String description,
                          CellRangeAddress cellAddresses) {
        return new ExportExcel(valueList, headers, fileName, description, cellAddresses);
    }

    /**
     * @param response 响应头
     */
    void generateHaveExcelName(HttpServletResponse response) {
        HSSFWorkbook wb = this.export();
        try {
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-disposition",
                    "attachment;filename=" + new String(fileName.getBytes(), "ISO8859-1") + ".xls");
            OutputStream outputStream = response.getOutputStream();
            wb.write(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成excel
     * @return excel
     */
    private HSSFWorkbook export() {
        //获取excel的样式
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet(fileName);
        //如果设置了描述描述
        if (ParamUtil.isNotEmpty(description)) {
            if (cellAddresses == null) {
                throw new NullPointerException("cellAddresses cannot be null");
            }
            //1. 设置描述
            HSSFRow row = sheet.createRow(0);
            // 合并单元格CellRangeAddress构造参数依次表示起始行，截至行，起始列， 截至列
            sheet.addMergedRegion(cellAddresses);
            HSSFCell cell = row.createCell(0);
            cell.setCellValue(description);
            cell.setCellStyle(this.setDescriptionStyle(wb));
            //2. 设置列表头
            row = sheet.createRow(cellAddresses.getLastRow() + 1);
            row.setHeight((short) 350);
            for (int i = 0; i < headers.length; i++) {
                HSSFCell cell1 = row.createCell(i);
                cell1.setCellStyle(this.setHeaderStyle(wb));
                cell1.setCellValue(headers[i]);
            }
            //设置单元格内容
            if (valueList != null) {
                int rouNum = row.getRowNum() + 1;
                for (int i = 0; i < valueList.size(); i++) {
                    Object[] obj = valueList.get(i);
                    HSSFRow row1 = sheet.createRow(rouNum+ i);
                    this.addCellValue(this.setValueStyle(wb), obj, row1);
                }
            }
        } else {
            HSSFRow row = sheet.createRow(0);
            row.setHeight((short) 350);
            // 设置列表头
            for (int i = 0; i < headers.length; i++) {
                HSSFCell cell1 = row.createCell(i);
                cell1.setCellStyle(this.setHeaderStyle(wb));
                cell1.setCellValue(headers[i]);
            }
            // 设置单元格内容
            if (valueList != null) {
                for (int i = 0; i < valueList.size(); i++) {
                    Object[] obj = valueList.get(i);
                    HSSFRow row1 = sheet.createRow(i + 1);
                    addCellValue(this.setValueStyle(wb), obj, row1);
                }
            }
        }
        return wb;
    }

    /**
     * 设置列表头的样式
     * @param workbook 工作簿
     * @return HSSFCellStyle
     */
    private HSSFCellStyle setHeaderStyle(HSSFWorkbook workbook) {
        HSSFCellStyle style = workbook.createCellStyle();
        HSSFDataFormat format = workbook.createDataFormat();
        style.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
        style.setDataFormat(format.getFormat("@"));
        this.setStyle(style);
        return style;
    }

    /**
     * 设置单元格样式
     * @param workbook 工作簿
     * @return HSSFCellStyle
     */
    private HSSFCellStyle setValueStyle(HSSFWorkbook workbook) {
        HSSFCellStyle style = workbook.createCellStyle();
        this.setStyle(style);
        return style;
    }

    /**
     * 设置excel描述的样式
     * @param workbook 工作簿
     * @return HSSFCellStyle
     */
    private HSSFCellStyle setDescriptionStyle(HSSFWorkbook workbook) {
        HSSFCellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        this.setStyle(style);
        return style;
    }

    /**
     * 设置单元格样式
     * @param style 样式
     */
    private void setStyle(HSSFCellStyle style) {
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
    }

    /**
     * 添加单元格内容
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

}
