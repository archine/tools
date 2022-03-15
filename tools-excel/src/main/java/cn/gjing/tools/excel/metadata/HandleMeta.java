package cn.gjing.tools.excel.metadata;

import cn.gjing.tools.excel.write.valid.handle.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Validates annotation processor metadata
 *
 * @author Gjing
 **/
public enum HandleMeta {
    INSTANCE;

    @Getter
    private final List<ExcelValidAnnotationHandler> handlers = new ArrayList<>();

    HandleMeta() {
        this.handlers.add(new DropdownBoxValidHandler());
        this.handlers.add(new NumericValidHandler());
        this.handlers.add(new RepeatValidHandler());
        this.handlers.add(new DateValidHandler());
        this.handlers.add(new CustomValidHandler());
    }
}
