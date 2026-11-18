package dev.oreui.components;

import dev.oreui.components.platform.IRenderContext;
import dev.oreui.components.platform.IText;
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
    private int caret;
    private float blinkElapsed;

    public OreTextField(ComponentContext context, InputValidation validation, int maxLength) {
        super(context);
        this.validation = validation;
        this.maxLength = maxLength;
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
        markDirty();
    }

    public int caret() {
        return caret;
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        blinkElapsed = 0f;
        if (focused) {
            caret = value.length();
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
        if (key == KeyCodes.BACKSPACE) {
            if (caret > 0) {
                pushUndo();
                value.deleteCharAt(caret - 1);
                caret--;
            }
            return true;
        }
        if (key == KeyCodes.DELETE) {
            if (caret < value.length()) {
                pushUndo();
                value.deleteCharAt(caret);
            }
            return true;
        }
        if (key == KeyCodes.LEFT) {
            caret = Math.max(0, caret - 1);
            return true;
        }
        if (key == KeyCodes.RIGHT) {
            caret = Math.min(value.length(), caret + 1);
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
    public boolean charTyped(char character) {
        if (!isEnabled() || !valid(character) || value.length() >= maxLength) {
            return false;
        }
        insert(String.valueOf(character));
        return true;
    }

    private void insert(String text) {
        if (text == null || text.isEmpty()) {
            return;
        }
        int room = maxLength - value.length();
        if (room <= 0) {
            return;
        }
        String insert = text.substring(0, Math.min(room, text.length()));
        pushUndo();
        value.insert(caret, insert);
        caret += insert.length();
    }

    private boolean valid(char character) {
        return switch (validation) {
            case INT -> Character.isDigit(character);
            case FLOAT -> Character.isDigit(character) || character == '.';
            case TEXT, SEARCH -> character >= ' ' && character != 127;
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
        IText display = new LiteralText(current);
        ColorRole color = isEnabled() ? ColorRole.TEXT_PRIMARY : ColorRole.TEXT_DISABLED;
        text(ctx, display, bounds().x() + 3f, bounds().y() + 5f, TypeRole.BODY, color);
        if (isFocused()) {
            float period = Motion.CARET_BLINK.millis() / 1000f;
            if ((blinkElapsed % (period * 2f)) < period) {
                String before = current.substring(0, Math.min(caret, current.length()));
                float caretX = bounds().x() + 3f
                        + context.textMeasurer().measure(new LiteralText(before), TypeRole.BODY, 0f).width();
                fill(ctx, ColorRole.CARET, caretX, bounds().y() + 4f, 1f, 12f);
            }
        }
    }
}