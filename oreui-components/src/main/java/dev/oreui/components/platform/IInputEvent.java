package dev.oreui.components.platform;

public interface IInputEvent {
    Kind kind();

    float x();

    float y();

    int code();

    int modifiers();

    int character();

    enum Kind {
        POINTER_MOVE,
        POINTER_DOWN,
        POINTER_UP,
        WHEEL,
        KEY_DOWN,
        KEY_UP,
        CHAR_TYPED,
        GAMEPAD_AXIS,
        GAMEPAD_BUTTON
    }
}