package cn.gjing.tools.excel.write.valid;

/**
 * Operation type
 *
 * @author Gjing
 **/
public enum OperatorType {
    /**
     * Operation type
     */
    BETWEEN(0),
    NOT_BETWEEN(1),
    EQUAL(2),
    NOT_EQUAL(3),
    GREATER_THAN(4),
    LESS_THAN(5),
    GREATER_OR_EQUAL(6),
    LESS_OR_EQUAL(7),
    IGNORED(0);

    private final int type;

    OperatorType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
