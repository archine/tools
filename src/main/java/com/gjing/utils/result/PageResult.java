package com.gjing.utils.result;

import lombok.Builder;
import lombok.Data;

/**
 * @author Archine
 * 主要用于分页查询时候进行返回,与ResultVo配合使用
 **/
@Data
@Builder
public class PageResult<T> {
    private T pageResult;
    private Integer size;
}
