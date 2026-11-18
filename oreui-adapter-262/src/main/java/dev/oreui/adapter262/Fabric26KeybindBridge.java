package dev.oreui.adapter262;

import com.mojang.blaze3d.platform.InputConstants;
import dev.oreui.components.platform.IKeybindBridge;

public final class Fabric26KeybindBridge implements IKeybindBridge {
    @Override
    public String displayName(int keyCode) {
        return InputConstants.Type.KEYSYM.getOrCreate(keyCode).getDisplayName().getString();
    }

    @Override
    public boolean conflicts(int firstKeyCode, int secondKeyCode) {
        return firstKeyCode == secondKeyCode;
    }
}