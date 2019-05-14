package cn.gjing;

/**
 * @author Gjing
 **/
public interface FeignService {
    /**
     * 通过url创建feign客户端实例
     *
     * @param url 动态url，包含协议、ip、端口、根目录，如:"http://192.168.153.1:5567/ems"
     * @return FeignBean
     */
    FeignBean newInstanceByUrl(String url);

    /**
     * 通过服务名创建url
     *
     * @param name 动态名称，包含协议、名称、根目录，如:"http://ems-core/ems"
     * @return FeignBean
     */
    FeignBean newInstanceByName(String name);

}
