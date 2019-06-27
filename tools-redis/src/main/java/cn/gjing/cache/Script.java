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
    SET_NX("local key = KEYS[1] local val = ARGV[1] local expire = ARGV[2] local data = redis.call(\"get\", key) " +
            "if data == false then " +
            "local setVal = redis.call(\"setNx\", key, val) " +
            "if setVal == 1 then     " +
            "if tonumber(expire) > 0 then     " +
            "if redis.call(\"expire\", key, expire) ~= 1 then     " +
            "redis.call(\"del\",key)     " +
            "return false " +
            "end     " +
            "return true " +
            "end     " +
            "return true " +
            "end " +
            "return false " +
            "else " +
            "return false " +
            "end"),
    SET("local key = KEYS[1] local val = ARGV[1] local expire = ARGV[2] local data = redis.call(\"get\", key) " +
            "if data == false then " +
            "local setVal = redis.call(\"set\", key, val) " +
            "if setVal then     " +
            "if tonumber(expire) > 0 then     " +
            "if redis.call(\"expire\", key, expire) ~= 1 then     " +
            "redis.call(\"del\",key)     " +
            "return false " +
            "end     " +
            "return true " +
            "end     " +
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
