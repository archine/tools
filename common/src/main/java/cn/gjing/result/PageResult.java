package cn.gjing.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Gjing
 * 主要用于分页查询时候进行返回
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {

    private T data;
    private Integer totalPage;

    /**
     * 构建分页结果
     * @param data page数据
     * @param totalPage 总页数
     * @return PageResult
     */
    public static PageResult of(Object data, Integer totalPage) {
        return PageResult.builder()
                .data(data)
                .totalPage(totalPage)
                .build();
    }
}
