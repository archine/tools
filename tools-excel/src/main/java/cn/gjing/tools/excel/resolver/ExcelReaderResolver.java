package cn.gjing.tools.excel.resolver;


import java.io.InputStream;
import java.util.List;
import java.util.function.Consumer;

/**
 * Excel reader resolver
 * @author Gjing
 **/
public interface ExcelReaderResolver {

    /**
     * 创建ExcelReaderResolver
     *
     * @param inputStream Excel文件输入流
     * @return ExcelReaderResolver
     */
    ExcelReaderResolver builder(InputStream inputStream);

    /**
     * 读Excel
     *
     * @param excelClass Excel对应的实体Class
     * @param acceptList 接收数据的集合
     * @param titleRow 大标题占用行
     */
    void read(Class<?> excelClass, Consumer<List<Object>> acceptList,int titleRow);
}
