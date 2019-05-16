package cn.gjing.swagger;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gjing
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Resources {

    /**
     * 是否开启
     */
    private boolean enable = false;
    /**
     * 是否注册本服务,默认true
     */
    private boolean registerMe = true;
    /**
     * 服务名
     */
    private List<String> serveList = new ArrayList<>();

}

