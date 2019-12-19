package cn.gjing.tools.common.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 分页结果集
 * @author Gjing
 **/
@Data
public class PageResult<T> implements Serializable {

    private T data;
    private long totalPages;
    private long currentPage;
    private long totalRows;
    private long pageRows;

    private PageResult() {

    }
    /**
     * 构建分页结果
     *
     * @param data        page数据
     * @param totalPages   总页数
     * @param currentPage 当前页数
     * @return PageResult
     */
    public static <T> PageResult<T> of(T data, long totalPages, long currentPage) {
        PageResult<T> pageResult = new PageResult<>();
        pageResult.setData(data);
        pageResult.setTotalPages(totalPages);
        pageResult.setCurrentPage(currentPage);
        return pageResult;
    }

    /**
     * 构建分页结果
     *
     * @param data      page数据
     * @param totalPages 总页数
     * @return PageResult
     */
    public static <T> PageResult<T> of(T data, long totalPages) {
        PageResult<T> pageResult = new PageResult<>();
        pageResult.setData(data);
        pageResult.setTotalPages(totalPages);
        return pageResult;
    }

    /**
     * 构建分页结果
     *
     * @param data      page数据
     * @param totalPages 总页数
     * @param currentPage 当前页数
     * @param totalRows 总条数
     * @return PageResult
     */
    public static <T> PageResult<T> of(T data, long totalPages, long currentPage, long totalRows) {
        PageResult<T> pageResult = new PageResult<>();
        pageResult.setData(data);
        pageResult.setTotalPages(totalPages);
        pageResult.setCurrentPage(currentPage);
        pageResult.setTotalRows(totalRows);
        return pageResult;
    }

    /**
     * 构建分页结果
     *
     * @param data      page数据
     * @param totalPages 总页数
     * @param currentPage 当前页数
     * @param totalRows 总条数
     * @param pageRows 每页条数
     * @return PageResult
     */
    public static <T> PageResult<T> of(T data, long totalPages, long currentPage, long totalRows,long pageRows) {
        PageResult<T> pageResult = new PageResult<>();
        pageResult.setData(data);
        pageResult.setTotalPages(totalPages);
        pageResult.setCurrentPage(currentPage);
        pageResult.setTotalRows(totalRows);
        pageResult.setPageRows(pageRows);
        return pageResult;
    }
}
