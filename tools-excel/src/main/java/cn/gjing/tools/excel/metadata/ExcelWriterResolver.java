package cn.gjing.tools.excel.metadata;

import cn.gjing.tools.excel.write.BigTitle;
import cn.gjing.tools.excel.write.ExcelWriterContext;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Excel writer resolver
 *
 * @author Gjing
 **/
public interface ExcelWriterResolver {

    void init(ExcelWriterContext context);

    /**
     * Write excel big title
     *
     * @param bigTitle Excel big title
     */
    void writeTitle(BigTitle bigTitle);

    /**
     * Write excel body
     *
     * @param data          data
     * @return this
     */
    ExcelWriterResolver write(List<?> data);

    /**
     * Write excel head
     *
     * @param needHead      Is needHead excel entity or sheet?
     * @param boxValues     Excel dropdown box values
     * @return this
     */
    ExcelWriterResolver writeHead(boolean needHead, Map<String, String[]> boxValues);

    /**
     * Output the contents of the cache
     *
     * @param context Excel write context
     * @param response response
     */
    void flush(HttpServletResponse response, ExcelWriterContext context);
}
