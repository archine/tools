package cn.gjing.cache;

import lombok.Getter;

/**
 * @author Gjing
 **/
@Getter
public enum Script {
    /**
     * 缓存脚本
     */
    SET_NX("local key1 = KEYS[1] local val = KEYS[2] local expire = ARGV[1] local data = redis.call(\"get\", key1) " +
            "if " +
            "data == false " +
            "then local setVal = redis.call(\"setNx\",key1,val) " +
            "if setVal == 1 " +
            "then     " +
            "redis.call(\"expire\",key1,expire)     " +
            "return true " +
            "end " +
            "return false " +
            "else " +
            "return false " +
            "end"),
    SET("local key1 = KEYS[1] local val = KEYS[2] local expire = ARGV[1] local data = redis.call(\"get\", key1) " +
            "if " +
            "data == false " +
            "then local setVal = redis.call(\"set\",key1,val) " +
            "if setVal " +
            "then     " +
            "redis.call(\"expire\",key1,expire)     " +
            "return true " +
            "end " +
            "return false " +
            "else " +
            "return false " +
            "end");
    private String script;

    Script(String script) {
        this.script = script;
    }
}
