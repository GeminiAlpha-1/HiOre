package dev.oreui.components;

import dev.oreui.components.platform.IRenderContext;
import dev.oreui.theme.ColorRole;
import dev.oreui.theme.ControlState;

public abstract class OreInteractive extends OreWidget {
    private boolean enabled = true;
    private boolean hovered;
    private boolean pressed;
    private boolean focused;

    protected OreInteractive(ComponentContext context) {
        super(context);
    }

    public ControlState state() {
        if (!enabled) {
            return ControlState.DISABLED;
        }
        if (pressed) {
            return ControlState.PRESSED;
        }
        if (hovered) {
            return ControlState.HOVER;
        }
        if (focused) {
            return ControlState.FOCUSED;
        }
        return ControlState.DEFAULT;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            hovered = false;
            pressed = false;
        }
    }

    public boolean isFocused() {
        return focused;
    }

    @Override
    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    @Override
    public void setHovered(boolean hovered) {
        this.hovered = enabled && hovered;
    }

    @Override
    public boolean isFocusable() {
        return enabled;
    }

    @Override
    public boolean pointerDown(float x, float y) {
        if (!enabled || !contains(x, y)) {
            return false;
        }
        pressed = true;
        return true;
    }

    @Override
    public boolean pointerUp(float x, float y) {
        if (!pressed) {
            return false;
        }
        pressed = false;
        if (enabled && contains(x, y)) {
            activate();
        }
        return true;
    }

    @Override
    public boolean keyPressed(int key, int modifiers) {
        if (!enabled) {
            return false;
        }
        if (key == KeyCodes.ENTER || key == KeyCodes.SPACE) {
            activate();
            return true;
        }
        return false;
    }

    protected void activate() {
    }

    protected void drawFocus(IRenderContext ctx) {
        if (focused && enabled) {
            outline(ctx, ColorRole.FOCUS_RING, bounds().x() - 2f, bounds().y() - 2f,
                    bounds().width() + 4f, bounds().height() + 4f, 2f);
        }
    }
}