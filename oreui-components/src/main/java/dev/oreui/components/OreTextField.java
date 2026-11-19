package dev.oreui.components;

import dev.oreui.components.platform.IRenderContext;
import dev.oreui.components.platform.IText;
import dev.oreui.components.platform.ITextInputHost;
import dev.oreui.components.platform.TextMetrics;
import dev.oreui.theme.ColorRole;
import dev.oreui.theme.Motion;
import dev.oreui.theme.TypeRole;

import java.util.ArrayDeque;
import java.util.Deque;

public final class OreTextField extends OreInteractive {
    private final StringBuilder value = new StringBuilder();
    private final int maxLength;
    private final InputValidation validation;
    private final Deque<String> undo = new ArrayDeque<>();
    private final ITextInputHost inputHost;
    private int caret;
    private float blinkElapsed;

    public OreTextField(ComponentContext context, InputValidation validation, int maxLength) {
        this(context, validation, maxLength, null);
    }

    public OreTextField(ComponentContext context, InputValidation validation, int maxLength,
                        ITextInputHost inputHost) {
        super(context);
        this.validation = validation;
        this.maxLength = maxLength;
        this.inputHost = inputHost;
        if (inputHost != null) {
            inputHost.setMaxLength(maxLength);
        }
        style().width(180f).height(20f);
    }

    public String text() {
        return value.toString();
    }

    public void setText(String text) {
        String next = text == null ? "" : text;
        if (next.length() > maxLength) {
            next = next.substring(0, maxLength);
        }
        value.setLength(0);
        value.append(next);
        caret = value.length();
        if (inputHost != null) {
            inputHost.setValue(next);
        }
        markDirty();
    }

    public int caret() {
        return caret;
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        blinkElapsed = 0f;
        if (inputHost != null) {
            inputHost.setFocused(focused);
        }
        if (focused) {
            caret = inputHost != null ? inputHost.cursorPosition() : value.length();
        }
    }

    @Override
    public void syncFrames() {
        super.syncFrames();
        if (inputHost != null) {
            inputHost.setBounds(bounds().x(), bounds().y(), bounds().width(), bounds().height());
        }
    }

    @Override
    public void update(float deltaSeconds) {
        blinkElapsed += deltaSeconds;
    }

    @Override
    public boolean pointerDown(float x, float y) {
        return isEnabled() && contains(x, y);
    }

