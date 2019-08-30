package cn.gjing.util.excel;

/**
 * Excel文档类型
 * @author Gjing
 **/
public enum DocType{
    /**
     * excel文档类型，XLXS：处理海量数据用、XLS：2003版本，超过6w多数据会报错或者OOM
     */
    XLSX,XLS
}
