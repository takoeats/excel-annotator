package io.github.takoeats.excelannotator.internal.metadata.style;

import io.github.takoeats.excelannotator.annotation.ConditionalStyle;
import io.github.takoeats.excelannotator.style.CustomExcelCellStyle;
import io.github.takoeats.excelannotator.style.ExcelCellStyleConfigurer;
import io.github.takoeats.excelannotator.style.internal.rule.StyleRule;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConditionalStyleParserTest {

    @Test
    void parse_withNullArray_returnsEmptyList() {
        List<StyleRule> rules = ConditionalStyleParser.parse(null);

        assertNotNull(rules);
        assertTrue(rules.isEmpty());
    }

    @Test
    void parse_withEmptyArray_returnsEmptyList() {
        ConditionalStyle[] emptyArray = new ConditionalStyle[0];

        List<StyleRule> rules = ConditionalStyleParser.parse(emptyArray);

        assertNotNull(rules);
        assertTrue(rules.isEmpty());
    }

    @Test
    void parse_withSingleConditionalStyle_returnsOneRule() {
        ConditionalStyle[] styles = {
                createConditionalStyle("value < 0", TestStyle.class, 1)
        };

        List<StyleRule> rules = ConditionalStyleParser.parse(styles);

        assertEquals(1, rules.size());
        assertEquals(1, rules.get(0).getPriority());
        assertEquals(TestStyle.class, rules.get(0).getStyleClass());
    }

    @Test
    void parse_withMultipleConditionalStyles_returnsSortedRules() {
        ConditionalStyle[] styles = {
                createConditionalStyle("value < 0", TestStyle.class, 1),
                createConditionalStyle("value > 100", TestStyle2.class, 3),
                createConditionalStyle("value == 0", TestStyle.class, 2)
        };

        List<StyleRule> rules = ConditionalStyleParser.parse(styles);

        assertEquals(3, rules.size());
        assertEquals(3, rules.get(0).getPriority());
        assertEquals(2, rules.get(1).getPriority());
        assertEquals(1, rules.get(2).getPriority());
    }

    private ConditionalStyle createConditionalStyle(String when, Class<? extends CustomExcelCellStyle> styleClass, int priority) {
        return new ConditionalStyle() {
            @Override
            public String when() {
                return when;
            }

            @Override
            public Class<? extends CustomExcelCellStyle> style() {
                return styleClass;
            }

            @Override
            public int priority() {
                return priority;
            }

            @Override
            public Class<ConditionalStyle> annotationType() {
                return ConditionalStyle.class;
            }
        };
    }

    private static class TestStyle extends CustomExcelCellStyle {
        @Override
        protected void configure(ExcelCellStyleConfigurer configurer) {
        }
    }

    private static class TestStyle2 extends CustomExcelCellStyle {
        @Override
        protected void configure(ExcelCellStyleConfigurer configurer) {
        }
    }
}
