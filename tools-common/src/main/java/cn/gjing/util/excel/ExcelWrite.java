package cn.gjing.util.excel;

import cn.gjing.annotation.ExcludeParam;
import cn.gjing.annotation.NotNull;
import cn.gjing.util.ParamUtil;
import cn.gjing.util.TimeUtil;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Excel导出
 * @author Gjing
 **/
@SuppressWarnings("unused")
public class ExcelWrite implements Closeable {

    /**
     * excel关联的实体
     */
    private Class<?> excelEntity;
    /**
     * 关联的实体集合
     */
    private List<?> entityList;
    /**
     * 响应头
     */
    private HttpServletResponse response;
    /**
     * excel文件注解
     */
    private Excel excel;

    /**
     * 输出流
     */
    private OutputStream outputStream;

    /**
     * 工作簿
     */
    private Workbook workbook;

    /**
     * excel体
     */
    private Sheet sheet;

    /**
     * 列表头样式
     */
    private CellStyle headerStyle;
    /**
     * 单元格样式
     */
    private CellStyle valueStyle;

    /**
     * 存放列表头
     */
    private List<String> headers = new ArrayList<>();

    /**
     * 带有@ExcelField注解的所有字段
     */
    private List<Field> hasExcelFieldList = new ArrayList<>();

    private ExcelWrite() {

    }

    private ExcelWrite(Class<?> excelEntity, List<?> entityList) {
        this.excelEntity = excelEntity;
        this.entityList = entityList;
    }

    /**
     * 生成excel读实例
     *
     * @param excelEntity 实体
     * @param entityList  实体列表
     * @param <T>         t
     * @return ExcelWrite
     */
    @NotNull
    public static <T> ExcelWrite of(Class<T> excelEntity, @ExcludeParam List<T> entityList) {
        return new ExcelWrite(excelEntity, entityList);
    }

    /**
     * 开始写入excel
     */
    @NotNull
    public void doWrite(HttpServletResponse response) {
        Excel excelAnnotation = excelEntity.getAnnotation(Excel.class);
        if (excelAnnotation == null) {
            throw new NullPointerException("@Excel was not found on the excelEntity");
        }
        this.response = response;
        this.excel = excelAnnotation;
        //得到所有声明字段
        Field[] declaredFields = excelEntity.getDeclaredFields();
        //找到所有带@ExcelField注解的字段
        this.hasExcelFieldList = Arrays.stream(declaredFields).filter(e -> e.getAnnotation(ExcelField.class) != null)
                .collect(Collectors.toList());
        //获取列表头
        this.headers = this.hasExcelFieldList.stream().map(e -> e.getAnnotation(ExcelField.class).name())
                .collect(Collectors.toList());
        switch (excelAnnotation.type()) {
            case XLS:
                this.workbook = new HSSFWorkbook();
                this.sheet = workbook.createSheet();
                this.headerStyle = setHeaderStyle(this.workbook);
                this.valueStyle = setValueStyle(this.workbook);
                this.writeXls();
                break;
            case XLSX:
                this.workbook = new SXSSFWorkbook();
                this.sheet = workbook.createSheet();
                this.headerStyle = setHeaderStyle(this.workbook);
                this.valueStyle = setValueStyle(this.workbook);
                this.writeXlsx();
                break;
            default:
                throw new NullPointerException("Doc type was not found");
        }
    }

