package cn.gjing.tools.swagger;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gjing
 **/
@Data
@Builder
@Component
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties("tools.doc.group")
public class DocGroup {
    /**
     * Open the aggregate document
     */
    private boolean enable;

    /**
     * Set of services
     */
    @Builder.Default
    private List<GroupService> serviceList = new ArrayList<>();
}
