package cn.gjing.tools.excel;

/**
 * Excel file type
 *
 * @author Gjing
 **/
public enum Type {
    /**
     * Excel document type, XLXS: used for big data processing, XLS: 2003 version, more than 6w data will report error or OOM
     */
    XLSX, XLS
}
