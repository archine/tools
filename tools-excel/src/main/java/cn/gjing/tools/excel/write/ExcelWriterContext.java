package cn.gjing.tools.excel.write;

import cn.gjing.tools.excel.util.ListenerChain;
import cn.gjing.tools.excel.write.listener.ExcelWriteListener;
import lombok.*;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Excel writer global context
 *
 * @author Gjing
 **/
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class ExcelWriterContext {
    /**
     * Current workbook
     */
    @Setter
    private Workbook workbook;

    /**
     * Current sheet
     */
    @Setter
    private Sheet sheet;

    /**
     * The Excel file name to be exported
     */
    @Setter
    private String fileName;

    /**
     * Whether to add validation when exporting Excel templates
     */
    @Setter
    @Builder.Default
    private Boolean needValid = false;

    /**
     * Whether to open multistage Excel headers
     */
    @Setter
    @Builder.Default
    private Boolean multiHead = false;

    /**
     * Excel headers
     */
    @Setter
    @Builder.Default
    private List<Field> excelFields = new ArrayList<>();

    /**
     * Excel header names
     */
    @Setter
    @Builder.Default
    private List<String[]> headNames = new ArrayList<>();

    /**
     * Export listener cache
     */
    @Builder.Default
    private Map<Class<? extends ExcelWriteListener>, List<ExcelWriteListener>> writeListenerCache = new HashMap<>(8);

    public ExcelWriterContext addListener(ExcelWriteListener excelWriteListener) {
        ListenerChain.addWriteListener(this.writeListenerCache, excelWriteListener);
        return this;
    }
}
