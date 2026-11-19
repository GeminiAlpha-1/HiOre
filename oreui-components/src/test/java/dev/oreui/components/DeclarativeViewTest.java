package dev.oreui.components;

import dev.oreui.components.declarative.DeclarativeView;
import dev.oreui.core.geom.Rect2f;
import dev.oreui.theme.ButtonVariant;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class DeclarativeViewTest {
    @Test
    void buildsTreeFromHtmlAndCss() {
        ComponentContext context = TestFixtures.context();
        OreWidget root = DeclarativeView.build(
                "<panel class=\"root\"><label class=\"title\">Hello</label><button>OK</button></panel>",
                ".root { width: 200; height: 120; gap: 6; padding: 4; } .title { width: 80; }",
                context);

        assertInstanceOf(OreSurface.class, root);
        assertEquals(200f, root.style().width(), 0.01f);
        assertEquals(120f, root.style().height(), 0.01f);
        assertEquals(2, root.children().size());

        OreLabel label = assertInstanceOf(OreLabel.class, root.children().get(0));
        assertTrue(label.lines().contains("Hello"));
        assertEquals(80f, label.style().width(), 0.01f);

        OreButton button = assertInstanceOf(OreButton.class, root.children().get(1));
        assertEquals(ButtonVariant.PRIMARY, button.variant());
    }

    @Test
    void supportsRowAndColumnContainers() {
        ComponentContext context = TestFixtures.context();
        OreWidget root = DeclarativeView.build(
                "<row><card><label>A</label></card><card><label>B</label></card></row>",
                "row { width: 240; height: 80; }", context);

        assertEquals(2, root.children().size());
        assertInstanceOf(OreSurface.class, root.children().get(0));
    }

    @Test
    void declarativeTreeParticipatesInLayout() {
        ComponentContext context = TestFixtures.context();
        OreWidget root = DeclarativeView.build(
                "<row class=\"r\"><card class=\"c\"><label>A</label></card>"
                        + "<card class=\"c\"><label>B</label></card></row>",
                ".r { width: 240; height: 80; gap: 6; } .c { width: 100; height: 40; }",
                context);
        ComponentTree tree = new ComponentTree(root);
        tree.layout(new Rect2f(0f, 0f, 240f, 80f));

        OreWidget first = root.children().get(0);
        OreWidget second = root.children().get(1);
        assertTrue(first.bounds().width() > 0f);
        assertTrue(second.bounds().width() > 0f);
        assertTrue(first.bounds().x() < second.bounds().x());
        assertEquals(first.bounds().y(), second.bounds().y(), 0.01f);
    }

    @Test
    void nestedContainersHonourLaterCssRuleAndRender() {
        ComponentContext context = TestFixtures.context();
        OreWidget root = DeclarativeView.build(
                "<column class=\"root\"><row class=\"r\"><card class=\"c\"><label>A</label></card>"
                        + "<card class=\"c\"><label>B</label></card></row></column>",
                ".root { width: 240; height: 120; } .r { width: 200; height: 80; gap: 4; }"
                        + " .c { width: 90; height: 40; } .c { width: 100; height: 40; }",
                context);
        OreWidget row = root.children().get(0);
        OreWidget first = row.children().get(0);

        assertEquals(100f, first.style().width(), 0.01f);
        ComponentTree tree = new ComponentTree(root);
        tree.layout(new Rect2f(0f, 0f, 240f, 120f));
        assertTrue(first.bounds().width() > 0f);
        assertDoesNotThrow(() -> tree.renderForeground(new TestRenderContext()));
    }
}