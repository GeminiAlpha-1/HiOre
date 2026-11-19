package dev.oreui.adapter262;

import dev.oreui.components.platform.ITextInputHost;
import net.minecraft.client.Minecraft;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;

public final class Fabric26TextFieldHost implements ITextInputHost {
    private final Fabric26EditBox box;

    public Fabric26TextFieldHost(int maxLength) {
        this.box = new Fabric26EditBox(0, 0, 100, 20, Component.empty());
        this.box.setBordered(false);
        this.box.setCanLoseFocus(true);
        this.box.setMaxLength(maxLength);
    }

    @Override
    public void setBounds(float x, float y, float width, float height) {
        box.setX(Math.round(x));
        box.setY(Math.round(y));
        box.setWidth(Math.max(1, Math.round(width)));
        box.setHeight(Math.max(1, Math.round(height)));
        if (box.isFocused()) {
            ensureScreenFocus();
        }
    }

    @Override
    public void setValue(String value) {
        box.setValue(value == null ? "" : value);
    }

    @Override
    public String value() {
        return box.getValue();
    }

    @Override
    public void setMaxLength(int maxLength) {
        box.setMaxLength(maxLength);
    }

    @Override
    public void setFocused(boolean focused) {
        box.setFocused(focused);
        Screen screen = screen();
        if (screen == null) {
            return;
        }
        if (focused) {
            ensureScreenFocus();
        } else if (screen.getFocused() == box) {
            screen.setFocused(null);
        }
    }

    @Override
    public boolean nativeFocused() {
        Screen screen = screen();
        return box.isFocused() && screen != null && screen.getFocused() == box;
    }

    @Override
    public boolean keyPressed(int key, int modifiers) {
        return box.keyPressed(new KeyEvent(key, 0, modifiers));
    }

    @Override
    public boolean charTyped(int codepoint) {
        return box.charTyped(new CharacterEvent(codepoint));
    }

    @Override
    public void insertText(String text) {
        box.insertText(text == null ? "" : text);
    }

    @Override
    public int cursorPosition() {
        return box.getCursorPosition();
    }

    @Override
    public String preeditText() {
        return box.preedit();
    }

    private static Screen screen() {
        return Minecraft.getInstance().gui.screen();
    }

    private void ensureScreenFocus() {
        Screen screen = screen();
        if (screen != null && screen.getFocused() != box) {
            screen.setFocused(box);
        }
    }
}