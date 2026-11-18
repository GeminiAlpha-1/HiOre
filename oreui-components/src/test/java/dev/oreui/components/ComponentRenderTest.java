package dev.oreui.components;

import dev.oreui.components.platform.IRenderContext;
import dev.oreui.components.platform.IText;
import dev.oreui.core.geom.Rect2f;
import dev.oreui.theme.ButtonVariant;
import dev.oreui.theme.ColorRole;
import dev.oreui.theme.TypeRole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ComponentRenderTest {
    @Test
    void treeRendersWithoutThrowing() {
        ComponentContext context = TestFixtures.context();
        OreSurface root = new OreSurface(context, SurfaceVariant.PANEL);
        TestFixtures.fillSurface(context, root, 200, 200);
        OreButton button = new OreButton(context, ButtonVariant.PRIMARY, ControlSize.MD,
                TestFixtures.text("Go"), () -> {
                });
        button.style().width(100f);
        root.addChild(button);
        ComponentTree tree = new ComponentTree(root);
        tree.layout(new Rect2f(0f, 0f, 200f, 200f));

        RecordingContext recording = new RecordingContext();
        tree.renderBackground(recording);
        tree.renderForeground(recording);
        assertTrue(recording.fills > 0);
        assertTrue(recording.texts > 0);
    }

    private static final class RecordingContext implements IRenderContext {
        private int fills;
        private int texts;

        @Override
        public void begin() {
        }

        @Override
        public void end() {
        }

        @Override
        public void submitPixelRect(Rect2f rect, int argb) {
            fills++;
        }

        @Override
        public void submitSdfRect(Rect2f rect, int argb, float radius, float border) {
            fills++;
        }

        @Override
        public void submitText(IText text, float x, float y, TypeRole role, ColorRole color) {
            texts++;
        }

        @Override
        public void pushClip(Rect2f rect) {
        }

        @Override
        public void popClip() {
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