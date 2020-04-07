package cn.gjing.tools.excel.write.listener;

import org.apache.poi.ss.usermodel.Sheet;

/**
 * Sheet lifecycle listener
 * @author Gjing
 **/
@FunctionalInterface
public interface BaseSheetWriteListener extends WriteListener{
    /**
     * Has been created
     * @param sheet Create the finished sheet
     */
    void createdSheet(Sheet sheet);
}
