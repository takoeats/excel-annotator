package com.junho.excel.style;

import lombok.Getter;

/** 폰트 스타일 Enum (볼드, 이탤릭, 밑줄 조합) */
@Getter
public enum FontStyle {
    NORMAL(false, false, false),
    BOLD(true, false, false),
    ITALIC(false, true, false),
    UNDERLINE(false, false, true),
    BOLD_ITALIC(true, true, false),
    BOLD_UNDERLINE(true, false, true),
    ITALIC_UNDERLINE(false, true, true),
    BOLD_ITALIC_UNDERLINE(true, true, true);

    private final boolean bold;
    private final boolean italic;
    private final boolean underline;

    FontStyle(boolean bold, boolean italic, boolean underline) {
        this.bold = bold;
        this.italic = italic;
        this.underline = underline;
    }

}