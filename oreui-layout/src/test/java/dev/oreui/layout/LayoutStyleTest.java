package dev.oreui.layout;

import dev.oreui.core.geom.Insets;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LayoutStyleTest {
    @Test
    void defaultsAreFlexRow() {
        LayoutStyle style = new LayoutStyle();
        assertEquals(LayoutStyle.Direction.ROW, style.direction());
        assertEquals(LayoutStyle.Align.STRETCH, style.alignItems());
        assertEquals(LayoutStyle.Justify.START, style.justifyContent());
        assertTrue(Float.isNaN(style.width()));
        assertTrue(Float.isNaN(style.height()));
        assertEquals(0f, style.minWidth(), 0.0001f);
        assertEquals(0f, style.minHeight(), 0.0001f);
        assertEquals(0f, style.rowGap(), 0.0001f);
        assertEquals(0f, style.columnGap(), 0.0001f);
        assertEquals(0f, style.flexGrow(), 0.0001f);
    }

    @Test
    void gapSetsBothAxes() {
        LayoutStyle style = new LayoutStyle().gap(4f);
        assertEquals(4f, style.rowGap(), 0.0001f);
        assertEquals(4f, style.columnGap(), 0.0001f);
        style.rowGap(2f).columnGap(6f);
        assertEquals(2f, style.rowGap(), 0.0001f);
        assertEquals(6f, style.columnGap(), 0.0001f);
    }

    @Test
    void copyIsIndependent() {
        LayoutStyle source = new LayoutStyle()
                .direction(LayoutStyle.Direction.COLUMN)
                .alignItems(LayoutStyle.Align.CENTER)
                .justifyContent(LayoutStyle.Justify.SPACE_BETWEEN)
                .width(120f)
                .height(40f)
                .minWidth(10f)
                .minHeight(20f)
                .gap(3f)
                .flexGrow(2f)
                .padding(new Insets(1f, 2f, 3f, 4f));

        LayoutStyle copy = new LayoutStyle().copyFrom(source);
        assertEquals(LayoutStyle.Direction.COLUMN, copy.direction());
        assertEquals(LayoutStyle.Align.CENTER, copy.alignItems());
        assertEquals(LayoutStyle.Justify.SPACE_BETWEEN, copy.justifyContent());
        assertEquals(120f, copy.width(), 0.0001f);
        assertEquals(40f, copy.height(), 0.0001f);
        assertEquals(10f, copy.minWidth(), 0.0001f);
        assertEquals(20f, copy.minHeight(), 0.0001f);
        assertEquals(3f, copy.rowGap(), 0.0001f);
        assertEquals(2f, copy.flexGrow(), 0.0001f);
        assertEquals(4f, copy.padding().left(), 0.0001f);

        source.padding().set(0f, 0f, 0f, 0f);
        assertEquals(4f, copy.padding().left(), 0.0001f);
        assertFalse(copy.width() == Float.NaN);
    }
}