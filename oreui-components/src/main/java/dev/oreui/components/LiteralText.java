package dev.oreui.components;

import dev.oreui.components.platform.IText;

import java.util.List;

public record LiteralText(String text) implements IText {
    public LiteralText {
        if (text == null) {
            throw new NullPointerException("text");
        }
    }

    @Override
    public String key() {
        return text;
    }

    @Override
    public List<Object> args() {
        return List.of();
    }
}