package dev.oreui.components.declarative;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Locale;
import java.util.Set;

public final class HtmlParser {
    private static final Set<String> VOID_TAGS = Set.of("br", "hr", "img", "input", "meta", "link");

    private HtmlParser() {
    }

    public static HtmlNode parse(String html) {
        HtmlNode root = new HtmlNode("#root");
        Deque<HtmlNode> stack = new ArrayDeque<>();
        stack.push(root);
        int index = 0;
        while (index < html.length()) {
            char current = html.charAt(index);
            if (current != '<') {
                int next = html.indexOf('<', index);
                int end = next < 0 ? html.length() : next;
                stack.peek().appendText(html.substring(index, end));
                index = end;
                continue;
            }
            if (html.startsWith("<!--", index)) {
                int end = html.indexOf("-->", index + 4);
                index = end < 0 ? html.length() : end + 3;
                continue;
            }
            if (html.startsWith("</", index)) {
                int end = html.indexOf('>', index + 2);
                if (end < 0) {
                    break;
                }
                if (stack.size() > 1) {
                    stack.pop();
                }
                index = end + 1;
                continue;
            }
            if (html.startsWith("<!", index) || html.startsWith("<?", index)) {
                int end = html.indexOf('>', index);
                index = end < 0 ? html.length() : end + 1;
                continue;
            }
            int end = findTagEnd(html, index + 1);
            if (end < 0) {
                break;
            }
            String raw = html.substring(index + 1, end);
            boolean selfClosing = raw.endsWith("/");
            if (selfClosing) {
                raw = raw.substring(0, raw.length() - 1);
            }
            String[] parts = raw.trim().split("\\s+", 2);
            String tag = parts[0].toLowerCase(Locale.ROOT);
            HtmlNode node = new HtmlNode(tag);
            if (parts.length > 1) {
                parseAttributes(parts[1], node);
            }
            stack.peek().addChild(node);
            if (!selfClosing && !VOID_TAGS.contains(tag)) {
                stack.push(node);
            }
            index = end + 1;
        }
        return root;
    }

    private static int findTagEnd(String html, int start) {
        boolean quoted = false;
        char quote = 0;
        for (int i = start; i < html.length(); i++) {
            char c = html.charAt(i);
            if (quoted) {
                if (c == quote) {
                    quoted = false;
                }
            } else if (c == '"' || c == '\'') {
                quoted = true;
                quote = c;
            } else if (c == '>') {
                return i;
            }
        }
        return -1;
    }

    private static void parseAttributes(String raw, HtmlNode node) {
        int index = 0;
        while (index < raw.length()) {
            while (index < raw.length() && Character.isWhitespace(raw.charAt(index))) {
                index++;
            }
            int nameStart = index;
            while (index < raw.length() && raw.charAt(index) != '=' && !Character.isWhitespace(raw.charAt(index))) {
                index++;
            }
            if (nameStart == index) {
                break;
            }
            String name = raw.substring(nameStart, index).toLowerCase(Locale.ROOT);
            while (index < raw.length() && Character.isWhitespace(raw.charAt(index))) {
                index++;
            }
            String value = "";
            if (index < raw.length() && raw.charAt(index) == '=') {
                index++;
                while (index < raw.length() && Character.isWhitespace(raw.charAt(index))) {
                    index++;
                }
                if (index < raw.length() && (raw.charAt(index) == '"' || raw.charAt(index) == '\'')) {
                    char quote = raw.charAt(index++);
                    int valueStart = index;
                    while (index < raw.length() && raw.charAt(index) != quote) {
                        index++;
                    }
                    value = raw.substring(valueStart, index);
                    if (index < raw.length()) {
                        index++;
                    }
                } else {
                    int valueStart = index;
                    while (index < raw.length() && !Character.isWhitespace(raw.charAt(index))) {
                        index++;
                    }
                    value = raw.substring(valueStart, index);
                }
            }
            node.attribute(name, value);
        }
    }
}