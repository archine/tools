package cn.gjing.tools.excel.write;

import cn.gjing.tools.excel.*;
import cn.gjing.tools.excel.resolver.ExcelWriterResolver;
import cn.gjing.tools.excel.util.ParamUtils;
import cn.gjing.tools.excel.util.TimeUtils;
import cn.gjing.tools.excel.valid.DateValid;
import cn.gjing.tools.excel.valid.ExcelValidation;
import cn.gjing.tools.excel.valid.ExplicitValid;
import cn.gjing.tools.excel.valid.NumericValid;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import javax.servlet.http.HttpServletResponse;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * XLS resolver
 *
 * @author Gjing
 **/
@SuppressWarnings("all")
class ExcelWriteXLSResolver implements ExcelWriterResolver, Closeable {
    private HSSFWorkbook workbook;
    private HSSFSheet sheet;
    private HttpServletResponse response;
    private List<String> header;
    private List<Field> fieldList;
    private BigTitle bigTitle;
    private String fileName;
    private int startRow = 0;
    private OutputStream outputStream;
    private HSSFCellStyle headerStyle;
    private HSSFCellStyle bigTitleStyle;
    private HSSFCellStyle contentStyle;
    private DataValidation dateValidation;
    private DataValidation explicitValidation;
    private DataValidation numberValidation;

    @Override
    public ExcelWriterResolver builder(Workbook workbook, HttpServletResponse response, List<String> header, List<Field> fieldList, String fileName) {
        this.workbook = (HSSFWorkbook) workbook;
        this.response = response;
        this.header = header;
        this.fieldList = fieldList;
        this.fileName = fileName;
        this.headerStyle = (HSSFCellStyle) new ExcelHeaderStyle().style(workbook.createCellStyle());
        this.bigTitleStyle = (HSSFCellStyle) new ExcelTitleStyle().style(workbook.createCellStyle());
        this.contentStyle = (HSSFCellStyle) new ExcelContentStyle().style(workbook.createCellStyle());
        return this;
    }

    @Override
    public void write(List<?> excelClassList) {
        HSSFSheet sheet = this.workbook.createSheet();
        this.sheet = sheet;
        HSSFRow row;
        HSSFCell cell;
        if (bigTitle != null) {
            if (bigTitle.isFront()) {
                this.startRow = bigTitle.getLastRow() + 1;
                sheet.addMergedRegion(new CellRangeAddress(0, bigTitle.getLastRow(), 0, this.header.size() - 1));
                row = sheet.createRow(0);
            } else {
                int bigTitleStartRow = excelClassList.size() + 1;
                sheet.addMergedRegion(new CellRangeAddress(bigTitleStartRow, bigTitleStartRow + bigTitle.getLastRow(), 0, this.header.size()));
                row = sheet.createRow(bigTitleStartRow);
            }
            cell = row.createCell(0);
            //set big title value
            cell.setCellStyle(this.bigTitleStyle);
            cell.setCellValue(this.bigTitle.getContent());
            HSSFRow headerRow = sheet.createRow(startRow);
            this.setVal(excelClassList, sheet, headerRow);
        } else {
            HSSFRow headerRow = sheet.createRow(startRow);
            this.setVal(excelClassList, sheet, headerRow);
        }
        this.response.setContentType("application/vnd.ms-excel");
        this.response.setHeader("Content-disposition",
                "attachment;filename=" + new String(fileName.getBytes(StandardCharsets.UTF_8),
                        StandardCharsets.ISO_8859_1) + ".xls");
        try {
            this.outputStream = response.getOutputStream();
            this.workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setBigTitle(BigTitle bigTitle) {
        this.bigTitle = bigTitle;
    }

    @Override
    public void setHeaderStyle(Supplier<? extends ExcelStyle> headerStyle) {
        this.headerStyle = (HSSFCellStyle) headerStyle.get().style(this.workbook.createCellStyle());
    }

    @Override
    public void setBigTitleStyle(Supplier<? extends ExcelStyle> bigTitleStyle) {
        this.bigTitleStyle = (HSSFCellStyle) bigTitleStyle.get().style(this.workbook.createCellStyle());
    }

    @Override
    public void setContentStyle(Supplier<? extends ExcelStyle> contentStyle) {
        this.contentStyle = (HSSFCellStyle) contentStyle.get().style(this.workbook.createCellStyle());
    }

    @Override
    public void setDateValidation(Supplier<? extends ExcelValidation> dateValidation) {
        this.dateValidation = dateValidation.get().valid(this.sheet);
    }

    @Override
    public void setExplicitValidation(Supplier<? extends ExcelValidation> explicitValidation) {
        this.explicitValidation = explicitValidation.get().valid(this.sheet);
    }

    @Override
    public void setNumberValidation(Supplier<? extends ExcelValidation> numberValidation) {
        this.numberValidation = numberValidation.get().valid(this.sheet);
    }

    @Override
    public void close() throws IOException {
        if (outputStream != null) {
            outputStream.flush();
            outputStream.close();
        }
    }

    private void setVal(List<?> excelClassList, HSSFSheet sheet, HSSFRow row) {
        HSSFCell cell;
        //设置列表头
        for (int i = 0; i < header.size(); i++) {
            cell = row.createCell(i);
            cell.setCellStyle(this.headerStyle);
            cell.setCellValue(header.get(i));
            Field field = fieldList.get(i);
            sheet.setColumnWidth(i, field.getAnnotation(ExcelField.class).width());
            //查看是否需要校验
            ExplicitValid explicitValid = field.getAnnotation(ExplicitValid.class);
            DateValid dateValid = field.getAnnotation(DateValid.class);
            NumericValid numericValid = field.getAnnotation(NumericValid.class);
            if (explicitValid != null) {
                sheet.addValidationData(this.explicitValidation == null
                        ? new ExcelExplicitValidation(explicitValid, row.getRowNum() + 1, i).valid(sheet)
                        : this.explicitValidation);
            }
            if (dateValid != null) {
                sheet.addValidationData(this.dateValidation == null
                        ? new ExcelDateValidation(dateValid, i, row.getRowNum() + 1).valid(sheet)
                        : this.dateValidation);
            }
            if (numericValid != null) {
                sheet.addValidationData(this.numberValidation == null
                        ? new ExcelNumberValidation(numericValid, row.getRowNum() + 1, i).valid(sheet)
                        : this.numberValidation);
            }
        }
        //设置正文
        int valueStartRow = this.startRow + 1;
        if (excelClassList == null) {
            return;
        }
        for (int i = 0; i < excelClassList.size(); i++) {
            Object o = excelClassList.get(i);
            row = sheet.createRow(valueStartRow + i);
            for (int j = 0; j < fieldList.size(); j++) {
                HSSFCell valueCell = row.createCell(j);
                valueCell.setCellStyle(this.contentStyle);
                Field field = this.fieldList.get(j);
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
                            Convert convert = field.getAnnotation(Convert.class);
                            Objects.requireNonNull(convert, "Enum convert cannot be null");
                            Class<? extends Enum> enumType = (Class<? extends Enum>) field.getType();
                            try {
                                EnumConvert enumConvert = convert.convert().newInstance();
                                valueCell.setCellValue(enumConvert.toDatabaseColumn(Enum.valueOf(enumType, ((Enum) value).name())).toString());
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
}
