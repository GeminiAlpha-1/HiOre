package dev.oreui.testmod;

import dev.oreui.adapter262.Fabric26Clipboard;
import dev.oreui.adapter262.Fabric26EntityHandle;
import dev.oreui.adapter262.Fabric26FluidHandle;
import dev.oreui.adapter262.Fabric26GameObjectRenderer;
import dev.oreui.adapter262.Fabric26ItemHandle;
import dev.oreui.adapter262.Fabric26KeybindBridge;
import dev.oreui.adapter262.Fabric26SoundBridge;
import dev.oreui.adapter262.Fabric26TextMeasurer;
import dev.oreui.adapter262.Fabric26TextSource;
import dev.oreui.adapter262.Fabric26TextFieldHost;
import dev.oreui.adapter262.Fabric26TimeSource;
import dev.oreui.adapter262.OreFabricScreenHost;
import dev.oreui.components.AccordionMode;
import dev.oreui.components.BadgeSeverity;
import dev.oreui.components.ChoiceMode;
import dev.oreui.components.ComponentContext;
import dev.oreui.components.ComponentTree;
import dev.oreui.components.ContextMenuEntry;
import dev.oreui.components.ControlSize;
import dev.oreui.components.DropdownSize;
import dev.oreui.components.IconKind;
import dev.oreui.components.InputValidation;
import dev.oreui.components.KeyCodes;
import dev.oreui.components.KeyConflictPolicy;
import dev.oreui.components.LabelOverflow;
import dev.oreui.components.LiteralText;
import dev.oreui.components.ModalSize;
import dev.oreui.components.OreAccordion;
import dev.oreui.components.OreBadge;
import dev.oreui.components.OreButton;
import dev.oreui.components.OreChoiceBox;
import dev.oreui.components.OreContextMenu;
import dev.oreui.components.OreDivider;
import dev.oreui.components.OreDropdown;
import dev.oreui.components.OreEntityPreview;
import dev.oreui.components.OreFluidSlot;
import dev.oreui.components.OreIcon;
import dev.oreui.components.OreItemGrid;
import dev.oreui.components.OreItemSlot;
import dev.oreui.components.OreKeybind;
import dev.oreui.components.OreLabel;
import dev.oreui.components.OreModal;
import dev.oreui.components.OrePanelFrame;
import dev.oreui.components.OreProgressBar;
import dev.oreui.components.OreScrollPane;
import dev.oreui.components.OreSegmentedControl;
import dev.oreui.components.OreSlider;
import dev.oreui.components.OreSpinner;
import dev.oreui.components.OreSurface;
import dev.oreui.components.OreTabs;
import dev.oreui.components.OreTextField;
import dev.oreui.components.OreToast;
import dev.oreui.components.OreToggle;
import dev.oreui.components.OreTooltip;
import dev.oreui.components.OreVirtualList;
import dev.oreui.components.OreWidget;
import dev.oreui.components.Orientation;
import dev.oreui.components.ProgressStyle;
import dev.oreui.components.RowHeightPolicy;
import dev.oreui.components.ScrollBarPolicy;
import dev.oreui.components.SliderMode;
import dev.oreui.components.SlotStyle;
import dev.oreui.components.SurfaceVariant;
import dev.oreui.components.TextAlign;
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
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public final class OreComponentShowcaseScreen extends Screen {
    private static final Logger LOG = LoggerFactory.getLogger("oreui-testmod");
    private static final float PANEL_W = 420f;
    private static final float PANEL_H = 232f;
    private static final float PAD = 4f;
    private static final float GAP = 2f;
    private static final float CARD_PAD = 2f;
    private static final float CARD_GAP = 1f;
    private static final float CONTENT_W = PANEL_W - PAD * 2f;
    private static final float CARD_W = CONTENT_W;
    private static final float CARD_INNER_W = CARD_W - 8f;
    private static final String[] PAGE_NAMES = {
            "基础原子", "表单控件", "反馈与文本", "导航控件", "滚动与浮层入口",
            "复合容器", "游戏物品", "游戏实体与面板", "模态宿主"
    };
    private static final int INITIAL_PAGE = Integer.getInteger("orui.page", 0);

    private final Rect2f viewport = new Rect2f();
    private final List<OreWidget> pages = new ArrayList<>();
    private ComponentTree tree;
    private OreFabricScreenHost host;
    private OreSurface root;
    private OreWidget stage;
    private OreLabel pageLabel;
    private OreModal modal;
    private OreContextMenu contextMenu;
    private OreToast toast;
    private OreItemGrid grid;
    private OreItemSlot slot;
    private OreEntityPreview preview;
    private float panelHeight;
    private int pageIndex;

    private boolean itemsWired;
    private boolean pendingLogged;
    private boolean dumped;


    public OreComponentShowcaseScreen() {
        super(Component.literal("OreUI Component Showcase"));
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

        panelHeight = Math.max(200f, Math.min(PANEL_H, height - 8f));
        root = new OreSurface(context, SurfaceVariant.PANEL);
        root.style().width(PANEL_W).height(panelHeight).padding(new Insets(PAD, PAD, PAD, PAD))
                .gap(GAP).direction(LayoutStyle.Direction.COLUMN);

        buildHeader(context);
        stage = new Box(context, LayoutStyle.Direction.COLUMN, GAP);
        stage.style().width(CONTENT_W).flexGrow(1f);
        root.addChild(stage);
        buildFooter(context);

        pages.add(buildBasics(context));
        pages.add(buildForms(context));
        pages.add(buildFeedback(context));
        pages.add(buildNavigation(context));
        pages.add(buildScrollAndOverlays(context));
        pages.add(buildComposites(context));
        pages.add(buildGameItems(context));
        pages.add(buildGameEntity(context));
        pages.add(buildModal(context));
        stage.addChild(pages.get(0));
        updatePageLabel();
        if (INITIAL_PAGE > 0) {
            showPage(Math.floorMod(INITIAL_PAGE, pages.size()));
        }

        tree = new ComponentTree(root);
        contextMenu.setOverlayManager(tree.overlayManager());
        host = new OreFabricScreenHost(this, OreTokens.DARK, textSource,
                (ctx, layer, tickDelta) -> {
                    if (layer == OreFabricScreenHost.RenderLayer.FOREGROUND) {

                        tree.renderBackground(ctx);
                        tree.renderForeground(ctx);
                    }
                }, event -> tree.dispatch(event), gameObjects);
        host.attach();

        LOG.info("[oreui] showcase screen opened");
    }

    private void buildHeader(ComponentContext context) {
        Box header = new Box(context, LayoutStyle.Direction.ROW, 6f);
        header.style().width(CONTENT_W).height(20f).alignItems(LayoutStyle.Align.CENTER);
        OreLabel title = new OreLabel(context, new LiteralText("OreUI Component Showcase"),
                TypeRole.BODY, LabelOverflow.ELLIPSIS, 178);
        title.style().width(178f);
        OreLabel hint = new OreLabel(context, new LiteralText("Esc 返回 · F8 重开"),
                TypeRole.CAPTION, LabelOverflow.CLIP, 120);
        hint.style().width(120f);
        OreBadge badge = new OreBadge(context, new LiteralText("28 widgets"), BadgeSeverity.INFO);
        badge.style().width(78f).height(12f);
        header.addChild(title);
        header.addChild(hint);
        header.addChild(badge);
        root.addChild(header);
    }

    private void buildFooter(ComponentContext context) {
        Box footer = new Box(context, LayoutStyle.Direction.ROW, 6f);
        footer.style().width(CONTENT_W).height(20f).alignItems(LayoutStyle.Align.CENTER);
        OreButton previous = new OreButton(context, ButtonVariant.SECONDARY, ControlSize.SM,
                new LiteralText("上一页"), () -> turn(-1));
        previous.style().width(64f);
        pageLabel = new OreLabel(context, new LiteralText(""), TypeRole.CAPTION,
                LabelOverflow.ELLIPSIS, 260);
        pageLabel.style().width(260f);
        OreButton next = new OreButton(context, ButtonVariant.PRIMARY, ControlSize.SM,
                new LiteralText("下一页"), () -> turn(1));
        next.style().width(64f);
        footer.addChild(previous);
        footer.addChild(pageLabel);
        footer.addChild(next);
        root.addChild(footer);
    }

    private void turn(int delta) {
        showPage(Math.floorMod(pageIndex + delta, pages.size()));
    }

    private void showPage(int index) {
        pageIndex = index;
        while (!stage.children().isEmpty()) {
            stage.removeChild(stage.children().get(0));
        }
        stage.addChild(pages.get(index));
        updatePageLabel();
        root.markDirty();
    }

    private void updatePageLabel() {
        pageLabel.setText(new LiteralText("第 " + (pageIndex + 1) + " / " + pages.size()
                + " 页 · " + PAGE_NAMES[pageIndex]));
    }

    public void tickFrame() {
        if (tree == null) {
            return;
        }
        if (!itemsWired && wireRealItems()) {
            itemsWired = true;
            LOG.info("[oreui] showcase items wired to ItemStack");
        }
        viewport.set((width - PANEL_W) * 0.5f, (height - panelHeight) * 0.5f, PANEL_W, panelHeight);
        tree.layout(viewport);
        tree.update(1f / 20f);

        if (!dumped && Boolean.getBoolean("orui.dump")) {
            dumped = true;
            dump(root, 0);
        }
        if (toast != null) {
            toast.show(3600f);
        }
        Minecraft client = Minecraft.getInstance();
        if (preview != null && preview.entity() == null && client.player != null) {
            preview.setEntity(new Fabric26EntityHandle(client.player));
            LOG.info("[oreui] showcase entity bound");
        }
    }

    private void dump(OreWidget widget, int depth) {
        LOG.info("[layout] {}{} {} {} {} {}", "  ".repeat(depth),
                widget.getClass().getSimpleName(), widget.bounds().x(), widget.bounds().y(),
                widget.bounds().width(), widget.bounds().height());
        for (OreWidget child : widget.children()) {
            dump(child, depth + 1);
        }
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
                LOG.info("[oreui] showcase items pending: {}", exception.getMessage());
            }
            return false;
        }
    }

    private static Fabric26ItemHandle realItem(ItemLike item, int count) {
        return new Fabric26ItemHandle(new ItemStack(item, count));
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


    private Box buildBasics(ComponentContext context) {
        Box page = newPage(context);

        Box labelRow = card(context, page, "OreLabel", "CLIP / ELLIPSIS / WRAP", 20f);
        addLabel(labelRow, context, "CLIP 裁掉超长文本且不显示省略号",
                LabelOverflow.CLIP, 125f, 1);
        addLabel(labelRow, context, "ELLIPSIS 超长时追加省略号",
                LabelOverflow.ELLIPSIS, 125f, 1);
        OreLabel wrap = new OreLabel(context, new LiteralText("WRAP 两行自动换行，中文断行"),
                TypeRole.CAPTION, LabelOverflow.WRAP, 125, 2, TextAlign.START);
        wrap.style().width(125f);
        labelRow.addChild(wrap);

        Box dividerRow = card(context, page, "OreDivider", "HORIZONTAL / VERTICAL", 34f);
        dividerRow.style().direction(LayoutStyle.Direction.COLUMN)
                .alignItems(LayoutStyle.Align.CENTER)
                .justifyContent(LayoutStyle.Justify.CENTER);
        OreDivider horizontal = new OreDivider(context, Orientation.HORIZONTAL);
        horizontal.style().width(CARD_INNER_W);
        OreDivider vertical = new OreDivider(context, Orientation.VERTICAL);
        vertical.style().height(12f);
        dividerRow.addChild(horizontal);
        dividerRow.addChild(vertical);

        Box iconRow = card(context, page, "OreIcon", "CHECK / CHEVRON_DOWN / CLOSE / SEARCH / DOT", 16f);
        for (IconKind kind : IconKind.values()) {
            iconRow.addChild(new OreIcon(context, kind, 12));
        }

        Box surfaceRow = card(context, page, "OreSurface", "PANEL / CARD / WELL / TOOLBAR", 28f);
        for (SurfaceVariant variant : SurfaceVariant.values()) {
            OreSurface surface = new OreSurface(context, variant);
            surface.style().width(94f).height(28f).padding(new Insets(0f, 0f, 0f, 0f)).gap(0f);
            surfaceRow.addChild(surface);
        }
        return page;
    }

    private Box buildForms(ComponentContext context) {
        Box page = newPage(context);

        Box buttonRow = card(context, page, "OreButton", "PRIMARY / SECONDARY / DANGER / HERO / GHOST", 20f);
        addButton(buttonRow, context, ButtonVariant.PRIMARY, "PRIMARY");
        addButton(buttonRow, context, ButtonVariant.SECONDARY, "SECONDARY");
        addButton(buttonRow, context, ButtonVariant.DANGER, "DANGER");
        addButton(buttonRow, context, ButtonVariant.HERO, "HERO");
        addButton(buttonRow, context, ButtonVariant.GHOST, "GHOST");

        Box toggleRow = card(context, page, "OreToggle", "开 / 关", 16f);
        OreToggle on = new OreToggle(context);
        on.setOn(true);
        toggleRow.addChild(on);
        toggleRow.addChild(new OreToggle(context));

        Box choiceRow = card(context, page, "OreChoiceBox", "CHECKBOX_MULTI / RADIO_SINGLE", 16f);
        OreChoiceBox checked = new OreChoiceBox(context, ChoiceMode.CHECKBOX_MULTI);
        checked.setSelected(true);
        OreChoiceBox radioOn = new OreChoiceBox(context, ChoiceMode.RADIO_SINGLE);
        radioOn.setSelected(true);
        choiceRow.addChild(checked);
        choiceRow.addChild(new OreChoiceBox(context, ChoiceMode.CHECKBOX_MULTI));
        choiceRow.addChild(radioOn);
        choiceRow.addChild(new OreChoiceBox(context, ChoiceMode.RADIO_SINGLE));

        Box sliderRow = card(context, page, "OreSlider", "CONTINUOUS / DISCRETE_TICKS", 20f);
        OreSlider continuous = new OreSlider(context, SliderMode.CONTINUOUS);
        continuous.style().width(190f);
        continuous.setValue(0.35f);
        OreSlider discrete = new OreSlider(context, SliderMode.DISCRETE_TICKS);
        discrete.style().width(190f);
        discrete.setValue(0.6f);
        sliderRow.addChild(continuous);
        sliderRow.addChild(discrete);
        return page;
    }

    private Box buildFeedback(ComponentContext context) {
        Box page = newPage(context);

        Box progressRow = card(context, page, "OreProgressBar", "CONTINUOUS / SEGMENTED / INDETERMINATE", 20f);
        addProgress(progressRow, context, ProgressStyle.CONTINUOUS, 0.6f, "60%");
        addProgress(progressRow, context, ProgressStyle.SEGMENTED, 0.45f, null);
        addProgress(progressRow, context, ProgressStyle.INDETERMINATE, 0f, null);

        Box fieldRow = card(context, page, "OreTextField", "TEXT / INT", 20f);
        OreTextField text = new OreTextField(context, InputValidation.TEXT, 32,
                new Fabric26TextFieldHost(32));
        text.style().width(190f);
        text.setText("OreUI text field");
        OreTextField integer = new OreTextField(context, InputValidation.INT, 8);
        integer.style().width(190f);
        integer.setText("12345");
        fieldRow.addChild(text);
        fieldRow.addChild(integer);

        Box spinnerRow = card(context, page, "OreSpinner", "循环旋转", 16f);
        spinnerRow.addChild(new OreSpinner(context));
        spinnerRow.addChild(new OreSpinner(context));

        Box badgeRow = card(context, page, "OreBadge", "NEUTRAL / INFO / SUCCESS / WARNING / ERROR", 14f);
        for (BadgeSeverity severity : BadgeSeverity.values()) {
            OreBadge badge = new OreBadge(context, new LiteralText(severity.name()), severity);
            badge.style().width(74f).height(14f);
            badgeRow.addChild(badge);
        }
        return page;
    }

    private Box buildNavigation(ComponentContext context) {
        Box page = newPage(context);

        Box keybindRow = card(context, page, "OreKeybind", "WARN 冲突态 / 正常态", 20f);
        OreKeybind normal = new OreKeybind(context, KeyConflictPolicy.WARN);
        normal.style().width(190f);
        normal.setKeyCode(KeyCodes.ENTER);
        OreKeybind conflict = new OreKeybind(context, KeyConflictPolicy.WARN);
        conflict.style().width(190f);
        conflict.setKeyCode(KeyCodes.ENTER);
        conflict.setConflict(true);
        keybindRow.addChild(normal);
        keybindRow.addChild(conflict);

        Box tabsRow = card(context, page, "OreTabs", "HORIZONTAL，指示条 + 选中态", 22f);
        OreTabs tabs = new OreTabs(context, Orientation.HORIZONTAL,
                List.of(new LiteralText("Basics"), new LiteralText("Forms"),
                        new LiteralText("Overlays")));
        tabs.style().width(390f).height(22f);
        tabs.select(1);
        tabsRow.addChild(tabs);

        Box segmentedRow = card(context, page, "OreSegmentedControl", "HORIZONTAL，井槽 + 选中凸起", 22f);
        OreSegmentedControl segments = new OreSegmentedControl(context, Orientation.HORIZONTAL,
                List.of(new LiteralText("Day"), new LiteralText("Week"), new LiteralText("Month")));
        segments.style().width(390f).height(22f);
        segments.select(1);
        segmentedRow.addChild(segments);

        Box dropdownRow = card(context, page, "OreDropdown", "STANDARD，点击展开 Overlay 弹层", 20f);
        OreDropdown dropdown = new OreDropdown(context, DropdownSize.STANDARD,
                List.of(new LiteralText("Alpha"), new LiteralText("Beta"), new LiteralText("Gamma")));

        dropdown.style().width(390f).height(20f);
        dropdown.select(0);
        dropdownRow.addChild(dropdown);
        return page;
    }

    private Box buildScrollAndOverlays(ComponentContext context) {
        Box page = newPage(context);

        Box scrollRow = card(context, page, "OreScrollPane", "ALWAYS，滚轮滚动 / 裁剪", 40f);
        OreSurface scrollContent = new OreSurface(context, SurfaceVariant.CARD);
        scrollContent.style().width(370f).height(90f).padding(new Insets(4f, 4f, 4f, 4f)).gap(2f);
        for (int row = 1; row <= 8; row++) {
            OreLabel line = new OreLabel(context, new LiteralText("Scroll row " + row),
                    TypeRole.CAPTION, LabelOverflow.CLIP, 350);
            line.style().width(350f);
            scrollContent.addChild(line);
        }
        OreScrollPane scroll = new OreScrollPane(context, scrollContent, ScrollBarPolicy.ALWAYS);
        scroll.style().width(390f).height(40f);

        scrollRow.addChild(scroll);

        Box menuRow = card(context, page, "OreContextMenu", "Overlay 弹层，含危险条目", 20f);
        contextMenu = new OreContextMenu(context, List.of(
                new ContextMenuEntry(new LiteralText("Rename"), false, () -> {
                }),
                new ContextMenuEntry(new LiteralText("Duplicate"), false, () -> {
                }),
                new ContextMenuEntry(new LiteralText("Delete"), true, () -> {
                })));
        OreButton openMenu = new OreButton(context, ButtonVariant.SECONDARY, ControlSize.SM,
                new LiteralText("打开右键菜单"),
                () -> contextMenu.open(viewport.x() + 24f, viewport.y() + 72f));
        openMenu.style().width(160f);
        menuRow.addChild(openMenu);

        Box tooltipRow = card(context, page, "OreTooltip", "BOTTOM，内联展示", 24f);
        OreTooltip tooltip = new OreTooltip(context, new LiteralText("OreTooltip：这是提示文本"),
                TooltipPlacement.BOTTOM);
        tooltip.style().width(220f).height(24f);
        tooltip.setShown(true);
        tooltipRow.addChild(tooltip);

        Box toastRow = card(context, page, "OreToast", "SUCCESS，队列内联展示", 30f);
        toast = new OreToast(context, new LiteralText("OreToast：保存成功"), ToastSeverity.SUCCESS);
        toast.style().width(380f).height(30f);
        toast.show(3600f);
        toastRow.addChild(toast);
        return page;
    }

    private Box buildComposites(ComponentContext context) {
        Box page = newPage(context);

        Box accordionRow = card(context, page, "OreAccordion", "SINGLE，展开触发增量布局", 84f);
        OreAccordion accordion = new OreAccordion(context, AccordionMode.SINGLE);
        accordion.style().width(385f);
        accordion.addSection(new LiteralText("General"), cardSurface(context, "General content", 24f));
        accordion.addSection(new LiteralText("Advanced"), cardSurface(context, "Advanced content", 24f));
        accordion.setExpanded(0, true);
        accordionRow.addChild(accordion);

        Box listRow = card(context, page, "OreVirtualList", "FIXED，仅实例化可视窗口", 52f);
        String[] rows = new String[16];
        for (int i = 0; i < rows.length; i++) {
            rows[i] = "Virtual row " + i;
        }
        OreVirtualList<String> list = new OreVirtualList<>(context, RowHeightPolicy.FIXED, 13f,
                List.of(rows), (rowContext, value) -> new OreLabel(rowContext, new LiteralText(value),
                        TypeRole.CAPTION, LabelOverflow.CLIP, 350));
        list.style().width(385f).height(52f);
        list.setScroll(13f);
        listRow.addChild(list);
        return page;
    }

    private Box buildGameItems(ComponentContext context) {
        Box page = newPage(context);

        Box slotRow = card(context, page, "OreItemSlot", "STANDARD_18 / LARGE_26 / OUTPUT_ONLY / GHOST_FILTER", 26f);
        OreItemSlot standard = new OreItemSlot(context, SlotStyle.STANDARD_18);
        standard.setItem(new PreviewItem(5, false, 0, 0));
        OreItemSlot large = new OreItemSlot(context, SlotStyle.LARGE_26);
        large.setItem(new PreviewItem(1, true, 30, 100));
        OreItemSlot output = new OreItemSlot(context, SlotStyle.OUTPUT_ONLY);
        output.setItem(new PreviewItem(64, false, 0, 0));
        OreItemSlot ghost = new OreItemSlot(context, SlotStyle.GHOST_FILTER);
        ghost.setItem(new PreviewItem(1, false, 0, 0));
        slot = large;
        slotRow.addChild(standard);
        slotRow.addChild(large);
        slotRow.addChild(output);
        slotRow.addChild(ghost);

        Box gridRow = card(context, page, "OreItemGrid", "3 列 / 9 项，行重建", 54f);
        grid = new OreItemGrid(context, 3);
        grid.style().width(54f).height(54f);
        grid.setItems(fallbackItems());
        gridRow.addChild(grid);

        Box fluidRow = card(context, page, "OreFluidSlot", "液面 + 刻度，level 0.35 / 0.85", 26f);
        addFluid(fluidRow, context, 0.35f);
        addFluid(fluidRow, context, 0.85f);
        return page;
    }

    private Box buildGameEntity(ComponentContext context) {
        Box page = newPage(context);

        Box entityRow = card(context, page, "OreEntityPreview", "DRAG_ROTATE，左键拖拽", 44f);
        OreEntityPreview entityPreview = new OreEntityPreview(context);
        entityPreview.style().width(44f).height(44f);
        entityPreview.setView(30f, 0f);
        preview = entityPreview;
        entityRow.addChild(entityPreview);

        Box panelRow = card(context, page, "OrePanelFrame", "FLOATING，标题栏可拖动", 96f);
        OrePanelFrame panel = new OrePanelFrame(context, new LiteralText("OrePanelFrame"));
        panel.style().width(395f).height(96f);
        panel.setContent(new OreLabel(context, new LiteralText("标题栏可拖动，内容区自动跟随"),
                TypeRole.CAPTION, LabelOverflow.CLIP, 300));
        panelRow.addChild(panel);
        return page;
    }

    private Box buildModal(ComponentContext context) {
        Box page = newPage(context);

        Box modalRow = card(context, page, "OreModal", "DIALOG_MD，内联宿主演示，点击遮罩关闭", 96f);
        OreButton openModal = new OreButton(context, ButtonVariant.SECONDARY, ControlSize.SM,
                new LiteralText("打开模态框"), () -> modal.setOpen(true));
        openModal.style().width(110f);
        modal = new OreModal(context, new LiteralText("OreModal"),
                new LiteralText("内联 viewport 演示"), ModalSize.DIALOG_MD);
        modal.style().width(250f).height(96f);
        modal.setOpen(true);
        modalRow.addChild(openModal);
        modalRow.addChild(modal);
        return page;
    }

    private static OreSurface cardSurface(ComponentContext context, String text, float height) {
        OreSurface surface = new OreSurface(context, SurfaceVariant.CARD);
        surface.style().width(360f).height(height).padding(new Insets(4f, 4f, 4f, 4f)).gap(2f);
        OreLabel label = new OreLabel(context, new LiteralText(text),
                TypeRole.CAPTION, LabelOverflow.CLIP, 340);
        label.style().width(340f);
        surface.addChild(label);
        return surface;
    }

    private static void addFluid(Box row, ComponentContext context, float level) {
        OreFluidSlot fluid = new OreFluidSlot(context);
        fluid.style().width(26f).height(26f);
        fluid.setLevel(level);
        fluid.setFluid(new Fabric26FluidHandle(
                Identifier.fromNamespaceAndPath("minecraft", "block/water_still"), -1));
        row.addChild(fluid);
    }

    private static void addButton(Box row, ComponentContext context, ButtonVariant variant, String label) {
        OreButton button = new OreButton(context, variant, ControlSize.MD,
                new LiteralText(label), () -> {
                });
        button.style().width(74f);
        row.addChild(button);
    }

    private static void addProgress(Box row, ComponentContext context, ProgressStyle style,
                                     float value, String text) {
        OreProgressBar progress = new OreProgressBar(context, style);
        progress.style().width(126f);
        progress.setValue(value);
        if (text != null) {
            progress.setText(new LiteralText(text));
        }
        row.addChild(progress);
    }

    private static void addLabel(Box row, ComponentContext context, String text,
                                 LabelOverflow overflow, float width, int maxLines) {
        OreLabel label = new OreLabel(context, new LiteralText(text), TypeRole.CAPTION,
                overflow, (int) width, maxLines, TextAlign.START);
        label.style().width(width);
        row.addChild(label);
    }

    private Box newPage(ComponentContext context) {
        Box page = new Box(context, LayoutStyle.Direction.COLUMN, GAP);
        page.style().width(CONTENT_W);
        return page;
    }

    private Box card(ComponentContext context, Box page, String name, String note, float demoHeight) {
        float nameLine = context.theme().type(TypeRole.CAPTION).lineHeight();
        float height = CARD_PAD * 2f + nameLine + CARD_GAP + demoHeight;
        OreSurface surface = new OreSurface(context, SurfaceVariant.CARD);
        surface.style().width(CARD_W).height(height)
                .padding(new Insets(CARD_PAD, 4f, CARD_PAD, 4f)).gap(CARD_GAP);
        OreLabel label = new OreLabel(context,
                new LiteralText(name + "   " + note), TypeRole.CAPTION,
                LabelOverflow.ELLIPSIS, (int) CARD_INNER_W);
        label.style().width(CARD_INNER_W);
        Box demo = new Box(context, LayoutStyle.Direction.ROW, 4f);
        demo.style().width(CARD_INNER_W).height(demoHeight)
                .alignItems(LayoutStyle.Align.CENTER);
        surface.addChild(label);
        surface.addChild(demo);
        page.addChild(surface);
        return demo;
    }

    private static List<IItemHandle> fallbackItems() {
        return List.of(
                new PreviewItem(5, false, 0, 0), new PreviewItem(12, false, 0, 0),
                new PreviewItem(32, false, 0, 0), new PreviewItem(8, false, 0, 0),
                new PreviewItem(3, false, 0, 0), new PreviewItem(1, false, 0, 0),
                new PreviewItem(16, false, 0, 0), new PreviewItem(2, false, 0, 0),
                new PreviewItem(4, false, 0, 0));
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