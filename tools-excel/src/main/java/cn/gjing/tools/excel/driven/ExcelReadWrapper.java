package cn.gjing.tools.excel.driven;

import cn.gjing.tools.excel.read.listener.ExcelReadListener;
import cn.gjing.tools.excel.read.listener.ExcelResultReadListener;
import lombok.Getter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel annotation-driven meta data wrapper
 *
 * @author Gjing
 **/
@Getter
@ToString
public class ExcelReadWrapper<R> {
    private List<ExcelReadListener> readListeners;
    private ExcelResultReadListener<R> resultReadListener;
    private InputStream inputStream;

    public ExcelReadWrapper() {
    }

    public ExcelReadWrapper(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public ExcelReadWrapper(File file) throws FileNotFoundException {
        this.inputStream = new FileInputStream(file);
    }

    public ExcelReadWrapper(MultipartFile file) throws IOException {
        this.inputStream = file.getInputStream();
    }

    /**
     * Set data
     *
     * @param file Excel file
     * @return this
     * @throws IOException IOException
     */
    public ExcelReadWrapper<R> data(MultipartFile file) throws IOException {
        this.inputStream = file.getInputStream();
        return this;
    }

    /**
     * Set data
     *
     * @param file Excel file
     * @return this
     * @throws FileNotFoundException FileNotFoundException
     */
    public ExcelReadWrapper<R> data(File file) throws FileNotFoundException {
        this.inputStream = new FileInputStream(file);
        return this;
    }

    /**
     * Set data
     *
     * @param inputStream Excel file inputStream
     * @return this
     */
    public ExcelReadWrapper<R> data(InputStream inputStream) {
        this.inputStream = inputStream;
        return this;
    }

    /**
     * Add read listener
     *
     * @param readListener readListener
     * @return this
     */
    public ExcelReadWrapper<R> addListener(ExcelReadListener readListener) {
        if (this.readListeners == null) {
            this.readListeners = new ArrayList<>();
        }
        this.readListeners.add(readListener);
        return this;
    }

    /**
     * Add read listener
     *
     * @param readListeners readListener
     * @return this
     */
    public ExcelReadWrapper<R> addListener(List<ExcelReadListener> readListeners) {
        if (this.readListeners == null) {
            this.readListeners = new ArrayList<>();
        }
        this.readListeners.addAll(readListeners);
        return this;
    }

    /**
     * Subscribe to all data at the end of the Excel read
     *
     * @param resultListener resultListener
     * @return this
     */
    public ExcelReadWrapper<R> subscribe(ExcelResultReadListener<R> resultListener) {
        this.resultReadListener = resultListener;
        return this;
    }
}
