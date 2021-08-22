package cn.gjing.tools.excel.write.resolver.core;

import cn.gjing.tools.excel.exception.ExcelResolverException;
import cn.gjing.tools.excel.metadata.ExcelType;
import cn.gjing.tools.excel.metadata.ExecType;
import cn.gjing.tools.excel.util.ExcelUtils;
import cn.gjing.tools.excel.util.ListenerChain;
import cn.gjing.tools.excel.write.BigTitle;
import cn.gjing.tools.excel.write.ExcelWriterContext;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Excel writer resolver
 *
 * @author Gjing
 **/
public abstract class ExcelWriterResolver {
    protected final ExcelWriterContext context;
    protected final ExcelBaseWriteExecutor writeExecutor;

    public ExcelWriterResolver(ExcelWriterContext context, ExecType execType) {
        this.context = context;
        if (execType == ExecType.BIND) {
            this.writeExecutor = new ExcelBindWriterExecutor(context);
        } else {
            this.writeExecutor = new ExcelSimpleWriterExecutor(context);
        }
    }

    /**
     * Write excel big title
     *
     * @param bigTitle Excel big title
     */
    public void writeTitle(BigTitle bigTitle) {
        if (bigTitle.getLastCol() < 1) {
            bigTitle.setLastCol(this.context.getExcelFields().size() - 1);
        }
        if (bigTitle.getLines() < 1) {
            bigTitle.setLines(1);
        }
        if (bigTitle.getFirstCol() < 0) {
            bigTitle.setFirstCol(0);
        }
        if (bigTitle.getLines() == 1 && bigTitle.getFirstCol() == bigTitle.getLastCol()) {
            throw new ExcelResolverException("Merged region must contain 2 or more cells");
        }
        int startOffset = bigTitle.getFirstRow() == -1 ? this.context.getSheet().getPhysicalNumberOfRows() : bigTitle.getFirstRow();
        int endOffset = startOffset + bigTitle.getLines() - 1;
        Row row;
        for (int i = 0; i < bigTitle.getLines(); i++) {
            row = this.context.getSheet().getRow(startOffset + i);
            if (row == null) {
                row = this.context.getSheet().createRow(startOffset + i);
                row.setHeight(bigTitle.getRowHeight());
            }
            Cell cell = row.createCell(bigTitle.getFirstCol());
            Object content = bigTitle.getCallback().apply(this.context.getWorkbook(), bigTitle.getContent());
            if (content instanceof RichTextString) {
                if (this.context.getExcelType() == ExcelType.XLSX) {
                    throw new ExcelResolverException("XLSX does not support rich text for now");
                }
                cell.setCellValue((RichTextString) content);
            } else {
                ExcelUtils.setCellValue(cell, content);
            }
            if (i == 0) {
                ListenerChain.doSetTitleStyle(this.context.getListenerCache(), bigTitle, cell);
            }
        }
        this.context.getSheet().addMergedRegion(new CellRangeAddress(startOffset, endOffset, bigTitle.getFirstCol(), bigTitle.getLastCol()));
    }

    /**
     * Write excel body
     *
     * @param data data
     */
    public abstract void write(List<?> data);

    /**
     * Write excel header
     *
     * @param needHead  Is needHead excel entity or sheet?
     * @param boxValues Excel dropdown box values
     * @return this
     */
    public abstract ExcelWriterResolver writeHead(boolean needHead, Map<String, String[]> boxValues);

    /**
     * Output the contents of the cache
     *
     * @param context  Excel write context
     * @param response response
     */
    public void flush(HttpServletResponse response, ExcelWriterContext context) {
        response.setContentType("application/vnd.ms-excel");
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        OutputStream outputStream = null;
        try {
            if (request.getHeader("User-Agent").toLowerCase().indexOf("firefox") > 0) {
                context.setFileName(new String(context.getFileName().getBytes(StandardCharsets.UTF_8), "ISO8859-1"));
            } else {
                context.setFileName(URLEncoder.encode(context.getFileName(), "UTF-8"));
            }
            response.setHeader("Content-disposition", "attachment;filename=" + context.getFileName() + (context.getExcelType() == ExcelType.XLS ? ".xls" : ".xlsx"));
            outputStream = response.getOutputStream();
            context.getWorkbook().write(outputStream);
        } catch (IOException e) {
            throw new ExcelResolverException("Excel cache data flush failure, " + e.getMessage());
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.flush();
                    outputStream.close();
                }
                if (context.getWorkbook() != null) {
                    context.getWorkbook().close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Output the contents of the cache to local
     *
     * @param path    Absolute path to the directory where the file is stored
     * @param context Excel write context
     */
    public void flushToLocal(String path, ExcelWriterContext context) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream((path.endsWith("/") ? path : path + "/") + context.getFileName() + (context.getExcelType() == ExcelType.XLS ? ".xls" : ".xlsx"));
            context.getWorkbook().write(fileOutputStream);
        } catch (IOException e) {
            throw new ExcelResolverException("Excel cache data flush failure, " + e.getMessage());
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
                if (context.getWorkbook() != null) {
                    context.getWorkbook().close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
