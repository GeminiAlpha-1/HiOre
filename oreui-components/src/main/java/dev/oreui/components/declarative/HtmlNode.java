package dev.oreui.components.declarative;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class HtmlNode {
    private final String tag;
    private final Map<String, String> attributes = new LinkedHashMap<>();
    private final List<HtmlNode> children = new ArrayList<>();
    private final StringBuilder text = new StringBuilder();

    HtmlNode(String tag) {
        this.tag = tag;
    }

    public String tag() {
        return tag;
    }

    public Map<String, String> attributes() {
        return attributes;
    }

    public String attribute(String name) {
        return attributes.get(name);
    }

    public List<HtmlNode> children() {
        return children;
    }

    public String text() {
        return text.toString();
    }

    void addChild(HtmlNode child) {
        children.add(child);
    }

    void appendText(String value) {
        text.append(value);
    }

    void attribute(String name, String value) {
        attributes.put(name, value);
    }
}