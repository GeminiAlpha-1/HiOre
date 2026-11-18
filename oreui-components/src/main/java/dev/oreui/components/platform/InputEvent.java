package dev.oreui.components.platform;

public record InputEvent(Kind kind, float x, float y, int code, int modifiers, char character)
        implements IInputEvent {
    public InputEvent {
        if (kind == null) {
            throw new NullPointerException("kind");
        }
    }
}