    @Override
    public boolean keyPressed(int key, int modifiers) {
        if (!isEnabled()) {
            return false;
        }
        if (inputHost != null) {
            if (inputHost.nativeFocused()) {
                syncFromHost();
                return true;
            }
            boolean handled = inputHost.keyPressed(key, modifiers);
            syncFromHost();
            return handled;
        }
        if (key == KeyCodes.BACKSPACE) {
            if (caret > 0) {
                int cp = value.codePointBefore(caret);
                pushUndo();
                value.delete(caret - Character.charCount(cp), caret);
                caret -= Character.charCount(cp);
            }
            return true;
        }
        if (key == KeyCodes.DELETE) {
            if (caret < value.length()) {
                int cp = value.codePointAt(caret);
                pushUndo();
                value.delete(caret, caret + Character.charCount(cp));
            }
            return true;
        }
        if (key == KeyCodes.LEFT) {
            caret = caret > 0 ? caret - Character.charCount(value.codePointBefore(caret)) : 0;
            return true;
        }
        if (key == KeyCodes.RIGHT) {
            caret = caret < value.length() ? caret + Character.charCount(value.codePointAt(caret)) : caret;
            return true;
        }
        if (key == KeyCodes.HOME) {
            caret = 0;
            return true;
        }
        if (key == KeyCodes.END) {
            caret = value.length();
            return true;
        }
        if (key == KeyCodes.C && (modifiers & 2) != 0) {
            context.clipboard().set(value.toString());
            return true;
        }
        if (key == KeyCodes.V && (modifiers & 2) != 0) {
            insert(context.clipboard().get());
            return true;
        }
        if (key == KeyCodes.Z && (modifiers & 2) != 0) {
            if (!undo.isEmpty()) {
                String previous = undo.pop();
                value.setLength(0);
                value.append(previous);
                caret = Math.min(caret, value.length());
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean charTyped(int codepoint) {
        if (!isEnabled() || !valid(codepoint) || value.length() >= maxLength) {
            return false;
        }
        if (inputHost != null) {
            if (inputHost.nativeFocused()) {
                syncFromHost();
                return true;
            }
            boolean handled = inputHost.charTyped(codepoint);
            syncFromHost();
            return handled;
        }
        String inserted = new String(Character.toChars(codepoint));
        if (value.length() + inserted.length() > maxLength) {
            return false;
        }
        insert(inserted);
        return true;
    }

    private void syncFromHost() {
        String next = inputHost.value();
        value.setLength(0);
        value.append(next);
        caret = Math.min(inputHost.cursorPosition(), value.length());
        markDirty();
    }

    private void insert(String text) {
        if (text == null || text.isEmpty()) {
            return;
        }
        if (inputHost != null) {
            inputHost.insertText(text);
            syncFromHost();
            return;
        }
        int room = maxLength - value.length();
        if (room <= 0) {
            return;
        }
        String insert = text;
        if (insert.length() > room) {
            insert = insert.substring(0, room);
            if (!insert.isEmpty() && Character.isHighSurrogate(insert.charAt(insert.length() - 1))) {
                insert = insert.substring(0, insert.length() - 1);
            }
        }
        if (insert.isEmpty()) {
            return;
        }
        pushUndo();
        value.insert(caret, insert);
        caret += insert.length();
    }

    private boolean valid(int codepoint) {
        return switch (validation) {
            case INT -> Character.isDigit(codepoint);
            case FLOAT -> Character.isDigit(codepoint) || codepoint == '.';
            case TEXT, SEARCH -> Character.isValidCodePoint(codepoint) && codepoint >= ' ' && codepoint != 127;
        };
    }

    private void pushUndo() {
        undo.push(value.toString());
        if (undo.size() > 16) {
            undo.removeLast();
        }
    }

    @Override
    public void renderBackground(IRenderContext ctx) {
        float x = bounds().x();
        float y = bounds().y();
        fill(ctx, ColorRole.SURFACE_SUNKEN, x, y, bounds().width(), bounds().height());
        outline(ctx, ColorRole.BORDER_OUTER, x, y, bounds().width(), bounds().height(), 1f);
        drawFocus(ctx);
    }

    @Override
    public void renderForeground(IRenderContext ctx) {
        String current = value.toString();
        int caretPos = Math.min(caret, current.length());
        String preedit = inputHost == null ? "" : inputHost.preeditText();
        String presented = preedit.isEmpty() ? current
                : current.substring(0, caretPos) + preedit + current.substring(caretPos);
        IText display = new LiteralText(presented);
        ColorRole color = isEnabled() ? ColorRole.TEXT_PRIMARY : ColorRole.TEXT_DISABLED;
        TextMetrics metrics = context.textMeasurer().measure(display, TypeRole.BODY, 0f);
        text(ctx, display, bounds().x() + 3f,
                textCenterY(bounds().y() + 1f, Math.max(1f, bounds().height() - 2f), metrics),
                TypeRole.BODY, color);
        if (isFocused()) {
            float period = Motion.CARET_BLINK.millis() / 1000f;
            if ((blinkElapsed % (period * 2f)) < period) {
                String before = presented.substring(0, caretPos + preedit.length());
                float caretX = bounds().x() + 3f
                        + context.textMeasurer().measure(new LiteralText(before), TypeRole.BODY, 0f).width();
                fill(ctx, ColorRole.CARET, caretX, bounds().y() + 4f, 1f, 12f);
            }
        }
    }
}