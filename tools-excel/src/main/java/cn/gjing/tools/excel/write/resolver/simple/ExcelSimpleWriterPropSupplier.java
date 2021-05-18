package cn.gjing.tools.excel.write.resolver.simple;

import cn.gjing.tools.excel.metadata.ExcelColor;
import cn.gjing.tools.excel.write.callback.ExcelAutoMergeCallback;
import cn.gjing.tools.excel.write.listener.ExcelStyleWriteListener;

/**
 * Simple type export supplier that can provide properties at export time,
 * This is set by {@link ExcelSimpleWriter#supply}
 *
 * @author Gjing
 **/
public interface ExcelSimpleWriterPropSupplier {
    /**
     * Whether null values need to be merged when {@link ExcelSimpleWriter#}
     *
     * @param colIndex Current column index
     * @return merge policy
     */
    default boolean mergeEmpty(int colIndex) {
        return false;
    }

    /**
     * Get the column width when {@link ExcelStyleWriteListener#setHeadStyle}
     *
     * @param colIndex Current column index
     * @return width
     */
    default int getColWidth(int colIndex) {
        return 5120;
    }

    /**
     * Get the excel head background-color when {@link ExcelStyleWriteListener#setHeadStyle}
     *
     * @param colIndex Current column index
     * @return ExcelColor
     */
    default ExcelColor[] getHeadColor(int colIndex) {
        return new ExcelColor[]{ExcelColor.LIME};
    }

    /**
     * Get the excel head font-color when {@link ExcelStyleWriteListener#setHeadStyle}
     *
     * @param colIndex Current column index
     * @return ExcelColor
     */
    default ExcelColor[] getHeadFontColor(int colIndex) {
        return new ExcelColor[]{ExcelColor.WHITE};
    }

    /**
     * Get cell format of the current column is set when {@link ExcelStyleWriteListener#setHeadStyle}
     *
     * @param colIndex Current column index
     * @return format
     */
    default String getFormat(int colIndex) {
        return "";
    }

    /**
     * Data conversion is performed when exporting
     *
     * @param colIndex Current column index
     * @param value    Current filed value
     * @return The converted value
     */
    default Object convert(int colIndex, Object value) {
        return value;
    }

    /**
     * Gets body automatic merge callback
     *
     * @param colIndex Current column index
     * @param head     Current excel head name
     * @return ExcelAutoMergeCallback
     */
    default ExcelAutoMergeCallback<?> getAutoMergeCallback(String head, int colIndex) {
        return null;
    }
}
