package cn.gjing.tools.excel.write;

import cn.gjing.tools.excel.*;
import cn.gjing.tools.excel.resolver.ExcelWriterResolver;
import cn.gjing.tools.excel.util.ParamUtils;
import cn.gjing.tools.excel.util.TimeUtils;
import cn.gjing.tools.excel.valid.ExcelValidation;
import cn.gjing.tools.excel.valid.ExplicitValid;
import cn.gjing.tools.excel.valid.NumericValid;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;
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
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * XLSX处理器
 *
 * @author Gjing
 **/
class ExcelWriteXLSXResolver implements ExcelWriterResolver, Closeable {
    /**
     * 工作簿
     */
    private SXSSFWorkbook workbook;

    /**
     * 偏移量
     */
    private int offset = 0;
    /**
     * 输出流
     */
    private OutputStream outputStream;

    /**
     * sheet
     */
    private SXSSFSheet sheet;

    @Override
    public void write(List<?> data, Workbook workbook, String sheetName, List<Field> headFieldList, MetaStyle metaStyle, BigTitle bigTitle) {
        this.workbook = (SXSSFWorkbook) workbook;
        SXSSFSheet sxssfSheet = this.workbook.getSheet(sheetName);
        if (sxssfSheet == null) {
            this.offset = 0;
            this.sheet = this.workbook.createSheet(sheetName);
        }
        //读取默认的偏移量
        this.offset = this.sheet.getLastRowNum() == 0 ? 0 : this.sheet.getLastRowNum() + 1;
        SXSSFRow row;
        SXSSFCell cell;
        if (bigTitle != null) {
            int titleOffset = this.offset + bigTitle.getLastRow() - 1;
            sheet.addMergedRegion(new CellRangeAddress(this.offset, titleOffset, 0, headFieldList.size() - 1));
            row = sheet.createRow(this.offset);
            cell = row.createCell(0);
            cell.setCellStyle(metaStyle.getTitleStyle());
            cell.setCellValue(bigTitle.getContent());
            this.offset = titleOffset + 1;
            SXSSFRow headerRow = sheet.createRow(this.offset);
            this.setVal(data, headFieldList, this.sheet, headerRow, metaStyle.getBodyStyle(), metaStyle.getHeadStyle());
        } else {
            SXSSFRow headerRow = sheet.createRow(this.offset);
            this.setVal(data, headFieldList, this.sheet, headerRow, metaStyle.getBodyStyle(), metaStyle.getHeadStyle());
        }

    }

    @Override
    public void flush(HttpServletResponse response, String fileName) {
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-disposition",
                "attachment;filename=" + new String(fileName.getBytes(StandardCharsets.UTF_8),
                        StandardCharsets.ISO_8859_1) + ".xlsx");
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

    @SuppressWarnings("unchecked")
    private void setVal(List<?> data, List<Field> headFieldList, SXSSFSheet sheet, SXSSFRow row, CellStyle bodyStyle, CellStyle headStyle) {
        ExcelValidation explicitValidation = null;
        ExcelValidation numericValidation = null;
        SXSSFCell cell;
        //设置列表头
        for (int i = 0; i < headFieldList.size(); i++) {
            cell = row.createCell(i);
            cell.setCellStyle(headStyle);
            Field field = headFieldList.get(i);
            ExcelField excelField = field.getAnnotation(ExcelField.class);
            cell.setCellValue(excelField.value());
            sheet.setColumnWidth(i, excelField.width());
            this.addValid(field, row, i, explicitValidation, numericValidation);
        }
        //设置正文
        this.offset++;
        if (data == null) {
            return;
        }
        for (int i = 0; i < data.size(); i++) {
            Object o = data.get(i);
            row = sheet.createRow(this.offset + i);
            for (int j = 0; j < headFieldList.size(); j++) {
                SXSSFCell valueCell = row.createCell(j);
                valueCell.setCellStyle(bodyStyle);
                Field field = headFieldList.get(j);
                ExcelField excelField = field.getAnnotation(ExcelField.class);
                field.setAccessible(true);
                Object value = null;
                try {
                    value = field.get(o);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                if (value == null) {
                    valueCell.setCellValue("");
                } else {
                    if (ParamUtils.equals("", excelField.pattern())) {
                        if (field.getType().isEnum()) {
                            ExcelEnumConvert excelEnumConvert = field.getAnnotation(ExcelEnumConvert.class);
                            Objects.requireNonNull(excelEnumConvert, "Enum convert cannot be null");
                            Class<? extends Enum> enumType = (Class<? extends Enum>) field.getType();
                            try {
                                EnumConvert enumConvert = excelEnumConvert.convert().newInstance();
                                valueCell.setCellValue(enumConvert.toExcelAttribute(Enum.valueOf(enumType, ((Enum) value).name())).toString());
                            } catch (InstantiationException | IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        } else {
                            valueCell.setCellValue(value.toString());
                        }
                    } else {
                        valueCell.setCellValue(TimeUtils.dateToString((Date) value, excelField.pattern()));
                    }
                }
            }
        }
    }

    private void addValid(Field field, SXSSFRow row, int i, ExcelValidation explicitValidation, ExcelValidation numericValidation) {
        ExplicitValid ev = field.getAnnotation(ExplicitValid.class);
        NumericValid nv = field.getAnnotation(NumericValid.class);
        if (ev != null) {
            try {
                if (explicitValidation == null) {
                    explicitValidation = ev.validClass().newInstance();
                }
                explicitValidation.valid(ev, this.workbook, sheet, row.getRowNum() + 1, i, i);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if (nv != null) {
            try {
                if (numericValidation == null) {
                    numericValidation = nv.validClass().newInstance();
                }
                numericValidation.valid(nv, sheet, row.getRowNum() + 1, i, i);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
