package dev.oreui.components;

import dev.oreui.core.geom.Rect2f;
import dev.oreui.theme.TypeRole;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Phase7ControlsTest {
    @Test
    void tabsSelectByPointer() {
        ComponentContext context = TestFixtures.context();
        OreTabs tabs = new OreTabs(context, Orientation.HORIZONTAL,
                List.of(TestFixtures.text("A"), TestFixtures.text("B"), TestFixtures.text("C")));
        tabs.layoutNode().frame().set(0f, 0f, 180f, 22f);
        tabs.syncFrames();
        tabs.pointerDown(90f, 10f);
        assertEquals(1, tabs.selected());
    }

    @Test
    void dropdownSelectsPopupItem() {
        ComponentContext context = TestFixtures.context();
        OreDropdown dropdown = new OreDropdown(context, DropdownSize.STANDARD,
                List.of(TestFixtures.text("A"), TestFixtures.text("B")));
        dropdown.style().width(100f);
        dropdown.layoutNode().frame().set(0f, 0f, 100f, 20f);
        dropdown.syncFrames();
        dropdown.pointerDown(10f, 10f);
        assertTrue(dropdown.isOpen());
        dropdown.setOpen(false);
        assertFalse(dropdown.isOpen());
    }

    @Test
    void scrollPaneClampsOffset() {
        ComponentContext context = TestFixtures.context();
        OreSurface content = new OreSurface(context, SurfaceVariant.CARD);
        content.style().width(180f).height(120f).padding(new dev.oreui.core.geom.Insets(0f, 0f, 0f, 0f)).gap(0f);
        OreScrollPane pane = new OreScrollPane(context, content, ScrollBarPolicy.ALWAYS);
        pane.layoutNode().frame().set(0f, 0f, 200f, 50f);
        pane.syncFrames();
        pane.scrollBy(30f);
        assertEquals(30f, pane.scrollY(), 0.1f);
        pane.scrollBy(1000f);
        assertEquals(70f, pane.scrollY(), 0.1f);
        pane.scrollBy(-1000f);
        assertEquals(0f, pane.scrollY(), 0.1f);
    }

    @Test
    void feedbackControlsUpdateState() {
        ComponentContext context = TestFixtures.context();
        OreToast toast = new OreToast(context, TestFixtures.text("saved"), ToastSeverity.SUCCESS);
        toast.show(1f);
        toast.update(0.5f);
        assertEquals(0.5f, toast.remaining(), 0.01f);
        OreModal modal = new OreModal(context, TestFixtures.text("Title"), TestFixtures.text("Body"),
                ModalSize.CONFIRM_SM);
        modal.setOpen(true);
        assertTrue(modal.isOpen());
        modal.layoutNode().frame().set(0f, 0f, 220f, 100f);
        modal.syncFrames();
        modal.pointerDown(10f, 10f);
        assertFalse(modal.isOpen());
        OreSpinner spinner = new OreSpinner(context);
        spinner.update(0.1f);
        OreBadge badge = new OreBadge(context, TestFixtures.text("NEW"), BadgeSeverity.INFO);
        badge.style().width(80f);
    }

    @Test
    void tooltipAndBadgeRenderTypesExist() {
        ComponentContext context = TestFixtures.context();
        OreTooltip tooltip = new OreTooltip(context, TestFixtures.text("tip"), TooltipPlacement.BOTTOM);
        tooltip.setShown(false);
        assertEquals(TooltipPlacement.BOTTOM, tooltip.placement());
        OreLabel label = new OreLabel(context, TestFixtures.text("x"), TypeRole.BODY,
                LabelOverflow.CLIP, 100);
        assertEquals(1, label.lines().size());
        assertTrue(new Rect2f(0f, 0f, 10f, 10f).contains(1f, 1f));
    }
}