package cn.gjing.tools.excel.read;


import cn.gjing.tools.excel.Listener;
import cn.gjing.tools.excel.resolver.ExcelReaderResolver;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Excel reader
 *
 * @author Gjing
 **/
public class ExcelReader<T> implements Closeable {

    /**
     * excel class
     */
    private Class<T> excelClass;
    /**
     * excel读处理器
     */
    private ExcelReaderResolver readerResolver;
    /**
     * 读到的结果
     */
    private List<T> data = new ArrayList<>();
    /**
     * 输入流
     */
    private InputStream inputStream;
    /**
     * 列表头下标
     */
    private int headerIndex;

    /**
     * 读取截止位
     */
    private int endIndex;

    private ExcelReader() {

    }

    public ExcelReader(Class<T> excelClass, InputStream inputStream) {
        this.excelClass = excelClass;
        this.inputStream = inputStream;
        this.readerResolver = new ExcelReadResolver();
        this.init();
    }

    /**
     * 初始化序号
     */
    private void init() {
        this.headerIndex = 0;
        this.endIndex = 0;
    }

    /**
     * 读Excel
     *
     * @return this
     */
    @SuppressWarnings("unchecked")
    public ExcelReader<T> read() {
        this.readerResolver.read(this.inputStream, this.excelClass, listener -> data = (List<T>) listener, this.headerIndex, this.endIndex, "sheet1");
        this.init();
        return this;
    }

    /**
     * 读Excel
     *
     * @param sheetName sheet名称
     * @return this
     */
    @SuppressWarnings("unchecked")
    public ExcelReader<T> read(String sheetName) {
        this.readerResolver.read(this.inputStream, this.excelClass, listener -> data = (List<T>) listener, this.headerIndex, this.endIndex, sheetName);
        this.init();
        return this;
    }

    /**
     * 重置处理器, 该操作要在其他操作之前进行, 否则之前的操作会无效
     *
     * @param excelReaderResolver 用户定义的Excel处理器
     * @return this
     */
    public ExcelReader<T> resetResolver(Supplier<? extends ExcelReaderResolver> excelReaderResolver) {
        this.readerResolver = excelReaderResolver.get();
        return this;
    }

    /**
     * 列表头开始行
     *
     * @param index 列表头下标, 为excel文件列表头左边的序号
     * @return this
     */
    public ExcelReader<T> headerIndex(int index) {
        this.headerIndex = index - 1;
        return this;
    }

    /**
     * 读取截止行
     *
     * @param index 结束读取下标, 为excel文件列表头左边的序号
     * @return this
     */
    public ExcelReader<T> endIndex(int index) {
        this.endIndex = index;
        return this;
    }

    /**
     * 获取结果
     *
     * @return List
     */
    public List<T> get() {
        return this.data;
    }

    /**
     * 获取结果
     *
     * @param resultListener 结果监听器
     * @return this
     */
    public ExcelReader<T> listener(Listener<List<T>> resultListener) {
        resultListener.notify(this.data);
        return this;
    }

    @Override
    public void close() throws IOException {
        if (this.inputStream != null) {
            this.inputStream.close();
        }
    }
}
