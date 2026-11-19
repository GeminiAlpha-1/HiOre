package dev.oreui.components;

import dev.oreui.components.platform.IRenderContext;
import dev.oreui.components.platform.IText;
import dev.oreui.core.geom.Rect2f;
import dev.oreui.theme.ColorRole;
import dev.oreui.theme.TypeRole;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RenderContextClipContractTest {
    private static final IText LABEL = new IText() {
        @Override
        public String key() {
            return "Hello";
        }

        @Override
        public List<Object> args() {
            return List.of();
        }
    };

    @Test
    void defaultClippedTextSkipsEmptyOrNullClip() {
        RecordingRenderContext ctx = new RecordingRenderContext();

        ctx.submitTextClipped(LABEL, 0f, 0f, TypeRole.BODY, ColorRole.TEXT_PRIMARY, null);
        ctx.submitTextClipped(LABEL, 0f, 0f, TypeRole.BODY, ColorRole.TEXT_PRIMARY, new Rect2f(0f, 0f, 0f, 10f));

        assertEquals(List.of(), ctx.events);
    }

    @Test
    void defaultClippedTextPushesAndPopsAroundSubmit() {
        RecordingRenderContext ctx = new RecordingRenderContext();
        Rect2f clip = new Rect2f(10f, 20f, 80f, 16f);

        ctx.submitTextClipped(LABEL, 12f, 22f, TypeRole.BODY, ColorRole.TEXT_PRIMARY, clip);

        assertEquals(List.of("pushClip", "submitText", "popClip"), ctx.events);
        assertEquals(clip, ctx.lastClip);
    }

    @Test
    void defaultClippedTextRestoresClipWhenSubmitFails() {
        RecordingRenderContext ctx = new RecordingRenderContext();
        ctx.failOnSubmit = true;

        assertThrows(IllegalStateException.class, () -> ctx.submitTextClipped(
                LABEL, 0f, 0f, TypeRole.BODY, ColorRole.TEXT_PRIMARY, new Rect2f(0f, 0f, 40f, 12f)));

        assertEquals(List.of("pushClip", "submitText", "popClip"), ctx.events);
    }

    private static final class RecordingRenderContext implements IRenderContext {
        private final List<String> events = new ArrayList<>();
        private Rect2f lastClip;
        private boolean failOnSubmit;

        @Override
        public void begin() {
        }

        @Override
        public void end() {
        }

        @Override
        public void submitPixelRect(Rect2f rect, int argb) {
        }

        @Override
        public void submitSdfRect(Rect2f rect, int argb, float radius, float border) {
        }

        @Override
        public void submitText(IText text, float x, float y, TypeRole role, ColorRole color) {
            events.add("submitText");
            if (failOnSubmit) {
                throw new IllegalStateException("boom");
            }
        }

        @Override
        public void pushClip(Rect2f rect) {
            events.add("pushClip");
            lastClip = rect;
        }

        @Override
        public void popClip() {
            events.add("popClip");
        }

        @Override
        public void pushTransform(float translateX, float translateY, float scale) {
        }

        @Override
        public void popTransform() {
        }

        @Override
        public void pushOpacity(float alpha) {
        }

        @Override
        public void popOpacity() {
        }
    }
}