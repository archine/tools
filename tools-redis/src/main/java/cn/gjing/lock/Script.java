package cn.gjing.lock;

import lombok.Getter;

/**
 * @author Gjing
 **/
@Getter
enum Script {
    /**
     * 脚本
     */
    LOCK("local key = KEYS[1] local val = KEYS[2] local expire = KEYS[3] " +
            "if redis.call(\"setNx\", key, val) == 1 " +
            "then " +
            "redis.call(\"expire\", key, expire) " +
            "return val " +
            "else " +
            "return nil " +
            "end"),
    RELEASE("local key = KEYS[1] local val = KEYS[2] if redis.call(\"get\", key) == val " +
            "then local delNum = redis.call(\"del\", key) " +
            "if delNum == 1 " +
            "then     " +
            "return key " +
            "else     " +
            "return nil " +
            "end " +
            "else return nil " +
            "end");
    private String script;

    Script(String script) {
        this.script = script;
    }
}
