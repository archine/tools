package cn.gjing.tools.excel.write;

import cn.gjing.tools.excel.metadata.ExcelType;
import cn.gjing.tools.excel.metadata.listener.ExcelWriteListener;
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class ExcelWriterContext {
    /**
     * Current workbook
     */
    @Getter
    @Setter
    private Workbook workbook;

    /**
     * Current sheet
     */
    @Getter
    @Setter
    private Sheet sheet;

    /**
     * Excel file name
     */
    @Getter
    @Setter
    private String fileName;

    /**
     * Whether validation annotations are enabled
     */
    @Builder.Default
    @Getter
    @Setter
    private boolean needValid = false;

    /**
     * Whether to open multistage Excel headers
     */
    @Builder.Default
    @Getter
    @Setter
    private boolean multiHead = false;

    /**
     * Excel header fields
     */
    @Builder.Default
    @Getter
    @Setter
    private List<Field> excelFields = new ArrayList<>();

    /**
     * Excel header names
     */
    @Builder.Default
    @Getter
    @Setter
    private List<String[]> headNames = new ArrayList<>();

    /**
     * Excel mapping class
     */
    @Getter
    @Setter
    private Class<?> excelClass;

    /**
     * Whether you need to add a file identifier when exporting an Excel file,
     * which can be used for template validation of the file at import time
     */
    @Builder.Default
    @Getter
    @Setter
    private boolean bind = true;

    /**
     * The unique key
     */
    @Getter
    @Setter
    private String uniqueKey;

    /**
     * Excel type
     */
    @Builder.Default
    @Getter
    @Setter
    private ExcelType excelType = ExcelType.XLS;

    /**
     * Export listener cache
     */
    @Builder.Default
    @Getter
    private final Map<Class<? extends ExcelWriteListener>, List<ExcelWriteListener>> writeListenerCache = new HashMap<>(8);

    /**
     * Excel head row height
     */
    @Builder.Default
    @Getter
    @Setter
    private short headerHeight = 400;

    /**
     * Excel body row height
     */
    @Builder.Default
    @Getter
    @Setter
    private short bodyHeight = 370;

    /**
     * Excel header series
     */
    @Builder.Default
    @Getter
    @Setter
    private int headerSeries = 1;
}
