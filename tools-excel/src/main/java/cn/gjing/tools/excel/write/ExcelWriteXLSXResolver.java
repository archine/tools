package cn.gjing.tools.excel.write;

import cn.gjing.tools.excel.*;
import cn.gjing.tools.excel.resolver.ExcelWriterResolver;
import cn.gjing.tools.excel.util.BeanUtils;
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
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * XLSX type excel file resolver
 *
 * @author Gjing
 **/
class ExcelWriteXLSXResolver implements ExcelWriterResolver, AutoCloseable {
    private SXSSFWorkbook workbook;
    private int offset = 0;
    private OutputStream outputStream;
    private SXSSFSheet sheet;
    private Map<String, String[]> explicitValues;

    @Override
    public void write(List<?> data, Workbook workbook, String sheetName, List<Field> headFieldList, MetaStyle metaStyle, BigTitle bigTitle,Map<String, String[]> explicitValues) {
        this.workbook = (SXSSFWorkbook) workbook;
        this.explicitValues = explicitValues;
        SXSSFSheet sxssfSheet = this.workbook.getSheet(sheetName);
        if (sxssfSheet == null) {
            this.offset = 0;
            this.sheet = this.workbook.createSheet(sheetName);
        }
        //Read the default offset
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

    @SuppressWarnings("unchecked")
    private void setVal(List<?> data, List<Field> headFieldList, SXSSFSheet sheet, SXSSFRow row, CellStyle bodyStyle, CellStyle headStyle) {
        int validIndex = 0;
        SXSSFCell cell;
        for (int i = 0; i < headFieldList.size(); i++) {
            cell = row.createCell(i);
            cell.setCellStyle(headStyle);
            Field field = headFieldList.get(i);
            ExcelField excelField = field.getAnnotation(ExcelField.class);
            cell.setCellValue(excelField.value());
            sheet.setColumnWidth(i, excelField.width());
            if (data == null || data.isEmpty()) {
                validIndex = this.addValid(field, row, i, validIndex);
            }        }
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
                            Class<? extends Enum<?>> enumType = (Class<? extends Enum<?>>) field.getType();
                            try {
                                EnumConvert<Enum<?>, ?> enumConvert = (EnumConvert<Enum<?>, ?>) excelEnumConvert.convert().newInstance();
                                valueCell.setCellValue(enumConvert.toExcelAttribute(BeanUtils.getEnum(enumType, value.toString())).toString());
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

    private int addValid(Field field, SXSSFRow row, int i, int validIndex) {
        ExplicitValid ev = field.getAnnotation(ExplicitValid.class);
        NumericValid nv = field.getAnnotation(NumericValid.class);
        if (ev != null) {
            String[] values = this.explicitValues.get(field.getName());
            try {
                ev.validClass().newInstance().valid(ev, this.workbook, sheet, row.getRowNum() + 1, i, i, validIndex, values);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            if (values == null) {
                return validIndex;
            }
            validIndex++;
            return validIndex;
        }
        if (nv != null) {
            try {
                nv.validClass().newInstance().valid(nv, sheet, row.getRowNum() + 1, i, i);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            return validIndex;
        }
        return validIndex;
    }
}
