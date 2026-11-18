package dev.oreui.components.platform;

import java.util.List;

public record SimpleText(String key, List<Object> args) implements IText {
    public SimpleText {
        if (key == null) {
            throw new NullPointerException("key");
        }
        args = args == null ? List.of() : List.copyOf(args);
    }
}