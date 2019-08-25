package cn.gjing.util.id;

import java.util.UUID;

/**
 * @author Gjing
 **/
public class IdUtil {

    private SnowFlake snowFlake;

    public IdUtil(SnowId snowId) {
        this.snowFlake = new SnowFlake(snowId.getCenterId(), snowId.getMachineId());
    }

    /**
     * 获取UUID
     * @return uuid
     */
    public String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 获取id
     * @return id
     */
    public Long snowId() {
        return snowFlake.nextId();
    }
}
