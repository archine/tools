package cn.gjing.tools.common.util.id;

import java.util.UUID;

/**
 * @author Gjing
 **/
public class IdUtils {

    private SnowFlake snowFlake;

    public IdUtils(long centerId, long machineId) {
        this.snowFlake = new SnowFlake(centerId, machineId);
    }

    /**
     * 获取UUID
     *
     * @return uuid
     */
    public String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 获取id
     *
     * @return id
     */
    public Long snowId() {
        return snowFlake.nextId();
    }
}
