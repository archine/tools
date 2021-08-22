package cn.gjing.tools.excel.write.resolver.core;

import cn.gjing.tools.excel.metadata.ExecType;
import cn.gjing.tools.excel.write.ExcelWriterContext;

import java.util.List;
import java.util.Map;

/**
 * XLS resolver
 *
 * @author Gjing
 **/
public class ExcelWriteXlsResolver extends ExcelWriterResolver {

    public ExcelWriteXlsResolver(ExcelWriterContext context, ExecType execType) {
        super(context, execType);
    }

    @Override
    public ExcelWriterResolver writeHead(boolean needHead, Map<String, String[]> dropdownBoxValues) {
        super.writeExecutor.writeHead(needHead, dropdownBoxValues);
        return this;
    }

    @Override
    public void write(List<?> data) {
        super.writeExecutor.writeBody(data);
    }
}
