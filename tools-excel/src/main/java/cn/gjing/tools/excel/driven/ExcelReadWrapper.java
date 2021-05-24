package cn.gjing.tools.excel.driven;

import cn.gjing.tools.excel.exception.ExcelInitException;
import cn.gjing.tools.excel.exception.ExcelTemplateException;
import cn.gjing.tools.excel.metadata.listener.ExcelReadListener;
import cn.gjing.tools.excel.read.listener.ExcelResultReadListener;
import cn.gjing.tools.excel.util.ParamUtils;
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
    private final Class<R> mapping;
    private List<ExcelReadListener> readListeners;
    private ExcelResultReadListener<R> resultReadListener;
    private InputStream inputStream;
    private String[] ignores;
    private String unqKey;

    private ExcelReadWrapper(Class<R> mapping) {
        this.mapping = mapping;
    }

    /**
     * Build a read wrapper
     *
     * @param mapping Excel mapping entity
     * @param <R>     Data generic
     * @return ExcelReadWrapper
     */
    public static <R> ExcelReadWrapper<R> build(Class<R> mapping) {
        return new ExcelReadWrapper<>(mapping);
    }

    /**
     * Set data
     *
     * @param file Excel file
     * @return this
     */
    public ExcelReadWrapper<R> data(MultipartFile file) {
        try {
            if (!ParamUtils.isExcel(file.getOriginalFilename())) {
                throw new ExcelTemplateException("File type does not belong to Excel");
            }
            this.inputStream = file.getInputStream();
        } catch (IOException e) {
            throw new ExcelInitException("Initialize read wrapper error," + e.getMessage());
        }
        return this;
    }

    /**
     * Set data
     *
     * @param file Excel file
     * @return this
     */
    public ExcelReadWrapper<R> data(File file) {
        try {
            if (!ParamUtils.isExcel(file.getName())) {
                throw new ExcelTemplateException("File type does not belong to Excel");
            }
            this.inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new ExcelInitException("Initialize read wrapper error," + e.getMessage());
        }
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
    public ExcelReadWrapper<R> listener(ExcelReadListener readListener) {
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
    public ExcelReadWrapper<R> listener(List<ExcelReadListener> readListeners) {
        if (this.readListeners == null) {
            this.readListeners = new ArrayList<>();
        }
        this.readListeners.addAll(readListeners);
        return this;
    }

    /**
     * Subscribe to all data at the end of the Excel read,
     * Subscribing to data in this way requires specifying the returned data List generics
     *
     * @param resultListener resultListener
     * @return this
     */
    public ExcelReadWrapper<R> subscribe(ExcelResultReadListener<R> resultListener) {
        this.resultReadListener = resultListener;
        return this;
    }

    /**
     * Which excel heads to be ignored when importing
     *
     * @param ignores ignore head
     * @return this
     */
    public ExcelReadWrapper<R> ignores(String... ignores) {
        this.ignores = ignores;
        return this;
    }

    /**
     * Check whether the imported Excel file matches the Excel mapping entity class.
     * Thrown {@link ExcelTemplateException} if there is don't match.
     *
     * @param key Unique key
     * @return this
     **/
    public ExcelReadWrapper<R> check(String key) {
        this.unqKey = key;
        return this;
    }
}
