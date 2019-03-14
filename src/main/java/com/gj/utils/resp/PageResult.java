package com.gj.utils.resp;

import lombok.Builder;
import lombok.Data;

/**
 * @author Archine
 * This class is called if the response needs to respond to paging data
 **/
@Data
@Builder
public class PageResult<T> {
    private T pageResult;
    private Integer size;
}
