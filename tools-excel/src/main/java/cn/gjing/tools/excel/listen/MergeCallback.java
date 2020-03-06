package cn.gjing.tools.excel.listen;

import java.lang.reflect.Field;

/**
 * @author Gjing
 **/
public interface MergeCallback<T> {
    /**
     * Whether to merge or not
     *
     * @param t Current object
     * @param field Current field
     * @param colIndex colIndex
     * @param rowIndex  rowIndex
     * @return boolean
     */
    boolean toMerge(T t, Field field, int colIndex, int rowIndex);
}
