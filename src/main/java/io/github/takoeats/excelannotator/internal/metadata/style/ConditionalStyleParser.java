package io.github.takoeats.excelannotator.internal.metadata.style;

import io.github.takoeats.excelannotator.annotation.ConditionalStyle;
import io.github.takoeats.excelannotator.style.rule.ExpressionCondition;
import io.github.takoeats.excelannotator.style.rule.StyleRule;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConditionalStyleParser {


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