    /**
     * 写xlx格式
     */
    private void writeXls() {
        if (!ParamUtil.equals("", excel.description())) {
            HSSFRow row = (HSSFRow) sheet.createRow(0);
            HSSFCell cell = row.createCell(0);
            cell.setCellStyle(this.setDescriptionStyle(workbook));
            //合并单元格
            sheet.addMergedRegion(new CellRangeAddress(excel.firstRow(), excel.lastRow(),
                    excel.firstCell(), excel.lastCell() == 0 ? headers.size() - 1 : excel.lastCell()));
            cell.setCellValue(excel.description());
            //添加列表头
            HSSFRow row1 = (HSSFRow) sheet.createRow(excel.lastRow() + 1);
            this.setHeader(headers, headerStyle, row1);
            //添加单元格内容
            if (this.entityList != null) {
                int hasRow = excel.lastRow() + 2;
                for (int i = 0; i < entityList.size(); i++) {
                    Object t = entityList.get(i);
                    row = (HSSFRow) sheet.createRow(hasRow + i);
                    this.setValue(valueStyle, t, row);
                }
            }
        } else {
            //设置列表头
            HSSFRow row = (HSSFRow) sheet.createRow(0);
            this.setHeader(headers, headerStyle, row);
            //设置单元格内容
            if (this.entityList != null) {
                for (int i = 0; i < entityList.size(); i++) {
                    Object t = entityList.get(i);
                    row = (HSSFRow) sheet.createRow(i + 1);
                    this.setValue(valueStyle, t, row);
                }
            }
        }
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-disposition",
                "attachment;filename=" + new String(excel.name().getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1) + ".xls");
        try {
            outputStream = response.getOutputStream();
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 写xlxs格式
     */
    private void writeXlsx() {
        if (!ParamUtil.equals("", this.excel.description())) {
            SXSSFRow row = (SXSSFRow) sheet.createRow(0);
            SXSSFCell cell = row.createCell(0);
            cell.setCellStyle(this.setDescriptionStyle(workbook));
            //合并单元格
            this.sheet.addMergedRegion(new CellRangeAddress(excel.firstRow(), excel.lastRow(),
                    excel.firstCell(), excel.lastCell() == 0 ? headers.size() - 1 : excel.lastCell()));
            cell.setCellValue(excel.description());
            //添加列表头
            this.setHeader(headers, headerStyle, sheet.createRow(excel.lastRow() + 1));
            //添加单元格内容
            if (this.entityList != null) {
                int hasRow = excel.lastRow() + 2;
                for (int i = 0; i < entityList.size(); i++) {
                    Object t = entityList.get(i);
                    row = (SXSSFRow) sheet.createRow(hasRow + i);
                    this.setValue(valueStyle, t, row);
                }
            }
        } else {
            //设置列表头
            this.setHeader(headers, headerStyle, sheet.createRow(0));
            //设置单元格内容
            if (this.entityList != null) {
                for (int i = 0; i < entityList.size(); i++) {
                    Object t = entityList.get(i);
                    Row row = sheet.createRow(i + 1);
                    this.setValue(valueStyle, t, row);
                }
            }
        }
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-disposition",
                "attachment;filename=" + new String(excel.name().getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1) + ".xlsx");
        try {
            outputStream = response.getOutputStream();
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置列表头
     *
     * @param headers     excel表头数据
     * @param headerStyle 列表头样式
     * @param row         列表行
     */
    private void setHeader(List<String> headers, CellStyle headerStyle, Row row) {
        for (int i = 0; i < headers.size(); i++) {
            //设置每列的宽度
            this.sheet.setColumnWidth(i, hasExcelFieldList.get(i).getAnnotation(ExcelField.class).width());
            row.setHeight((short) 300);
            Cell cell1 = row.createCell(i);
            cell1.setCellStyle(headerStyle);
            cell1.setCellValue(headers.get(i));
        }
    }

    /**
     * 添加单元格内容
     *
     * @param style 单元格样式
     * @param t     对象
     * @param row   excel列
     */
    private void setValue(CellStyle style, Object t, Row row) {
        for (int i = 0; i < this.hasExcelFieldList.size(); i++) {
            Cell cell = row.createCell(i);
            cell.setCellStyle(style);
            Field field = this.hasExcelFieldList.get(i);
            ExcelField excelField = field.getAnnotation(ExcelField.class);
            field.setAccessible(true);
            Object value = null;
            try {
                value = field.get(t);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (value == null) {
                cell.setCellValue("");
            } else {
                if (ParamUtil.equals("", excelField.pattern())) {
                    cell.setCellValue(value.toString());
                } else {
                    cell.setCellValue(TimeUtil.dateToString((Date) value, excelField.pattern()));
                }
            }
        }
    }

    /**
     * 设置excel表头样式
     *
     * @param workbook workbook
     * @return CellStyle
     */
    private CellStyle setHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
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
     * @param workbook workbook
     * @return CellStyle
     */
    private CellStyle setValueStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        //自动换行
        style.setWrapText(this.excel.autoWrap());
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        return style;
    }

    /**
     * 设置excel额外内容的样式
     *
     * @param workbook workbook
     * @return CellStyle
     */
    private CellStyle setDescriptionStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    /**
     * 关闭流
     * @throws IOException io
     */
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
