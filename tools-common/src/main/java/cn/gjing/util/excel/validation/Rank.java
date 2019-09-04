package cn.gjing.util.excel.validation;

/**
 * @author Gjing
 **/
@SuppressWarnings("unused")
public enum Rank {
    WARNING(1),STOP(0),INFO(2);
    private int rank;

    Rank(int type) {
        this.rank = type;
    }

    public int getRank() {
        return rank;
    }
}
