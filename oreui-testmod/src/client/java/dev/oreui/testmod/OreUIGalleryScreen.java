package dev.oreui.testmod;

import dev.oreui.adapter262.Fabric26Clipboard;
import dev.oreui.adapter262.Fabric26GameObjectRenderer;
import dev.oreui.adapter262.Fabric26ItemHandle;
import dev.oreui.adapter262.Fabric26KeybindBridge;
import dev.oreui.adapter262.Fabric26SoundBridge;
import dev.oreui.adapter262.Fabric26TextMeasurer;
import dev.oreui.adapter262.Fabric26TextSource;
import dev.oreui.adapter262.Fabric26TimeSource;
import dev.oreui.adapter262.OreFabricScreenHost;
import dev.oreui.components.BadgeSeverity;
import dev.oreui.components.ChoiceMode;
import dev.oreui.components.ComponentContext;
import dev.oreui.components.ComponentTree;
import dev.oreui.components.ControlSize;
import dev.oreui.components.DropdownSize;
import dev.oreui.components.IconKind;
import dev.oreui.components.InputValidation;
import dev.oreui.components.LabelOverflow;
import dev.oreui.components.LiteralText;
import dev.oreui.components.ModalSize;
import dev.oreui.components.OreBadge;
import dev.oreui.components.OreButton;
import dev.oreui.components.OreChoiceBox;
import dev.oreui.components.OreDivider;
import dev.oreui.components.OreDropdown;
import dev.oreui.components.OreIcon;
import dev.oreui.components.OreItemGrid;
import dev.oreui.components.OreItemSlot;
import dev.oreui.components.OreLabel;
import dev.oreui.components.OreModal;
import dev.oreui.components.OreProgressBar;
import dev.oreui.components.OreScrollPane;
import dev.oreui.components.OreSlider;
import dev.oreui.components.OreSpinner;
import dev.oreui.components.OreSurface;
import dev.oreui.components.OreTabs;
import dev.oreui.components.OreTextField;
import dev.oreui.components.OreToast;
import dev.oreui.components.OreToggle;
import dev.oreui.components.OreTooltip;
import dev.oreui.components.OreWidget;
import dev.oreui.components.Orientation;
import dev.oreui.components.ProgressStyle;
import dev.oreui.components.ScrollBarPolicy;
import dev.oreui.components.SliderMode;
import dev.oreui.components.SlotStyle;
import dev.oreui.components.SurfaceVariant;
import dev.oreui.components.ToastSeverity;
import dev.oreui.components.TooltipPlacement;
import dev.oreui.components.platform.IGameObjectRenderer.IItemHandle;
import dev.oreui.core.geom.Insets;
import dev.oreui.core.geom.Rect2f;
import dev.oreui.layout.LayoutStyle;
import dev.oreui.theme.ButtonVariant;
import dev.oreui.theme.OreTokens;
import dev.oreui.theme.TypeRole;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public final class OreUIGalleryScreen extends Screen {
    private static final Logger LOG = LoggerFactory.getLogger("oreui-testmod");
    private static final float PANEL_W = 420f;
    private static final float PANEL_H = 232f;
    private static final float PAD = 4f;
    private static final float LEFT_W = 232f;
    private static final float RIGHT_W = 176f;
    private static final float COLUMN_GAP = 2f;

    private final Rect2f viewport = new Rect2f();
    private ComponentTree tree;
    private OreFabricScreenHost host;
    private OreItemGrid grid;
    private OreItemSlot slot;
    private boolean itemsReady;
    private boolean pendingLogged;

    public OreUIGalleryScreen() {
        super(Component.literal("OreUI Gallery"));
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

        Box left = new Box(context, LayoutStyle.Direction.COLUMN, COLUMN_GAP);
        left.style().width(LEFT_W);
        Box right = new Box(context, LayoutStyle.Direction.COLUMN, COLUMN_GAP);
        right.style().width(RIGHT_W);
        root.addChild(left);
        root.addChild(right);

        buildLeft(context, left);
        buildRight(context, right);

        tree = new ComponentTree(root);
        host = new OreFabricScreenHost(this, OreTokens.DARK, textSource,
                (ctx, layer, tickDelta) -> {
                    if (layer == OreFabricScreenHost.RenderLayer.FOREGROUND) {
                        tree.renderBackground(ctx);
                        tree.renderForeground(ctx);
                    }
                }, event -> tree.dispatch(event), gameObjects);
        host.attach();
    }

    public boolean itemsReady() {
        return itemsReady;
    }

    public void tickFrame() {
        if (tree == null) {
            return;
        }
        if (!itemsReady && wireRealItems()) {
            itemsReady = true;
            LOG.info("[oreui] gallery items wired to ItemStack");
        }
        viewport.set((width - PANEL_W) * 0.5f, (height - PANEL_H) * 0.5f, PANEL_W, PANEL_H);
        tree.layout(viewport);
        tree.update(1f / 20f);
    }

    private boolean wireRealItems() {
        try {
            grid.setItems(List.of(
                    realItem(Items.APPLE, 5), realItem(Items.STICK, 12), realItem(Items.STONE, 32),
                    realItem(Items.COAL, 8), realItem(Items.IRON_INGOT, 3),
                    realItem(Items.GOLD_INGOT, 1), realItem(Items.REDSTONE, 16),
                    realItem(Items.EMERALD, 2), realItem(Items.DIAMOND, 4)));
            ItemStack worn = new ItemStack(Items.DIAMOND_SWORD);
            worn.setDamageValue(120);
            slot.setItem(new Fabric26ItemHandle(worn));
            return true;
        } catch (RuntimeException exception) {
            if (!pendingLogged) {
                pendingLogged = true;
                LOG.info("[oreui] gallery items pending: {}", exception.getMessage());
            }
            return false;
        }
    }

    private static Fabric26ItemHandle realItem(ItemLike item, int count) {
        return new Fabric26ItemHandle(new ItemStack(item, count));
    }

    private static List<IItemHandle> fallbackItems() {
        return List.of(
                new PreviewItem(5, false, 0, 0), new PreviewItem(12, false, 0, 0),
                new PreviewItem(32, false, 0, 0), new PreviewItem(8, false, 0, 0),
                new PreviewItem(3, false, 0, 0), new PreviewItem(1, false, 0, 0),
                new PreviewItem(16, false, 0, 0), new PreviewItem(4, false, 0, 0),
                new PreviewItem(2, false, 0, 0));
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
        OreLabel title = new OreLabel(context, new LiteralText("OreUI v1 Gallery"), TypeRole.BODY,
                LabelOverflow.CLIP, (int) LEFT_W);
        title.style().width(LEFT_W);
        left.addChild(title);

        OreDivider divider = new OreDivider(context, Orientation.HORIZONTAL);
        divider.style().width(LEFT_W);
        left.addChild(divider);

        Box buttons = new Box(context, LayoutStyle.Direction.ROW, PAD);
        OreButton primary = new OreButton(context, ButtonVariant.PRIMARY, ControlSize.MD,
                new LiteralText("Apply"), () -> {
                });
        primary.style().width(80f);
        OreButton secondary = new OreButton(context, ButtonVariant.SECONDARY, ControlSize.MD,
                new LiteralText("Cancel"), () -> {
                });
        secondary.style().width(80f);
        OreToggle toggle = new OreToggle(context);
        toggle.setOn(true);
        buttons.addChild(primary);
        buttons.addChild(secondary);
        buttons.addChild(toggle);
        left.addChild(buttons);

        OreSlider slider = new OreSlider(context, SliderMode.CONTINUOUS);
        slider.style().width(LEFT_W);
        slider.setValue(0.35f);
        left.addChild(slider);

        OreTextField field = new OreTextField(context, InputValidation.TEXT, 24);
        field.style().width(LEFT_W);
        field.setText("oreui");
        left.addChild(field);

        OreProgressBar progress = new OreProgressBar(context, ProgressStyle.CONTINUOUS);
        progress.style().width(LEFT_W);
        progress.setValue(0.6f);
        left.addChild(progress);

        OreTabs tabs = new OreTabs(context, Orientation.HORIZONTAL,
                List.of(new LiteralText("Basics"), new LiteralText("Forms"),
                        new LiteralText("Overlays")));
        tabs.style().width(LEFT_W).height(18f);
        left.addChild(tabs);

        OreDropdown dropdown = new OreDropdown(context, DropdownSize.STANDARD,
                List.of(new LiteralText("Alpha"), new LiteralText("Beta")));
        dropdown.style().width(LEFT_W).height(16f);
        left.addChild(dropdown);

        OreSurface content = new OreSurface(context, SurfaceVariant.CARD);
        content.style().width(LEFT_W).height(96f).padding(new Insets(2f, 2f, 2f, 2f)).gap(2f);
        for (int row = 1; row <= 5; row++) {
            content.addChild(new OreLabel(context, new LiteralText("Scroll row " + row),
                    TypeRole.CAPTION, LabelOverflow.CLIP, (int) LEFT_W - 4));
        }
        OreScrollPane scroll = new OreScrollPane(context, content, ScrollBarPolicy.ALWAYS);
        scroll.style().width(LEFT_W).height(44f);
        scroll.scrollBy(10f);
        left.addChild(scroll);

        OreSurface card = new OreSurface(context, SurfaceVariant.CARD);
        card.style().width(LEFT_W).height(24f).padding(new Insets(2f, 2f, 2f, 2f)).gap(0f);
        card.addChild(new OreLabel(context, new LiteralText("Surface / CARD"), TypeRole.CAPTION,
                LabelOverflow.CLIP, (int) LEFT_W - 4));
        left.addChild(card);
    }

    private void buildRight(ComponentContext context, Box right) {
        Box icons = new Box(context, LayoutStyle.Direction.ROW, PAD);
        OreChoiceBox choice = new OreChoiceBox(context, ChoiceMode.CHECKBOX_MULTI);
        choice.setSelected(true);
        icons.addChild(choice);
        icons.addChild(new OreIcon(context, IconKind.CHECK, 12));
        icons.addChild(new OreIcon(context, IconKind.CLOSE, 8));
        icons.addChild(new OreIcon(context, IconKind.DOT, 8));
        right.addChild(icons);

        Box feedback = new Box(context, LayoutStyle.Direction.ROW, PAD);
        OreBadge badge = new OreBadge(context, new LiteralText("NEW"), BadgeSeverity.INFO);
        badge.style().width(80f);
        OreSpinner spinner = new OreSpinner(context);
        spinner.style().width(10f).height(10f);
        feedback.addChild(badge);
        feedback.addChild(spinner);
        right.addChild(feedback);

        OreTooltip tooltip = new OreTooltip(context, new LiteralText("Tooltip"), TooltipPlacement.BOTTOM);
        tooltip.style().width(RIGHT_W).height(18f);
        tooltip.setShown(true);
        right.addChild(tooltip);

        OreToast toast = new OreToast(context, new LiteralText("Saved"), ToastSeverity.SUCCESS);
        toast.show(30f);
        toast.style().width(RIGHT_W).height(24f);
        right.addChild(toast);

        grid = new OreItemGrid(context);
        grid.setItems(fallbackItems());
        right.addChild(grid);

        slot = new OreItemSlot(context, SlotStyle.LARGE_26);
        slot.setItem(new PreviewItem(1, true, 30, 100));
        right.addChild(slot);

        OreModal modal = new OreModal(context, new LiteralText("Modal"),
                new LiteralText("Overlay host"), ModalSize.DIALOG_MD);
        modal.style().width(RIGHT_W).height(80f);
        modal.setOpen(true);
        right.addChild(modal);
    }


    private static final class Box extends OreWidget {
        private Box(ComponentContext context, LayoutStyle.Direction direction, float gap) {
            super(context);
            style().direction(direction);
            style().gap(gap);
        }
    }

    private static final class PreviewItem implements IItemHandle {
        private final int count;
        private final boolean damageable;
        private final int durability;
        private final int durabilityMax;

        private PreviewItem(int count, boolean damageable, int durability, int durabilityMax) {
            this.count = count;
            this.damageable = damageable;
            this.durability = durability;
            this.durabilityMax = durabilityMax;
        }

        @Override
        public boolean isEmpty() {
            return count <= 0;
        }

        @Override
        public int count() {
            return count;
        }

        @Override
        public boolean isDamageable() {
            return damageable;
        }

        @Override
        public int durability() {
            return durability;
        }

        @Override
        public int durabilityMax() {
            return durabilityMax;
        }
    }
}