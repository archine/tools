package cn.gjing.tools.excel.write.resolver.core;

import cn.gjing.tools.excel.metadata.ExecType;
import cn.gjing.tools.excel.write.ExcelWriterContext;

import java.util.List;
import java.util.Map;

/**
 * Xlsx resolver
 *
 * @author Gjing
 **/
public class ExcelWriteXlsxResolver extends ExcelWriterResolver {

    public ExcelWriteXlsxResolver(ExcelWriterContext context, ExecType execType) {
        super(context, execType);
    }

    @Override
    public ExcelWriterResolver writeHead(boolean needHead, Map<String, String[]> boxValues) {
        super.writeExecutor.writeHead(needHead, boxValues);
        return this;
    }

    @Override
    public void write(List<?> data) {
        super.writeExecutor.writeBody(data);
    }
}
