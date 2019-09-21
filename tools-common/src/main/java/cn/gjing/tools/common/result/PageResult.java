package cn.gjing.tools.common.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Gjing
 * 主要用于分页查询时候进行返回
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("unused")
public class PageResult<T> implements Serializable {

    private T data;
    private int totalPages;
    private int currentPage;
    private long totalRows;
    private int pageRows;
    /**
     * 构建分页结果
     *
     * @param data        page数据
     * @param totalPages   总页数
     * @param currentPage 当前页数
     * @return PageResult
     */
    public static <T> PageResult of(T data, int totalPages, int currentPage) {
        return PageResult.builder()
                .data(data)
                .totalPages(totalPages)
                .currentPage(currentPage)
                .build();
    }

    /**
     * 构建分页结果
     *
     * @param data      page数据
     * @param totalPages 总页数
     * @return PageResult
     */
    public static <T> PageResult of(T data, int totalPages) {
        return PageResult.builder()
                .data(data)
                .totalPages(totalPages)
                .build();
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
    public static <T> PageResult of(T data, int totalPages, int currentPage, long totalRows) {
        return PageResult.builder()
                .data(data)
                .totalPages(totalPages)
                .currentPage(currentPage)
                .totalRows(totalRows)
                .build();
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
    public static <T> PageResult of(T data, int totalPages, int currentPage, long totalRows,int pageRows) {
        return PageResult.builder()
                .data(data)
                .totalPages(totalPages)
                .currentPage(currentPage)
                .totalRows(totalRows)
                .pageRows(pageRows)
                .build();
    }
}
