package cn.gjing.util.excel.validation;

/**
 * 校验类型
 * @author Gjing
 **/
@SuppressWarnings("unused")
public enum ValidationType {
    /**
     * 校验类型
     */
    INTEGER(1),
    DECIMAL(2),
    TEXT_LENGTH(6);

    private int type;

    ValidationType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
