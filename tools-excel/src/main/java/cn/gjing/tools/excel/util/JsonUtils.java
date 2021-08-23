package cn.gjing.tools.excel.util;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * Json convert utils
 *
 * @author Gjing
 **/
public final class JsonUtils {
    private static final Gson GSON = new Gson();

    /**
     * Java object to json
     *
     * @param o Java object
     * @return Json
     */
    public static String toJson(Object o) {
        return GSON.toJson(o);
    }

    /**
     * Json to java object
     *
     * @param json     Json string
     * @param classOfT Java class
     * @param <T>      T
     * @return Java object
     */
    public static <T> T toObj(String json, Class<T> classOfT) {
        return GSON.fromJson(json, classOfT);
    }

    /**
     * Json to java object
     *
     * @param json Json string
     * @param type Java type
     * @param <T>  T
     * @return Java object
     */
    public static <T> T toObj(String json, Type type) {
        return GSON.fromJson(json, type);
    }
}
