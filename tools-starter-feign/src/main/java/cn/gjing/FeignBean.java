package cn.gjing;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URI;
import java.util.Map;

/**
 * @author Gjing
 **/
interface FeignBean {

    /**
     * post
     *
     * @param uri      请求url
     * @param queryMap 参数
     * @return string
     */
    @RequestMapping(method = RequestMethod.POST)
    String post(URI uri, @RequestParam Map<String, ?> queryMap);

    /**
     * post
     *
     * @param uri 请求url
     * @return string
     */
    @RequestMapping(method = RequestMethod.POST)
    String post(URI uri);

    /**
     * post
     *
     * @param uri       请求url
     * @param queryBody body参数
     * @return string
     */
    @RequestMapping(method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    String postBody(URI uri, @RequestBody Object queryBody);

    /**
     * get
     *
     * @param uri      请求url
     * @param queryMap 参数
     * @return string
     */
    @RequestMapping(method = RequestMethod.GET)
    String get(URI uri, @RequestParam Map<String, ?> queryMap);

    /**
     * get
     *
     * @param uri 请求url
     * @return string
     */
    @RequestMapping(method = RequestMethod.GET)
    String get(URI uri);

    /**
     * delete
     *
     * @param uri      请求url
     * @param queryMap 参数
     * @return string
     */
    @RequestMapping(method = RequestMethod.DELETE)
    String delete(URI uri, @RequestParam Map<String, ?> queryMap);

    /**
     * delete
     *
     * @param uri 请求url
     * @return string
     */
    @RequestMapping(method = RequestMethod.DELETE)
    String delete(URI uri);

    /**
     * put
     *
     * @param uri      请求url
     * @param queryMap 参数
     * @return string
     */
    @RequestMapping(method = RequestMethod.PUT)
    String put(URI uri, @RequestParam Map<String, ?> queryMap);

    /**
     * put
     *
     * @param uri 请求url
     * @return string
     */
    @RequestMapping(method = RequestMethod.PUT)
    String put(URI uri);

    /**
     * patch
     *
     * @param uri      请求url
     * @param queryMap 参数
     * @return string
     */
    @RequestMapping(method = RequestMethod.PATCH)
    String patch(URI uri, @RequestParam Map<String, ?> queryMap);

    /**
     * patch
     *
     * @param uri 请求url
     * @return string
     */
    @RequestMapping(method = RequestMethod.PATCH)
    String patch(URI uri);
}
