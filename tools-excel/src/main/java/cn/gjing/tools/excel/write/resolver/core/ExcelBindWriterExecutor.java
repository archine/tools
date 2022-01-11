package cn.gjing.tools.excel.write.resolver.core;

import cn.gjing.tools.excel.convert.ExcelDataConvert;
import cn.gjing.tools.excel.metadata.ExcelFieldProperty;
import cn.gjing.tools.excel.metadata.RowType;
import cn.gjing.tools.excel.util.BeanUtils;
import cn.gjing.tools.excel.util.ExcelUtils;
import cn.gjing.tools.excel.util.ListenerChain;
import cn.gjing.tools.excel.write.ExcelWriterContext;
import cn.gjing.tools.excel.write.valid.handle.ExcelValidAnnotationHandler;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * Export processor for Excel bind mode
 *
 * @author Gjing
 **/
class ExcelBindWriterExecutor extends ExcelBaseWriteExecutor {
    public ExcelBindWriterExecutor(ExcelWriterContext context) {
        super(context);
    }

    /**
     * Set excel head
     *
     * @param needHead  Whether to set header
     * @param boxValues Excel dropdown box value
     */
    @Override
    public void writeHead(boolean needHead, Map<String, String[]> boxValues) {
        if (!needHead || this.context.getFieldProperties().isEmpty()) {
            this.context.setExistHead(false);
            return;
        }
        Row headRow;
        String[] currentRowHeadArray = new String[this.context.getFieldProperties().size()];
        for (int index = 0; index < this.context.getHeaderSeries(); index++) {
            ListenerChain.doCreateRowBefore(this.context.getListenerCache(), this.context.getSheet(), index, RowType.HEAD);
            headRow = this.context.getSheet().createRow(this.context.getSheet().getPhysicalNumberOfRows());
            if (this.context.getHeaderHeight() > 0) {
                headRow.setHeight(this.context.getHeaderHeight());
            }
            for (int colIndex = 0, headSize = this.context.getFieldProperties().size(); colIndex < headSize; colIndex++) {
                ExcelFieldProperty property = this.context.getFieldProperties().get(colIndex);
                String headName = property.getValue()[index];
                currentRowHeadArray[colIndex] = headName;
                Cell headCell = headRow.createCell(headRow.getPhysicalNumberOfCells());
                ListenerChain.doSetHeadStyle(this.context.getListenerCache(), headRow, headCell, property, index, colIndex);
                headName = (String) ListenerChain.doAssignmentBefore(this.context.getListenerCache(), this.context.getSheet(), headRow, headCell,
                        property, index, headCell.getColumnIndex(), RowType.HEAD, headName);
                headCell.setCellValue(headName);
                if (this.context.isNeedValid() && index == this.context.getHeaderSeries() - 1) {
                    Field field = this.context.getExcelFields().get(colIndex);
                    for (ExcelValidAnnotationHandler validAnnotationHandler : this.context.getValidAnnotationHandlers()) {
                        Annotation annotation = this.context.getExcelFields().get(colIndex).getAnnotation(validAnnotationHandler.getAnnotationClass());
                        if (annotation != null) {
                            validAnnotationHandler.handle(annotation, this.context, field, headRow, colIndex, boxValues);
                            break;
                        }
                    }
                }
                ListenerChain.doCompleteCell(this.context.getListenerCache(), this.context.getSheet(), headRow, headCell, property, index,
                        headCell.getColumnIndex(), RowType.HEAD);
            }
            ListenerChain.doCompleteRow(this.context.getListenerCache(), this.context.getSheet(), headRow, currentRowHeadArray, index, RowType.HEAD);
        }
    }

    /**
     * Set excel body
     *
     * @param data Export data
     */
    @Override
    public void writeBody(List<?> data) {
        EvaluationContext context = new StandardEvaluationContext();
        for (int index = 0, dataSize = data.size(); index < dataSize; index++) {
            Object o = data.get(index);
            context.setVariable(o.getClass().getSimpleName(), o);
            ListenerChain.doCreateRowBefore(this.context.getListenerCache(), this.context.getSheet(), index, RowType.BODY);
            Row valueRow = this.context.getSheet().createRow(this.context.getSheet().getPhysicalNumberOfRows());
            if (this.context.getBodyHeight() > 0) {
                valueRow.setHeight(this.context.getBodyHeight());
            }
            for (int colIndex = 0, headSize = this.context.getExcelFields().size(); colIndex < headSize; colIndex++) {
                Field field = this.context.getExcelFields().get(colIndex);
                ExcelFieldProperty property = this.context.getFieldProperties().get(colIndex);
                Object value = BeanUtils.getFieldValue(o, field);
                Cell valueCell = valueRow.createCell(valueRow.getPhysicalNumberOfCells());
                context.setVariable(field.getName(), value);
                ListenerChain.doSetBodyStyle(this.context.getListenerCache(), valueRow, valueCell, property, index, colIndex);
                value = this.convert(value, o, field.getAnnotation(ExcelDataConvert.class), context,
                        this.createDataConvert(colIndex, property));
                value = ListenerChain.doAssignmentBefore(this.context.getListenerCache(), this.context.getSheet(),
                        valueRow, valueCell, property, index, valueCell.getColumnIndex(), RowType.BODY, value);
                ExcelUtils.setCellValue(valueCell, value);
                if (property.isAutoMerge()) {
                    this.autoMergeY(this.createMergeCallback(colIndex, property), valueRow, property.isMergeEmpty(), index,
                            valueCell.getColumnIndex(), value, o, dataSize, field);
                }
                ListenerChain.doCompleteCell(this.context.getListenerCache(), this.context.getSheet(), valueRow, valueCell,
                        property, index, valueCell.getColumnIndex(), RowType.BODY);
            }
            ListenerChain.doCompleteRow(this.context.getListenerCache(), this.context.getSheet(), valueRow, o, index, RowType.BODY);
        }
    }
}
