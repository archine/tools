package cn.gjing.tools.excel.read.resolver.core;

import cn.gjing.tools.excel.exception.ExcelResolverException;
import cn.gjing.tools.excel.metadata.ExecType;
import cn.gjing.tools.excel.metadata.RowType;
import cn.gjing.tools.excel.read.ExcelReaderContext;
import cn.gjing.tools.excel.util.ListenerChain;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 * @author Gjing
 **/
class ExcelSimpleReadExecutor<R> extends ExcelBaseReadExecutor<R> {
    public ExcelSimpleReadExecutor(ExcelReaderContext<R> context) {
        super(context);
    }

    @Override
    public void read(int headerIndex, String sheetName) {
        super.checkSheet(sheetName);
        ListenerChain.doReadBefore(super.context.getListenerCache());
        boolean continueRead = true;
        for (Row row : super.context.getSheet()) {
            if (!continueRead) {
                break;
            }
            if (row.getRowNum() < headerIndex) {
                continueRead = super.readHeadBefore(super.context.getListenerCache(), row);
                continue;
            }
            if (row.getRowNum() == headerIndex) {
                continueRead = super.readHead(super.context.getListenerCache(), row);
                continue;
            }
            for (int i = 0, size = super.context.getHeadNames().size(); i < size; i++) {
                String head = super.context.getHeadNames().get(i);
                if ("ignored".equals(head)) {
                    continue;
                }
                Cell cell = row.getCell(i);
                Object value;
                try {
                    if (cell != null) {
                        value = this.getValue(null, cell, null, head, false, false, RowType.BODY, ExecType.SIMPLE);
                        ListenerChain.doReadCell(super.context.getListenerCache(), value, cell, row.getRowNum(), i, RowType.BODY);
                    } else {
                        ListenerChain.doReadCell(super.context.getListenerCache(), null, null, row.getRowNum(), i, RowType.BODY);
                    }
                } catch (Exception e) {
                    throw new ExcelResolverException(e.getMessage());
                }
            }
            continueRead = ListenerChain.doReadRow(super.context.getListenerCache(), null, row, RowType.BODY);
        }
        ListenerChain.doReadFinish(super.context.getListenerCache());
    }
}
