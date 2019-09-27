package cn.gjing.tools.excel.read;

import cn.gjing.tools.excel.resolver.ExcelReaderResolver;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Excel reader
 *
 * @author Gjing
 **/
public class ExcelReader<T> implements Closeable {

    private Class<T> excelClass;
    private ExcelReaderResolver excelReaderResolver;
    private List<T> data = new ArrayList<>();
    private InputStream inputStream;
    private int titleRow = 0;

    public ExcelReader(Class<T> excelClass, InputStream inputStream) {
        this.excelClass = excelClass;
        this.inputStream = inputStream;
        this.excelReaderResolver = new ExcelReadResolver().builder(inputStream);
    }

    /**
     * 读Excel
     *
     * @return 指定类型的实体集合
     */
    @SuppressWarnings("unchecked")
    public List<T> read() {
        this.excelReaderResolver.read(this.excelClass, acceptList -> data = (List<T>) acceptList,this.titleRow);
        return this.data;
    }

    /**
     * 读Excel
     *
     * @param acceptList 接收读取完毕后的实体集合
     */
    public void read(Consumer<List<Object>> acceptList) {
        this.excelReaderResolver.read(this.excelClass, acceptList,this.titleRow);
    }

    /**
     * 使用用户自定义的处理器
     *
     * @param supplier 用户自定义的Excel处理器
     * @return ExcelReader
     */
    public ExcelReader<T> changeResolver(Supplier<? extends ExcelReaderResolver> supplier) {
        this.excelReaderResolver = supplier.get().builder(this.inputStream);
        return this;
    }

    /**
     * 如果excel文件有大标题，需要指定大标题所占行数
     * @param row 行数
     * @return ExcelReader
     */
    public ExcelReader<T> titleRow(int row) {
        this.titleRow = row;
        return this;
    }

    @Override
    public void close() throws IOException {
        if (inputStream != null) {
            this.inputStream.close();
        }
    }
}
