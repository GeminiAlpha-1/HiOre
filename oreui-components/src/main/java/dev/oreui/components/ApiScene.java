package dev.oreui.components;

import dev.oreui.api.OreScene;
import dev.oreui.components.platform.IClipboard;
import dev.oreui.components.platform.IGameObjectRenderer.IItemHandle;
import dev.oreui.components.platform.IKeybindBridge;
import dev.oreui.components.platform.IRenderContext;
import dev.oreui.components.platform.IText;
import dev.oreui.components.platform.ITextMeasurer;
import dev.oreui.components.platform.TextMetrics;
import dev.oreui.core.geom.Insets;
import dev.oreui.core.geom.Rect2f;
import dev.oreui.theme.ButtonVariant;
import dev.oreui.theme.ColorRole;
import dev.oreui.theme.OreTheme;
import dev.oreui.theme.OreTokens;
import dev.oreui.theme.SizeToken;
import dev.oreui.theme.TypeRole;

import java.util.ArrayList;
import java.util.List;

public final class ApiScene implements OreScene {
    private static final float PAD = 4f;
    private static final float GAP = 2f;
    private static final int TEXT_WIDTH = 6;

    private final ComponentContext context;
    private final OreSurface root;
    private final ComponentTree tree;
    private final Rect2f viewport;
    private final float contentWidth;

    public ApiScene(SizeToken width, SizeToken height) {
        OreTheme theme = OreTokens.DARK;
        this.context = new ComponentContext(theme, IText::key, new EstimatedMeasurer(theme),
                new NullClipboard(), (role, volume, pitch) -> {
                }, new NullKeybinds(), () -> 0.016f, null);
        float w = width.value();
        float h = height.value();
        this.viewport = new Rect2f(0f, 0f, w, h);
        this.contentWidth = w - PAD * 2f;
        this.root = new OreSurface(context, SurfaceVariant.PANEL);
        root.style().width(w).height(h).padding(new Insets(PAD, PAD, PAD, PAD)).gap(GAP);
        this.tree = new ComponentTree(root);
    }

    @Override
    public OreScene label(String text) {
        OreLabel label = new OreLabel(context, new LiteralText(text), TypeRole.BODY,
                LabelOverflow.CLIP, (int) contentWidth);
        label.style().width(contentWidth);
        root.addChild(label);
        return this;
    }

    @Override
    public OreScene button(String text, boolean primary, Runnable action) {
        OreButton button = new OreButton(context,
                primary ? ButtonVariant.PRIMARY : ButtonVariant.SECONDARY, ControlSize.MD,
                new LiteralText(text), action);
        button.style().width(contentWidth);
        root.addChild(button);
        return this;
    }

    @Override
    public OreScene toggle(boolean on) {
        OreToggle toggle = new OreToggle(context);
        toggle.setOn(on);
        root.addChild(toggle);
        return this;
    }

    @Override
    public OreScene slider(float value) {
        OreSlider slider = new OreSlider(context, SliderMode.CONTINUOUS);
        slider.style().width(contentWidth);
        slider.setValue(value);
        root.addChild(slider);
        return this;
    }

    @Override
    public OreScene textField(String text) {
        OreTextField field = new OreTextField(context, InputValidation.TEXT, 64);
        field.style().width(contentWidth);
        field.setText(text);
        root.addChild(field);
        return this;
    }

    @Override
    public OreScene accordion(String title, String body) {
        OreAccordion accordion = new OreAccordion(context, AccordionMode.SINGLE);
        accordion.style().width(contentWidth);
        OreLabel content = new OreLabel(context, new LiteralText(body), TypeRole.CAPTION,
                LabelOverflow.CLIP, (int) contentWidth - 8);
        accordion.addSection(new LiteralText(title), content);
        accordion.setExpanded(0, true);
        root.addChild(accordion);
        return this;
    }

    @Override
    public OreScene itemSlot(int count, boolean damageable, int durability, int durabilityMax) {
        OreItemSlot slot = new OreItemSlot(context, SlotStyle.STANDARD_18);
        slot.setItem(new PlainItem(count, damageable, durability, durabilityMax));
        root.addChild(slot);
        return this;
    }

    @Override
    public OreScene itemGrid(int columns, int... counts) {
        OreItemGrid grid = new OreItemGrid(context, Math.max(1, columns));
        List<IItemHandle> items = new ArrayList<>();
        for (int count : counts) {
            items.add(new PlainItem(count, false, 0, 0));
        }
        grid.setItems(items);
        root.addChild(grid);
        return this;
    }

    @Override
    public void layout() {
        tree.layout(viewport);
    }

    @Override
    public void update(float deltaSeconds) {
        tree.update(deltaSeconds);
    }

    @Override
    public void render() {
        tree.renderBackground(new NullRenderContext());
        tree.renderForeground(new NullRenderContext());
    }

    @Override
    public int widgetCount() {
        return count(root);
    }

    private static int count(OreWidget widget) {
        int total = 1;
        for (OreWidget child : widget.children()) {
            total += count(child);
        }
        return total;
    }

    private static final class EstimatedMeasurer implements ITextMeasurer {
        private final OreTheme theme;

        private EstimatedMeasurer(OreTheme theme) {
            this.theme = theme;
        }

        @Override
        public TextMetrics measure(IText text, TypeRole role, float maxWidth) {
            String value = text.key();
            return new TextMetrics(value.length() * TEXT_WIDTH, theme.type(role).lineHeight(),
                    List.of(value));
        }
    }

    private static final class NullClipboard implements IClipboard {
        private String value = "";

        @Override
        public String get() {
            return value;
        }

        @Override
        public void set(String text) {
            value = text;
        }
    }

    private static final class NullKeybinds implements IKeybindBridge {
        @Override
        public String displayName(int keyCode) {
            return Integer.toString(keyCode);
        }

        @Override
        public boolean conflicts(int firstKeyCode, int secondKeyCode) {
            return firstKeyCode == secondKeyCode;
        }
    }

    private static final class PlainItem implements IItemHandle {
        private final int count;
        private final boolean damageable;
        private final int durability;
        private final int durabilityMax;

        private PlainItem(int count, boolean damageable, int durability, int durabilityMax) {
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

    private static final class NullRenderContext implements IRenderContext {
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