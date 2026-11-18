package dev.oreui.core.text;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TextGeometryTest {
    private static final TextMeasure WIDTH = text -> {
        int total = 0;
        for (int i = 0; i < text.length(); ) {
            int codePoint = Character.codePointAt(text, i);
            total += isCjk(codePoint) ? 9 : (codePoint == ' ' ? 4 : 6);
            i += Character.charCount(codePoint);
        }
        return total;
    };

    @Test
    void wrapsCjkByCharacter() {
        TextLayout layout = new TextLayout();
        TextGeometry.wrap("基岩版 OreUI 风格界面采用硬边像素阶梯，中文文本需要逐字断行并遵守标点禁则。",
                300, WIDTH, 3, layout);
        assertTrue(layout.size() >= 2);
        assertTrue(WIDTH.width(layout.line(0)) <= 300);
        assertFalse(layout.line(1).startsWith("，"));
    }

    @Test
    void forbiddenCommaMovesToNextLine() {
        TextLayout layout = new TextLayout();
        TextGeometry.wrap("甲乙丙丁戊己庚辛壬癸，子丑寅卯辰巳午未", 90, WIDTH, 3, layout);
        assertEquals("甲乙丙丁戊己庚辛壬", layout.line(0));
        assertTrue(layout.line(1).startsWith("癸，"));
    }

    @Test
    void forbiddenOpenBracketMovesToNextLine() {
        TextLayout layout = new TextLayout();
        TextGeometry.wrap("甲乙丙丁戊己庚辛壬癸（子丑寅卯辰巳午未申酉", 90, WIDTH, 3, layout);
        assertEquals("甲乙丙丁戊己庚辛壬癸", layout.line(0));
        assertTrue(layout.line(1).startsWith("（"));
    }

    @Test
    void ellipsisCountsItsOwnWidth() {
        String text = "这是一段很长的说明文字，用于验证省略号是否把自身宽度计入宽度限制。";
        String shortened = TextGeometry.ellipsize(text, 220, WIDTH);
        assertTrue(shortened.endsWith(TextGeometry.ELLIPSIS));
        assertTrue(WIDTH.width(shortened) <= 220);
        assertEquals("abc", TextGeometry.ellipsize("abc", 100, WIDTH));
    }

    @Test
    void maxLinesTruncatesWithEllipsis() {
        TextLayout layout = new TextLayout();
        TextGeometry.wrap("这是一段很长的说明文字，用于验证省略号是否把自身宽度计入宽度限制。",
                120, WIDTH, 1, layout);
        assertEquals(1, layout.size());
        assertTrue(layout.line(0).endsWith(TextGeometry.ELLIPSIS));
        assertTrue(WIDTH.width(layout.line(0)) <= 120);
    }

    @Test
    void latinBreaksAtSpaces() {
        TextLayout layout = new TextLayout();
        TextGeometry.wrap("hello world foo", 60, WIDTH, 3, layout);
        assertEquals("hello", layout.line(0));
        assertFalse(layout.line(1).startsWith(" "));
    }

    @Test
    void mixedTextHandlesBothRules() {
        TextLayout layout = new TextLayout();
        TextGeometry.wrap("OreUI 中文测试", 60, WIDTH, 3, layout);
        assertTrue(layout.size() >= 2);
        assertFalse(layout.line(1).startsWith(" "));
    }

    @Test
    void languageSwitchUsesNewMeasure() {
        TextMeasure narrow = text -> text.length() * 5;
        TextMeasure wide = text -> text.length() * 10;
        TextLayout layout = new TextLayout();
        TextGeometry.wrap("abcdefghij", 50, narrow, 5, layout);
        int narrowLines = layout.size();
        TextGeometry.wrap("abcdefghij", 50, wide, 5, layout);
        int wideLines = layout.size();
        assertTrue(wideLines > narrowLines);
    }

    @Test
    void emptyAndSingleCharacterAreStable() {
        TextLayout layout = new TextLayout();
        TextGeometry.wrap("", 100, WIDTH, 3, layout);
        assertTrue(layout.isEmpty());
        TextGeometry.wrap("中", 100, WIDTH, 3, layout);
        assertEquals(1, layout.size());
        assertEquals("中", layout.line(0));
    }

    @Test
    void singleCharWiderThanLineIsKept() {
        TextLayout layout = new TextLayout();
        TextGeometry.wrap("中", 1f, WIDTH, 1, layout);
        assertEquals(1, layout.size());
        assertEquals("中", layout.line(0));
    }

    @Test
    void exposesReusableLinesView() {
        TextLayout layout = new TextLayout();
        TextGeometry.wrap("abc def", 100, WIDTH, 3, layout);
        assertEquals(1, layout.lines().size());
        assertEquals("abc def", layout.lines().get(0));
    }

    @Test
    void layoutBufferIsReusable() {
        TextLayout layout = new TextLayout();
        TextGeometry.wrap("这是一段很长的说明文字", 60, WIDTH, 3, layout);
        assertTrue(layout.size() > 1);
        TextGeometry.wrap("短", 60, WIDTH, 3, layout);
        assertEquals(1, layout.size());
        assertEquals("短", layout.line(0));
    }

    private static boolean isCjk(int codePoint) {
        return Character.UnicodeScript.of(codePoint) == Character.UnicodeScript.HAN
                || (codePoint >= 12288 && codePoint <= 12351)
                || (codePoint >= 65280 && codePoint <= 65535);
    }
}