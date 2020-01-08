package cn.gjing.tools.excel;

import lombok.*;

import java.util.Map;

/**
 * Meta object
 * @author Gjing
 **/
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetaObject {
    /**
     * Big title
     */
    private BigTitle bigTitle;
    /**
     * Meta excel style
     */
    private MetaStyle metaStyle;
    /**
     * Combobox values
     */
    private Map<String, String[]> explicitValues;
}
