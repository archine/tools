package cn.gjing.result;

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
    private Integer totalPage;
    private Integer currentPage;

    /**
     * 构建分页结果
     *
     * @param data        page数据
     * @param totalPage   总页数
     * @param currentPage 当前页数
     * @return PageResult
     */
    public static <T> PageResult of(T data, Integer totalPage, Integer currentPage) {
        return PageResult.builder()
                .data(data)
                .totalPage(totalPage)
                .currentPage(currentPage)
                .build();
    }

    /**
     * 构建分页结果
     *
     * @param data      page数据
     * @param totalPage 总页数
     * @return PageResult
     */
    public static <T> PageResult of(T data, Integer totalPage) {
        return PageResult.builder()
                .data(data)
                .totalPage(totalPage)
                .build();
    }
}
