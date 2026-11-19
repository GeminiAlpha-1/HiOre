package dev.oreui.components;

import dev.oreui.components.platform.IInputEvent;
import dev.oreui.components.platform.InputEvent;
import dev.oreui.components.platform.IRenderContext;
import dev.oreui.core.geom.Rect2f;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WidgetFaultIsolationTest {
    @Test
    void updateFailureQuarantinesOnlyFailingWidget() {
        ComponentContext context = TestFixtures.context();
        FaultyWidget faulty = new FaultyWidget(context, FaultPhase.UPDATE);
        ProbeWidget probe = new ProbeWidget(context);
        ComponentTree tree = tree(faulty, probe);

        tree.update(1f);

        assertTrue(probe.updated);
        assertTrue(faulty.isQuarantined());
        assertEquals("update", faulty.failure().phase());
        assertEquals(1, tree.failures().size());
    }

    @Test
    void renderFailureDoesNotStopSiblings() {
        ComponentContext context = TestFixtures.context();
        FaultyWidget faulty = new FaultyWidget(context, FaultPhase.RENDER);
        ProbeWidget probe = new ProbeWidget(context);
        ComponentTree tree = tree(faulty, probe);

        tree.renderForeground(new TestRenderContext());

        assertTrue(probe.rendered);
        assertTrue(faulty.isQuarantined());
        assertEquals("renderForeground", faulty.failure().phase());
    }

    @Test
    void inputFailureIsIsolated() {
        ComponentContext context = TestFixtures.context();
        FaultyWidget faulty = new FaultyWidget(context, FaultPhase.INPUT);
        ProbeWidget probe = new ProbeWidget(context);
        ComponentTree tree = tree(faulty, probe);
        Rect2f bounds = faulty.bounds();

        boolean handled = tree.dispatch(new InputEvent(IInputEvent.Kind.POINTER_DOWN,
                bounds.x() + 1f, bounds.y() + 1f, 0, 0, 0));

        assertFalse(handled);
        assertTrue(faulty.isQuarantined());
        assertEquals("pointerDown", faulty.failure().phase());
        assertFalse(probe.clicked);
    }

    private static ComponentTree tree(FaultyWidget faulty, ProbeWidget probe) {
        OreSurface root = new OreSurface(faulty.context, SurfaceVariant.PANEL);
        root.style().width(240f).height(180f);
        root.addChild(faulty);
        root.addChild(probe);
        ComponentTree tree = new ComponentTree(root);
        tree.layout(new Rect2f(0f, 0f, 240f, 180f));
        return tree;
    }

    private enum FaultPhase {
        UPDATE,
        RENDER,
        INPUT
    }

    private static final class FaultyWidget extends OreWidget {
        private final FaultPhase phase;

        private FaultyWidget(ComponentContext context, FaultPhase phase) {
            super(context);
            this.phase = phase;
            style().width(40f).height(20f);
        }

        @Override
        public void update(float deltaSeconds) {
            if (phase == FaultPhase.UPDATE) {
                throw new IllegalStateException("update failure");
            }
        }

        @Override
        public void renderForeground(IRenderContext ctx) {
            if (phase == FaultPhase.RENDER) {
                throw new IllegalStateException("render failure");
            }
        }

        @Override
        public boolean pointerDown(float x, float y) {
            if (phase == FaultPhase.INPUT) {
                throw new IllegalStateException("input failure");
            }
            return true;
        }
    }

    private static final class ProbeWidget extends OreWidget {
        private boolean updated;
        private boolean rendered;
        private boolean clicked;

        private ProbeWidget(ComponentContext context) {
            super(context);
            style().width(40f).height(20f);
        }

        @Override
        public void update(float deltaSeconds) {
            updated = true;
        }

        @Override
        public void renderForeground(IRenderContext ctx) {
            rendered = true;
        }

        @Override
        public boolean pointerDown(float x, float y) {
            clicked = true;
            return true;
        }
    }
}