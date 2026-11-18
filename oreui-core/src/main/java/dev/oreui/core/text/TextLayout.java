package dev.oreui.core.text;

import java.util.ArrayList;
import java.util.List;

public final class TextLayout {
    private final List<String> lines = new ArrayList<>();
    private final StringBuilder buffer = new StringBuilder();

    public List<String> lines() {
        return lines;
    }

    public int size() {
        return lines.size();
    }

    public String line(int index) {
        return lines.get(index);
    }

    public boolean isEmpty() {
        return lines.isEmpty();
    }

    public void clear() {
        lines.clear();
        buffer.setLength(0);
    }

    StringBuilder buffer() {
        buffer.setLength(0);
        return buffer;
    }

    void add(String line) {
        lines.add(line);
    }
}