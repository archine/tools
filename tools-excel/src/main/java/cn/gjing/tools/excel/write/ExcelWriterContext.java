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
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class ExcelWriterContext {
    /**
     * Current workbook
     */
    private Workbook workbook;

    /**
     * Current sheet
     */
    private Sheet sheet;

    /**
     * The Excel file name to be exported
     */
    private String fileName;

    /**
     * Whether to add validation when exporting Excel templates
     */
    @Builder.Default
    private Boolean needValid = false;

    /**
     * Whether to open multistage Excel headers
     */
    @Builder.Default
    private Boolean multiHead = false;

    /**
     * Excel headers
     */
    @Builder.Default
    private List<Field> excelFields = new ArrayList<>();

    /**
     * Excel header names
     */
    @Builder.Default
    private List<String[]> headNames = new ArrayList<>();

    /**
     * Excel mapping class
     */
    private Class<?> excelClass;

    /**
     * Whether you need to add a file identifier when exporting an Excel file,
     * which can be used for template validation of the file at import time
     */
    @Builder.Default
    private boolean identifier = true;

    /**
     * Export listener cache
     */
    @Builder.Default
    private Map<Class<? extends ExcelWriteListener>, List<ExcelWriteListener>> writeListenerCache = new HashMap<>(8);

    /**
     * Add write listener
     *
     * @param excelWriteListener excelWriteListener
     */
    public void addListener(ExcelWriteListener excelWriteListener) {
        ListenerChain.addWriteListener(this.writeListenerCache, excelWriteListener, this.workbook);
    }
}
