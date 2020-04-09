package cn.gjing.tools.excel.driven;

import cn.gjing.tools.excel.read.listener.ExcelReadListener;
import cn.gjing.tools.excel.read.listener.ExcelResultReadListener;
import lombok.Getter;
import lombok.Setter;

import java.io.InputStream;
import java.util.List;

/**
 * @author Gjing
 **/
@Getter
@Setter
@SuppressWarnings("rawtypes")
public class ReadMetadata {
    /**
     * File inputStream
     */
    private InputStream inputStream;
    /**
     * Read completion result listener
     */
    private ExcelResultReadListener resultReadListener;
    /**
     * Read excel listeners
     */
    private List<ExcelReadListener> readListeners;

    public ReadMetadata(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public ReadMetadata(InputStream inputStream, ExcelResultReadListener<?> resultReadListener) {
        this.inputStream = inputStream;
        this.resultReadListener = resultReadListener;
    }

    public ReadMetadata(InputStream inputStream, List<ExcelReadListener> readListeners) {
        this.inputStream = inputStream;
        this.readListeners = readListeners;
    }

    public ReadMetadata(InputStream inputStream, ExcelResultReadListener<?> resultReadListener, List<ExcelReadListener> readListeners) {
        this.inputStream = inputStream;
        this.resultReadListener = resultReadListener;
        this.readListeners = readListeners;
    }
}
