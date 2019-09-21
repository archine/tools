package cn.gjing.tools.excel.valid;

import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * Excel校验器
 * @author Gjing
 **/
public interface ExcelValidation {
    DataValidation valid(Sheet sheet);
}
