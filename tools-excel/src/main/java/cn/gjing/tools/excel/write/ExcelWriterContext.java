package cn.gjing.tools.excel.write;

import cn.gjing.tools.excel.write.listener.*;
import lombok.*;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Excel writer context
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
    private Boolean needValid = false;
    /**
     * Whether there are more than one header
     */
    private Boolean multiHead = false;
    /**
     * Excel headers
     */
    private List<Field> excelFields = new ArrayList<>();
    /**
     * Excel header names
     */
    private List<String[]> headNames = new ArrayList<>();
    /**
     * Export listener cache
     */
    private Map<Class<? extends ExcelWriteListener>, List<ExcelWriteListener>> writeListenerCache = new HashMap<>(8);
}
