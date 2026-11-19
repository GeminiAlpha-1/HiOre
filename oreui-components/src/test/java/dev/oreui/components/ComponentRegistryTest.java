package dev.oreui.components;

import org.junit.jupiter.api.Test;
import dev.oreui.theme.TypeRole;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ComponentRegistryTest {
    @Test
    void createsRegisteredWidget() {
        ComponentContext context = TestFixtures.context();
        ComponentRegistry registry = new ComponentRegistry()
                .register("label", ctx -> new OreLabel(ctx, TestFixtures.text("demo"),
                        TypeRole.CAPTION, LabelOverflow.CLIP, 100));

        OreWidget widget = registry.create("label", context);

        assertNotNull(widget);
        assertTrue(registry.contains("label"));
        assertTrue(registry.ids().contains("label"));
    }

    @Test
    void featureFlagCanDisableExperimentalWidget() {
        ComponentContext context = TestFixtures.context();
        ComponentRegistry registry = new ComponentRegistry()
                .setFeatureFlags(feature -> !feature.equals("experimental"))
                .register("exp", "experimental", ctx -> new OreLabel(ctx, TestFixtures.text("exp"),
                        TypeRole.CAPTION, LabelOverflow.CLIP, 100));

        assertNull(registry.create("exp", context));
        assertTrue(registry.contains("exp"));
    }

    @Test
    void unknownWidgetFailsFast() {
        ComponentRegistry registry = new ComponentRegistry();
        assertThrows(IllegalArgumentException.class,
                () -> registry.create("missing", TestFixtures.context()));
    }
}