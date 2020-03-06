package cn.gjing.tools.excel.write;

import cn.gjing.tools.excel.listen.MergeCallback;

import java.lang.reflect.Field;

/**
 * @author Gjing
 **/
public class DefaultMergeCallback implements MergeCallback<Object> {
    @Override
    public boolean toMerge(Object o, Field field, int colIndex, int rowIndex) {
        return true;
    }
}
