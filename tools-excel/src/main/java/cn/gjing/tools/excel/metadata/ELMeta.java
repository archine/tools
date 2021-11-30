package cn.gjing.tools.excel.metadata;

import org.springframework.expression.spel.standard.SpelExpressionParser;

/**
 * EL global expression parser
 *
 * @author Gjing
 **/
public enum ELMeta {
    PARSER;

    private final SpelExpressionParser parser = new SpelExpressionParser();

    public SpelExpressionParser getParser() {
        return PARSER.parser;
    }
}
