package dev.oreui.components.platform;

public interface ITextInputHost {
    void setBounds(float x, float y, float width, float height);

    void setValue(String value);

    String value();

    void setMaxLength(int maxLength);

    void setFocused(boolean focused);

    boolean keyPressed(int key, int modifiers);

    boolean charTyped(int codepoint);

    void insertText(String text);

    int cursorPosition();

    default boolean nativeFocused() {
        return false;
    }

    default String preeditText() {
        return "";
    }
}