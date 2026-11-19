package dev.oreui.adapter262;

import dev.oreui.components.platform.ITextInputHost;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;

public final class Fabric26TextFieldHost implements ITextInputHost {
    private final EditBox box;

    public Fabric26TextFieldHost(int maxLength) {
        this.box = new EditBox(Minecraft.getInstance().font, 0, 0, 100, 20, Component.empty());
        this.box.setBordered(false);
        this.box.setVisible(false);
        this.box.setCanLoseFocus(true);
        this.box.setMaxLength(maxLength);
    }

    @Override
    public void setBounds(float x, float y, float width, float height) {
        box.setX(Math.round(x));
        box.setY(Math.round(y));
        box.setWidth(Math.max(1, Math.round(width)));
        box.setHeight(Math.max(1, Math.round(height)));
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
}