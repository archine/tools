package cn.gjing.tools.excel.driven;

import cn.gjing.tools.excel.write.listener.ExcelWriteListener;
import lombok.*;

import java.util.List;

/**
 * @author Gjing
 **/
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WriteMetadata {
    /**
     * The data that needs to be exported
     */
    private List<?> data;

    /**
     * The listener used
     */
    private List<? extends ExcelWriteListener> listeners;

    /**
     * Ignore the exported headers
     */
    private String[] ignores;

    public WriteMetadata(List<?> data) {
        this.data = data;
    }

    public WriteMetadata(List<?> data, List<? extends ExcelWriteListener> listeners) {
        this.data = data;
        this.listeners = listeners;
    }
}
