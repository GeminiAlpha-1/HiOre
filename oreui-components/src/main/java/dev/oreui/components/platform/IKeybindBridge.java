package dev.oreui.components.platform;

public interface IKeybindBridge {
    String displayName(int keyCode);

    boolean conflicts(int firstKeyCode, int secondKeyCode);
}