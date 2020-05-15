package cn.gjing.tools.excel.metadata;

/**
 * Excel color index
 *
 * @author Gjing
 **/
public enum ExcelColor {
    // 0-7 duplicates of 8-15 for compatibility (OOXML spec pt.1 sec. 18.8.27)
    NONE(-1),
    BLACK1(0),
    WHITE1(1),
    RED1(2),
    BRIGHT_GREEN1(3),
    BLUE1(4),
    YELLOW1(5),
    PINK1(6),
    TURQUOISE1(7),
    BLACK(8),
    WHITE(9),
    RED(10),
    BRIGHT_GREEN(11),
    BLUE(12),
    YELLOW(13),
    PINK(14),
    TURQUOISE(15),
    DARK_RED(16),
    GREEN(17),
    DARK_BLUE(18),
    DARK_YELLOW(19),
    VIOLET(20),
    TEAL(21),
    GREY_25_PERCENT(22),
    GREY_50_PERCENT(23),
    CORNFLOWER_BLUE(24),
    MAROON(25),
    LEMON_CHIFFON(26),
    LIGHT_TURQUOISE1(27),
    ORCHID(28),
    CORAL(29),
    ROYAL_BLUE(30),
    LIGHT_CORNFLOWER_BLUE(31),
    SKY_BLUE(40),
    LIGHT_TURQUOISE(41),
    LIGHT_GREEN(42),
    LIGHT_YELLOW(43),
    PALE_BLUE(44),
    ROSE(45),
    LAVENDER(46),
    TAN(47),
    LIGHT_BLUE(48),
    AQUA(49),
    LIME(50),
    GOLD(51),
    LIGHT_ORANGE(52),
    ORANGE(53),
    BLUE_GREY(54),
    GREY_40_PERCENT(55),
    DARK_TEAL(56),
    SEA_GREEN(57),
    DARK_GREEN(58),
    OLIVE_GREEN(59),
    BROWN(60),
    PLUM(61),
    INDIGO(62),
    GREY_80_PERCENT(63),
    AUTOMATIC(64);

    public final short index;

    ExcelColor(int idx) {
        index = (short) idx;
    }

    /**
     * Returns index of this color
     *
     * @return index of this color
     */
    public short getIndex() {
        return index;
    }
}
