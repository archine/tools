package cn.gjing.tools.excel;

/**
 * @author Gjing
 **/
public interface ReadCallback<T> {
    /**
     * Read each line for a callback
     *
     * @param value    Get the object
     * @param rowIndex The index of the current row
     * @return value
     */
    default T readLine(T value, int rowIndex) {
        return value;
    }

    /**
     * The method callback occurs when the cell data is null and the policy is set to jump
     *
     * @param rowIndex The index of the current row
     * @param colIndex The index of the current col
     */
    default void readJump(int rowIndex, int colIndex) {
    }
}
