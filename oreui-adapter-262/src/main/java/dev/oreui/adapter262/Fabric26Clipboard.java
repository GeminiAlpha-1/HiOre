package dev.oreui.adapter262;

import dev.oreui.components.platform.IClipboard;
import net.minecraft.client.Minecraft;

public final class Fabric26Clipboard implements IClipboard {
    @Override
    public String get() {
        return Minecraft.getInstance().keyboardHandler.getClipboard();
    }

    @Override
    public void set(String text) {
        if (text == null) {
            throw new NullPointerException("text");
        }
        Minecraft.getInstance().keyboardHandler.setClipboard(text);
    }
}