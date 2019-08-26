package cn.gjing.util.id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Gjing
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Component
@ConfigurationProperties("snow")
public class SnowId {

    /**
     * 数据中心Id, 范围(0-31)
     */
    @Builder.Default
    private Long centerId = 0L;
    /**
     * 机器标识符, 范围(0-31)
     */
    @Builder.Default
    private Long machineId = 0L;
}
