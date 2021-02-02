package cn.gjing.tools.excel.metadata.function;

/**
 * Represents a function that accepts three argument and produces a result
 * @author Gjing
 **/
@FunctionalInterface
public interface TFunction<P1, P2, P3, R> {
    /**
     * Applies this function to the given argument
     * @param param1 p1
     * @param param2 p2
     * @param param3 p3
     * @return R
     */
    R apply(P1 param1, P2 param2, P3 param3);
}
