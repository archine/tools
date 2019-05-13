package cn.gjing;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URI;
import java.util.Map;

/**
 * @author Gjing
 **/
public interface FeignBean {

    @RequestMapping(method = RequestMethod.POST)
    String post(URI uri, @RequestParam Map<String, ?> param);

    @RequestMapping(method = RequestMethod.POST)
    String post(URI uri);

//    @RequestMapping(method = RequestMethod.POST)
//    <T> T postBody(URI uri, @RequestBody Object param, Class<T> responseType);

    @RequestMapping(method = RequestMethod.GET)
    String get(URI uri, @RequestParam Map<String, ?> param);

    @RequestMapping(method = RequestMethod.GET)
    String get(URI uri);

    @RequestMapping(method = RequestMethod.DELETE)
    String delete(URI uri, @RequestParam Map<String, ?> param);

    @RequestMapping(method = RequestMethod.DELETE)
    String delete(URI uri);

    @RequestMapping(method = RequestMethod.PUT)
    String put(URI uri, @RequestParam Map<String, ?> param);

    @RequestMapping(method = RequestMethod.PUT)
    String put(URI uri);

    @RequestMapping(method = RequestMethod.PATCH)
    String patch(URI uri, @RequestParam Map<String, ?> param);

    @RequestMapping(method = RequestMethod.PATCH)
    String patch(URI uri);
}
