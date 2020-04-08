package cn.gjing.tools.excel.write.callback;

import java.lang.reflect.Field;

/**
 * Default auto merge callback of export excel
 * @author Gjing
 **/
public class DefaultAutoMergeCallback implements AutoMergeCallback<Object> {
    @Override
    public boolean mergeY(Object o, Field field, int colIndex, int index) {
        return true;
    }
}
