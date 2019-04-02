package cn.gjing.result;

import cn.gjing.enums.HttpStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import cn.gjing.ParamUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Gjing
 * 返回模板
 **/
@Getter
@Setter
@Builder
public class ResultVo<T> {
    private Integer code;
    private String msg;
    private T data;

    public static ResultVo success(String msg, Object data) {
        return ResultVo.builder().code(HttpStatus.OK.getCode()).msg(msg).data(data).build();
    }

    public static ResultVo success() {
        return ResultVo.builder().code(HttpStatus.OK.getCode()).msg(HttpStatus.OK.getMsg()).build();
    }

    public static ResultVo success(Object data) {
        return ResultVo.builder().code(HttpStatus.OK.getCode()).msg(HttpStatus.OK.getMsg()).data(data).build();
    }

    public static ResultVo error(Integer code, String msg) {
        return ResultVo.builder().code(code).msg(msg).build();
    }

    public static ResultVo error() {
        return ResultVo.builder().code(HttpStatus.BAD_REQUEST.getCode()).msg(HttpStatus.BAD_REQUEST.getMsg()).build();
    }

    public static ResultVo error(String msg) {
        return ResultVo.builder().code(HttpStatus.BAD_REQUEST.getCode()).msg(msg).build();
    }

    /**
     * 用户自定义返回,一般用于返回多个数据
     *
     * @param keys key
     * @param val  value
     * @return map
     */
    public static Map<String, Object> find(List<String> keys, List<Object> val) {
        Map<String, Object> map = new HashMap<>(16);
        for (int i = 0; i < keys.size(); i++) {
            map.put(keys.get(i), val.get(i));
        }
        return map;
    }

    /**
     * 一般用于登录
     * @param isLogin 非空则为登录
     * @param keys 还要返回的其他参数
     * @param values 参数对应的value
     * @return map
     */
    public static Map<String, Object> login(String isLogin,List<String> keys, List<Object> values) {
        Map<String, Object> map = new HashMap<>(10);
        if (ParamUtil.paramIsNotEmpty(isLogin)) {
            map.put("code", HttpStatus.OK.getCode());
            map.put("msg", HttpStatus.OK.getMsg());
        }else {
            map.put("code", HttpStatus.NO_LOGIN.getCode());
            map.put("msg", HttpStatus.NO_LOGIN.getMsg());
        }
        if (keys != null && values != null) {
            for (int i = 0; i < keys.size(); i++) {
                map.put(keys.get(i), values.get(i));
            }
        }
        return map;
    }

}
