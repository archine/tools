package cn.gjing.excel;

import cn.gjing.ex.ParamException;
import cn.gjing.enums.HttpStatus;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import cn.gjing.ParamUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.List;

/**
 * @author Gjing
 * Excel export
 **/
class ExportExcel {

    /**
     * @param response 响应头
     * @param list     excel单元格内容集合,导出空白excel时传null
     * @param headers  excel列表头
     * @param title    excel文件名
     * @param info     excel额外的内容,如果不需要直接传null或者空字符串
     */
    static void generateHaveExcelName(HttpServletResponse response, List<Object[]> list, String[] headers, String title, String info) {
        if (ParamUtil.multiEmpty(response,headers,title)) {
            throw new ParamException(HttpStatus.PARAM_EMPTY.getMsg());
        }
        HSSFWorkbook wb = export(list, headers, title, info);
        try {
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-disposition",
                    "attachment;filename=" + new String(title.getBytes(), "ISO8859-1") + ".xlsx");
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
     * @param list    单元格内容集合
     * @param headers excel列表头
     * @param title   excel标题
     * @param info    excel额外的内容,如果不需要直接传null或者空字符串
     * @return excel
     */
    private static HSSFWorkbook export(List<Object[]> list, String[] headers, String title, String info) {
        //获取excel的样式
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet(title);
        sheet.setDefaultColumnWidth(15);
        HSSFRow row = sheet.createRow(0);
        //标题背景
        HSSFCellStyle style = wb.createCellStyle();
        HSSFDataFormat format = wb.createDataFormat();
        style.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
        style.setDataFormat(format.getFormat("@"));
        style.setAlignment(HorizontalAlignment.CENTER);
        setStyle(style);
        //内容背景
        HSSFCellStyle style2 = wb.createCellStyle();
        style2.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        style2.setAlignment(HorizontalAlignment.CENTER);
        style2.setVerticalAlignment(VerticalAlignment.CENTER);
        setStyle(style2);
        //添加excel的列表头
        for (int i = 0; i < headers.length; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellStyle(style);
            cell.setCellValue(headers[i]);
        }
        //如果含有excel的简介
        if (ParamUtil.isNotEmpty(info)) {
            //添加额外的excel内容
            row = sheet.createRow(1);
            HSSFCell cell;
            cell = row.createCell(0);
            cell.setCellStyle(style2);
            // 合并单元格CellRangeAddress构造参数依次表示起始行，截至行，起始列， 截至列. ========(合并4行)
            sheet.addMergedRegion(new CellRangeAddress(1, 4, 0, headers.length - 1));
            cell.setCellValue(info);
            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    Object[] obj = list.get(i);
                    HSSFRow row1 = sheet.createRow(i + 5);
                    row1.setHeight((short) (25 * 30));
                    addCellValue(style2, obj, row1);
                }
            }
        } else {
            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    Object[] obj = list.get(i);
                    HSSFRow row1 = sheet.createRow(i + 1);
                    row1.setHeight((short) (25 * 30));
                    addCellValue(style2, obj, row1);
                }
            }
        }
        return wb;
    }

    /**
     * 设置单元格样式
     * @param style 样式
     */
    private static void setStyle(HSSFCellStyle style) {
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
    }

    /**
     * 添加单元格内容
     * @param style2 单元格样式
     * @param obj    内容集
     * @param row    excel列
     */
    private static void addCellValue(HSSFCellStyle style2, Object[] obj, HSSFRow row) {
        for (int j = 0; j < obj.length; j++) {
            HSSFCell cell;
            cell = row.createCell(j);
            cell.setCellStyle(style2);
            if (!"".equals(obj[j]) && obj[j] != null) {
                cell.setCellValue(obj[j].toString());
            } else {
                cell.setCellValue("");
            }
        }
    }

}
