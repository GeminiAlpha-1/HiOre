package dev.oreui.components.platform;

import dev.oreui.core.geom.Insets;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlatformTypesTest {
    @Test
    void simpleTextCopiesArgs() {
        List<Object> args = new ArrayList<>();
        args.add("one");
        SimpleText text = new SimpleText("oreui.test", args);
        args.add("two");

        assertEquals("oreui.test", text.key());
        assertEquals(1, text.args().size());
        assertThrows(UnsupportedOperationException.class, () -> text.args().add("three"));
        assertThrows(NullPointerException.class, () -> new SimpleText(null, List.of()));
    }

    @Test
    void textMetricsCopiesLines() {
        List<String> lines = new ArrayList<>();
        lines.add("a");
        TextMetrics metrics = new TextMetrics(10, 20, lines);
        lines.add("b");

        assertEquals(1, metrics.lines().size());
        assertEquals(10, metrics.width());
        assertEquals(20, metrics.height());
        assertThrows(IllegalArgumentException.class, () -> new TextMetrics(-1, 20, List.of()));
    }

    @Test
    void inputEventValidatesKind() {
        InputEvent event = new InputEvent(IInputEvent.Kind.KEY_DOWN, 0f, 0f, 65, 1, '\0');
        assertEquals(IInputEvent.Kind.KEY_DOWN, event.kind());
        assertEquals(65, event.code());
        assertThrows(NullPointerException.class,
                () -> new InputEvent(null, 0f, 0f, 0, 0, '\0'));
    }

    @Test
    void resourceInfoValidates() {
        IResourceProvider.SpriteInfo sprite =
                new IResourceProvider.SpriteInfo("oreui:test", 16, 16);
        assertEquals(16, sprite.width());
        assertThrows(IllegalArgumentException.class,
                () -> new IResourceProvider.SpriteInfo("oreui:test", -1, 16));
        assertTrue(new IResourceProvider.NineSliceInfo("oreui:test", new Insets()).id()
                .startsWith("oreui:"));
    }
}