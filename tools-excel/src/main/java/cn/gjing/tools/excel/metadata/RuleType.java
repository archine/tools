package cn.gjing.tools.excel.metadata;

import cn.gjing.tools.excel.util.ExcelUtils;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * Multi-level header merge rules
 *
 * @author Gjing
 * @deprecated You need to control the direction of the merge dynamically by setting the {@link ExcelUtils#merge(Sheet, int, int, int, int)} in the listener
 **/
@Deprecated
public enum RuleType {
    /**
     * X: Horizontal merge priority
     * Y: Vertical merge priority
     */
    X, Y
}
