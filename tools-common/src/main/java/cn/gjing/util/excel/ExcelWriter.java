package cn.gjing.util.excel;

import cn.gjing.annotation.Exclude;
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
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Excel导出
 *
 * @author Gjing
 **/
@SuppressWarnings("unused")
public class ExcelWriter implements AutoCloseable {

    /**
     * excel关联的实体
     */
    private Class<?> excelClass;
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
    private List<String> headers;

    /**
     * 带有@ExcelField注解的所有字段
     */
    private List<Field> hasExcelFieldList;

    /**
     * 忽略导出的字段
     */
    private String[] ignores;

    private ExcelWriter() {

    }

    private ExcelWriter(Class<?> excelClass, List<?> entityList, String... ignores) {
        this.excelClass = excelClass;
        this.entityList = entityList;
        this.ignores = ignores;
    }

    /**
     * 生成excel读实例
     *
     * @param excelClass 实体
     * @param entityList 实体列表
     * @param ignores    忽略的字段
     * @param <T>        t
     * @return ExcelWrite
     */
    @NotNull
    public static <T> ExcelWriter of(Class<T> excelClass, @Exclude List<T> entityList, @Exclude String... ignores) {

        return new ExcelWriter(excelClass, entityList, ignores);
    }

    /**
     * 开始写入excel
     */
    @NotNull
    public void doWrite(HttpServletResponse response) {
        // 拿到实体class的注解, 没有的话说明没有与excel关联, 不进行导出
        Excel excelAnnotation = excelClass.getAnnotation(Excel.class);
        if (excelAnnotation == null) {
            throw new NullPointerException("@Excel was not found on the excelClass");
        }
        this.response = response;
        this.excel = excelAnnotation;
        // 得到所有声明字段
        Field[] declaredFields = excelClass.getDeclaredFields();
        // 找到所有带@ExcelField注解的字段
        this.hasExcelFieldList = Arrays.stream(declaredFields)
                .filter(e -> e.isAnnotationPresent(ExcelField.class))
                .filter(e -> !ParamUtil.contains(ignores, e.getName()))
                .collect(Collectors.toList());
        //获取列表头
        this.headers = this.hasExcelFieldList.stream()
                .map(e -> e.getAnnotation(ExcelField.class).name())
                .collect(Collectors.toList());
        switch (excelAnnotation.type()) {
            case XLS:
                this.workbook = new HSSFWorkbook();
                this.sheet = workbook.createSheet();
                this.headerStyle = setHeaderStyle(this.workbook);
                this.valueStyle = setValueStyle(this.workbook);
                this.writeXLS();
                break;
            case XLSX:
                this.workbook = new SXSSFWorkbook();
                this.sheet = workbook.createSheet();
                this.headerStyle = setHeaderStyle(this.workbook);
                this.valueStyle = setValueStyle(this.workbook);
                this.writeXLSX();
                break;
            default:
                throw new NullPointerException("Doc type was not found");
        }
    }

