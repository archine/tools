package cn.gjing.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Gjing
 * 主要用于分页查询时候进行返回,与ResultVo配合使用
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    private T pageResult;
    private Integer totalPage;
}
