package cn.gjing.util.excel;

import cn.gjing.annotation.ExcludeParam;
import cn.gjing.annotation.NotNull;
import cn.gjing.util.ParamUtil;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Gjing
 **/
public class ExcelWrite<T> implements Closeable {

    /**
     * excel关联的实体
     */
    private Class<T> excelEntity;
    /**
     * 实体集合
     */
    private List<T> entityList;
    /**
     * 响应
     */
    private HttpServletResponse response;
    /**
     * excel注解
     */
    private Excel excel;

    /**
     * 输出流
     */
    private OutputStream outputStream;

    /**
     * 工作普
     */
    private Workbook workbook;

    private ExcelWrite() {

    }

    private ExcelWrite(Class<T> excelEntity, List<T> entityList) {
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
    @SuppressWarnings("unchecked")
    @NotNull
    public static <T> ExcelWrite of(Class<T> excelEntity,@ExcludeParam List<T> entityList) {
        return new ExcelWrite(excelEntity, entityList);
    }

    /**
     * 开始写入excel
     */
    @NotNull
    public void doWrite(HttpServletResponse response) {
        Excel excelEntityAnnotation = excelEntity.getAnnotation(Excel.class);
        if (excelEntityAnnotation == null) {
            throw new NullPointerException("@Excel was not found on the excelEntity");
        }
        this.response = response;
        this.excel = excelEntityAnnotation;
        switch (excelEntityAnnotation.type()) {
            case XLS:
                writeXls();
                break;
            case XLSX:
                writeXlsx();
                break;
            default:
        }
    }

    /**
     * 写xlx格式
     */
    private void writeXls() {
        //新建列表头装载容器
        List<String> headers = new ArrayList<>();
        HSSFWorkbook workbook = new HSSFWorkbook();
        this.workbook = workbook;
        HSSFSheet sheet = workbook.createSheet();
        // 列表头的样式
        CellStyle headerStyle = this.setHeaderStyle(workbook);
        // 单元格内容的样式
        CellStyle valueStyle = this.setValueStyle(workbook);
        //找到这个实体的所有字段
        Field[] declaredFields = excelEntity.getDeclaredFields();
        Arrays.stream(declaredFields).forEach(e -> {
            //获取这个实体的带有@ExcelFeild
            ExcelField fieldAnnotation = e.getAnnotation(ExcelField.class);
            if (fieldAnnotation != null) {
                headers.add(fieldAnnotation.name());
            }
        });
        if (!ParamUtil.equals("", excel.description())) {
            HSSFRow row = sheet.createRow(0);
            HSSFCell cell = row.createCell(0);
            cell.setCellStyle(this.setDescriptionStyle(workbook));
            //合并单元格
            sheet.addMergedRegion(new CellRangeAddress(excel.firstRow(), excel.lastRow(),
                    excel.firstCell(), excel.lastCell() == 0 ? headers.size() - 1 : excel.lastCell()));
            cell.setCellValue(excel.description());
            //添加列表头
            HSSFRow row1 = sheet.createRow(excel.lastRow() + 1);
            this.setHeader(headers, headerStyle, row1);
            //添加单元格内容
            if (entityList != null) {
                int hasRow = excel.lastRow() + 2;
                for (int i = 0; i < entityList.size(); i++) {
                    T t = entityList.get(i);
                    row = sheet.createRow(hasRow + i);
                    this.setValue(valueStyle, t, row);
                }
            }
        } else {
            //设置列表头
            HSSFRow row = sheet.createRow(0);
            this.setHeader(headers, headerStyle, row);
            //设置单元格内容
            if (entityList != null) {
                for (int i = 0; i < entityList.size(); i++) {
                    T t = entityList.get(i);
                    row = sheet.createRow(i + 1);
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
        // 列表头储存容器
        List<String> headers = new ArrayList<>();
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        this.workbook = workbook;
        SXSSFSheet sheet = workbook.createSheet();
        // 列表头的样式
        CellStyle headerStyle = this.setHeaderStyle(workbook);
        // 单元格内容的样式
        CellStyle valueStyle = this.setValueStyle(workbook);
        //找到这个实体的所有字段
        Field[] declaredFields = excelEntity.getDeclaredFields();
        Arrays.stream(declaredFields).forEach(e -> {
            //获取这个实体的带有@ExcelFeild
            ExcelField fieldAnnotation = e.getAnnotation(ExcelField.class);
            if (fieldAnnotation != null) {
                headers.add(fieldAnnotation.name());
            }
        });
        if (!ParamUtil.equals("", this.excel.description())) {
            SXSSFRow row = sheet.createRow(0);
            SXSSFCell cell = row.createCell(0);
            cell.setCellStyle(this.setDescriptionStyle(workbook));
            //合并单元格
            sheet.addMergedRegion(new CellRangeAddress(excel.firstRow(), excel.lastRow(),
                    excel.firstCell(), excel.lastCell() == 0 ? headers.size() - 1 : excel.lastCell()));
            cell.setCellValue(excel.description());
            //添加列表头
            row = sheet.createRow(excel.lastRow() + 1);
            this.setHeader(headers, headerStyle, row);
            //添加单元格内容
            if (entityList != null) {
                int hasRow = excel.lastRow() + 2;
                for (int i = 0; i < entityList.size(); i++) {
                    T t = entityList.get(i);
                    row = sheet.createRow(hasRow + i);
                    this.setValue(valueStyle, t, row);
                }
            }
        } else {
            //设置列表头
            SXSSFRow row = sheet.createRow(0);
            this.setHeader(headers, headerStyle, row);
            //设置单元格内容
            if (entityList != null) {
                for (int i = 0; i < entityList.size(); i++) {
                    T t = entityList.get(i);
                    row = sheet.createRow(i + 1);
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
            //设置列表头
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
    private void setValue(CellStyle style, T t, Row row) {
        Field[] declaredFields = t.getClass().getDeclaredFields();
        List<Field> fieldList = Arrays.stream(declaredFields).filter(e -> e.getAnnotation(ExcelField.class) != null)
                .collect(Collectors.toList());
        for (int i = 0; i < fieldList.size(); i++) {
            Cell cell = row.createCell(i);
            cell.setCellStyle(style);
            Field field = fieldList.get(i);
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
                cell.setCellValue(value.toString());
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
