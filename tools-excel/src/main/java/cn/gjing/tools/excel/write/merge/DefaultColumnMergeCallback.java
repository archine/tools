package cn.gjing.tools.excel.write.merge;

import cn.gjing.tools.excel.write.callback.AutoColumnMergeCallback;

import java.lang.reflect.Field;

/**
 * @author Gjing
 **/
public class DefaultColumnMergeCallback implements AutoColumnMergeCallback<Object> {
    @Override
    public boolean toMerge(Object o, Field field, int colIndex, int rowIndex) {
        return true;
    }
}
