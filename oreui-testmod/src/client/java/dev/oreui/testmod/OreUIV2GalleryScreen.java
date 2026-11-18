package dev.oreui.testmod;

import dev.oreui.adapter262.Fabric26Clipboard;
import dev.oreui.adapter262.Fabric26EntityHandle;
import dev.oreui.adapter262.Fabric26FluidHandle;
import dev.oreui.adapter262.Fabric26GameObjectRenderer;
import dev.oreui.adapter262.Fabric26KeybindBridge;
import dev.oreui.adapter262.Fabric26SoundBridge;
import dev.oreui.adapter262.Fabric26TextMeasurer;
import dev.oreui.adapter262.Fabric26TextSource;
import dev.oreui.adapter262.Fabric26TimeSource;
import dev.oreui.adapter262.OreFabricScreenHost;
import dev.oreui.components.AccordionMode;
import dev.oreui.components.ComponentContext;
import dev.oreui.components.ComponentTree;
import dev.oreui.components.ContextMenuEntry;
import dev.oreui.components.KeyConflictPolicy;
import dev.oreui.components.KeyCodes;
import dev.oreui.components.LabelOverflow;
import dev.oreui.components.LiteralText;
import dev.oreui.components.OreAccordion;
import dev.oreui.components.OreContextMenu;
import dev.oreui.components.OreEntityPreview;
import dev.oreui.components.OreFluidSlot;
import dev.oreui.components.OreKeybind;
import dev.oreui.components.OreLabel;
import dev.oreui.components.OrePanelFrame;
import dev.oreui.components.OreSegmentedControl;
import dev.oreui.components.OreSurface;
import dev.oreui.components.OreVirtualList;
import dev.oreui.components.OreWidget;
import dev.oreui.components.Orientation;
import dev.oreui.components.RowHeightPolicy;
import dev.oreui.components.SurfaceVariant;
import dev.oreui.core.geom.Insets;
import dev.oreui.core.geom.Rect2f;
import dev.oreui.layout.LayoutStyle;
import dev.oreui.theme.OreTokens;
import dev.oreui.theme.TypeRole;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public final class OreUIV2GalleryScreen extends Screen {
    private static final Logger LOG = LoggerFactory.getLogger("oreui-testmod");
    private static final float PANEL_W = 420f;
    private static final float PANEL_H = 232f;
    private static final float PAD = 4f;
    private static final float LEFT_W = 232f;
    private static final float RIGHT_W = 176f;
    private static final float GAP = 2f;

    private final Rect2f viewport = new Rect2f();
    private ComponentTree tree;
    private OreFabricScreenHost host;
    private OreContextMenu menu;
    private OrePanelFrame panel;
    private OreEntityPreview preview;
    private boolean overlaysPlaced;
    private boolean entityRectLogged;

    public OreUIV2GalleryScreen() {
        super(Component.literal("OreUI v2 Gallery"));
    }

    @Override
    protected void init() {
        if (tree != null) {
            return;
        }
        Minecraft client = Minecraft.getInstance();
        Fabric26TextSource textSource = new Fabric26TextSource();
        Fabric26TextMeasurer measurer = new Fabric26TextMeasurer(client.font, textSource, OreTokens.DARK);
        Fabric26GameObjectRenderer gameObjects = new Fabric26GameObjectRenderer();
        ComponentContext context = new ComponentContext(OreTokens.DARK, textSource, measurer,
                new Fabric26Clipboard(), new Fabric26SoundBridge(), new Fabric26KeybindBridge(),
                new Fabric26TimeSource(), gameObjects);

        OreSurface root = new OreSurface(context, SurfaceVariant.PANEL);
        root.style().width(PANEL_W).height(PANEL_H).padding(new Insets(PAD, PAD, PAD, PAD))
                .gap(PAD).direction(LayoutStyle.Direction.ROW);

        Box left = new Box(context, LayoutStyle.Direction.COLUMN, GAP);
        left.style().width(LEFT_W);
        Box right = new Box(context, LayoutStyle.Direction.COLUMN, GAP);
        right.style().width(RIGHT_W);
        root.addChild(left);
        root.addChild(right);

        buildLeft(context, left);
        buildRight(context, right, client);

        tree = new ComponentTree(root);
        menu = new OreContextMenu(context, List.of(
                new ContextMenuEntry(new LiteralText("Rename"), false, () -> {
                }),
                new ContextMenuEntry(new LiteralText("Duplicate"), false, () -> {
                }),
                new ContextMenuEntry(new LiteralText("Delete"), true, () -> {
                })));
        root.addChild(menu);
        menu.setOverlayManager(tree.overlayManager());

        panel = new OrePanelFrame(context, new LiteralText("v2 Panel"));
        panel.setContent(new OreLabel(context, new LiteralText("Draggable panel frame"),
                TypeRole.CAPTION, LabelOverflow.CLIP, 180));
        panel.setOverlayManager(tree.overlayManager());

        host = new OreFabricScreenHost(this, OreTokens.DARK, textSource,
                (ctx, layer, tickDelta) -> {
                    if (layer == OreFabricScreenHost.RenderLayer.FOREGROUND) {
                        tree.renderBackground(ctx);
                        tree.renderForeground(ctx);
                    }
                }, event -> tree.dispatch(event), gameObjects);
        host.attach();
    }

    public void tickFrame() {
        if (tree == null) {
            return;
        }
        viewport.set((width - PANEL_W) * 0.5f, (height - PANEL_H) * 0.5f, PANEL_W, PANEL_H);
        tree.layout(viewport);
        tree.update(1f / 20f);
        if (!overlaysPlaced) {
            overlaysPlaced = true;
            placeOverlays();
        }
        Minecraft client = Minecraft.getInstance();
        if (preview != null && preview.entity() == null && client.player != null) {
            preview.setEntity(new Fabric26EntityHandle(client.player));
            LOG.info("[oreui] v2 entity bound");
        }
        if (preview != null && !entityRectLogged && !preview.bounds().isEmpty()) {
            entityRectLogged = true;
            LOG.info("[oreui] v2 entity rect {}x{}", preview.bounds().width(), preview.bounds().height());
        }
    }

    private void placeOverlays() {
        float x = viewport.x();
        float y = viewport.y();
        panel.layoutNode().frame().set(x + 236f, y + 96f, 172f, 120f);
        panel.syncFrames();
        panel.open();
        menu.open(x + 40f, y + 170f);
        LOG.info("[oreui] v2 gallery overlays placed");
    }

    @Override
    public void removed() {
        if (host != null) {
            host.detach();
        }
        super.removed();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void buildLeft(ComponentContext context, Box left) {
        OreSegmentedControl segments = new OreSegmentedControl(context, Orientation.HORIZONTAL,
                List.of(new LiteralText("Day"), new LiteralText("Week"), new LiteralText("Month")));
        segments.style().width(LEFT_W).height(22f);
        segments.select(1);
        left.addChild(segments);

        OreAccordion accordion = new OreAccordion(context, AccordionMode.SINGLE);
        accordion.style().width(LEFT_W);
        accordion.addSection(new LiteralText("General"), card(context, "General content", LEFT_W, 34f));
        accordion.addSection(new LiteralText("Advanced"), card(context, "Advanced content", LEFT_W, 34f));
        accordion.setExpanded(0, true);
        left.addChild(accordion);

        String[] rows = new String[24];
        for (int i = 0; i < rows.length; i++) {
            rows[i] = "Virtual row " + i;
        }
        OreVirtualList<String> list = new OreVirtualList<>(context, RowHeightPolicy.FIXED, 16f,
                List.of(rows), (rowContext, value) -> new OreLabel(rowContext, new LiteralText(value),
                        TypeRole.CAPTION, LabelOverflow.CLIP, 200));
        list.style().width(LEFT_W).height(64f);
        list.setScroll(32f);
        left.addChild(list);

        OreKeybind keybind = new OreKeybind(context, KeyConflictPolicy.WARN);
        keybind.style().width(LEFT_W);
        keybind.setKeyCode(KeyCodes.ENTER);
        keybind.setConflict(true);
        left.addChild(keybind);
    }

    private void buildRight(ComponentContext context, Box right, Minecraft client) {
        OreFluidSlot fluid = new OreFluidSlot(context);
        fluid.style().width(26f).height(26f);
        fluid.setLevel(0.65f);
        fluid.setFluid(new Fabric26FluidHandle(
                Identifier.fromNamespaceAndPath("minecraft", "block/water_still"), -1));
        right.addChild(fluid);

        OreEntityPreview previewWidget = new OreEntityPreview(context);
        previewWidget.style().width(48f).height(48f);
        previewWidget.setView(30f, 0f);
        preview = previewWidget;
        right.addChild(preview);

        right.addChild(new OreLabel(context, new LiteralText("fluid / entity / menu / panel"),
                TypeRole.CAPTION, LabelOverflow.CLIP, (int) RIGHT_W));
    }

    private static OreSurface card(ComponentContext context, String text, float width, float height) {
        OreSurface card = new OreSurface(context, SurfaceVariant.CARD);
        card.style().width(width).height(height);
        card.addChild(new OreLabel(context, new LiteralText(text), TypeRole.CAPTION,
                LabelOverflow.CLIP, (int) width - 12));
        return card;
    }

    private static final class Box extends OreWidget {
        private Box(ComponentContext context, LayoutStyle.Direction direction, float gap) {
            super(context);
            style().direction(direction);
            style().gap(gap);
        }
    }
}