    /**
     * 写xlx格式
     */
    private void writeXLS() {
        if (!ParamUtil.equals("", excel.description())) {
            HSSFRow row = (HSSFRow) sheet.createRow(excel.firstRow());
            HSSFCell cell = row.createCell(excel.firstCell());
            cell.setCellStyle(this.setDescriptionStyle(workbook));
            // 添加描述
            sheet.addMergedRegion(new CellRangeAddress(excel.firstRow(), excel.lastRow(),
                    excel.firstCell(), excel.lastCell() == 0
                    ? headers.size() - 1
                    : excel.lastCell()));
            cell.setCellValue(excel.description());
            // 添加列表头
            HSSFRow headerRow = (HSSFRow) sheet.createRow(excel.lastRow() + 1);
            this.setHeader(headers, headerRow);
            // 添加单元格内容
            if (this.entityList != null) {
                int hasRow = excel.lastRow() + 2;
                for (int i = 0; i < entityList.size(); i++) {
                    Object t = entityList.get(i);
                    row = (HSSFRow) sheet.createRow(hasRow + i);
                    this.setValue(t, row);
                }
            }
        } else {
            // 设置列表头
            HSSFRow row = (HSSFRow) sheet.createRow(0);
            this.setHeader(headers, row);
            // 设置单元格内容
            if (this.entityList != null) {
                for (int i = 0; i < entityList.size(); i++) {
                    Object t = entityList.get(i);
                    row = (HSSFRow) sheet.createRow(i + 1);
                    this.setValue(t, row);
                }
            }
        }
        this.response.setContentType("application/vnd.ms-excel");
        this.response.setHeader("Content-disposition",
                "attachment;filename=" + new String(excel.name().getBytes(StandardCharsets.UTF_8),
                        StandardCharsets.ISO_8859_1) + ".xls");
        try {
            this.outputStream = response.getOutputStream();
            this.workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 写xlsx格式
     */
    private void writeXLSX() {
        if (!ParamUtil.equals("", this.excel.description())) {
            SXSSFRow row = (SXSSFRow) sheet.createRow(excel.firstRow());
            SXSSFCell cell = row.createCell(excel.firstCell());
            cell.setCellStyle(this.setDescriptionStyle(workbook));
            // 添加描述
            this.sheet.addMergedRegion(new CellRangeAddress(excel.firstRow(), excel.lastRow(),
                    excel.firstCell(), excel.lastCell() == 0 ? headers.size() - 1 : excel.lastCell()));
            cell.setCellValue(excel.description());
            // 添加列表头
            this.setHeader(this.headers, this.sheet.createRow(excel.lastRow() + 1));
            // 添加单元格内容
            if (this.entityList != null) {
                int hasRow = excel.lastRow() + 2;
                for (int i = 0; i < entityList.size(); i++) {
                    Object t = entityList.get(i);
                    row = (SXSSFRow) sheet.createRow(hasRow + i);
                    this.setValue( t, row);
                }
            }
        } else {
            // 设置列表头
            this.setHeader(headers, sheet.createRow(0));
            // 设置单元格内容
            if (this.entityList != null) {
                for (int i = 0; i < entityList.size(); i++) {
                    Object t = entityList.get(i);
                    Row row = sheet.createRow(i + 1);
                    this.setValue(t, row);
                }
            }
        }
        this.response.setContentType("application/vnd.ms-excel");
        this.response.setHeader("Content-disposition",
                "attachment;filename=" + new String(excel.name().getBytes(StandardCharsets.UTF_8),
                        StandardCharsets.ISO_8859_1) + ".xlsx");
        try {
            this.outputStream = response.getOutputStream();
            this.workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置列表头
     *
     * @param headers     excel表头数据
     * @param row         列表行
     */
    private void setHeader(List<String> headers, Row row) {
        for (int i = 0; i < headers.size(); i++) {
            // 设置每列的宽度
            this.sheet.setColumnWidth(i, hasExcelFieldList.get(i).getAnnotation(ExcelField.class).width());
            row.setHeight((short) 300);
            Cell cell1 = row.createCell(i);
            cell1.setCellStyle(this.headerStyle);
            cell1.setCellValue(headers.get(i));
        }
    }

    /**
     * 添加单元格内容
     * @param t     对象
     * @param row   excel列
     */
    private void setValue(Object t, Row row) {
        for (int i = 0; i < this.hasExcelFieldList.size(); i++) {
            Cell cell = row.createCell(i);
            cell.setCellStyle(this.valueStyle);
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
                    if (field.getType().isEnum()) {
                        try {
                            Method method = field.getType().getDeclaredMethod("from", field.getType());
                            Object data = field.get(t);
                            cell.setCellValue(method.invoke(data, data).toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return;
                    } else {
                        cell.setCellValue(value.toString());
                    }
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
        style.setFillForegroundColor(this.excel.headerColor().getIndex());
        style.setWrapText(this.excel.autoWrap());
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
        style.setFillForegroundColor(this.excel.valueColor().getIndex());
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
        style.setFillForegroundColor(this.excel.descriptionColor().getIndex());
        style.setWrapText(true);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    /**
     * 关闭流
     *
     * @throws IOException io
     */
    @Override
    public void close() throws IOException {
        if (outputStream != null) {
            this.outputStream.flush();
            this.outputStream.close();
        }
        if (workbook != null) {
            this.workbook.close();
        }
    }
}
