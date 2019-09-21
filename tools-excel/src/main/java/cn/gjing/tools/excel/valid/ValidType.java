package cn.gjing.tools.excel.valid;

/**
 * 校验类型
 * @author Gjing
 **/
@SuppressWarnings("unused")
public enum ValidType {
    /**
     * 校验类型
     */
    INTEGER(1),
    DECIMAL(2),
    TEXT_LENGTH(6);

    private int type;

    ValidType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
