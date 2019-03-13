package com.gj.utils.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Archine
 * @date 2019-03-13
 * This class is called if the response needs to respond to paging data
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResult<T> {
    private T data;
    private Integer totals;
}
