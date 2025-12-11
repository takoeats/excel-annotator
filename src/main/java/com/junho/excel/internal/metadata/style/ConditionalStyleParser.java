package com.junho.excel.internal.metadata.style;

import com.junho.excel.annotation.ConditionalStyle;
import com.junho.excel.style.rule.ExpressionCondition;
import com.junho.excel.style.rule.StyleRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ConditionalStyleParser {

    private ConditionalStyleParser() {
        throw new AssertionError("Utility class cannot be instantiated");
    }

    public static List<StyleRule> parse(ConditionalStyle[] conditionalStyles) {
        if (conditionalStyles == null || conditionalStyles.length == 0) {
            return Collections.emptyList();
        }

        List<StyleRule> rules = new ArrayList<>();
        for (ConditionalStyle cs : conditionalStyles) {
            ExpressionCondition condition = new ExpressionCondition(cs.when());

            StyleRule rule = StyleRule.builder()
                    .condition(condition)
                    .styleClass(cs.style())
                    .priority(cs.priority())
                    .build();
            rules.add(rule);
        }

        Collections.sort(rules);
        return rules;
    }
}
